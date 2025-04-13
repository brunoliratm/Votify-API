package com.votify.infra.security;

import com.votify.models.UserModel;
import com.votify.services.TokenService;
import com.votify.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserService userService;

    public SecurityFilter(@Lazy TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        if (token != null) {
            try {
                var login = tokenService.validateToken(token);
                UserModel userModelOptional = userService.loadUserByLogin(login);

                var authentication = new UsernamePasswordAuthenticationToken(userModelOptional, null, userModelOptional.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
    private String recoverToken(HttpServletRequest request) {
        var header = request.getHeader("Authorization");
        if (header == null ) return null;
        return header.replace("Bearer ", "");
    }

}
