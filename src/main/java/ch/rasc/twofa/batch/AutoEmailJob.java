package ch.rasc.twofa.batch;

import ch.rasc.twofa.dao.UserLogRepository;
import ch.rasc.twofa.dao.UserRepository;
import ch.rasc.twofa.entity.UserLog;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

@DisallowConcurrentExecution
public class AutoEmailJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger( AutoEmailJob.class );

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLogRepository userLogRepository;

    @Override
    protected void executeInternal( JobExecutionContext context ) throws JobExecutionException
    {
        LOGGER.info( "Executing Job with key {}", context.getJobDetail().getKey() );
        try {
            String email = "hihearay@gmail.com";
            sendmail(email);
            LOGGER.info( "Email has been sent to {}", email );
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendmail(String email) throws MessagingException, IOException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        /* 3 hours interval */
        Long date = new Date().getTime();
        Timestamp tStart = new Timestamp(date - 1000 * 60 * 60 * 24);
        Timestamp tEnd = new Timestamp(date);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

        ArrayList<UserLog> userLogList = userLogRepository.findByLoginTimeBetween(tStart, tEnd);
        generateCsvFile(userLogList, "src/main/resources/static/",
                "Login log between " + formatter.format(tStart) + " and " + formatter.format(tEnd) + ".csv");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("iwuters@gmail.com", "Iam123,.");
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("iwuters@gmail.com", false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        msg.setSubject("Login log for users from " + formatter.format(tStart) + " to " + formatter.format(tEnd));
        msg.setContent("Please see the attachment", "text/html");
        msg.setSentDate(new Date());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent("Please see the attachment", "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        MimeBodyPart attachPart = new MimeBodyPart();
        attachPart.attachFile("src/main/resources/static/Login log between " + formatter.format(tStart) + " and " + formatter.format(tEnd) + ".csv");
        multipart.addBodyPart(attachPart);
        msg.setContent(multipart);
        Transport.send(msg);
    }

    private String addOneLine(UserLog userLog) { // create as an object
            String username = userLog.getUsername();
            Timestamp loginTime = userLog.getLoginTime();
            String roleName = userRepository.findByUsername(username).getRoleName();
            boolean is2FA;
            if (userRepository.findByUsername(username).getSecret() == null) {is2FA = false;}
            else {is2FA = true;}

            return username + "," + roleName + "," + is2FA + "," + loginTime.toString() + "\n";
    }


    private void generateCsvFile(ArrayList<UserLog> userLogList, String outputPath, String fileName) throws IOException {
        File file = new File(outputPath + fileName);
        try (OutputStreamWriter ow =
                     new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            ow.write("username, rolename, 2FA status, login time\n");

            for (UserLog userLog : userLogList) { // object + object
                ow.write(addOneLine(userLog));
            }
            ow.flush();
        }
        catch (IOException e) {
            System.err.println("error in generateCsvFile");
        }
    }
}
