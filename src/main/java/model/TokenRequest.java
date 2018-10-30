package model;


import org.codehaus.jackson.annotate.JsonProperty;

public class TokenRequest {
    @JsonProperty(value = "client_id")
    private String clientId;
    @JsonProperty(value = "client_secret")
    private String clientSecret;
    @JsonProperty(value = "email")
    private String email;
    @JsonProperty(value = "password")
    private String password;
    @JsonProperty(value = "grant_type")
    private String grantType;
    @JsonProperty(value = "refresh_token")
    private String refreshToken;

    public TokenRequest(String clientId, String clientSecret, String email, String password, String grantType, String refreshToken) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.email = email;
        this.password = password;
        this.grantType = grantType;
        this.refreshToken = refreshToken;
    }

    public TokenRequest() {

    }
    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public String getClientSecret() {
        return clientSecret;
    }
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getGrantType() {
        return grantType;
    }
    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
