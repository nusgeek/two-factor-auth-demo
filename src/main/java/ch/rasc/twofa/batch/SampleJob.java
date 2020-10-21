package ch.rasc.twofa.batch;

import ch.rasc.twofa.dao.UserLogRepository;
import ch.rasc.twofa.dao.UserRepository;
import ch.rasc.twofa.entity.UserLog;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.*;
import javax.mail.internet.*;
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

@DisallowConcurrentExecution//Job中的任务有可能并发执行，例如任务的执行时间过长，而每次触发的时间间隔太短，则会导致任务会被并发执行。如果是并发执行，就需要一个数据库锁去避免一个数据被多次处理。
public class SampleJob implements Job {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLogRepository userLogRepository;


    @lombok.SneakyThrows
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String email = (String) jobExecutionContext.getJobDetail().getJobDataMap().get("email");
        sendmail(email);

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
        generateCsvFile(userLogList, "server/src/main/resources/static/",
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


    private void generateCsvFile(ArrayList<UserLog> userLogList, String outputPath, String fileName) throws IOException {
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

/*需实现Job接口，这个接口就一个execute()方法需要重写，方法内容就是具体的业务逻辑。
如果是动态任务呢，比如取消订单，每次执行都是不同的订单号。
这个时候就需要在创建任务(JobDetail)或者创建触发器(Trigger)的那里传入参数，然后在这里通过JobExecutionContext来获取参数进行处理，*/
