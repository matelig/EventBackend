package controllers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.DatabaseConnection;
import database.entity.Event;
import helpers.Parser;
import model.AddEventRequest;
import org.bson.Document;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;


@Path("/events")
public class EventsController {

    private String[] categories = new String[]{"Name1", "Name2"};

    @Path("/categories")
    @GET
    @Produces("application/json")
    public JsonArray getAll() {
        MongoDatabase database = DatabaseConnection.shared.getDatabase();
        MongoCollection<Document> collection = database.getCollection("Categories");
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
//        if (Authorization.shared.isAuthenticated(request).getStatusCode() != 200) {
//            return Response.status(Response.Status.UNAUTHORIZED).build();
//        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            AddEventRequest tokenRequest = mapper.readValue(jsonString, AddEventRequest.class);
            System.out.println("mfjjfjf");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Produces("application/json")
    public JsonArray getAllEvents() {
        MongoDatabase database = DatabaseConnection.shared.getDatabase();
        MongoCollection<Document> collection = database.getCollection("Events");
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (Document doc : collection.find()) {
            builder.add(Parser.shared.parse(doc));
        }
        return builder.build();
    }

    @GET
    @Path("/{eventId}")
    @Produces("application/json")
    public Response getEventById(@PathParam("eventId") String eventId) {
        MongoDatabase database = DatabaseConnection.shared.getDatabase();
        MongoCollection<Event> events = database.getCollection("Events", Event.class);
        Event event = events.find(eq("_id", eventId)).first();
        return Response.ok(new JSONObject(event)).build();
    }
}
