package ch.rasc.twofa.util;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import software.amazon.awssdk.regions.Region;

import java.util.HashMap;
import java.util.Map;


public class SmsSenderAWS {
    public static void main(String[] args) {
        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAUHW7W74BQUY3Y5MY",
                "isjJe+f/SOs39nMnWnBs7HYcScu2/zBU+TSqa74f");

        AmazonSNS snsClient = AmazonSNSClient
                .builder()
                .withRegion(Region.AP_SOUTHEAST_1.toString())
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

        String message = "--------------------My SMS message--------------";
        String phoneNumber = "+6585866220";
        Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
        //<set SMS attributes, here it's null>
        sendSMSMessage(snsClient, message, phoneNumber, smsAttributes);
    }

    public static void sendSMSMessage(AmazonSNS snsClient, String message,
                                      String phoneNumber, Map<String, MessageAttributeValue> smsAttributes) {
        PublishResult result = snsClient.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(phoneNumber)
                .withMessageAttributes(smsAttributes));
        System.out.println(result); // Prints the message ID.
    }
}
