package ch.rasc.twofa.util;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.model.Region;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.DeleteTopicRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import software.amazon.awssdk.services.sns.model.PublishRequest;

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
    private AmazonSNS getSnsBuilder() {
        return AmazonSNSClient
                .builder()
                .withRegion(Region.AP_Singapore.toString())
                .withCredentials(getAWSCredentials(accessKey, secretKey))
                .build();
    }

    private AWSCredentialsProvider getAWSCredentials(String accessKey, String secretKey) {
        BasicAWSCredentials awsBasicCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return new AWSStaticCredentialsProvider(awsBasicCredentials);
    }

    @PostMapping("/createTopic")
    private ModelAndView createTopic(@RequestParam("topic_name") String topicName) throws URISyntaxException {

        // Topic name cannot contain spaces
        final CreateTopicRequest topicCreateRequest = new CreateTopicRequest(topicName);

//        Tag tag = new Tag();
//        tag.setKey("tagKey");
//        tag.setValue("tagValue");
//        CreateTopicRequest createTopicRequest = topicCreateRequest.withTags(tag);

        // Helper method makes the code more readable
        AmazonSNS snsClient = getSnsBuilder();

        final CreateTopicResult topicCreateResult = snsClient.createTopic(topicCreateRequest);

        logger.info(topicCreateResult.toString());
        logger.info("Topic creation successful. Topic ARN: {}", topicCreateResult.getTopicArn());
        logger.info("Topics: {}", snsClient.listTopics());

        Map<String, String> model = new HashMap<>();
        model.put("add_topic_reminder", "Topic created successfully. Topic ARN: " + topicCreateResult.getTopicArn());
        return new ModelAndView("aws-email", model);
    }

    @PostMapping("/deleteTopic")
    private ModelAndView deleteTopic(@RequestParam("topic_arn") String topicArn) throws URISyntaxException {
        AmazonSNS snsClient = getSnsBuilder();

        // Delete an Amazon SNS topic.
        final DeleteTopicRequest deleteTopicRequest = new DeleteTopicRequest(topicArn);
        snsClient.deleteTopic(deleteTopicRequest);

        // Print the request ID for the DeleteTopicRequest action.
        Map<String, String> model = new HashMap<>();
        model.put("delete_topic_reminder", "Topic deleted successfully.");
        return new ModelAndView("aws-email", model);
    }

    @PostMapping("/addSubscribers")
    private ModelAndView addSubscriberToTopic(@RequestParam("arn") String arn, @RequestParam("email_address") String email)
            throws URISyntaxException {

        AmazonSNS snsClient = getSnsBuilder();

        final SubscribeRequest subscribeRequest = new SubscribeRequest(arn, "email", email);

        snsClient.subscribe(subscribeRequest);

        Map<String, String> model = new HashMap<>();
        model.put("add_subscriber_reminder", "Please check your email to activate the subscription.");
//        return "Topic ARN: " + topicCreateResult.getTopicArn();
        return new ModelAndView("aws-email", model);
    }

    @PostMapping("/sendEmail")
    private String sendEmail(@RequestParam("arn") String arn, @RequestParam("subject") String subject,
                             @RequestParam("content") String content) throws URISyntaxException {

        AmazonSNS snsClient = getSnsBuilder();

        final String msg = "This Stack Abuse Demo email works!";

        final PublishRequest publishRequest = PublishRequest.builder()
                .topicArn(arn)
                .subject(subject)
                .message(content)
                .build();

//        PublishResult publishResponse = snsClient.publish(publishRequest);
//
//        if (publishResponse.sdkHttpResponse().isSuccessful()) {
//            System.out.println("Message publishing successful");
//        } else {
//            throw new ResponseStatusException(
//                    HttpStatus.INTERNAL_SERVER_ERROR, publishResponse.sdkHttpResponse().statusText().get());
//        }
//
//        snsClient.close();
        return "Email sent to subscribers. Message-ID: ";
    }


    public static void main(String[] args) {

        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAUHW7W74BQUY3Y5MY",
                "isjJe+f/SOs39nMnWnBs7HYcScu2/zBU+TSqa74f");

        AmazonSNS snsClient = AmazonSNSClient
                .builder()
                .withRegion(Region.EU_London.toString())
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

        // Create an Amazon SNS topic.
        final CreateTopicRequest createTopicRequest = new CreateTopicRequest("MyTopic");
        final CreateTopicResult createTopicResponse = snsClient.createTopic(createTopicRequest);

        // Print the topic ARN.
        System.out.println("TopicArn:" + createTopicResponse.getTopicArn());

        // Print the request ID for the CreateTopicRequest action.
        System.out.println("CreateTopicRequest: " + snsClient.getCachedResponseMetadata(createTopicRequest));


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
