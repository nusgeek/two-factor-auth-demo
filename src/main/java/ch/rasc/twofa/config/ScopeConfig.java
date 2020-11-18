package ch.rasc.twofa.config;

import ch.rasc.twofa.util.UserInfoGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class ScopeConfig {

    @Bean
    @RequestScope
    public UserInfoGenerator sessionScopedBean() {
        return new UserInfoGenerator();
    }
}
