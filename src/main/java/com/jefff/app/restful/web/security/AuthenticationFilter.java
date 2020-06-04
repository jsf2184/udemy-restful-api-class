package com.jefff.app.restful.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jefff.app.restful.web.SpringApplicationContext;
import com.jefff.app.restful.web.service.UserService;
import com.jefff.app.restful.web.shared.dto.UserDto;
import com.jefff.app.restful.web.ui.controller.UserController;
import com.jefff.app.restful.web.ui.model.request.UserLoginRequestModel;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import io.jsonwebtoken.Jwts;

// Note authentication is about determining who someone is whereas authorization is about whether someone
// has rights to do something. The AuthenticationFilter's attemptAuthentication() method is invoked
// during the processing of the user's login.
//
public class AuthenticationFilter  extends UsernamePasswordAuthenticationFilter {
    public static Logger _log = LoggerFactory.getLogger(AuthenticationFilter.class) ;


    private final AuthenticationManager _authenticationManager;

    // The AuthenticationManager injected here is one that our WebSecurity class helped
    // construct. It knows how we are doing password encryption.
    //
    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        _authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException
    {
        try {

            // Pull the username and password out of the json in the HttpServletRequest into our UserLoginRequestModel
            // That json looks like this...
            //
            UserLoginRequestModel creds = new ObjectMapper()
                    .readValue(request.getInputStream(), UserLoginRequestModel.class);

            // Once we have the UserLoginRequestModel (i.e. creds) we encapsulate them into an authenticationToken
            String userEmail = creds.getEmail();
            String userPassword = creds.getPassword();
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userEmail,
                    userPassword,
                    new ArrayList<>());


            // The authenticationManager will run this user password thru encryption to
            // see if it matches the encrypted password in the db.
            //
            _log.info("AuthenticationFilter.attemptAuthentication() about to call _authenticationManager.authenticate() w/ {}/{}",
                      userEmail, userPassword);

            Authentication authentication = _authenticationManager.authenticate(authenticationToken);
            _log.info("AuthenticationFilter.attemptAuthentication() back from call to _authenticationManager.authenticate()");
            return authentication;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // This method gets called on a successful authentication.

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException
    {
        User user = (User) authentication.getPrincipal();
        String userName = user.getUsername();
        String password = user.getPassword();

        _log.info("AuthenticationFilter.successfulAuthentication(): email: {} w/ password: {}",
                                userName, password);

        // User our TokenSecret as a key to encrypt the user's email address into a token that we are going to embed
        // into the Http Header with the header key: 'Authorization'
        //
        String encodedUserName = Jwts.builder()
                .setSubject(userName)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret() )
                .compact();
        // Set the authorization header to the encoded email address
        response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + encodedUserName);

        // retrieve the guid like user id (from the db) associated with this eres id
        UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl");
        UserDto userDto = userService.getUser(userName);

        // Add the userid to the header also. This is the semi-random-guid-like id that is stored in db rec for each user
        String userId = userDto.getUserId();
        response.addHeader("UserId", userId);
        _log.info("AuthenticationFilter.successfulAuthentication(): ready to return");

    }
}
