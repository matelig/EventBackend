package controllers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import database.DatabaseConnection;
import org.bson.Document;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Arrays;

@Path("/events")
public class EventsController {

    private String[] categories = new String[]{"Name1", "Name2"};

    @Path("/categories")
    @GET
    @Produces("application/json")
    public JsonArray getAll() {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (String s: categories) {
            builder.add(Json.createObjectBuilder().add("name", s));
        }
        return builder.build();
    }

    @Path("/dbTest")
    @GET
    @Produces("application/json")
    public JsonArray databaseTest() {
        DatabaseConnection dbConnection = new DatabaseConnection("admin1", "ch0w4jmyN4523d4n3", "test", "80.211.62.201", "admin");
        MongoDatabase db = dbConnection.getDatabase();
        MongoCollection<Document> collection = db.getCollection("test");
        Document doc = new Document("name", "MongoDB")
                .append("type", "database")
                .append("count", 1)
                .append("versions", Arrays.asList("v3.2", "v3.0", "v2.6"))
                .append("info", new Document("x", 203).append("y", 102));
        collection.insertOne(doc);
        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(Json.createObjectBuilder().add("first", collection.find().first().toJson()));
        return builder.build();
    }
}
