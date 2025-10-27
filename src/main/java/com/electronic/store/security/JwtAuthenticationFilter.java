package com.electronic.store.security;

import com.electronic.store.service.UserDetailsServiceImpl;
import com.electronic.store.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// @Component - Disabled because Spring Security is disabled
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Lấy JWT token từ request
            String jwt = parseJwt(request);

            // Nếu có token và token hợp lệ
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // Lấy username từ token
                String username = jwtUtils.getUsernameFromJwtToken(jwt);

                // Load user details từ database
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Validate token với user details
                if (jwtUtils.validateJwtToken(jwt, userDetails)) {
                    // Tạo authentication object
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // Set authentication details
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication vào SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    logger.debug("Set authentication for user: {}", username);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
            // Clear security context nếu có lỗi
            SecurityContextHolder.clearContext();
        }

        // Tiếp tục filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Lấy JWT token từ Authorization header
     * Format: Bearer <token>
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Bỏ "Bearer " (7 ký tự)
        }

        return null;
    }

    /**
     * Kiểm tra request có cần authenticate không
     * Override method này nếu muốn skip một số endpoint
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Skip authentication cho các endpoint public
        return path.startsWith("/api/auth/") ||
               path.startsWith("/api/public/") ||
               path.equals("/api/") ||
               path.startsWith("/h2-console/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs");
    }
}