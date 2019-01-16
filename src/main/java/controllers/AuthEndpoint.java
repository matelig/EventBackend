package controllers;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.DatabaseConnection;
import database.entity.User;
import helpers.Config;
import model.ApiException;
import model.SocialAuthRequest;
import model.TokenRequest;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.codehaus.jackson.map.ObjectMapper;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.UUID;

import static com.mongodb.client.model.Filters.*;

@Path("/authorization")
public class AuthEndpoint {
    private static final Gson gson = new Gson();
    MongoDatabase database = DatabaseConnection.shared.getDatabase();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response tokenRequest(String tokenRequestString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TokenRequest tokenRequest = mapper.readValue(tokenRequestString, TokenRequest.class);
        if (tokenRequest.getGrantType().equals(GrantType.PASSWORD.toString())) {
            return returnAccessTokenResponse(tokenRequest);
        } else if (tokenRequest.getGrantType().equals(GrantType.REFRESH_TOKEN.toString())) {
            return returnRefreshTokenResponse(tokenRequest);
        } else {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(gson.toJson(new ApiException("Wrong grant type"))).build();
        }
    }

    @POST
    @Path("/social")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response socialToken(String socialRequestString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SocialAuthRequest socialAuthRequest = mapper.readValue(socialRequestString, SocialAuthRequest.class);

        MongoCollection<User> users = database.getCollection("Users", User.class);
        User existingUser = users.find(eq("email", socialAuthRequest.getEmail())).first();
        if (existingUser == null) {
            User newUser = new User(socialAuthRequest.getEmail(), socialAuthRequest.getName(), "", socialAuthRequest.getClientId());
            newUser.setId(UUID.randomUUID().toString());
            users.insertOne(newUser);
        }

        existingUser = users.find(eq("email", socialAuthRequest.getEmail())).first();

        if (existingUser == null || existingUser.getUserId() == null || existingUser.getUserId().equals("")) {
            return Response.status(Response.Status.CONFLICT).entity(gson.toJson(new ApiException("Email registered to other user"))).build();
        }

        OAuthClientRequest request = null;
        try {
            request = OAuthClientRequest.tokenLocation(
                    Config.authorizationUrl + "accessToken")
                    .setGrantType(GrantType.PASSWORD)
                    .setClientId(socialAuthRequest.getClientId())
                    .setClientSecret(socialAuthRequest.getClientSecret())
                    .setUsername(existingUser.getEmail())
                    .setPassword(existingUser.getUserId()).buildBodyMessage();
        } catch (OAuthSystemException e) {
            e.printStackTrace();
        }
        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        OAuthAccessTokenResponse oauthResponse = null;
        try {
            oauthResponse = oAuthClient.accessToken(request);
        } catch (OAuthSystemException e) {
            e.printStackTrace();
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        }
        System.out.println(oauthResponse.getAccessToken());
        System.out.println(oauthResponse.getExpiresIn());
        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("access_token", oauthResponse.getAccessToken());
        json.add("refresh_token", oauthResponse.getRefreshToken());
        json.add("expires_in", oauthResponse.getExpiresIn());
        json.add("token_type", oauthResponse.getTokenType());
        json.add("user_id", existingUser.getId());
        json.add("user_nickname", existingUser.getNickname());
        return Response.ok(json.build(), MediaType.APPLICATION_JSON).build();

    }

    private Response returnAccessTokenResponse(TokenRequest tokenRequest) {
        try {

            MongoCollection<User> users = database.getCollection("Users", User.class);

            User existingUser = users.find(and(eq("email", tokenRequest.getEmail()), eq("password", tokenRequest.getPassword()))).first();
            if (existingUser == null) {
                return Response.status(Response.Status.NOT_FOUND).entity(gson.toJson(new ApiException("User does not exists"))).build();
            }

            OAuthClientRequest request = OAuthClientRequest.tokenLocation(
                    Config.authorizationUrl + "accessToken")
                    .setGrantType(GrantType.PASSWORD)
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
            json.add("user_id", existingUser.getId());
            json.add("user_nickname", existingUser.getNickname());
            return Response.ok(json.build(), MediaType.APPLICATION_JSON).build();
        } catch (OAuthSystemException var4) {
            var4.printStackTrace();
            return null;
        } catch (OAuthProblemException var5) {
            var5.printStackTrace();
            return null;
        }
    }

    private Response returnRefreshTokenResponse(TokenRequest tokenRequest) {
        try {
            OAuthClientRequest request = OAuthClientRequest.tokenLocation(
                    Config.authorizationUrl + "refreshToken")
                    .setGrantType(GrantType.REFRESH_TOKEN)
                    .setClientId(tokenRequest.getClientId())
                    .setClientSecret(tokenRequest.getClientSecret())
                    .setRefreshToken(tokenRequest.getRefreshToken())
                    .buildBodyMessage();
            OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
            OAuthAccessTokenResponse oauthResponse = oAuthClient.accessToken(request);
            System.out.println(oauthResponse.getAccessToken());
            System.out.println(oauthResponse.getExpiresIn());
            JsonObjectBuilder json = Json.createObjectBuilder();
            json.add("access_token", oauthResponse.getAccessToken());
            json.add("refresh_token", oauthResponse.getRefreshToken());
            json.add("expires_in", oauthResponse.getExpiresIn());
            json.add("token_type", oauthResponse.getTokenType());
            return Response.ok(json.build(), MediaType.APPLICATION_JSON).build();
        } catch (OAuthSystemException var4) {
            var4.printStackTrace();
            return null;
        } catch (OAuthProblemException var5) {
            var5.printStackTrace();
            return null;
        }
    }

}
