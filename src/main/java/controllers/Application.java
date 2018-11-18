package controllers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.DatabaseConnection;
import database.entity.Category;

import javax.ws.rs.ApplicationPath;
import java.util.ArrayList;

@ApplicationPath("/api")
public class Application extends javax.ws.rs.core.Application {

    private String[] categories = {"Sport", "Szkolenia", "Koncerty", "Film", "Konferencje", "Teatr", "Literatura", "Kulinaria",
            "Taniec", "Turystyka", "Motoryzacja", "Biegi", "Gry zespołowe", "Inne"};

    public Application() {
        initCategories();
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
}


