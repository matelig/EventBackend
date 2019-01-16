package controllers;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.DatabaseConnection;
import database.entity.Event;
import helpers.DateHelper;
import model.ApiException;
import model.EventMapData;
import org.w3c.dom.ranges.RangeException;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.gte;

@Path("/map")
public class MapController {

    private static Gson gson = new Gson();
    private MongoDatabase database = DatabaseConnection.shared.getDatabase();

    @Path("/events/area")
    @GET
    @Produces("application/json")
    public Response findEvents(@QueryParam("lat") double latitude, @QueryParam("lng") double longitude,
                               @DefaultValue("20") @QueryParam("rad") double radius) {
        MongoCollection<Event> events = database.getCollection("Events", Event.class);
        Long currentDateSecond = DateHelper.getEpochTimeInSeconds();
        FindIterable<Event> results = events.find(gte("startDate", currentDateSecond));
        try {
            List<EventMapData> eventsInRadius = new ArrayList<>();
            for (Event e : results) {
                if (radius > distanceBetweenTwoPoints(latitude, longitude, e.getLatitude(), e.getLongitude())) {
                    EventMapData data = new EventMapData(e.getId(), e.getLongitude(), e.getLatitude(), e.getTitle());
                    eventsInRadius.add(data);
                }
            }
            return Response.ok(gson.toJson(eventsInRadius)).build();
        } catch (RangeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException(e.getMessage()))).build();
        }
    }

    private double distanceBetweenTwoPoints(double lat1, double lon1, double lat2, double lon2) throws RangeException {
        if (lat1 > 90 || lat1 < -90 || lon1 > 180 || lon1 < -180 || lat2 > 90 || lat2 < -90 || lon2 > 180 || lon2 < -180)
            throw new RangeException((short) -1, "Coordinates have wrong range");
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double earthRadius = 6371.01;
        return earthRadius * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    }
}
