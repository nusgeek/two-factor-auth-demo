package ch.rasc.twofa.dao;

import ch.rasc.twofa.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    ArrayList<Subscription> findAll();

}
