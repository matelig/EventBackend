package model;

import org.codehaus.jackson.annotate.JsonProperty;

public class SocialAuthRequest {

    @JsonProperty(value = "name")
    private String name;
    @JsonProperty(value = "email")
    private String email;
    @JsonProperty(value = "userId")
    private String userId;
    @JsonProperty(value = "client_secret")
    private String clientSecret;
    @JsonProperty(value = "client_id")
    private String clientId;

    public SocialAuthRequest(String name, String email, String userId, String clientSecret, String clientId) {
        this.name = name;
        this.email = email;
        this.userId = userId;
        this.clientSecret = clientSecret;
        this.clientId = clientId;
    }

    public SocialAuthRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
