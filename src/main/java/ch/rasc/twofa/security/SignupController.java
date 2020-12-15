package ch.rasc.twofa.security;

import javax.validation.constraints.NotEmpty;

import ch.rasc.twofa.dao.UserRepository;
import ch.rasc.twofa.entity.User;
import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Base32;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.passpol.PasswordPolicy;
import com.codahale.passpol.Status;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
public class SignupController {

  private final PasswordEncoder passwordEncoder;

  private final DSLContext dsl;

  private final PasswordPolicy passwordPolicy;



  @Value("${password.minUppercaseNum}")
  private int minUppercaseNum;

  public SignupController(PasswordEncoder passwordEncoder, PasswordPolicy passwordPolicy,
      DSLContext dsl) {
    this.passwordEncoder = passwordEncoder;
    this.passwordPolicy = passwordPolicy;
    this.dsl = dsl;
  }
  @Autowired
  private UserRepository userRepository;

  @GetMapping("/signup")
  public String signupRedirect(Model model,
                                     @Value("${password.minLen}") Integer minLen,
                                     @Value("${password.minUppercaseNum}") Integer minUppercaseNum,
                                     @Value("${password.minLowercaseNum}") Integer minLowercaseNum,
                                     @Value("${password.minSpecialCharNum}") Integer minSpecialCharNum,
                                     @Value("${password.minDigitNum}") Integer minDigitNum) {
    model.addAttribute("minLen", minLen)
            .addAttribute("minUppercaseNum", minUppercaseNum)
            .addAttribute("minLowercaseNum", minLowercaseNum)
            .addAttribute("minSpecialCharNum", minSpecialCharNum)
            .addAttribute("minDigitNum", minDigitNum);
    return "signinup/signup";
  }

  @PostMapping("/signup")
  public ModelAndView signup(@RequestParam("username") @NotEmpty String username,
                             @RequestParam("password") @NotEmpty String password,
                             @RequestParam("comfirm_password") @NotEmpty String comfirm_password,
                             @RequestParam("role_name") String role_name,
                             @RequestParam("detail") String detail,
                             @RequestParam("totp") boolean totp) {
    User user = new User();

    Map<String, Object> model = new HashMap<>();
    // cancel if the user is already registered

    int count = userRepository.countAllByUsername(username);
    if (count > 0) {
      model.put("status", "username has been taken");
      return new ModelAndView("signinup/signup", model);
    }

    Status status = this.passwordPolicy.check(password);
    if (status != Status.OK) {
      model.put("password", "weak password, at least 8 characters");
      return new ModelAndView("signinup/signup", model);
    }

    user.setUsername(username);
    user.setPassword(this.passwordEncoder.encode(password));
    user.setAdditionalSecurity(false);
    user.setRoleName(role_name);
    user.setDetail(detail);

    if (totp) {
      String secret = Base32.random();
      user.setEnabled(false);
      user.setSecret(secret);
      userRepository.save(user);
      model.put("secret", "otpauth://totp/" + username + "?secret=" + secret + "&issuer=2fademo");
      model.put("username", username);
      return new ModelAndView("signinup/signup_secret", model);
    }

    user.setPassword(this.passwordEncoder.encode(password));
    user.setEnabled(true);
    user.setSecret(null);
    userRepository.save(user);

    return new ModelAndView("signinup/signup_success", model);
  }

  @PostMapping("/signup-confirm-secret")
  public ModelAndView signupConfirmSecret(@RequestParam("username") String username,
                                 @RequestParam("code") @NotEmpty String code) {
    User user = userRepository.findByUsername(username);

//    var record = this.dsl.select(APP_USER.ID, APP_USER.SECRET).from(APP_USER)
//        .where(APP_USER.USERNAME.eq(username)).fetchOne();

    if (user != null) {
      String secret = user.getSecret();
      Totp totp = new Totp(secret);
      if (totp.verify(code)) {
        user.setEnabled(true);
        userRepository.save(user);
//        this.dsl.update(APP_USER).set(APP_USER.ENABLED, true)
//            .where(APP_USER.ID.eq(record.get(APP_USER.ID))).execute();
        return new ModelAndView("signinup/signup_success");
      }
    }
    Map<String, Object> model = new HashMap<>();
    model.put("secret", "otpauth://totp/" + username + "?secret=" + user.getSecret() + "&issuer=2fademo");
    model.put("alert", "please use Google Authenticator, scan QR code again and input correct 6 digits");
    model.put("username", username);
    return new ModelAndView("signinup/signup_secret",model);
  }

}
