package oauth2;

import helpers.TokenData;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class TokenStorageDatabase {
    private Set<TokenData> authCodes = new HashSet<TokenData>();
    private Set<TokenData> tokens = new HashSet<TokenData>();
    public void addAuthCode(TokenData authCode) {
        authCodes.add(authCode);
    }
    public boolean isValidAuthCode(TokenData authCode) {
        return authCodes.contains(authCode);
    }
    public void addToken(TokenData token) {
        tokens.add(token);
    }
    public boolean isValidToken(String token) {
        return tokens.contains(token);
    }

    public Integer getId() {
        return tokens.size() + 1;
    }
}