package controllers;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.DatabaseConnection;
import database.entity.Category;
import database.entity.Event;
import database.entity.User;
import helpers.Authorization;
import helpers.GeocodingHelper;
import helpers.KeyDecoder;
import helpers.Parser;
import model.AddEventRequest;
import model.ApiException;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

@Path("/events")
public class EventsController {
    private static Gson gson = new Gson();

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
            return Response.status(Response.Status.CREATED).entity(gson.toJson(event)).build();
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException(e.getMessage()))).build();
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("Wrong date format"))).build();
        }
    }

    private Event createEventObject(AddEventRequest addEventRequest) throws NumberFormatException {
        Event newEvent = new Event(addEventRequest.getName(), addEventRequest.getDescription(), Double.parseDouble(addEventRequest.getLatitude()),
                Double.parseDouble(addEventRequest.getLongitude()), Integer.parseInt(addEventRequest.getMaxParticipants()),
                Boolean.getBoolean(addEventRequest.isOnlyRegistered()), addEventRequest.getCategoryId(),
                Double.parseDouble(addEventRequest.getCost()), addEventRequest.getExternalUrl());

        Long startDate = Long.parseLong(addEventRequest.getStartDate());
        newEvent.setStartDate(new Date(startDate));

        Long endDate = Long.parseLong(addEventRequest.getEndDate());
        newEvent.setEndDate(new Date(endDate));
        newEvent.setAddress(GeocodingHelper.reverseGeocode(Double.parseDouble(addEventRequest.getLatitude()),
                Double.parseDouble(addEventRequest.getLongitude())));
        return newEvent;
    }

    @GET
    @Produces("application/json")
    public Response getAllEvents() {
        MongoDatabase database = DatabaseConnection.shared.getDatabase();
        MongoCollection<Event> events = database.getCollection("Events", Event.class);
        Date inputDate = new Date();
        FindIterable<Event> results = events.find(gte("startDate", inputDate));
        List<Event> resultEvents = new ArrayList<Event>();
        for (Event event : results) {
            resultEvents.add(event);
        }
        return Response.ok(gson.toJson(resultEvents)).build();
    }

    @GET
    @Path("/{eventId}")
    @Produces("application/json")
    public Response getEventById(@PathParam("eventId") String eventId) {
        ObjectId eventObjectId;
        try {
            eventObjectId = new ObjectId(eventId);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("Wrong eventId format"))).build();
        }
        MongoDatabase database = DatabaseConnection.shared.getDatabase();
        MongoCollection<Event> events = database.getCollection("Events", Event.class);
        Event event = events.find(eq("_id", eventObjectId)).first();
        if (event == null)
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("Event not found"))).build();
        return Response.ok(gson.toJson(event)).build();
    }

    @GET
    @Path("/users/{ownerId}")
    @Produces("application/json")
    public Response getEventsByOwnerId(@PathParam("ownerId") String ownerId) {
        MongoDatabase database = DatabaseConnection.shared.getDatabase();
        MongoCollection<User> users = database.getCollection("Users", User.class);
        User existingUser = users.find(eq("_id", ownerId)).first();
        if (existingUser == null)
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("User not found"))).build();
        MongoCollection<Event> eventsCollection = database.getCollection("Events", Event.class);
        List<Event> events = new ArrayList<Event>();
        Date inputDate = new Date();
        FindIterable<Event> results = eventsCollection.find(and(eq("ownerId", ownerId), gte("startDate", inputDate)));
        for (Event event : results)
            events.add(event);
        return Response.ok(gson.toJson(events)).build();
    }

    @GET
    @Path("/categories/{categoryId}")
    @Produces("application/json")
    public Response getEventsByCategoryId(@PathParam("categoryId") String categoryId) {
        int categoryIdInt;
        try {
            categoryIdInt = Integer.parseInt(categoryId);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("Wrong categoryId format"))).build();
        }
        MongoDatabase database = DatabaseConnection.shared.getDatabase();
        MongoCollection<Category> categories = database.getCollection("Categories", Category.class);
        Category category = categories.find(eq("_id", categoryIdInt)).first();
        if (category == null)
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("Category not found"))).build();
        MongoCollection<Event> eventsCollection = database.getCollection("Events", Event.class);
        List<Event> events = new ArrayList<Event>();
        Date inputDate = new Date();
        FindIterable<Event> results = eventsCollection.find(and(eq("categoryId", categoryId), gte("startDate", inputDate)));
        for (Event event : results)
            events.add(event);
        return Response.ok(gson.toJson(events)).build();
    }
}
