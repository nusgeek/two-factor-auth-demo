package ch.rasc.twofa.controller.sns;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Controller
@PropertySource("classpath:aws.properties")
public class EmailSenderAWSController {
    @Value("${accessKey}")
    private String accessKey;

    @Value("${secretKey}")
    private String secretKey;

    Logger logger = LoggerFactory.getLogger(getClass());
    // helper method is more elegant
    private SnsClient getSnsClient() {
        return SnsClient.builder()
//                .credentialsProvider(getAWSCredentials(accessKey, secretKey))
                .region(Region.AP_SOUTHEAST_1)
                .build();
    }

    private AwsCredentialsProvider getAWSCredentials(String accessKey, String secretKey) {
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        AwsCredentialsProvider awsCredentialsProvider = () -> awsBasicCredentials;
        return awsCredentialsProvider;
    }

    @PostMapping("/createTopic")
    private ModelAndView createTopic(@RequestParam("topic_name") String topicName) throws URISyntaxException {

        final CreateTopicRequest topicCreateRequest = CreateTopicRequest.builder().name(topicName).build();

        SnsClient snsClient = getSnsClient();

        final CreateTopicResponse topicCreateResponse = snsClient.createTopic(topicCreateRequest);

        Map<String, String> model = new HashMap<>();
        if (topicCreateResponse.sdkHttpResponse().isSuccessful()) {
            logger.info(topicCreateResponse.toString());
            logger.info("Topic creation successful. Topic ARN: {}", topicCreateResponse.topicArn());
            logger.info("Topics: {}", snsClient.listTopics());
            model.put("add_topic_reminder", "Topic created successfully. Topic ARN: " + topicCreateResponse.topicArn());
        } else {
            model.put("add_topic_reminder", "Failed to create topic.");
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, topicCreateResponse.sdkHttpResponse().statusText().get()
            );
        }

        snsClient.close();

        return new ModelAndView("aws-email", model);
    }

    @PostMapping("/deleteTopic")
    private ModelAndView deleteTopic(@RequestParam("topic_arn") String topicArn) throws URISyntaxException {
        SnsClient snsClient = getSnsClient();

        final DeleteTopicRequest deleteTopicRequest = DeleteTopicRequest.builder().topicArn(topicArn).build();

        DeleteTopicResponse topicDeleteResponse = snsClient.deleteTopic(deleteTopicRequest);

        Map<String, String> model = new HashMap<>();
        if (topicDeleteResponse.sdkHttpResponse().isSuccessful()) {
            model.put("delete_topic_reminder", "Topic deleted successfully.");
            logger.info("Topic deletion successful. Topic ARN: {}", topicDeleteResponse.toString());
        } else {
            model.put("delete_topic_reminder", "Failed to delete topic.");
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, topicDeleteResponse.sdkHttpResponse().statusText().get()
            );}

        return new ModelAndView("aws-email", model);
    }

    @PostMapping("/addSubscribers")
    private ModelAndView addSubscriberToTopic(@RequestParam("arn") String arn, @RequestParam("email_address") String email)
            throws URISyntaxException {

        SnsClient snsClient = getSnsClient();

        final SubscribeRequest subscribeRequest = SubscribeRequest.builder()
                .protocol("email")
                .topicArn(arn)
                .endpoint(email)
                .build();

        SubscribeResponse subscribeResponse = snsClient.subscribe(subscribeRequest);

        Map<String, String> model = new HashMap<>();
        if (subscribeResponse.sdkHttpResponse().isSuccessful()) {
            logger.info("Subscriber {} subscribes topic successfully.", email);
            model.put("add_subscriber_reminder", "Please check your email to activate the subscription.");
        } else {
            model.put("add_subscriber_reminder", "Failed to add a subscriber.");
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, subscribeResponse.sdkHttpResponse().statusText().get()
            );
        }

        snsClient.close();

        return new ModelAndView("aws-email", model);
    }

    @PostMapping("/sendEmail")
    private ModelAndView sendEmail(@RequestParam("arn") String arn, @RequestParam("subject") String subject,
                          @RequestParam("content") String content) throws URISyntaxException {

        SnsClient snsClient = getSnsClient();

        final PublishRequest publishRequest = PublishRequest.builder()
                .topicArn(arn)
                .subject(subject)
                .message(content)
                .build();

        PublishResponse publishResponse = snsClient.publish(publishRequest);

        Map<String, String> model = new HashMap<>();
        if (publishResponse.sdkHttpResponse().isSuccessful()) {
            model.put("send_email_reminder", "Email sent successfully!");
            logger.info("Message publishing successful");
        } else {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, publishResponse.sdkHttpResponse().statusText().get());
        }

        snsClient.close();
        return new ModelAndView("aws-email", model);
    }

    @PostMapping("/removeSubscriber")
    private ModelAndView removeSubscriber(@RequestParam("arn") String arn) {
        SnsClient snsClient = getSnsClient();

        Map<String, String> model = new HashMap<>();
        try {
            UnsubscribeRequest unsubscribeRequest = UnsubscribeRequest.builder()
                    .subscriptionArn(arn)
                    .build();

            snsClient.unsubscribe(unsubscribeRequest);

            model.put("unsub_endpoint_reminder", "The user has been successfully unsubscribed.");
        } catch (SnsException e) {
            logger.error(e.awsErrorDetails().errorMessage());
            model.put("unsub_endpoint_reminder", "Unsubscription failed!");
            return new ModelAndView("aws-email", model);
        }
        return new ModelAndView("aws-email", model);
    }

    public static void main(String[] args) {

        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAUHW7W74BQUY3Y5MY",
                "isjJe+f/SOs39nMnWnBs7HYcScu2/zBU+TSqa74f");

        AmazonSNS snsClient = AmazonSNSClient
                .builder()
                .withRegion(Region.AP_SOUTHEAST_1.toString())
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
//
//        // Create an Amazon SNS topic.
//        final CreateTopicRequest createTopicRequest = new CreateTopicRequest("MyTopic");
//        final CreateTopicResult createTopicResponse = snsClient.createTopic(createTopicRequest);
//
//        // Print the topic ARN.
//        System.out.println("TopicArn:" + createTopicResponse.getTopicArn());
//
//        // Print the request ID for the CreateTopicRequest action.
//        System.out.println("CreateTopicRequest: " + snsClient.getCachedResponseMetadata(createTopicRequest));


//        // Publish a message to an Amazon SNS topic.
//        final String msg = "If you receive this message, publishing a message to an Amazon SNS topic works.";
//        final String topicArn = "arn:aws:sns:eu-west-2:291453206275:email-test-topic";
//        final PublishRequest publishRequest = new PublishRequest(topicArn, msg);
//        final PublishResult publishResponse = snsClient.publish(publishRequest);
//
//        // Print the MessageId of the message.
//        System.out.println("MessageId: " + publishResponse.getMessageId());



//        // Subscribe an email endpoint to an Amazon SNS topic.
//        final SubscribeRequest subscribeRequest = new SubscribeRequest(topicArn, "email",
//                "hihearay@gmail.com");
//        snsClient.subscribe(subscribeRequest);
//
//// Print the request ID for the SubscribeRequest action.
//        System.out.println("SubscribeRequest: " + snsClient.getCachedResponseMetadata(subscribeRequest));
//        System.out.println("To confirm the subscription, check your email.");
    }
}
