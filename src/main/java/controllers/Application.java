package controllers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.DatabaseConnection;
import database.entity.Category;
import database.entity.Event;

import javax.ws.rs.ApplicationPath;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@ApplicationPath("/api")
public class Application extends javax.ws.rs.core.Application {

    private String[] categories = {"Sport", "Szkolenia", "Koncerty", "Film", "Konferencje", "Teatr", "Literatura", "Kulinaria",
            "Taniec", "Turystyka", "Motoryzacja", "Biegi", "Gry zespołowe", "Inne"};

    public Application() {
        initCategories();
        // initEvents();
    }

    private void initCategories() {
        MongoDatabase database = DatabaseConnection.shared.getDatabase();
        if (!database.listCollectionNames().into(new ArrayList<String>()).contains("Categories")) {
            MongoCollection<Category> categoryMongoCollection = database.getCollection("Categories", Category.class);
            for (int i = 0; i < categories.length; i++) {
                Category category = new Category(i, categories[i], "url!");
                categoryMongoCollection.insertOne(category);
            }
        }
    }

    //todo delete helper method
    private void initEvents() {
        MongoDatabase database = DatabaseConnection.shared.getDatabase();
        MongoCollection<Event> eventMongoCollection = database.getCollection("Events", Event.class);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        try {
            date = sdf.parse("21/12/2018");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Event event = new Event("Koncert", "Koncert Bryan Adams", date, true, 40,
                false, "2", 50.0d, "www.google.pl");
        event.setOwnerId("4153f486-094d-4443-9742-0480590c52e2");
        eventMongoCollection.insertOne(event);
    }
}


