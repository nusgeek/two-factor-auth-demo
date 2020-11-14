package ch.rasc.twofa.controller.sns;

import ch.rasc.twofa.dao.TopicRepository;
import ch.rasc.twofa.entity.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

@Controller
public class TopicController {

    private static final List<String> TOPIC_TYPE = Arrays.asList("Standard", "FIFO");

//    @Autowired
    private final TopicRepository topicRepository;


    public TopicController(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @GetMapping("/topic")
    public String showTopic(Model model) throws ParseException {
        model.addAttribute("topics", topicRepository.findAll());
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

    @PostMapping("/addOneTopic")
    public String addOneTopic(@RequestParam String topicName,
                              @RequestParam String topicType,
                              @RequestParam String topicArn) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        Topic topic = new Topic();
        topic.setTopicName(topicName);
        topic.setTopicType(topicType);
        topic.setTopicArn(topicArn);
        topic.setTimestamp(timestamp);
        topicRepository.save(topic);
        return "redirect:/topic";
    }


    @GetMapping("/deleteOneTopic")
    public String deleteOneTopic(@RequestParam int id) {
        topicRepository.deleteById(id);
        return "redirect:/topic";
    }

    @PostMapping("/editOneTopic")
    public String editOneTopic(@RequestParam String topicName,
                               @RequestParam String topicType,
                               @RequestParam String topicArn,
                               @RequestParam Integer id) {
        Optional<Topic> oTopic = topicRepository.findById(id);
        Topic topic = oTopic.orElseGet(Topic::new);
        topic.setTopicArn(topicArn);
        topic.setTopicType(topicType);
        topic.setTopicName(topicName);
        topicRepository.save(topic);
        return "redirect:/topic";
    }
}
