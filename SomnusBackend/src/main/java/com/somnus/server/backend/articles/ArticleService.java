package com.somnus.server.backend.articles;

import com.somnus.server.backend.articles.domain.Article;
<<<<<<< Updated upstream
import com.somnus.server.backend.articles.domain.ArticleTopic;
=======
>>>>>>> Stashed changes
import com.somnus.server.backend.articles.dto.ArticleDto;
import com.somnus.server.backend.articles.repository.ArticleRepository;
import com.somnus.server.backend.articles.repository.DeletedArticleRepository;
import com.somnus.server.backend.exceptions.ErrorMessage;
import com.somnus.server.backend.exceptions.SomnusException;
import com.somnus.server.backend.post.PostService;
import com.somnus.server.backend.topic.ArticleTopic;
import com.somnus.server.backend.users.domain.Role;
import com.somnus.server.backend.users.domain.User;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private DeletedArticleRepository deletedArticleRepository;

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ArticleDto> getAllArticles() {
        List<ArticleDto> articleDtos = new ArrayList<>();
        articleRepository.findAll().forEach(article -> articleDtos.add(
                createArticleDto(article)));
        return articleDtos;
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public  List<ArticleDto> getDeletedArticles(User user) {
        List<Article> articles = deletedArticleRepository.getAllDeletedArticlesFromAuthorUsername(user.getUsername());
        if (!articles.isEmpty()) {
            throw new SomnusException(ErrorMessage.NO_NEWS_POST_FOUND);
        }

        List<ArticleDto> articleDtos = new ArrayList<>();
        articles.forEach(post -> articleDtos.add(createArticleDto(post)));
        return articleDtos;
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void restoreArticle(User user, Integer id) {
        Optional<Article> optionalArticle = deletedArticleRepository.findById(id);
        if (optionalArticle.isEmpty()) {
            throw new SomnusException(ErrorMessage.NO_ARTICLE_FOUND);
        }
        Article article = optionalArticle.get();

        postService.postCreateAuthCheck(user);
        articleRepository.save(article);
        deletedArticleRepository.delete(article);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ArticleDto> getArticlesOfTopic(String articleTopic) {
        List<ArticleDto> articleDtos = new ArrayList<>();
        articleRepository.getArticlesMatchingTopic(articleTopic).forEach(article -> articleDtos.add(
                createArticleDto(article)));
        return articleDtos;
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ArticleDto getArticle(Integer id) {
        Optional<Article> optionalArticle = articleRepository.findById(id);
        if (optionalArticle.isEmpty()) {
            throw new SomnusException(ErrorMessage.NO_ARTICLE_FOUND);
        }

        Article article = optionalArticle.get();
        return createArticleDto(article);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void createOrUpdateArticle(User user, ArticleDto articleDto) {
        // Parse article Topic
        if (Strings.isBlank(articleDto.getTopic())) {
            throw new SomnusException(ErrorMessage.NO_TOPIC_PROVIDED);
        }

        Article article = null;
        if (articleDto.getId() != null) {
            article = articleRepository.getOne(Integer.parseInt(articleDto.getId()));
        }

        if (!user.getRole().equals(Role.ADMIN) && !user.getRole().equals(Role.MANAGER) &&
                !user.getRole().equals(Role.EDITOR)) {
            throw new SomnusException(ErrorMessage.ROLE_NOT_ALLOWED);
        }

        ArticleTopic articleTopic = ArticleTopic.valueOf(articleDto.getTopic()
                .replace(" ", "_").toUpperCase());
        if (article == null) {
            article = new Article(user, articleDto.getArticleName(),
                    articleDto.getAuthorUserName(), articleDto.getDescription(),
                    articleTopic, articleDto.getContent());
        } else {
            article.setArticleName(articleDto.getArticleName());
            article.setDescription(articleDto.getDescription());
            article.setTopic(articleTopic);
            article.setContent(articleDto.getContent());
            article.updateLastUpdate();
        }
        articleRepository.save(article);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteArticle(User user, Integer id) {
        Article article = articleRepository.getOne(id);

        // Article can only be deleted by the person who wrote it or the admin
        if (!article.getAuthor().getUsername().equals(user.getUsername()) &&
                !user.getRole().equals(Role.ADMIN)) {
            throw new SomnusException(ErrorMessage.DELETE_ARTICLE_NOT_ALLOWED);
        }

        deletedArticleRepository.save(article);
        articleRepository.deleteById(id);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ArticleDto> searchArticles(String nameContent) {
        // TODO: Find more efficient search method
        String searchQuery = "%" + nameContent + "%";
        List<Article> articles = articleRepository.getAllArticlesContainingSubstring(searchQuery);
        List<ArticleDto> articleDtos = new ArrayList<>();
        articles.forEach(article -> articleDtos.add(createArticleDto(article)));
        return articleDtos;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////// Private Methods ///////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private ArticleDto createArticleDto(Article article) {
        return new ArticleDto(String.valueOf(article.getId()), article.getArticleName(),
                normalizeName(article.getArticleName()), article.getAuthorUserName(),
                article.getAuthor().getUsername(), article.getDescription(), article.getDatePublished(),
                article.getLastUpdate(), article.getTopic().name, normalizeName(article.getTopic().name),
                article.getContent());
    }
<<<<<<< Updated upstream

    private String normalizeName(String articleName) {
        return Arrays.stream(articleName.toLowerCase()
                .split(" "))
                .reduce((a, b) -> a.concat("-").concat(b)).get();
    }
=======
>>>>>>> Stashed changes
}
