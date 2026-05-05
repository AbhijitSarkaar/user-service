
package com.rest.user_service.security.service;

import com.rest.user_service.model.User;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UserDetailsImpl implements UserDetails {

    private static final Long serialVersionUID = 1L;

    private Long id;

    private String username;
    private String password;
    private String email;

    Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username, String email,
                           String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public static UserDetails build(User user) {

        Collection<? extends GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(item -> new SimpleGrantedAuthority(item.getRoleName().name()))
                .toList();

        return new UserDetailsImpl(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );

    }
}
