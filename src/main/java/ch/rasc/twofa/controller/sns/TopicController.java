package ch.rasc.twofa.controller.sns;

import ch.rasc.twofa.dao.TopicRepository;
import ch.rasc.twofa.entity.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.util.Date;

@Controller
public class TopicController {

    @Autowired
    TopicRepository topicRepository;


    public TopicController() {    }

    @GetMapping("/topic")
    public ModelAndView showTopic() {
        return new ModelAndView("sns/sns_topic");
    }

    @GetMapping("/toCreateTopicPage")
    public ModelAndView toCreateTopicPage() {
        return new ModelAndView("sns/sns_topic_add");
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

}
