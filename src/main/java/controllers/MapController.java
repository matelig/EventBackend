package controllers;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.DatabaseConnection;
import database.entity.Event;
import helpers.DateHelper;
import model.ApiException;
import model.EventsMapRequest;
import model.TokenRequest;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.gte;

@Path("/map")
public class MapController {

    private MongoDatabase database = DatabaseConnection.shared.getDatabase();
    private static Gson gson = new Gson();

    @Path("/radius")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public Response findEvents(@Context HttpServletRequest request, String jsonString){
        MongoCollection<Event> events = database.getCollection("Events", Event.class);
        Long currentDateSecond = DateHelper.getEpochTimeInSeconds();
        FindIterable<Event> results = events.find(gte("startDate", currentDateSecond));
        ObjectMapper mapper = new ObjectMapper();
        try {
            EventsMapRequest eventsMapRequest = mapper.readValue(jsonString, EventsMapRequest.class);
            List<EventMapData> eventsInRadius = new ArrayList<>();
            for(Event e : results) {
                if (eventsMapRequest.getRadius() > distanceBetweenTwoPoints(eventsMapRequest.getLatitude(),
                        eventsMapRequest.getLongitude(), e.getLatitude(), e.getLongitude())) {
                    EventMapData data = new EventMapData(e.getId(), e.getLongitude(), e.getLatitude(), e.getTitle(), e.getPhotoUrl());
                    eventsInRadius.add(data);
                }
            }
            return Response.ok(gson.toJson(eventsInRadius)).build();
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ApiException(e.getMessage()))).build();
        }
    }

    private double distanceBetweenTwoPoints(double lat1, double lon1, double lat2, double lon2) {
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double earthRadius = 6371.01;
        return earthRadius * Math.acos(Math.sin(lat1)*Math.sin(lat2) + Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon1 - lon2));
    }

    private class EventMapData {
        private String id;
        private double longitude;
        private double latitude;
        private String name;
        private String photoUrl;

        public EventMapData(String id, double longitude, double latitude, String name, String photoUrl) {
            this.id = id;
            this.longitude = longitude;
            this.latitude = latitude;
            this.name = name;
            this.photoUrl = photoUrl;
        }
    }
}
