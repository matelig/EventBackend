package controllers;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.DatabaseConnection;
import database.entity.User;
import model.ApiException;
import model.SignUpUserRequest;
import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

@Path("/register")
public class RegisterController {

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Gson gson = new Gson();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerAction(@Context HttpHeaders headers, String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            SignUpUserRequest tokenRequest = mapper.readValue(jsonString, SignUpUserRequest.class);

            if (!validate(tokenRequest.getEmail())) {
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(gson.toJson(new ApiException("Bad e-mail format: " + tokenRequest.getEmail()))).build();
            }

            MongoDatabase database = DatabaseConnection.shared.getDatabase();
            MongoCollection<User> users = database.getCollection("Users", User.class);

            User existingUser = users.find(or(eq("nickname", tokenRequest.getNickname()), eq("email", tokenRequest.getEmail()))).first();
            if (existingUser != null) {
                return Response.status(Response.Status.CONFLICT).entity(gson.toJson(new ApiException("User already exists"))).build();
            }
            User newUser = new User(tokenRequest.getEmail(), tokenRequest.getNickname(), tokenRequest.getPassword());
            newUser.setId(UUID.randomUUID().toString());
            users.insertOne(newUser);
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(gson.toJson(new ApiException("Bad registration request"))).build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

}
