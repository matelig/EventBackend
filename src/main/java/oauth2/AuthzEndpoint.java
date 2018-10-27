package oauth2;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.*;
import java.net.URI;
import java.net.URISyntaxException;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import helpers.TokenData;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;

@Path("auth")
public class AuthzEndpoint {

    @Inject
    public TokenStorageDatabase tokenStorageDatabase;

    @GET
    public Response authorize(@Context HttpServletRequest request)
            throws URISyntaxException, OAuthSystemException {
        try {
            OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
            OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());

            //build response according to response_type
            String responseType = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);

            OAuthASResponse.OAuthAuthorizationResponseBuilder builder =
                    OAuthASResponse.authorizationResponse(request, HttpServletResponse.SC_FOUND);

            if (responseType.equals(ResponseType.CODE.toString())) {
                final String authorizationCode = oauthIssuerImpl.authorizationCode();
                //tokenStorageDatabase.addAuthCode(authorizationCode);
                builder.setCode(authorizationCode);
            }
            if (responseType.equals(ResponseType.TOKEN.toString())) {
                TokenData tokenData = new TokenData();
                Long currentDateTime = System.currentTimeMillis();
                tokenData.setAccessToken(oauthIssuerImpl.accessToken());
                tokenData.setRefreshToken(oauthIssuerImpl.refreshToken());
                tokenData.setAccessTokenCreationTime(currentDateTime);
                tokenData.setRefreshTokenCreationTime(currentDateTime);
                tokenData.setId(tokenStorageDatabase.getId());
                tokenStorageDatabase.addToken(tokenData);

                builder.setAccessToken(tokenData.getAccessToken());
                builder.setParam("refresh_token", tokenData.getRefreshToken());
                builder.setParam("token_type", "Bearer");
                builder.setExpiresIn(3600l);
            }
            String redirectURI = oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);
            final OAuthResponse response = builder.location(redirectURI).buildQueryMessage();
            URI url = new URI(response.getLocationUri());
            return Response.status(response.getResponseStatus()).location(url).build();
        } catch (OAuthProblemException e) {
            final Response.ResponseBuilder responseBuilder = Response.status(HttpServletResponse.SC_FOUND);
            String redirectUri = e.getRedirectUri();

            if (OAuthUtils.isEmpty(redirectUri)) {
                throw new WebApplicationException(
                        responseBuilder.entity("OAuth callback url needs to be provided by client!!!").build());
            }
            final OAuthResponse response =
                    OAuthASResponse.errorResponse(HttpServletResponse.SC_FOUND)
                            .error(e).location(redirectUri).buildQueryMessage();
            final URI location = new URI(response.getLocationUri());
            return responseBuilder.location(location).build();
        }
    }
}
