package az.gov.adra.util;

import az.gov.adra.config.EmailConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Component
public class EmailSenderUtil {

    @Autowired
    private EmailConfig emailConfig;

    public void sendEmailMessage(Address to, String subject, String body) {

        //Create a email sender
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(this.emailConfig.getHost());
        mailSender.setPort(this.emailConfig.getPort());
        mailSender.setUsername(this.emailConfig.getUsername());
        mailSender.setPassword(this.emailConfig.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        //Send email
        mailSender.send(new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                mimeMessage.setFrom(emailConfig.getUsername());
                mimeMessage.setRecipient(Message.RecipientType.TO, to);
                mimeMessage.setSubject(subject, "UTF-8");
                mimeMessage.setText(body, "UTF-8");

            }
        });

    }

}
