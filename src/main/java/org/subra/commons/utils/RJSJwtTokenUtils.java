package org.subra.commons.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RJSJwtTokenUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RJSJwtTokenUtils.class);

    private static long jwtExpirationInMs;
    private static String jwtSecret;

    private RJSJwtTokenUtils() {
        throw new IllegalStateException(this.getClass().getSimpleName());
    }

    public static void configure(final long expirationInMs, final String secret) {
        jwtExpirationInMs = expirationInMs;
        jwtSecret = secret;
    }

    public static String generateTokenWithUserName(String userName, String accountId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uname", userName);
        claims.put("aid", accountId);

        return Jwts.builder().setSubject(userName).setIssuedAt(new Date(System.currentTimeMillis())).addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs)).setIssuer("SubraAuthService")
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

    public static Boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public static Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }

    /*
     * Return UserName i.e. Subject from Authentication Header value by trimming it
     * to JWT Token
     */
    public static String extractUserName(String token) {
        try {
            return extractAllClaims(token).getSubject();
        } catch (Exception e) {
            LOGGER.error("Cannot validate user token `{}`: error thrown - {}", token, e.getMessage());
        }
        return null;
    }

    public static String extractAccid(String token) {
        try {
            return (String) extractAllClaims(token).get("aid");
        } catch (Exception e) {
            LOGGER.error("Cannot validate user token `{}`: error thrown - {}", token, e.getMessage());
        }
        return null;
    }

}
