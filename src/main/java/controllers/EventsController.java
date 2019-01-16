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
import model.*;
import org.bson.Document;
import org.codehaus.jackson.map.ObjectMapper;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

@Path("/events")
public class EventsController {

    private static Gson gson = new Gson();
    private MongoDatabase database = DatabaseConnection.shared.getDatabase();

    @Path("/categories")
    @GET
    @Produces("application/json")
    public JsonArray getAll() {
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
        User existingUser = Authorization.shared.getUser(request);

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

        newEvent.setTags(addEventRequest.getTags());

        Long startDate = Long.parseLong(addEventRequest.getStartDate());
        newEvent.setStartDate(startDate);

        Long endDate = Long.parseLong(addEventRequest.getEndDate());
        newEvent.setEndDate(endDate);
        newEvent.setAddress(GeocodingHelper.reverseGeocode(Double.parseDouble(addEventRequest.getLatitude()),
                Double.parseDouble(addEventRequest.getLongitude())));
        newEvent.setPhotoUrl(addEventRequest.getPhotoUrl());
        newEvent.setId(UUID.randomUUID().toString());
        newEvent.setReminderSend(false);
        return newEvent;
    }

    @GET
    @Produces("application/json")
    public Response getAllEvents(@Context HttpServletRequest request) {
        if (!request.getParameterMap().isEmpty())
            return getFilteredEvents(request);
        MongoCollection<Event> events = database.getCollection("Events", Event.class);
        Long currentDateSecond = DateHelper.getEpochTimeInSeconds();
        FindIterable<Event> results = events.find(gte("startDate", currentDateSecond));
        List<Event> resultEvents = new ArrayList<>();
        for (Event event : results) {
            resultEvents.add(event);
        }
        List<EventShortDataDto> resultDtos = new ArrayList<>();
        for (Event event : resultEvents) {
            resultDtos.add(getShortEventDto(event, request));
        }
        return Response.ok(gson.toJson(resultDtos)).build();
    }

    private Response getFilteredEvents(@Context HttpServletRequest request) {
        EventsFilter filter = new EventsFilter(request);
        MongoCollection<Event> events = database.getCollection("Events", Event.class);
        Long currentDateSecond = DateHelper.getEpochTimeInSeconds();
        FindIterable<Event> results = events.find(gte("startDate", currentDateSecond));
        List<Event> resultEvents = new ArrayList<Event>();
        for (Event event : results) {
            resultEvents.add(event);
        }
        List<Event> filteredEvents = filter.filterEvents(resultEvents);
        List<EventShortDataDto> resultDtos = new ArrayList<>();
        for (Event event : filteredEvents) {
            resultDtos.add(getShortEventDto(event, request));
        }
        return Response.ok(gson.toJson(resultDtos)).build();
    }

    private EventShortDataDto getShortEventDto(Event event, HttpServletRequest request) {
        User currentUser = getUserFromRequest(request);
        boolean didUserJoin = event.getParticipantsIds() != null && currentUser != null && event.getParticipantsIds().contains(currentUser.getId());
        MongoCollection<User> users = database.getCollection("Users", User.class);
        User owner = users.find(eq("_id", event.getOwnerId())).first();
        return new EventShortDataDto(event, owner.getNickname(), didUserJoin);
    }

