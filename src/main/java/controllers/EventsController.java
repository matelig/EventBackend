package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.DatabaseConnection;
import database.entity.Category;
import database.entity.Event;
import database.entity.User;
import helpers.*;
import model.AddEventRequest;
import model.ApiException;
import model.EventsFilter;
import org.bson.Document;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import javax.json.*;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

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
        } catch (NullPointerException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("All required fields must be provided"))).build();
        }
    }

    private Event createEventObject(AddEventRequest addEventRequest) throws NumberFormatException, NullPointerException {
        Event newEvent = new Event(addEventRequest.getName(), addEventRequest.getDescription(), Double.parseDouble(addEventRequest.getLatitude()),
                Double.parseDouble(addEventRequest.getLongitude()), Integer.parseInt(addEventRequest.getMaxParticipants()),
                Boolean.getBoolean(addEventRequest.isOnlyRegistered()), addEventRequest.getCategoryId(),
                Double.parseDouble(addEventRequest.getCost()), addEventRequest.getExternalUrl());

        Long startDate = Long.parseLong(addEventRequest.getStartDate());
        newEvent.setStartDate(startDate);

        Long endDate = Long.parseLong(addEventRequest.getEndDate());
        newEvent.setEndDate(endDate);
        newEvent.setAddress(GeocodingHelper.reverseGeocode(Double.parseDouble(addEventRequest.getLatitude()),
                Double.parseDouble(addEventRequest.getLongitude())));
        newEvent.setPhotoUrl(addEventRequest.getPhotoUrl());
        newEvent.setId(UUID.randomUUID().toString());
        return newEvent;
    }

    @GET
    @Produces("application/json")
    public Response getAllEvents(@Context HttpServletRequest request) {
        if (!request.getParameterMap().isEmpty())
            return getFilteredEvents(request);
        MongoDatabase database = DatabaseConnection.shared.getDatabase();
        MongoCollection<Event> events = database.getCollection("Events", Event.class);
        Long currentDateSecond = DateHelper.getEpochTimeInSeconds();
        FindIterable<Event> results = events.find(gte("startDate", currentDateSecond));
        List<Event> resultEvents = new ArrayList<Event>();
        for (Event event : results) {
            resultEvents.add(event);
        }
        return Response.ok(gson.toJson(resultEvents)).build();
    }

    private Response getFilteredEvents(@Context HttpServletRequest request) {
        EventsFilter filter = new EventsFilter(request);
        MongoDatabase database = DatabaseConnection.shared.getDatabase();
        MongoCollection<Event> events = database.getCollection("Events", Event.class);
        Long currentDateSecond = DateHelper.getEpochTimeInSeconds();
        FindIterable<Event> results = events.find(gte("startDate", currentDateSecond));
        List<Event> resultEvents = new ArrayList<Event>();
        for (Event event : results) {
            resultEvents.add(event);
        }
        List<Event> filteredEvents = filter.filterEvents(resultEvents);
        return Response.ok(gson.toJson(filteredEvents)).build();
    }

    @GET
    @Path("/{eventId}")
    @Produces("application/json")
    public Response getEventById(@PathParam("eventId") String eventId) {
        MongoDatabase database = DatabaseConnection.shared.getDatabase();
        MongoCollection<Event> events = database.getCollection("Events", Event.class);
        MongoCollection<User> users = database.getCollection("Users", User.class);
        Event event = events.find(eq("_id", eventId)).first();
        if (event == null)
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("Event not found"))).build();
        User owner = users.find(eq("_id", event.getOwnerId())).first();
        JsonElement eventJsonElement = gson.toJsonTree(event);
        if (owner != null)
            eventJsonElement.getAsJsonObject().addProperty("ownerName", owner.getNickname());
        return Response.ok(eventJsonElement.toString()).build();
    }

    @PATCH
    @Path("/{eventId}")
    @Produces("application/json")
    public Response updateEvent(@PathParam("eventId") String eventId, @Context HttpServletRequest request, String jsonString) {
        if (Authorization.shared.isAuthenticated(request).getStatusCode() != 200) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(gson.toJson(new ApiException("User authorization failed"))).build();
        }
        String email = KeyDecoder.shared.decode(request);
        MongoDatabase database = DatabaseConnection.shared.getDatabase();
        MongoCollection<User> users = database.getCollection("Users", User.class);
        User existingUser = users.find(eq("email", email)).first();
        if(existingUser == null)
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("User not found"))).build();

        ObjectMapper mapper = new ObjectMapper();
        try {
            AddEventRequest receivedEvent = mapper.readValue(jsonString, AddEventRequest.class);
            Event updateEvent = createEventObject(receivedEvent);
            MongoCollection<Event> events = database.getCollection("Events", Event.class);
            Event existingEvent = events.find(eq("_id", eventId)).first();
            if(existingEvent == null)
                return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("Event not found"))).build();
            if(!existingEvent.getOwnerId().equals(existingUser.getId()))
                return Response.status(Response.Status.UNAUTHORIZED).entity(gson.toJson(new ApiException("User is not owner of this event"))).build();
            events.updateOne(eq("_id", existingEvent.getId()),
                    combine(set("title", updateEvent.getTitle()),
                            set("description", updateEvent.getDescription()),
                            set("photoUrl", updateEvent.getPhotoUrl()),
                            set("latitude", updateEvent.getLatitude()),
                            set("longitude", updateEvent.getLongitude()),
                            set("startDate", updateEvent.getStartDate()),
                            set("endDate", updateEvent.getEndDate()),
                            set("showGuestList", updateEvent.isShowGuestList()),
                            set("maxParticipants", updateEvent.getMaxParticipants()),
                            set("onlyRegistered", updateEvent.isOnlyRegistered()),
                            set("categoryId", updateEvent.getCategoryId()),
                            set("cost", updateEvent.getCost()),
                            set("externalUrl", updateEvent.getExternalUrl()),
                            set("address", updateEvent.getAddress())));
            return  Response.ok().build();
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException(e.getMessage()))).build();
        }
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
        Long inputDate = System.currentTimeMillis();
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
        MongoCollection<User> users = database.getCollection("Users", User.class);
        Category category = categories.find(eq("_id", categoryIdInt)).first();
        if (category == null)
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("Category not found"))).build();
        MongoCollection<Event> eventsCollection = database.getCollection("Events", Event.class);
        JsonArrayBuilder jsonArray = Json.createArrayBuilder();
        Long inputDate = System.currentTimeMillis();
        FindIterable<Event> results = eventsCollection.find(and(eq("categoryId", categoryId), gte("startDate", inputDate)));
        for (Event event : results) {
            User owner = users.find(eq("_id", event.getOwnerId())).first();
            jsonArray.add(createEventForCategoryJsonResponse(event, owner.getNickname()));
        }
        return Response.ok(jsonArray.build()).build();
    }

    private JsonObject createEventForCategoryJsonResponse(Event event, String userName) {
        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("id", event.getId());
        json.add("title", event.getTitle());
        json.add("description", event.getDescription());
        json.add("ownerId", event.getOwnerId());
        json.add("photoUrl", event.getPhotoUrl());
        json.add("ownerName", userName);
        return json.build();
    }

}
