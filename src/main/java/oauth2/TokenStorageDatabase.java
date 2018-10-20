package oauth2;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class TokenStorageDatabase {
    private Set<String> authCodes = new HashSet<String>();
    private Set<String> tokens = new HashSet<String>();
    public void addAuthCode(String authCode) {
        authCodes.add(authCode);
    }
    public boolean isValidAuthCode(String authCode) {
        return authCodes.contains(authCode);
    }
    public void addToken(String token) {
        tokens.add(token);
    }
    public boolean isValidToken(String token) {
        return tokens.contains(token);
    }
}