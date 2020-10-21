package ch.rasc.twofa.dao;

import ch.rasc.twofa.entity.User;
import ch.rasc.twofa.entity.UserLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;

// this is actually DAO interface
@Repository
public interface UserLogRepository extends CrudRepository<UserLog, Integer> {
    ArrayList<UserLog> findByLoginTimeBetween(Timestamp tOld, Timestamp tNew);
}