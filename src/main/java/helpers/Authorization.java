package helpers;

import org.glassfish.jersey.client.JerseyClientBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class Authorization {

    public static Authorization shared = new Authorization();

    private Authorization() { }

    /**
     * Authorization test - check if access token passed in
     * @param request - request which is tested
     * @return status code based on authorization status
     */
    public Response.Status isAuthenticated(@Context HttpServletRequest request) {
        try {
            URL restURL = new URL(Config.authorizationUrl + "validation/accessToken");
            Client client = JerseyClientBuilder.newClient();
            WebTarget target = client.target(restURL.toURI());
            Response entity = target.request(MediaType.TEXT_HTML)
                    .header("Authorization", request.getHeader("Authorization"))
                    .get(Response.class);
            return Response.Status.fromStatusCode(entity.getStatus());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return Response.Status.UNAUTHORIZED;
    }

}
