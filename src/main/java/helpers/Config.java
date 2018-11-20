package helpers;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class Config {

    public static String authorizationUrl = "http://80.211.62.201:8080/AuthorizationServer/";
    public static String databaseName = "eventDatabase";
    public static String databaseUserName = "admin1";
    public static String databaseAdminPassword = "ch0w4jmyN4523d4n3";
    public static String databaseUrl = "80.211.62.201";
    public static String databaseAdminName = "admin";
    public static Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    public static String geocodingApiKey = "9ae4702d5beb4db48ebaf98a3d849341";
}
