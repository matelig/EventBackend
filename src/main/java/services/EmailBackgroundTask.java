package services;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.DatabaseConnection;
import database.entity.Address;
import database.entity.Event;
import database.entity.User;
import helpers.DateHelper;
import model.EmailReminder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class EmailBackgroundTask {

    public static EmailBackgroundTask shared = new EmailBackgroundTask();

    private Long hours_24 = 24*60*60L; //TODO: discuss about time

    private MongoDatabase database = DatabaseConnection.shared.getDatabase();
    private DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyy");
    private DateFormat timeFormatter = new SimpleDateFormat("HH:mm");

    private EmailBackgroundTask() {
    }

    public void sendReminders() {
        MongoCollection<Event> events = database.getCollection("Events", Event.class);
        MongoCollection<User> users = database.getCollection("Users", User.class);
        Long currentDateSecond = DateHelper.getEpochTimeInSeconds();
        System.out.println("Email Scheduler working");
        FindIterable<Event> results = events.find(and(
                gte("startDate", currentDateSecond), //events in future
                lt("startDate", currentDateSecond + hours_24), //events that incoming in next 24h
                eq("reminderSend", false))); //reminders not send yet
        for (Event event: results) {
            if (event.getParticipantsIds()!=null) {
                for (String userId : event.getParticipantsIds()) {
                    User user = users.find(eq("_id", userId)).first();
                    if (user != null) {
                        EmailReminder er = createEventModel(event.getTitle(), event.getStartDate(), user.getEmail(), event.getAddress());
                        System.out.println("Email - Sending message");
                        boolean send = EmailSender.shared.send(er);
                        if (send) {
                            events.updateOne(eq("_id", event.getId()),
                                    combine(set("reminderSend", true)));
                        }
                        System.out.println("Message send :" + send);
                    }
                }
            }
        }
        System.out.println("Email scheduler end work");
    }

    private EmailReminder createEventModel(String eventName, Long startDate, String userEmail, Address address) {
        Date date = new Date(startDate*1000);
        EmailReminder er = new EmailReminder();
        er.setRecipient(userEmail);
        er.setSubject("EventMap - nadchodzące wydarzenie");
        String message = "Zapisałeś się na wydarzenie " + eventName + "\n";
        message += "Przypominamy, wydarzenie odbędzie się dnia " + dateFormatter.format(date) + " o godzinie " + timeFormatter.format(date) + "\n";
        if (addressExists(address)) {
            message += createAddressMessage(address);
        }
        message += "Po więcej szczegółów zapraszamy na: <LINK>";
        //TODO: link to event on our server
        er.setMessage(message);
        return er;
    }

    private String createAddressMessage(Address address) {
        String message = "Wydarzenie odbędzie się w " + address.getCountry() + ", " + address.getCity() + "\n";
        return message;
    }

    private boolean addressExists(Address address) {
        return address != null && address.getCountry() != null && address.getCity() != null; //checking only if country and city exists
    }
}
