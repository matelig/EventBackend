package model;

import org.codehaus.jackson.annotate.JsonProperty;

public class SignUpUserRequest {
    @JsonProperty(value = "client_id")
    private String clientId;
    @JsonProperty(value = "client_secret")
    private String clientSecret;
    @JsonProperty(value = "email")
    private String email;
    @JsonProperty(value = "nickname")
    private String nickname;
    @JsonProperty(value = "password")
    private String password;

    public SignUpUserRequest(String clientId, String clientSecret, String email, String nickname, String password) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }

    public SignUpUserRequest(){ }

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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
