package controllers;

import helpers.Common;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.glassfish.jersey.client.JerseyClientBuilder;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

@Path("/test")
public class AuthEndpoint {

    private String url = "http://localhost:8080/EventBackend/";
    private Client client = JerseyClientBuilder.newClient();

    @Path("/test")
    @GET
    public void directTokenRequest() {
        OAuthClientRequest request = null;
        try {
            Response response = makeRequest();
            String authCode = getAuthCode(response);
            OAuthAccessTokenResponse oauthResponse = makeTokenRequestWithAuthCode(authCode);
            System.out.println(oauthResponse.getAccessToken());
            System.out.println(oauthResponse.getExpiresIn());
            System.out.println(oauthResponse.getRefreshToken());
            System.out.println(oauthResponse.getTokenType());

        } catch (OAuthSystemException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        }

    }

    private Response makeRequest() throws OAuthSystemException, URISyntaxException {
        OAuthClientRequest request = OAuthClientRequest
                .authorizationLocation(url.toString() + "api/auth")
                .setClientId(Common.CLIENT_ID)
                .setRedirectURI(url.toString() + "api/redirect")
                .setResponseType(ResponseType.CODE.toString())
                .setState("state")
                .buildQueryMessage();
        WebTarget target = client.target(new URI(request.getLocationUri()));
        Response response = target.request(MediaType.TEXT_HTML).get();
        return response;
    }

    private String getAuthCode(Response response) throws JSONException {
        JSONObject obj = new JSONObject(response.readEntity(String.class));
        JSONObject qp = obj.getJSONObject("queryParameters");
        String authCode = null;
        if (qp != null) {
            authCode = qp.getString("code");
        }

        return authCode;
    }

    private OAuthAccessTokenResponse makeTokenRequestWithAuthCode(String authCode) throws OAuthProblemException, OAuthSystemException {
        OAuthClientRequest request = OAuthClientRequest
                .tokenLocation(url + "api/token")
                .setClientId(Common.CLIENT_ID)
                .setClientSecret(Common.CLIENT_SECRET)
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setCode(authCode)
                .setRedirectURI(url + "api/redirect")
                .buildBodyMessage();
        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        OAuthAccessTokenResponse oauthResponse = oAuthClient.accessToken(request);
        return oauthResponse;
    }

}
