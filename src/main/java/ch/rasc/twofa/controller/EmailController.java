package ch.rasc.twofa.controller;

import ch.rasc.twofa.dao.UserLogRepository;
import ch.rasc.twofa.dao.UserRepository;
import ch.rasc.twofa.entity.UserLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@RestController
public class EmailController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLogRepository userLogRepository;

    @RequestMapping(value = "/sendemail")
    public String sendEmail() throws IOException, MessagingException {
        sendmail();
        return "Email sent successfully";
    }

    private void sendmail() throws AddressException, MessagingException, IOException {
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
        generateCsvFile(userLogList, "server/src/main/resources/static/",
                "Login log between " + formatter.format(tStart) + " and " + formatter.format(tEnd) + ".csv");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("iwuters@gmail.com", "Iam123,.");
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("iwuters@gmail.com", false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("hihearsay@gmail.com"));
        msg.setSubject("Tutorials point email");
        msg.setContent("Tutorials point email", "text/html");
        msg.setSentDate(new Date());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent("Tutorials point email", "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        MimeBodyPart attachPart = new MimeBodyPart();

        attachPart.attachFile("server/src/main/resources/static/Login log between " + formatter.format(tStart) + " and " + formatter.format(tEnd) + ".csv");
        multipart.addBodyPart(attachPart);
        msg.setContent(multipart);
        Transport.send(msg);
    }

    private String addOneLine(UserLog userLog) {
        String username = userLog.getUsername();
        Timestamp loginTime = userLog.getLoginTime();
        String roleName = userRepository.findByUsername(username).getRoleName();
        boolean is2FA;
        if (userRepository.findByUsername(username).getSecret() == null) {is2FA = false;}
        else {is2FA = true;}
        return username + "," + roleName + "," + is2FA + "," + loginTime.toString() + "\n";
    }


    private void generateCsvFile(ArrayList<UserLog> userLogList, String outputPath, String fileName) throws IOException{
        File file = new File(outputPath+fileName);
        OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
        ow.write("username, rolename, 2FA status, login time\n");

        for (int i = 0; i < userLogList.size(); i++) {
            ow.write(addOneLine(userLogList.get(i)));
        }
        ow.flush();
        ow.close();
    }

}