package ch.rasc.twofa.dao;

import ch.rasc.twofa.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    @Query("select s from Subscription s where s.isDeleted=false")
    ArrayList<Subscription> findAllNotDeleted();

    @Query("select s from Subscription s where s.topicId = ?1 and s.isDeleted = false ")
    ArrayList<Subscription> findAllByTopicId(Integer id);

}
