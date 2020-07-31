var express = require('express');
var router = express.Router();

var articles_controller = require("../controllers/articlesController");

router.get('/articles', articles_controller.article_list);

router.get('/article/:id', articles_controller.article_detail);

router.post('/article/create', articles_controller.article_create);

module.exports = router;