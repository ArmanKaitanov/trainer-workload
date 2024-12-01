package com.epam.trainer_workload.util;

import com.epam.trainer_workload.exception.AuthenticationException;
import com.epam.trainer_workload.exception.ParseTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${token.secret.key}")
    private String secretKey;

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Claims parseClaims(String token) {
        logger.debug("Trying to validate token");
        try {
            Claims claims = getClaims(token);
            logger.debug("Token successfully validated");

            return claims;
        } catch (ExpiredJwtException e) {
            logger.error("Token is expired");
            throw new AuthenticationException("Token is expired");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported token");
            throw new AuthenticationException("Unsupported Jwt token");
        } catch (MalformedJwtException e) {
            logger.error("Invalid token format");
            throw new AuthenticationException("Invalid Jwt token");
        } catch (SignatureException e) {
            logger.error("Invalid token signature");
            throw new AuthenticationException("Invalid token signature");
        } catch (IllegalArgumentException e) {
            logger.error("Invalid token or token handler");
            throw new AuthenticationException("Invalid token or token handler");
        } catch (Exception e) {
            logger.error("Couldn't parse token", e);
            throw new ParseTokenException("Couldn't parse token");
        }
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
