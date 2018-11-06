package helpers;

import org.bson.Document;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.Map;

public class Parser {

    static public Parser shared = new Parser();

    private Parser() { }

    public JsonObject parse(Document document) {
        JsonObjectBuilder object = Json.createObjectBuilder();
        for (Map.Entry map: document.entrySet()) {
            object.add(map.getKey().toString(), map.getValue().toString());
        }
        return object.build();
    }
}
