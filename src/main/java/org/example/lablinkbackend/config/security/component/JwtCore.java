package org.example.lablinkbackend.config.security.component;

import org.example.lablinkbackend.config.security.auth.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import java.util.Date;

@Component
public class JwtCore {
    @Value("${lablink.app.secret}")
    private String secret;
    @Value("${lablink.app.expiration}")
    private int lifetime;

    public String generateToken(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder().setSubject((userDetails.getUsername())).setIssuedAt(new Date())
                .setExpiration(new Date((new Date().getTime() + lifetime)))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String getNameFromJwt(String jwt) {
        return Jwts.parser()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }
}
