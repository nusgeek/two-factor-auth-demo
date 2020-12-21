package ch.rasc.twofa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@ServletComponentScan
public class Application {

  public static final Logger log = LoggerFactory.getLogger("app");

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}

@RefreshScope
@RestController
class MessageRestController {

  @Value("${message:Hello default}")
  private String message;

  @RequestMapping("/message")
  String getMessage() {
    return this.message;
  }
}