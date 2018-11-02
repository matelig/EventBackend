package database;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import helpers.Config;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import static java.util.Arrays.asList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class DatabaseConnection {

    public static DatabaseConnection shared = new DatabaseConnection();

    private MongoDatabase database;

    public DatabaseConnection() {
        MongoClient mongoClient = MongoClients.create("mongodb://" + Config.databaseUserName + ":" + Config.databaseAdminPassword + "@" + Config.databaseUrl + ":" + 27017 + "/" + Config.databaseName + "?authSource=" + Config.databaseAdminName);
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        this.database = mongoClient.getDatabase(Config.databaseName).withCodecRegistry(pojoCodecRegistry);
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
