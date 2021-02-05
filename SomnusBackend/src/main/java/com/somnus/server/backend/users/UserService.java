package com.somnus.server.backend.users;

import com.somnus.server.backend.auth.dto.AuthDto;
import com.somnus.server.backend.auth.firebase.FirebaseParser;
import com.somnus.server.backend.auth.firebase.FirebaseTokenHolder;
import com.somnus.server.backend.users.domain.Role;
import com.somnus.server.backend.users.domain.User;
import com.somnus.server.backend.users.dto.UserDto;
import com.somnus.server.backend.users.repository.RoleRepository;
import com.somnus.server.backend.users.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RolesHandler rolesHandler;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = userRepository.findByUserName(username);
        if(userDetails == null)
            return null;

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (GrantedAuthority role : userDetails.getAuthorities()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        }

        return new org.springframework.security.core.userdetails.User(userDetails.getUsername(),
                userDetails.getPassword(), userDetails.getAuthorities());
    }

    public AuthDto registerNewUser(String firebaseToken) {
        if(StringUtils.isBlank(firebaseToken)) {
            throw new IllegalArgumentException("Blank Firebase Token");
        }
        FirebaseTokenHolder firebaseTokenHolder = FirebaseParser.parseToken(firebaseToken);

        // add user to repository if it does not exist already
        User user = userRepository.findByUserName(firebaseTokenHolder.getUid());
        if(user == null) {
            // TODO change password auth
            user = new User(firebaseTokenHolder.getUid(), UUID.randomUUID().toString(),
                    firebaseTokenHolder.getEmail(), rolesHandler.getRole(Role.USER));
            userRepository.save(user);
        }

        // get user roles
        List<String> roles = new ArrayList<>();
        user.getAuthorities().stream().forEach(auth -> roles.add(auth.getAuthority()));

        // create new AuthDto
        return new AuthDto(firebaseToken, new UserDto(user.getUsername(), user.getEmail(), roles));
    }
}
