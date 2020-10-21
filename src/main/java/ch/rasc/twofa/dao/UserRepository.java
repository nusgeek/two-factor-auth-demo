package ch.rasc.twofa.dao;

import ch.rasc.twofa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// this is actually DAO interface
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);

    User findAllById(Integer id);

    int countAllByUsername(String username);
}
