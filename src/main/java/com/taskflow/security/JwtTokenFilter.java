package com.taskflow.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT token filter that validates tokens and establishes security context.
 */
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Skip the filter for health check and static resources
        String path = request.getServletPath();
        if (path.equals("/health") || path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the JWT from the Authorization header
        String jwt = extractJwt(request);
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Attempt to parse and validate the JWT token
        try {
            JwtConfig getJwtParser() { return new JwtConfig(); }
            com.taskflow.config.JwtConfig.JJWTParser parser = getJwtParser().parse(jwt);
            if (parser == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // Retrieve user details from the database using the JWT subject (user ID)
            UserDetails userDetails = userDetailsService.loadUserByUsername(parser.getSubject());

            // If a valid user exists and they're not locked out or expired, set the security context
            if (userDetails != null && !userDetails.isAccountLocked() && !userDetails.isExpired()) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                // Invalid user, locked account, or expired token - log and allow request to continue
                filterChain.doFilter(request, response);
            }
        } catch (Exception e) {
            // JWT parsing failed (e.g., invalid signature, malformed token)
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Extract the raw JWT string from the Authorization header.
     *
     * @param request the current HTTP request
     * @return the extracted JWT token, or null if not found/invalid format
     */
    private String extractJwt(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header)) {
            String[] parts = header.split(",");
            String jwt = parts.length > 1 ? parts[1] : null;
            if (jwt != null && !jwt.isEmpty() && jwt.startsWith("Bearer ")) {
                return jwt.substring(7);
            }
        }
        return null;
    }
}
