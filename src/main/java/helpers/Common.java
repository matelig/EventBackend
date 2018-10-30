package helpers;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class Common {
    public static Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    public static Long accessTokenExpirationTime = 600000L; //10 minutes
    public static Long refreshTokenExpirationTime = 604800000L; //one week
    public static String CLIENT_ID = "taipaiOauth2Test";
    public static String CLIENT_SECRET = "taipaiOauth2ClientSectent";
    public static String AUTHORIZATION_CODE = "taipaiOauth2authcode";
    public static String USERNAME = "user";
    public static String PASSWORD = "pass";
    public static String RESOURCE_SERVER_NAME = "resource";
    public static final String ACCESS_TOKEN_VALID = "access_token_valid";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String AUTHORIZATION_HEADER_OAUTH2 = "Bearer " + ACCESS_TOKEN_VALID;
}
