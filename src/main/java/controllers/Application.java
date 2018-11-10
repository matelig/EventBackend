package controllers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.DatabaseConnection;
import database.entity.Category;
import database.entity.Event;

import javax.ws.rs.ApplicationPath;
import java.util.ArrayList;
import java.util.Date;

@ApplicationPath("/api")
public class Application extends javax.ws.rs.core.Application {

    private String[] categories = {"Sport", "Szkolenia", "Koncerty", "Film", "Konferencje", "Teatr", "Literatura", "Kulinaria",
            "Taniec", "Turystyka", "Motoryzacja", "Biegi", "Gry zespołowe", "Inne"};

    public Application() {
        initCategories();
        initEvents();
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

    private void initEvents() {
        MongoDatabase database = DatabaseConnection.shared.getDatabase();
        MongoCollection<Event> eventMongoCollection = database.getCollection("Events", Event.class);
        Event event = new Event();
        event.setCost(52l);
        event.setStartDate(new Date());
        event.setDescription("My fourth event");
        event.setOwner(1l);
        eventMongoCollection.insertOne(event);
    }
}


