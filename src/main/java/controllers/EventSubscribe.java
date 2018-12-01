package controllers;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.DatabaseConnection;
import database.entity.Event;
import database.entity.User;
import helpers.Authorization;
import helpers.KeyDecoder;
import model.ApiException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

@Path("/subscribeEvent")
public class EventSubscribe {
    private static Gson gson = new Gson();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response signUpForEvent(@Context HttpServletRequest request) {
        String eventId = request.getParameter("eventId");
        if (eventId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("EventId must be provided."))).build();
        }
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

        MongoCollection<Event> events = database.getCollection("Events", Event.class);
        Event currentEvent = events.find(eq("_id", eventId)).first();

        if (currentEvent == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("Event with given ID does not exists."))).build();
        }

        List<String> participantsIds = currentEvent.getParticipantsIds();
        if (participantsIds == null) {
            participantsIds = new ArrayList<>();
        }
        if (participantsIds.contains(existingUser.getId())) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("User currently signed up to this event."))).build();
        }
        participantsIds.add(existingUser.getId());

        events.updateOne(eq("_id", currentEvent.getId()), set("participantsIds", participantsIds));

        return Response.ok().build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response unsubscribeFromEvent(@Context HttpServletRequest request) {
        String eventId = request.getParameter("eventId");
        if (eventId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("EventId must be provided."))).build();
        }
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

        MongoCollection<Event> events = database.getCollection("Events", Event.class);
        Event currentEvent = events.find(eq("_id", eventId)).first();

        if (currentEvent == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("Event with given ID does not exists."))).build();
        }

        List<String> participantsIds = currentEvent.getParticipantsIds();
        for (Iterator<String> iter = participantsIds.listIterator(); iter.hasNext(); ) {
            String a = iter.next();
            if (a.equals(existingUser.getId())) {
                iter.remove();
            }
        }

        events.updateOne(eq("_id", currentEvent.getId()), set("participantsIds", participantsIds));

        return Response.ok().build();
    }
}
