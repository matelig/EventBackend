package helpers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.enterprise.context.ApplicationScoped;
import java.security.Key;
import java.util.Date;

@ApplicationScoped
public class KeysCoder {

    public static KeysCoder shared = new KeysCoder();

    private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private KeysCoder() {}

    public String generateAccessToken(String username) {
        Date expirationDate = new Date(System.currentTimeMillis()+Common.accessTokenExpirationTime);
        //should also contain clientSecret ?
        return  Jwts.builder().setExpiration(expirationDate).setSubject(username).signWith(key).compact();
    }

    public String generateRefreshToken() {
        Date expirationDate = new Date(System.currentTimeMillis()+Common.refreshTokenExpirationTime);
        return  Jwts.builder().setExpiration(expirationDate).signWith(key).compact();
    }

    public String decodeAccessToken(String jwtToken) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken).getBody().getSubject(); // should return user email
    }

}
