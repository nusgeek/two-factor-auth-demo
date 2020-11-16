package ch.rasc.twofa.dao;

import ch.rasc.twofa.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {
    @Query("select t from Topic t where t.isDeleted=false")
    ArrayList<Topic> findAllNotDeleted();

    Topic findByTopicName(String name);

    @Query("select t.topicName from Topic t where t.id=?1 and t.isDeleted=false ")
    String findTopicNameById(Integer id);

//    @Query("update Topic t set t.isDeleted = true where t.id=:id")
//    void deleteByIdSelf(@Param("id")Integer id);

    @Query("select t.topicArn from Topic t")
    ArrayList<String> findAllTopicArn();

    @Query("select t.id from Topic t where t.topicArn = ?1")
    Integer findIdByTopicArn(String topicArn);
}