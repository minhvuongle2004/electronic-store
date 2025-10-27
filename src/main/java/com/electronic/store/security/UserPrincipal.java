package com.electronic.store.security;

import com.electronic.store.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private User.Role role;
    private User.Status status;
    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, String username, String email, String password,
                        String fullName, User.Role role, User.Status status,
                        Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.status = status;
        this.authorities = authorities;
    }

    // Tạo UserPrincipal từ User entity
    public static UserPrincipal create(User user) {
        // Tạo authorities từ role
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getFullName(),
                user.getRole(),
                user.getStatus(),
                Collections.singletonList(authority)
        );
    }

    // Getters cho các thuộc tính tùy chỉnh
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public User.Role getRole() {
        return role;
    }

    public User.Status getStatus() {
        return status;
    }

    // Implement UserDetails interface
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Tài khoản không bao giờ hết hạn
    }

    @Override
    public boolean isAccountNonLocked() {
        return status == User.Status.ACTIVE; // Tài khoản không bị khóa nếu status = ACTIVE
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Credentials không bao giờ hết hạn
    }

    @Override
    public boolean isEnabled() {
        return status == User.Status.ACTIVE; // Tài khoản được kích hoạt nếu status = ACTIVE
    }

    // Kiểm tra quyền admin
    public boolean isAdmin() {
        return role == User.Role.ADMIN;
    }

    // Kiểm tra quyền user
    public boolean isUser() {
        return role == User.Role.USER;
    }
}