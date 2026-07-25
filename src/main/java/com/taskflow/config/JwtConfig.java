package com.taskflow.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT configuration for generating and validating access tokens.
 */
@Slf4j
@Configuration
public class JwtConfig {

    @Value("${spring.security.jwt.secret:TaskFlowSecret2024}")
    private String jwtSecret;

    @Value("${spring.security.jwt.expiration:3600000}")
    private Long expirationMillis;

    /**
     * Generate a secret key from the provided JWT secret string.
     *
     * @return the SecretKey to be used for signing and verifying JWTs
     */
    @Bean
    public SecretKey jwtSecretKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Build a JJWT parser using the generated secret key.
     *
     * @return JWTParser configured to verify tokens with our secret and expiration claims
     */
    @Bean
    public JJWTParser jwtParser() {
        return new JJWTParser(jwtSecretKey(), expirationMillis);
    }

    /**
     * Parse a JWT token string into its components.
     *
     * @param token the JWT token to parse and validate
     * @return JwtClaims containing the validated claims, or null if invalid
     */
    public static JJWTParser getJwtParser() {
        return new JJWTParser(jwtSecretKey(), expirationMillis);
    }

    private static SecretKey jwtSecretKey() {
        String secret = System.getenv("JWT_SECRET") != null ?
                System.getenv("JWT_SECRET") : "TaskFlowSecret2024";
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private static Long expirationMillis() {
        String expEnv = System.getenv("JWT_EXPIRATION");
        if (expEnv != null && !expEnv.isEmpty()) {
            try {
                return Long.parseLong(expEnv);
            } catch (NumberFormatException e) {
                log.warn("Invalid JWT_EXPIRATION env var, using default: {}", expEnv);
            }
        }
        String expProp = System.getProperty("jwt.expiration");
        if (expProp != null && !expProp.isEmpty()) {
            try {
                return Long.parseLong(expProp);
            } catch (NumberFormatException e) {
                log.warn("Invalid jwt.expiration property, using default: {}", expProp);
            }
        }
        // Default 1 hour
        return 3600L * 1000;
    }

    /**
     * JWT Parser class for parsing and validating tokens.
     */
    private static class JJWTParser {
        private final SecretKey key;
        private final Long expirationMillis;

        public JJWTParser(SecretKey key, Long expirationMillis) {
            this.key = key;
            this.expirationMillis = expirationMillis != null ? expirationMillis : expirationMillis();
        }

        /**
         * Parse and validate a JWT token string.
         *
         * @param token the JWT token to parse
         * @return JwtClaims with validated claims, or null if parsing/validation fails
         */
        public JJWTParser.JwtClaims parse(String token) {
            try {
                // Decode payload without signature validation first (for error messages)
                DecodedJwt decodedJwt = decodeWithoutValidation(token);
                if (decodedJwt == null) {
                    return null;
                }

                // Validate signature and expiration
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String expStr = claims.getExpiration().toString();
                long expTime = parseDate(expStr);
                if (expTime < expirationMillis) {
                    // Token expired within our allowed window
                    log.debug("Token {} still valid but near expiration ({}ms vs limit {}ms)",
                            decodedJwt.getHeader().getSubject(), expTime, expirationMillis);
                }

                return new JwtClaims(decodedJwt);
            } catch (ExpiredJwtException e) {
                String msg = e.getClaims().getExpiration().toString();
                log.debug("Token {} expired: {}", decodedJwt.getHeader().getSubject(), msg);
                return null;
            } catch (SignatureException e) {
                log.debug("Invalid JWT signature: {}", token, e);
                return null;
            }
        }

        private DecodedJwt decodeWithoutValidation(String token) {
            try {
                JwtDecoders.fromIssuerLocation("")
                        .decode(token,
                                decoder -> decoder.withSigningKeySignature(false)
                                        .withPermittedSubjects("*")
                                        .setRequireExpirationTime(true)
                                        .setAcceptableSkew(Duration.ofMinutes(15)));
            } catch (JwtException e) {
                log.debug("Failed to decode JWT: {}", token, e);
                return null;
            }
        }

        private long parseDate(String dateStr) {
            try {
                return Long.parseLong(dateStr.replace('T', ' ').substring(0, 19));
            } catch (NumberFormatException ex) {
                log.warn("Failed to parse expiration date: {}", dateStr, ex);
                return -1L;
            }
        }

        public static class JwtClaims {
            private final String subject;
            private final Date issuedAt;
            private final Date expiration;
            private final String jti;
            private final Long id;

            public JwtClaims(DecodedJwt jwt) {
                this.subject = jwt.getPayload().getSubject();
                this.issuedAt = new java.util.Date(jwt.getPayload().getIssuedAt());
                this.expiration = jwt.getPayload().getExpiration();
                this.jti = jwt.getPayload().getId();
                this.id = Long.parseLong(subject.replace('-', '0'));
            }

            public String getSubject() { return subject; }
            public Date getIssuedAt() { return issuedAt; }
            public Date getExpiration() { return expiration; }
            public String getJti() { return jti; }
            public Long getId() { return id; }
        }
    }
}
