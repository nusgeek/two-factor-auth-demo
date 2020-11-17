package ch.rasc.twofa.controller.sns;

import ch.rasc.twofa.dao.SubscriptionRepository;
import ch.rasc.twofa.dao.TopicRepository;
import ch.rasc.twofa.entity.Subscription;
import ch.rasc.twofa.util.GenerateRandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.util.*;

import static java.util.Map.entry;

@Controller
public class SubscriptionController {

    public static final List<String> PROTOCOL = Arrays.asList("HTTP", "HTTPS", "Email", "SNS", "Amazon SQS",
            "AWS Lambda");

    public static final Map<String, String> ENDPOINT_MAP = Map.ofEntries(
            entry("HTTP", "http://www.example.com"),
            entry("HTTPS", "https://www.example.com"),
            entry("Email", "test@example.com"),
            entry("SMS", "+12223334444"),
            entry("Amazon SQS", "arn:aws:sqs:us-east-1:123456789012:MyQueue"),
            entry("AWS Lambda", "arn:aws:lambda:us-east-1:123456789012:function:MyLambdaFunction"));

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private TopicRepository topicRepository;

    public SubscriptionController() {}

    public SubscriptionController(SubscriptionRepository subscriptionRepository, TopicRepository topicRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.topicRepository = topicRepository;
    }

    @GetMapping("/subs")
    public String subscription(Model model, @ModelAttribute("classes") String classes, @ModelAttribute("msg") String msg) {
        ArrayList<Subscription> subscriptions = subscriptionRepository.findAllNotDeleted();
        List<String> topicNames = getTopicNamesOfSubs(subscriptions);
        model.addAttribute("subs", subscriptions);
        model.addAttribute("names", topicNames);
        model.addAttribute("classes", classes);
        model.addAttribute("msg", msg);
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
    public String addOneSub(@RequestParam String topicArnSelector, @RequestParam String subProtocol,
                            @RequestParam String subEndpoint, RedirectAttributes redirectAttributes) {

        Timestamp timestamp = new Timestamp(new Date().getTime());
        String subscriptionId = UUID.randomUUID().toString();
        Integer topicId = topicRepository.findIdByTopicArn(topicArnSelector);

        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(subscriptionId);
        subscription.setEndpoint(subEndpoint);
        subscription.setProtocol(subProtocol);
        subscription.setTopicId(topicId);
        subscription.setStatus(true);
        subscription.setCreationDate(timestamp);
        subscriptionRepository.save(subscription);

        redirectAttributes.addAttribute("classes", "msg-div green-msg");
        redirectAttributes.addAttribute("msg", "You have successfully added the subscription: " + subscriptionId);


        return "redirect:/subs";
    }

    @GetMapping("/deleteOneSub")
    public String deleteOneSub(@RequestParam Integer id, RedirectAttributes redirectAttributes) {
        Subscription subscription = subscriptionRepository.getOne(id);
        subscription.setIsDeleted(true);
        subscriptionRepository.save(subscription);

        redirectAttributes.addAttribute("classes", "msg-div red-msg");
        redirectAttributes.addAttribute("msg", "You have successfully deleted the subscription: " +
                subscription.getSubscriptionId());
        return "redirect:/subs";
    }

    @PostMapping("/editOneSub")
    public String editOneSub() {
        return "";
    }

    public List<String> getTopicNamesOfSubs(ArrayList<Subscription> subscriptions) {
        List<String> topicNames = new LinkedList<>();
        for (Subscription subscription : subscriptions) {
            String topicName = topicRepository.findTopicNameById(subscription.getTopicId());
            if (topicName == null || topicName.isEmpty()) {
                topicName = "no subscription found";
            }
            topicNames.add(topicName);
        }
        return topicNames;
    }

}
