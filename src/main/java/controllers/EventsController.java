package controllers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.DatabaseConnection;
import database.entity.Event;
import database.entity.User;
import helpers.Authorization;
import helpers.KeyDecoder;
import helpers.Parser;
import model.AddEventRequest;
import org.bson.Document;
import org.codehaus.jackson.map.ObjectMapper;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

@Path("/events")
public class EventsController {

    private String[] categories = new String[]{"Name1", "Name2"};

    @Path("/categories")
    @GET
    @Produces("application/json")
    public JsonArray getAll() {
        MongoDatabase database = DatabaseConnection.shared.getDatabase();
        MongoCollection<Document> collection =  database.getCollection("Categories");
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (Document doc : collection.find()) {
            builder.add(Parser.shared.parse(doc));
        }
        return builder.build();
    }

    @Path("/add")
    @POST
    @Produces("application/json")
    public Response addNewEvent(@Context HttpServletRequest request, String jsonString) {
        if (Authorization.shared.isAuthenticated(request).getStatusCode() != 200) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        String userEmail = KeyDecoder.shared.decode(request);
        MongoDatabase database = DatabaseConnection.shared.getDatabase();
        MongoCollection<User> users = database.getCollection("Users", User.class);
        User existingUser = users.find(eq("email", userEmail)).first();

        if (existingUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            AddEventRequest tokenRequest = mapper.readValue(jsonString, AddEventRequest.class);
            Event event = createEventObject(tokenRequest);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    private Event createEventObject(AddEventRequest addEventRequest) {
        Event newEvent = new Event();

        return newEvent;
    }


}
