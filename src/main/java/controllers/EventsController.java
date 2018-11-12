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
import java.util.Date;

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
            event.setOwnerId(existingUser.getId());
            MongoCollection<Event> events = database.getCollection("Events", Event.class);
            events.insertOne(event);
            return Response.status(Response.Status.CREATED).build();
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    private Event createEventObject(AddEventRequest addEventRequest) throws NumberFormatException {
        Event newEvent = new Event();
        newEvent.setLatitude(Double.parseDouble(addEventRequest.getLatitude()));
        newEvent.setLongitude(Double.parseDouble(addEventRequest.getLongitude()));
        newEvent.setCost(Double.parseDouble(addEventRequest.getCost()));
        newEvent.setDescription(addEventRequest.getDescription());
        newEvent.setExternalUrl(addEventRequest.getExternalUrl());
        newEvent.setMaxParticipants(Integer.parseInt(addEventRequest.getMaxParticipants()));
        newEvent.setOnlyRegistered(Boolean.getBoolean(addEventRequest.isOnlyRegistered()));
        newEvent.setTitle(addEventRequest.getName());

        Long startDate = Long.parseLong(addEventRequest.getStartDate());
        newEvent.setStartDate(new Date(startDate));

        Long endDate = Long.parseLong(addEventRequest.getEndDate());
        newEvent.setEndDate(new Date(endDate));
        return newEvent;
    }


}
