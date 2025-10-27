package com.electronic.store.config;

import com.electronic.store.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

// @Configuration  // Disabled - No Security
// @EnableWebSecurity  // Disabled
// @EnableMethodSecurity(prePostEnabled = true) // Disabled
public class SecurityConfig {

    // @Autowired
    // private UserDetailsServiceImpl userDetailsService;

    /**
     * Password Encoder - sử dụng BCrypt - Kept for AuthService
     */
    // @Bean  // Disabled - will create manually in AuthService
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // /**
    //  * Authentication Provider - kết nối UserDetailsService với PasswordEncoder
    //  */
    // @Bean
    // public DaoAuthenticationProvider authenticationProvider() {
    //     DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    //     authProvider.setUserDetailsService(userDetailsService);
    //     authProvider.setPasswordEncoder(passwordEncoder());
    //     return authProvider;
    // }

    // /**
    //  * Authentication Manager
    //  */
    // @Bean
    // public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    //     return authConfig.getAuthenticationManager();
    // }

    /**
     * CORS Configuration
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // Cho phép tất cả origin trong dev
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Security Filter Chain - DISABLED for testing
     */
    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     // Security disabled - all endpoints accessible
    //     return http.build();
    // }
}