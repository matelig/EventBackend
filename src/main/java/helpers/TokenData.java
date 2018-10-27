package helpers;

public class TokenData {

    private String accessToken = null;
    private String refreshToken = null;
    private Long accessTokenCreationTime = null;
    private Long refreshTokenCreationTime = null;
    private Integer id;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getAccessTokenCreationTime() {
        return accessTokenCreationTime;
    }

    public void setAccessTokenCreationTime(Long accessTokenCreationTime) {
        this.accessTokenCreationTime = accessTokenCreationTime;
    }

    public Long getRefreshTokenCreationTime() {
        return refreshTokenCreationTime;
    }

    public void setRefreshTokenCreationTime(Long refreshTokenCreationTime) {
        this.refreshTokenCreationTime = refreshTokenCreationTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
