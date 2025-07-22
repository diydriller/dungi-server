package com.dungi.apiserver.web;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.dungi.common.util.NumberUtil.ACCESS_TOKEN_DURATION;
import static com.dungi.common.util.NumberUtil.REFRESH_TOKEN_DURATION;

@Component
public class TokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public void verifyToken(String jwtToken) {
        JWT.require(Algorithm.HMAC512(jwtSecret)).build().verify(jwtToken);
    }

    public String createAccessToken(String email) {
        return JWT.create()
                .withClaim("email", email)
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_DURATION))
                .sign(Algorithm.HMAC512(jwtSecret));
    }

    public String createRefreshToken() {
        return JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_DURATION))
                .sign(Algorithm.HMAC512(jwtSecret));
    }

    public long getExpirationDuration(String jwtToken) {
        long duration = JWT.decode(jwtToken).getExpiresAt().getTime() - System.currentTimeMillis();
        return duration < 0 ? 0 : duration;
    }
}
