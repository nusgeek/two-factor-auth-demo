package ch.rasc.twofa.util.mockdata;

import ch.rasc.twofa.controller.sns.SubscriptionController;
import ch.rasc.twofa.controller.sns.TopicController;
import ch.rasc.twofa.dao.SubscriptionRepository;
import ch.rasc.twofa.dao.TopicRepository;
import ch.rasc.twofa.entity.Subscription;
import ch.rasc.twofa.entity.Topic;
import ch.rasc.twofa.util.GenerateRandomString;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

@Controller
public class MockupUtils {
    private TopicRepository topicRepository;
    private SubscriptionRepository subscriptionRepository;
    private final Integer topicLen = 2;
    private final Integer subscriptionLen = 5;


    public MockupUtils(TopicRepository topicRepository, SubscriptionRepository subscriptionRepository) {
        this.topicRepository = topicRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @GetMapping("/addFakeTopic")
    public String addFakeTopic() {
        for (int i = 0; i < topicLen; i++) {
            String timeStr = String.valueOf(new Date().getTime());
            String str = timeStr.substring(4);
            addOneTopic(str);
        }
        return "redirect:/topic";
    }

    private void addOneTopic(String randStr) {
        Timestamp timestamp = new Timestamp(new Date().getTime());

        Topic topic = new Topic();
        topic.setTopicName("topic-name" + randStr);
        topic.setTopicType(TopicController.TOPIC_TYPE.get(Integer.parseInt(randStr)%2));
        topic.setTopicArn("topic-arn" + randStr);
        topic.setTimestamp(timestamp);

        topicRepository.save(topic);
    }


    @GetMapping("/addFakeSubs")
    public String addFakeSubs() {
        ArrayList<Integer> topicIds = topicRepository.findAllIds();
        Random rand = new Random();
        for (int i = 0; i < subscriptionLen; i++) {
            // get random topic id
            Integer topicId = topicIds.get(rand.nextInt(topicIds.size()));
            // get random str
            String timeStr = String.valueOf(new Date().getTime());
            String str = timeStr.substring(8);

            addOneSub(str, topicId);
        }
        return "redirect:/subs";
    }

    private void addOneSub(String randStr, Integer topicId) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        String protocol = SubscriptionController.PROTOCOL.get(Integer.parseInt(randStr)%6);

        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(GenerateRandomString.getOne());
        subscription.setProtocol(protocol);
        subscription.setStatus(true);
        subscription.setEndpoint(SubscriptionController.ENDPOINT_MAP.get(protocol) + randStr);
        subscription.setCreationDate(timestamp);
        subscription.setTopicId(topicId);


        subscriptionRepository.save(subscription);
    }

}
