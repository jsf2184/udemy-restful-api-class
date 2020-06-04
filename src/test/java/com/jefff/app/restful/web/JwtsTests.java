package com.jefff.app.restful.web;

import com.jefff.app.restful.web.security.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class JwtsTests {
    public static final String tokenSecret = "jf9i4jgu83nfl0";

    @Test
    public void encodeAndDecodeWithShortExpiration() {
        validateEncodeAndDecode("fred", SecurityConstants.MINI_EXPIRATION_TIME, true);
    }

    @Test
    public void encodeAndDecodeWithLongExpiration() {
        validateEncodeAndDecode("fred", SecurityConstants.EXPIRATION_TIME, false);
    }

    @Test
    public void encodeAndDecodeUserIdWithLongExpiration() {
        validateEncodeAndDecode("1MEqUT3b6OtUuCJiqJfYTbgzwbxXSF", SecurityConstants.EXPIRATION_TIME, false);
    }

    public void validateEncodeAndDecode(String inputStr, long expirationMs, boolean expectException) {

        long currentTimeMillis = System.currentTimeMillis();
        String encodedUserName = encode(inputStr, currentTimeMillis, expirationMs);

        System.out.printf("inputStr: '%s' encoded into: '%s'\n", inputStr, encodedUserName);

        boolean caught = false;
        String decodedValue = null;
        try {
            decodedValue = decode(encodedUserName);
        } catch (Exception e) {
            caught = true;
        }
        Assert.assertEquals(expectException, caught);
        if (!caught) {
            System.out.printf("decodedValue: '%s'\n", decodedValue);
            Assert.assertEquals(inputStr, decodedValue);
        }
    }

    @Test
    public void decodeNonsense() {

        boolean caught = false;
        try {
            String encodedUserName = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmcmVkIiwiZXhwIjoxNTUuHyQsms.eFXQMD7P_SdHkhQ2J4xsn5A";
            String decodedValue = decode(encodedUserName);
            System.out.printf("decodedValue: '%s'\n", decodedValue);
        } catch (Exception e) {
            caught = true;
        }
        Assert.assertTrue(caught);
    }



    public String encode(String userName, long currentTimeMillis, long expirationMs) {
        String res = Jwts.builder()
                .setSubject(userName)
                .setExpiration(new Date(currentTimeMillis + expirationMs))
                .signWith(SignatureAlgorithm.HS512, tokenSecret )
                .compact();
        return res;
    }

    public String decode(String encodedName) {
        String res = Jwts.parser()
                .setSigningKey(tokenSecret)
                .parseClaimsJws( encodedName )
                .getBody()
                .getSubject();
        return res;
    }
}
