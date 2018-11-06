package controllers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.DatabaseConnection;
import helpers.Parser;
import org.bson.Document;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Map;

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

    @Path("/dbTest")
    @GET
    @Produces("application/json")
    public JsonArray databaseTest() {
        MongoDatabase db = DatabaseConnection.shared.getDatabase();
        MongoCollection<Document> collection = db.getCollection("test"); //nazwa tabeli
        Document doc = new Document("name", "MongoDB") // nazwa tabeli
                .append("type", "database") //kolejne kolumny
                .append("count", 1)
                .append("versions", Arrays.asList("v3.2", "v3.0", "v2.6"))
                .append("info", new Document("x", 203).append("y", 102));
        collection.insertOne(doc);
        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(Json.createObjectBuilder().add("first", collection.find().first().toJson()));
        return builder.build();
    }

    @Path("/authTest")
    @GET
    public Response authTest(@Context HttpServletRequest request) {
        if (Authorization.shared.isAuthenticated(request) != Response.Status.OK) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        //to some stuff and return data
        return Response.status(Response.Status.OK).build();
    }

}
