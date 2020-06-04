package com.jefff.app.restful.web.security;

import com.jefff.app.restful.web.service.UserService;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Use the @EnableWebSecurity annotation to get this class instantiated as our WebSecurityConfigurer
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    public static class FilterFactory {
        AuthenticationManager _authenticationManager;

        public FilterFactory(AuthenticationManager authenticationManager) {
            _authenticationManager = authenticationManager;
        }

        // Authentication - Deals with authenticating the user (i.e. login)
        AuthenticationFilter createAuthenticationFilter() throws Exception {
            // Note that the authenticationManager we retrieve here will be configured above to reference our UserService
            // and password encoder. This will allow it to help process a login.
            //
            AuthenticationFilter res = new AuthenticationFilter(_authenticationManager);
            // We could certainly have left the login url as '/login' but the instructor wanted to demonstrate
            // that the login can be changed.
            //
            res.setFilterProcessesUrl("/users/login");
            return res;
        }

        // Deals with authorization - what is an authenticated user authorized to do.
        AuthorizationFilter createAuthorizationFilter() {
            AuthorizationFilter res = new AuthorizationFilter(_authenticationManager);
            return res;
        }
    }
    private UserService _userService;
    private BCryptPasswordEncoder _bCryptPasswordEncoder;

    public WebSecurity(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        _userService = userService;
        _bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        // Set things up so that the resultant AuthenticationManager uses our _userService (which implements
        // UserDetailsService's loadUserByUserName() method) to provide userDetails in subsequent
        // authentication activity.
        builder.userDetailsService(_userService)
                .passwordEncoder(_bCryptPasswordEncoder);
    }


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // when configuring HttpSecurity, allow any POST requests to create a new user via POST
        // while any other requests must be authenticated. Makes sense because if it is a new
        // user, we won't have the users password yet.
        //

        // Construct an AuthenticationFilter which wraps the Spring AuthenticationManager which is
        // configured to our specifications in the other configure() method above.
        //

        FilterFactory filterFactory = new FilterFactory(authenticationManager());
        AuthenticationFilter authenticationFilter = filterFactory.createAuthenticationFilter();
        AuthorizationFilter authorizationFilter = filterFactory.createAuthorizationFilter();


         httpSecurity
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL)
                .permitAll()
                .anyRequest().authenticated().and()
                .addFilter(authenticationFilter)
                .addFilter(authorizationFilter);

        // Note that if we want session management to be stateless, we make these 2 additional calls.
        httpSecurity
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }




}