    @GET
    @Path("/{eventId}")
    @Produces("application/json")
    public Response getEventById(@PathParam("eventId") String eventId, @Context HttpServletRequest request) {
        User currentUser = getUserFromRequest(request);
        MongoCollection<Event> events = database.getCollection("Events", Event.class);
        MongoCollection<User> users = database.getCollection("Users", User.class);
        Event event = events.find(eq("_id", eventId)).first();
        if (event == null)
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("Event not found"))).build();
        User owner = users.find(eq("_id", event.getOwnerId())).first();
        JsonElement eventJsonElement = gson.toJsonTree(event);
        if (owner != null)
            eventJsonElement.getAsJsonObject().addProperty("ownerName", owner.getNickname());
        if (currentUser != null) {
            if (event.getParticipantsIds() == null) {
                eventJsonElement.getAsJsonObject().addProperty("signed", false);
            } else {
                eventJsonElement.getAsJsonObject().addProperty("signed", event.getParticipantsIds().contains(currentUser.getId()));
            }
        }
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
                            set("address", updateEvent.getAddress()),
                            set("tags", updateEvent.getTags())));
            return  Response.ok().build();
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException(e.getMessage()))).build();
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("Wrong date format"))).build();
        } catch (NullPointerException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("All required fields must be provided"))).build();
        }
    }

    @GET
    @Path("/users/{ownerId}")
    @Produces("application/json")
    public Response getEventsByOwnerId(@PathParam("ownerId") String ownerId, @Context HttpServletRequest request) {
        MongoCollection<User> users = database.getCollection("Users", User.class);
        User existingUser = users.find(eq("_id", ownerId)).first();
        if (existingUser == null)
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("User not found"))).build();
        MongoCollection<Event> eventsCollection = database.getCollection("Events", Event.class);
        Long inputDate = DateHelper.getEpochTimeInSeconds();
        FindIterable<Event> results = eventsCollection.find(and(eq("ownerId", ownerId), gte("startDate", inputDate)));
        List<EventShortDataDto> resultDtos = new ArrayList<>();
        for (Event event : results) {
            resultDtos.add(getShortEventDto(event, request));
        }
        return Response.ok(gson.toJson(resultDtos)).build();
    }

    @GET
    @Path("/categories/{categoryId}")
    @Produces("application/json")
    public Response getEventsByCategoryId(@PathParam("categoryId") String categoryId, @Context HttpServletRequest request) {
        int categoryIdInt;
        try {
            categoryIdInt = Integer.parseInt(categoryId);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("Wrong categoryId format"))).build();
        }
        MongoCollection<Category> categories = database.getCollection("Categories", Category.class);
        Category category = categories.find(eq("_id", categoryIdInt)).first();
        if (category == null)
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("Category not found"))).build();
        MongoCollection<Event> eventsCollection = database.getCollection("Events", Event.class);
        Long inputDate = DateHelper.getEpochTimeInSeconds();
        FindIterable<Event> results = eventsCollection.find(and(eq("categoryId", categoryId), gte("startDate", inputDate)));
        List<EventShortDataDto> resultDtos = new ArrayList<>();
        for (Event event : results) {
            resultDtos.add(getShortEventDto(event, request));
        }
        return Response.ok(gson.toJson(resultDtos)).build();
    }

    @GET
    @Path("/{eventId}/participants")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getParticipantsList(@PathParam("eventId") String eventId, @Context HttpServletRequest request) {

        Event currentEvent = getDatabaseEventById(eventId);
        MongoCollection<User> users = database.getCollection("Users", User.class);

        if (currentEvent == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("Provided event ID has bad format"))).build();
        }

        List<String> participantsIds = currentEvent.getParticipantsIds();
        if (participantsIds == null) {
            participantsIds = new ArrayList<>();
        }

        List<UserShortDto> userShortDtoList = new ArrayList<>();
        for (String userId: participantsIds) {
            User existingUser = users.find(eq("_id", userId)).first();
            if (existingUser != null) {
                UserShortDto userShortDto = new UserShortDto(existingUser.getId(), existingUser.getEmail(), existingUser.getNickname());
                userShortDtoList.add(userShortDto);
            }
        }
        return Response.ok(gson.toJson(userShortDtoList)).build();
    }

    @POST
    @Path("/{eventId}/participants")
    @Produces(MediaType.APPLICATION_JSON)
    public Response signUpForEvent(@PathParam("eventId") String eventId, @Context HttpServletRequest request) {

        User existingUser = getUserFromRequest(request);

        if (existingUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Event currentEvent = getDatabaseEventById(eventId);

        if (currentEvent == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("Provided event ID has bad format"))).build();
        }

        List<String> participantsIds = currentEvent.getParticipantsIds();
        if (participantsIds == null) {
            participantsIds = new ArrayList<>();
        }
        if (participantsIds.contains(existingUser.getId())) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("User currently signed up to this event."))).build();
        }
        participantsIds.add(existingUser.getId());

        updateParticipantsList(participantsIds, currentEvent);

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{eventId}/participants")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unsubscribeFromEvent(@PathParam("eventId") String eventId, @Context HttpServletRequest request) {
        User existingUser = getUserFromRequest(request);

        if (existingUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Event currentEvent = getDatabaseEventById(eventId);

        if (currentEvent == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("Provided event ID has bad format"))).build();
        }

        List<String> participantsIds = currentEvent.getParticipantsIds();
        for (Iterator<String> iter = participantsIds.listIterator(); iter.hasNext(); ) {
            String a = iter.next();
            if (a.equals(existingUser.getId())) {
                iter.remove();
            }
        }

        updateParticipantsList(participantsIds, currentEvent);

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    private User getUserFromRequest(HttpServletRequest request) {
        if (Authorization.shared.isAuthenticated(request).getStatusCode() != 200) {
            return null;
        }
        return Authorization.shared.getUser(request);
    }

    private Event getDatabaseEventById(String eventId) {
        if (eventId == null) {
            return null;
        }
        MongoCollection<Event> events = database.getCollection("Events", Event.class);

        return events.find(eq("_id", eventId)).first();
    }

    private void updateParticipantsList(List<String> participantsIds, Event currentEvent) {
        MongoCollection<Event> events = database.getCollection("Events", Event.class);
        events.updateOne(eq("_id", currentEvent.getId()), set("participantsIds", participantsIds));
    }

}
