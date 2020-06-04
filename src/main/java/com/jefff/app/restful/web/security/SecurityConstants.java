package com.jefff.app.restful.web.security;

import com.jefff.app.restful.web.SpringApplicationContext;
import com.jefff.app.restful.web.shared.utility.AppProperties;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 864000000; // 10 days in ms

    public static final long MINI_EXPIRATION_TIME = 10; // 1 second in ms

    // this is the name of the header field we insert to convey authorization info
    public static final String HEADER_STRING = "Authorization"; // lives in http header for post request

    // And we prefix the value of that authorization field with this string.
    public static final String TOKEN_PREFIX = "Bearer "; // lives in http header for post request

    public static final String SIGN_UP_URL = "/users";
//    public static final String TOKEN_SECRET = "jf9i4jgu83nfl0";
    public static final long PASSWORD_RESET_EXPIRATION_TIME = 3600000; // 1 hour
    public static final String VERIFICATION_EMAIL_URL = "/users/email-verification";
    public static final String PASSWORD_RESET_REQUEST_URL = "/users/password-reset-request";
    public static final String PASSWORD_RESET_URL = "/users/password-reset";

    public static String getTokenSecret()
    {
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("appProperties");
        String res = appProperties.getTokenSecret();
        return res;
    }
}
