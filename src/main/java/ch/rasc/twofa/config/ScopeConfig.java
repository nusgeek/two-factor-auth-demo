package ch.rasc.twofa.config;

import ch.rasc.twofa.util.UserInfoGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.annotation.SessionScope;

@Configuration
public class ScopeConfig {

    @Bean
    @SessionScope
    public UserInfoGenerator sessionScopedBean() {
        return new UserInfoGenerator();
    }
}
