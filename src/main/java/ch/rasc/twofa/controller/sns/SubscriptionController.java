package ch.rasc.twofa.controller.sns;

import ch.rasc.twofa.dao.SubscriptionRepository;
import ch.rasc.twofa.dao.TopicRepository;
import ch.rasc.twofa.entity.Subscription;
import ch.rasc.twofa.util.GenerateRandomString;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
        ArrayList<Subscription> subscriptions = subscriptionRepository.findAllNotDeleted();
        List<String> topicNames = new LinkedList<>();
        for (Subscription subscription : subscriptions) {
            String topicName = topicRepository.findTopicNameById(subscription.getTopicId());
            if (topicName == null || topicName.isEmpty()) {
                topicName = "no subscription found";
            }
            topicNames.add(topicName);
        }
        model.addAttribute("subs", subscriptions);
        model.addAttribute("names", topicNames);
        return "sns/sns_subs";
    }

    @GetMapping("/toCreateSubPage")
    public String toCreateSubPage(Model model) {
        ArrayList<String> allTopicArn = topicRepository.findAllTopicArn();
        model.addAttribute("topicArns", allTopicArn);
        return "sns/sns_subs_add";
    }

//    @GetMapping("/toEditSubPage")

    @PostMapping("/addOneSub")
    public String addOneSub(@RequestParam String topicArnSelector,
                            @RequestParam String subProtocol,
                            @RequestParam String subEndpoint) {

        Timestamp timestamp = new Timestamp(new Date().getTime());

        Integer topicId = topicRepository.findIdByTopicArn(topicArnSelector);

        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(GenerateRandomString.getOne());
        subscription.setEndpoint(subEndpoint);
        subscription.setProtocol(subProtocol);
        subscription.setTopicId(topicId);
        subscription.setStatus(true);
        subscription.setCreationDate(timestamp);

        subscriptionRepository.save(subscription);

        return "redirect:/subs";
    }

    @GetMapping("/deleteOneSub")
    public String deleteOneSub(@RequestParam Integer id) {
        Subscription subscription = subscriptionRepository.getOne(id);
        subscription.setIsDeleted(true);
        subscriptionRepository.save(subscription);
        return "redirect:/subs";
    }

    @PostMapping("/editOneSub")
    public String editOneSub() {
        return "";
    }



}
