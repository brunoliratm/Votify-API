package com.votify.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.votify.dtos.AuthenticationDto;
import com.votify.exceptions.InvalidTokenException;
import com.votify.models.UserModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String SECRET_KEY;

    private final AuthenticationManager authenticationManager;

    public TokenService(@Lazy AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public String createToken(AuthenticationDto loginDTO) {

        try {
            var userPassword = new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password());
            var auth = this.authenticationManager.authenticate(userPassword);
            var token = generateToken((UserModel) auth.getPrincipal());
            return token.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error while logging in", e.getCause());
        }
    }
    private String generateToken(UserModel userModel) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            String[] roles = userModel.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toArray(String[]::new);
            String token = JWT.create()
                    .withIssuer("api-v1-auth")
                    .withClaim("id", userModel.getId())
                    .withClaim("email", userModel.getEmail())
                    .withArrayClaim("roles", roles)
                    .withIssuedAt(genIssuedAtDate())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating token", exception);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .withIssuer("api-v1-auth")
                    .build()
                    .verify(token);

            if (decodedJWT.getExpiresAt().before(new Date())) {
                throw new InvalidTokenException("Token expired");
            }

            return decodedJWT.getClaim("email").asString();
        } catch (JWTVerificationException exception) {
            throw new InvalidTokenException();
        }
    }

    private Instant genIssuedAtDate() {
        return LocalDateTime.now().toInstant(ZoneOffset.of("-03:00"));
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
