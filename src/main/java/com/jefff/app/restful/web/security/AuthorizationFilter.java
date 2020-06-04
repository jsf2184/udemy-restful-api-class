package com.jefff.app.restful.web.security;

import com.jefff.app.restful.web.ui.controller.UserController;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class AuthorizationFilter extends BasicAuthenticationFilter {
    public static Logger _log = LoggerFactory.getLogger(AuthorizationFilter.class) ;

    public AuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {

        _log.info(String.format("AuthorizationFilter.doFilterInternal(): called"));

        String authorizationString = req.getHeader(SecurityConstants.HEADER_STRING);

        if (authorizationString == null || !authorizationString.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(authorizationString);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authenticationToken);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String authorizationString) {
        _log.info(String.format("AuthorizationFilter.getAuthentication(): %s", authorizationString));

        if (authorizationString != null) {

            // That authorization string begins with the string 'Bearer '. Remove that from the
            // authozization string.
            //
            authorizationString = authorizationString.replace(SecurityConstants.TOKEN_PREFIX, "");

            // use the same  TOKEN_SECRET that was used to encrypt the user's email
            // address to decrypt it so that we get back the user's email address.
            //
            String tokenSecret = SecurityConstants.getTokenSecret();
            String user;
            try {
                user = Jwts.parser()
                        .setSigningKey(tokenSecret)
                        .parseClaimsJws(authorizationString)
                        .getBody()
                        .getSubject();
            } catch (MalformedJwtException e) {
                _log.warn("AuthorizationFilter.getAuthentication(): caught MalformedJwtException");
                return null;
            }
            _log.info(String.format("AuthorizationFilter.getAuthentication(): called, user = %s", user));
            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user,
                                                               null,
                                                               new ArrayList<>());
            }

            return null;
        }

        return null;
    }

}
