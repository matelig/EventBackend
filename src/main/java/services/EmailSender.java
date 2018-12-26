package services;

import model.EmailReminder;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {


    public static EmailSender shared = new EmailSender();

    private String sender = "event.backend.reminder@gmail.com";
    private String host = "localhost";
    private String username = "event.backend.reminder";
    private String password = "zgadnij123";

    private EmailSender(){ }

    public boolean send(EmailReminder reminder) {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getDefaultInstance(properties,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(reminder.getRecipient()));

            message.setSubject(reminder.getSubject());
            message.setText(reminder.getMessage());

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
