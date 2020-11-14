package ch.rasc.twofa.security;

import com.codahale.passpol.BreachDatabase;
import com.codahale.passpol.PasswordPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity // to enable Spring Security’s web security support and provide the Spring MVC integration.
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /*disabled the Spring Boot auto-configuration of Spring Security with the following code.
    * The application still uses Spring Security for authorization.
    *  This code only disables the authentication part of Spring Security.
    * */
    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return authentication -> {
            throw new AuthenticationServiceException("Cannot authenticate " + authentication);
        };
    }

    /*The application uses Argon2 for password hashing.*/
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 32, 8, 1 << 16, 4);
    }

    @Bean
    public PasswordPolicy passwordPolicy() {
        return new PasswordPolicy(BreachDatabase.top100K(), 8, 256);
    }
    /*This demo leverages the traditional HTTP session with session cookie approach.
    This is not a requirement for TOTP and you can use other authentication workflows like JWT.*/

    /*The code then configures a list of endpoints that don't need authentication.
     These are all part of the sign-up and sign-in workflow. */

    /*Lastly, the application configures the logout handler.
    This handler by defaults sends back a redirect request, but for single-page applications,
    it is easier when the endpoint just returns an HTTP status code. */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf(customizer -> customizer.disable()).authorizeRequests(customizer -> {
            customizer
                    .antMatchers("/authenticate", "/signin", "/verify-totp",
                            "/verify-totp-additional-security", "/signup", "/signup-confirm-secret", "/test", "/signupme",
                            "/jte/getUsers", "/jte/queryAll", "/invokejob", "/sendemail", "/Quartz", "/shutdown","/resume","/del")
                    .permitAll().anyRequest().authenticated();
        }).logout().logoutRequestMatcher(new AntPathRequestMatcher("/signout")).logoutSuccessUrl("/");

//                logout(customizer -> customizer
//                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler()));
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/", "/assets/**/*", "/svg/**/*", "/*.br", "/*.gz",
                "/*.html", "/*.js", "/**/*.css", "/*.woff2", "/*.ttf", "/*.eot",
                "/*.svg", "/*.woff", "/*.ico", "/webjars/**");
    }

}
