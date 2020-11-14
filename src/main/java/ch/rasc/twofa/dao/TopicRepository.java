package ch.rasc.twofa.dao;

import ch.rasc.twofa.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {
    ArrayList<Topic> findAll();

    Topic findByTopicName(String name);

    @Query("select t.topicName from Topic t where t.id=?1")
    String findTopicNameById(Integer id);
}
