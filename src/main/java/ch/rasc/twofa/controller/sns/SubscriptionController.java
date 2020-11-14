package ch.rasc.twofa.controller.sns;

import ch.rasc.twofa.dao.SubscriptionRepository;
import ch.rasc.twofa.dao.TopicRepository;
import ch.rasc.twofa.entity.Subscription;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Controller
public class SubscriptionController {

    private final SubscriptionRepository subscriptionRepository;

    private final TopicRepository topicRepository;

    public SubscriptionController(SubscriptionRepository subscriptionRepository, TopicRepository topicRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.topicRepository = topicRepository;
    }

    @GetMapping("/subs")
    public String subscription(Model model) {
        ArrayList<Subscription> subscriptions = subscriptionRepository.findAll();
        List<String> topicNames = new LinkedList<>();
        for (Subscription subscription : subscriptions) {
            String topicName = topicRepository.findTopicNameById(subscription.getTopicId());
            topicNames.add(topicName);
        }
        model.addAttribute("subs", subscriptions);
        model.addAttribute("names", topicNames);
        return "sns/sns_subs";
    }

    



}
