package helpers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.DatabaseConnection;
import database.entity.User;
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

import static com.mongodb.client.model.Filters.eq;

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

    public Response.Status removeTokens(@Context HttpServletRequest request) {
        try {
            URL restURL = new URL(Config.authorizationUrl + "remove/accessToken");
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

    public User getUser(HttpServletRequest request) {
        String userEmail = KeyDecoder.shared.decode(request);
        MongoDatabase database = DatabaseConnection.shared.getDatabase();
        MongoCollection<User> users = database.getCollection("Users", User.class);
        return users.find(eq("email", userEmail)).first();
    }

}
