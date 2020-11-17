package ch.rasc.twofa.config;

import ch.rasc.twofa.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;

@Configuration
public class ScopeConfig {

    @Bean
    @SessionScope
    public User userInSession() {
        return new User();
    }
}
