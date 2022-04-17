package com.ead.notification.configs.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class AuthenticationJwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider jwtProvider;

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String jwtStr = getTokenHeader(request);

            if (jwtStr != null && jwtProvider.validateJwt(jwtStr)) {

                String userID = jwtProvider.getSubjectJwt(jwtStr);
                String rolesStr = jwtProvider.getClaimNameJwt(jwtStr, "roles");

                UserDetails userDetails = UserDetailsImpl.build(UUID.fromString(userID), rolesStr);

                final var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

            }

        } catch (Exception e) {
            logger.error("Something went wrong: " + e.getMessage());
        }

        filterChain.doFilter(request, response);

    }

    private String getTokenHeader(HttpServletRequest request) {

        String headerAuth = request.getHeader("Authorization");

        if (headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
