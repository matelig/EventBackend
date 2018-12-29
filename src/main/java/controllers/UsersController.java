package controllers;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.DatabaseConnection;
import database.entity.Event;
import database.entity.User;
import helpers.DateHelper;
import model.ApiException;
import model.EventShortDataDto;
import model.UserProfileDto;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

@Path("/users")
public class UsersController {

    private static Gson gson = new Gson();
    private MongoDatabase database = DatabaseConnection.shared.getDatabase();

    @GET
    @Path("/{userId}")
    @Produces("application/json")
    public Response getUserProfileById(@PathParam("userId") String userId, @Context HttpServletRequest request) {
        MongoCollection<Event> events = database.getCollection("Events", Event.class);
        MongoCollection<User> users = database.getCollection("Users", User.class);

        User user = users.find(eq("_id", userId)).first();
        if (user == null)
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException("User not found"))).build();

        Long inputDate = DateHelper.getEpochTimeInSeconds();
        FindIterable<Event> userEventResults = events.find(and(eq("ownerId", userId), gte("startDate", inputDate)));
        List<EventShortDataDto> userEvents = new ArrayList<>();
        for (Event event : userEventResults) {
            boolean didUserJoin = event.getParticipantsIds() != null && event.getParticipantsIds().contains(userId);
            userEvents.add(new EventShortDataDto(event, user.getNickname(), didUserJoin));
        }
        FindIterable<Event> allEvents = events.find(gte("startDate", inputDate));
        List<EventShortDataDto> upcomingUserEvents = new ArrayList<>();

        for (Event event : allEvents)
            if (event.getParticipantsIds() != null && event.getParticipantsIds().contains(userId))
                upcomingUserEvents.add(new EventShortDataDto(event, user.getNickname(), true));

        return Response.ok().entity(gson.toJson(new UserProfileDto(user, upcomingUserEvents, userEvents))).build();
    }
}