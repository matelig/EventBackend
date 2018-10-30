package database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DatabaseConnection {

    private MongoClient mongoClient;
    private MongoDatabase database;

    public DatabaseConnection(String userName, String password, String database, String hostName, String authSource) {
        this.mongoClient = MongoClients.create("mongodb://" + userName + ":" + password + "@" + hostName + ":" + 27017 + "/" + database + "?authSource=" + authSource);
        //mongodb://localhost:27017/test?authSource=admin --username admin1
        //this.mongoClient = MongoClients.create();
        this.database = mongoClient.getDatabase(database);
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
