package org.jacpower.ruleEngine.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.enterprise.context.ApplicationScoped;
import org.jacpower.records.Authentication;
import org.jacpower.records.Token;
import org.jacpower.utility.Util;

import java.util.Base64;
import java.util.Date;
@ApplicationScoped
public class JwtService {

    public String generateAccessToken(Authentication authentication){
        long milliseconds=(new Date().getTime()) + (3600 * 1000);
        Date expireDate=new Date(milliseconds);

        return JWT.create()
                .withClaim("createdAt", Util.getTimestamp())
                .withClaim("username", authentication.username())
                .withIssuer("klaus")
                .withClaim("expiresIn", 3600)
                .withExpiresAt(expireDate)
                .sign(Algorithm.HMAC512("ninja"));
    }
    public Token decodeAccessToken(String accessToken){
        byte [] decodedBytes= Base64.getDecoder().decode(accessToken.getBytes());
        accessToken=new String(decodedBytes);

        JWTVerifier verifier=JWT.require(Algorithm.HMAC512("ninja"))
                .withIssuer("klaus")
                .acceptExpiresAt(3600)
                .build();
        DecodedJWT decodedJWT= verifier.verify(accessToken);
        String username=decodedJWT.getClaims().get("username").asString();
        return new Token(true, username);
    }
}
