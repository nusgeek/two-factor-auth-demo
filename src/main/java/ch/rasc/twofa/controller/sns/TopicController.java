package ch.rasc.twofa.controller.sns;

import ch.rasc.twofa.dao.SubscriptionRepository;
import ch.rasc.twofa.dao.TopicRepository;
import ch.rasc.twofa.entity.Subscription;
import ch.rasc.twofa.entity.Topic;
import com.amazonaws.services.s3.model.ObjectListing;
import org.jooq.tools.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

@Controller
public class TopicController {

    public static final List<String> TOPIC_TYPE = Arrays.asList("Standard", "FIFO");

    private final TopicRepository topicRepository;

    private final SubscriptionRepository subscriptionRepository;


    public TopicController(TopicRepository topicRepository, SubscriptionRepository subscriptionRepository) {
        this.topicRepository = topicRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @GetMapping("/topic")
    public String showTopic(Model model, @ModelAttribute("classes") String classes,
                            @ModelAttribute("msg") String msg) throws ParseException {

        model.addAttribute("classes", classes);
        model.addAttribute("msg", msg);
        model.addAttribute("topics", topicRepository.findAllNotDeleted());
        return "sns/sns_topic";
    }

    @GetMapping("/toCreateTopicPage")
    public ModelAndView toCreateTopicPage() {
        return new ModelAndView("sns/sns_topic_add");
    }

    @PostMapping("/toEditTopicPage")
    public ModelAndView toEditTopicPage(@RequestParam String topicName,
                                        @RequestParam String topicType,
                                        @RequestParam String topicArn,
                                        @RequestParam Integer id,
                                        Model model) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("topicName", topicName);
        dataModel.put("topicType", topicType);
        dataModel.put("topicArn", topicArn);
        dataModel.put("id", id);
        model.addAttribute("types", TopicController.TOPIC_TYPE);
        return new ModelAndView("sns/sns_topic_edit", dataModel);
    }

    @RequestMapping("/topicDetail")
    public String toTopicDetailPage(@RequestParam Integer id, Model model) {
        Topic topic = topicRepository.getOne(id);
        ArrayList<Subscription> subscriptions = subscriptionRepository.findAllByTopicId(id);

        SubscriptionController subscriptionController = new SubscriptionController(subscriptionRepository,
                topicRepository);
        List<String> names = subscriptionController.getTopicNamesOfSubs(subscriptions);

        model.addAttribute("topic", topic);
        model.addAttribute("subs", subscriptions);
        model.addAttribute("names", names);

        return "sns/sns_topic_detail";
    }

    @PostMapping("/addOneTopic")
    public String addOneTopic(@RequestParam String topicName,
                              @RequestParam String topicType,
                              @RequestParam String topicArn,
                              RedirectAttributes redirectAttributes) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        Topic topic = new Topic();
        topic.setTopicName(topicName);
        topic.setTopicType(topicType);
        topic.setTopicArn(topicArn);
        topic.setTimestamp(timestamp);
        topicRepository.save(topic);

        redirectAttributes.addAttribute("classes", "msg-div green-msg");
        redirectAttributes.addAttribute("msg", "You have successfully added the topic: " + topicName);
        return "redirect:/topic";
    }


    @GetMapping("/deleteOneTopic")
    public String deleteOneTopic(@RequestParam Integer id, RedirectAttributes redirectAttributes) {
        /* use update in this way instead of query in Repository*/
        Topic topic = topicRepository.getOne(id);
        topic.setIsDeleted(true);
        topicRepository.save(topic);

        redirectAttributes.addAttribute("classes", "msg-div red-msg");
        redirectAttributes.addAttribute("msg", "You have successfully deleted the topic: " + topic.getTopicName());
        return "redirect:/topic";
    }

    @PostMapping("/editOneTopic")
    public String editOneTopic(@RequestParam String topicName, @RequestParam String topicType,
                               @RequestParam String topicArn, @RequestParam Integer id,
                               RedirectAttributes redirectAttributes) {
        Optional<Topic> oTopic = topicRepository.findById(id);
        Topic topic = oTopic.orElseGet(Topic::new);
        topic.setTopicArn(topicArn);
        topic.setTopicType(topicType);
        topic.setTopicName(topicName);
        topicRepository.save(topic);

        redirectAttributes.addAttribute("classes", "msg-div orange-msg");
        redirectAttributes.addAttribute("msg", "You have successfully updated the topic: " + topicName);
        return "redirect:/topic";
    }

}
