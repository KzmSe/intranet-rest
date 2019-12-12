package az.gov.adra.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailSenderUtil {

    @Autowired
    private JavaMailSender emailSender;

    public void sendEmailMessage(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            emailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
