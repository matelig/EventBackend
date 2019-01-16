package controllers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.DatabaseConnection;
import database.entity.Category;
import services.EmailBackgroundTask;

import javax.ws.rs.ApplicationPath;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ApplicationPath("/api")
public class Application extends javax.ws.rs.core.Application {

    private int eventSchedulerDelay = 0;
    private int eventSchedulerPeriod = 1;
    private String[] categories = {"Sport", "Trainings", "Concerts", "Film", "Conferences", "Theater", "Literature", "Culinary",
            "Dance", "Tourism", "Motoring", "Racing", "Team games", "Others"};

    public Application() {
        initCategories();
        initEventScheduler();
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

    private void initEventScheduler() {
        System.out.println("scheduler init");
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                EmailBackgroundTask.shared.sendReminders();
            }
        }, eventSchedulerDelay, eventSchedulerPeriod, TimeUnit.HOURS);
    }
}


