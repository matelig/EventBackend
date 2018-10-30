package controllers;

import helpers.Common;
import model.TokenRequest;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/test")
public class AuthEndpoint {

    private String url = "http://localhost:8080/EventBackend/";

    @Path("/test2")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public JsonObject tokenRequest(String tokenRequestString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TokenRequest tokenRequest = mapper.readValue(tokenRequestString, TokenRequest.class);
        try {
            OAuthClientRequest request = OAuthClientRequest.tokenLocation(
                    this.url + "api/token")
                    .setGrantType(GrantType.valueOf(tokenRequest.getGrantType().toUpperCase()))
                    .setClientId(tokenRequest.getClientId())
                    .setClientSecret(tokenRequest.getClientSecret())
                    .setUsername(tokenRequest.getEmail())
                    .setPassword(tokenRequest.getPassword()).buildBodyMessage();
            OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
            OAuthAccessTokenResponse oauthResponse = oAuthClient.accessToken(request);
            System.out.println(oauthResponse.getAccessToken());
            System.out.println(oauthResponse.getExpiresIn());
            JsonObjectBuilder json = Json.createObjectBuilder();
            json.add("access_token", oauthResponse.getAccessToken());
            json.add("refresh_token", oauthResponse.getRefreshToken());
            json.add("expires_in", oauthResponse.getExpiresIn());
            json.add("token_type", oauthResponse.getTokenType());
            return json.build();
        } catch (OAuthSystemException var4) {
            var4.printStackTrace();
            return null;
        } catch (OAuthProblemException var5) {
            var5.printStackTrace();
            return null;
        }
    }

}
