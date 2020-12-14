package ch.rasc.twofa.security;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ch.rasc.twofa.dao.UserLogRepository;
import ch.rasc.twofa.dao.UserRepository;
import ch.rasc.twofa.entity.User;
import ch.rasc.twofa.entity.UserLog;
import ch.rasc.twofa.util.UserInfoGenerator;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class AuthController {

  @Resource(name = "sessionScopedBean")
  UserInfoGenerator sessionScopedBean;

  private final static String USER_AUTHENTICATION_OBJECT = "USER_AUTHENTICATION_OBJECT";

  private final PasswordEncoder passwordEncoder;

  private final DSLContext dsl;

  private final String userNotFoundEncodedPassword;

  public AuthController(PasswordEncoder passwordEncoder, DSLContext dsl) {
    this.passwordEncoder = passwordEncoder;
    this.dsl = dsl;
    this.userNotFoundEncodedPassword = this.passwordEncoder
        .encode("userNotFoundPassword");
  }

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserLogRepository userLogRepository;

  @GetMapping("/signin")
  public ModelAndView loginPage() {
    return new ModelAndView("signinup/signin");
  }

  @GetMapping("/home")
  public ModelAndView home(Model model) {
    model.addAttribute("userName", sessionScopedBean.getUserName());
    model.addAttribute("roleName", sessionScopedBean.getRoleName());
    return new ModelAndView("main", "user", model);
  }

  @GetMapping("/authenticate")
  public RedirectView authenticate(HttpServletRequest request, Model model) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth instanceof AppUserAuthentication) {
      Map<String, Object> map = new HashMap<>();

//      AppUserAuthentication appUserAuthentication = (AppUserAuthentication) httpSession.getAttribute(USER_AUTHENTICATION_OBJECT); //appUserAuthentication is null
      AppUserDetail appUserDetail = (AppUserDetail) auth.getPrincipal();
      map.put("username", appUserDetail.getUsername());
      map.put("role_name", appUserDetail.getRoleName());
      map.put("detail", appUserDetail.getDetail());

      String name = sessionScopedBean.getUserName();

      model.addAttribute("info", map);
      return new RedirectView("/home");
    }

    HttpSession httpSession = request.getSession(false);
    if (httpSession != null) {
      httpSession.invalidate();
    }

    return new RedirectView("/signin");
  }

  @PostMapping("/signin")
  public ModelAndView login(@RequestParam String username, @RequestParam String password,
                            HttpSession httpSession, Model model) throws Exception {
    Map<String, Object> map = new HashMap<>();
    User user = userRepository.findByUsername(username);

    if (user != null) {
      boolean pwMatches = this.passwordEncoder.matches(password, user.getPassword());
      if (pwMatches && user.isEnabled()) {

        AppUserDetail detail = new AppUserDetail(user);
        AppUserAuthentication userAuthentication = new AppUserAuthentication(detail);
        if (isNotBlank(user.getSecret())) {
          httpSession.setAttribute(USER_AUTHENTICATION_OBJECT, userAuthentication);

          if (isUserInAdditionalSecurityMode(detail.getAppUserId())) {
            return new ModelAndView("signinup/signin_additional_check");
          }

          return new ModelAndView("signinup/signin_totp_check");
        }

        SecurityContextHolder.getContext().setAuthentication(userAuthentication);
        /*set httpSession timeout -- time unit: second*/
        httpSession.setMaxInactiveInterval(120);
        /* record login time*/
        recordUserLogin(detail);

        sessionScopedBean.setUserName(detail.getUsername());
        sessionScopedBean.setRoleName(detail.getRoleName());
        model.addAttribute("userName", sessionScopedBean.getUserName());
        model.addAttribute("roleName", sessionScopedBean.getRoleName());

        return new ModelAndView("main", "user", model);
      }
    }
    else {
      this.passwordEncoder.matches(password, this.userNotFoundEncodedPassword);
    }
    map.put("reminder", "username or password is not correct");
    return new ModelAndView("signinup/signin", map);
  }

  @PostMapping("/verify-totp")
  public ModelAndView totp(@RequestParam String code, HttpSession httpSession, Model model) {
    Map<String, Object> map = new HashMap<>();
    /*The /verify-totp receives the TOTP code and checks if an AppUserAuthentication instance is stored in the HTTP session.*/
    AppUserAuthentication userAuthentication = (AppUserAuthentication) httpSession
        .getAttribute(USER_AUTHENTICATION_OBJECT);
    if (userAuthentication == null) {
      map.put("noAuth", "Please sign in with username and password first.");
      return new ModelAndView("signinup/signin", map);
    }

    /*handler has to check if the user is in "additional verification" mode.*/
    AppUserDetail detail = (AppUserDetail) userAuthentication.getPrincipal();
    if (isUserInAdditionalSecurityMode(detail.getAppUserId())) {
      return new ModelAndView("signinup/signin_additional_check");
    }

    String secret = ((AppUserDetail) userAuthentication.getPrincipal()).getSecret();
    if (isNotBlank(secret) && isNotBlank(code)) {
      CustomTotp totp = new CustomTotp(secret);
      if (totp.verify(code, 2, 2).isValid()) {
        SecurityContextHolder.getContext().setAuthentication(userAuthentication);
        /* record login time*/
        recordUserLogin(detail);
        /* transfer data to frontend*/
        map = returnViewPara(detail);
        model.addAttribute("user", map);
        return new ModelAndView("main");
      }

      setAdditionalSecurityFlag(detail.getAppUserId());
      return new ModelAndView("signinup/signin_additional_check");
    }

    map.put("noAuth", "Please re-signin");
    return new ModelAndView("/", map);
  }

  @PostMapping("/verify-totp-additional-security")
  public ModelAndView verifyTotpAdditionalSecurity(
      @RequestParam String code1, @RequestParam String code2, @RequestParam String code3,
      HttpSession httpSession) {
    Map<String, Object> map = new HashMap<>();
    AppUserAuthentication userAuthentication = (AppUserAuthentication) httpSession
        .getAttribute(USER_AUTHENTICATION_OBJECT);
    if (userAuthentication == null) {
      map.put("noAuth", "Please sign in with username and password first.");
      return new ModelAndView("signinup/signin", map);
    }

    if (code1.equals(code2) || code1.equals(code3) || code2.equals(code3)) {
      map.put("noAuth", "Please re-signin");
      return new ModelAndView("signinup/signin", map);
    }

    String secret = ((AppUserDetail) userAuthentication.getPrincipal()).getSecret();
    if (isNotBlank(secret) && isNotBlank(code1) && isNotBlank(code2)
        && isNotBlank(code3)) {
      CustomTotp totp = new CustomTotp(secret);

      // check 25 hours into the past and future.
      long noOf30SecondsIntervals = TimeUnit.HOURS.toSeconds(25) / 30;
      CustomTotp.Result result = totp.verify(List.of(code1, code2, code3),
          noOf30SecondsIntervals, noOf30SecondsIntervals);
      if (result.isValid()) {
        if (result.getShift() > 2 || result.getShift() < -2) {
          httpSession.setAttribute("signinup/totp-shift", result.getShift());
        }

        AppUserDetail detail = (AppUserDetail) userAuthentication.getPrincipal();
        clearAdditionalSecurityFlag(detail.getAppUserId());
        httpSession.removeAttribute(USER_AUTHENTICATION_OBJECT);
        /* record user login */
        recordUserLogin(detail);

        SecurityContextHolder.getContext().setAuthentication(userAuthentication);
        /* transfer data to frontend*/
        map = returnViewPara(detail);
        return new ModelAndView("main", map);
      }
    }
    map.put("noAuth", "Please re-signin");
    return new ModelAndView("signinup/signin", map);
  }

  @GetMapping("/totp-shift")
  public String getTotpShift(HttpSession httpSession) {
    Long shift = (Long) httpSession.getAttribute("totp-shift");
    if (shift == null) {
      return null;
    }
    httpSession.removeAttribute("totp-shift");

    StringBuilder out = new StringBuilder();
    long total30Seconds = (int) Math.abs(shift);
    long hours = total30Seconds / 120;
    total30Seconds = total30Seconds % 120;
    long minutes = total30Seconds / 2;
    boolean seconds = total30Seconds % 2 != 0;

    if (hours == 1) {
      out.append("1 hour ");
    }
    else if (hours > 1) {
      out.append(hours).append(" hours ");
    }

    if (minutes == 1) {
      out.append("1 minute ");
    }
    else if (minutes > 1) {
      out.append(minutes).append(" minutes ");
    }

    if (seconds) {
      out.append("30 seconds ");
    }

    return out.append(shift < 0 ? "behind" : "ahead").toString();
  }

  private static boolean isNotBlank(String str) {
    return str != null && !str.isBlank();
  }

  private Boolean isUserInAdditionalSecurityMode(Integer appUserId) {
    return userRepository.findAllById(appUserId).isAdditionalSecurity();
  }

  private void setAdditionalSecurityFlag(Integer appUserId) {
    userRepository.findAllById(appUserId).setAdditionalSecurity(true);
  }

  private void clearAdditionalSecurityFlag(Integer appUserId) {
    userRepository.findAllById(appUserId).setAdditionalSecurity(false);
  }

  private void recordUserLogin(AppUserDetail detail) {
    Timestamp timestamp = new Timestamp(new Date().getTime());
    UserLog userLog = new UserLog();
    userLog.setUsername(detail.getUsername());
    userLog.setLoginTime(timestamp);
    userLogRepository.save(userLog);
  }

  private Map<String, Object> returnViewPara(AppUserDetail detail) {
    Map<String, Object> map = new HashMap<>();
    map.put("userName", detail.getUsername());
    map.put("role_name", detail.getRoleName());
    map.put("detail", detail.getDetail());
    return map;
  }
}
