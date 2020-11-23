package ch.rasc.twofa.entity;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "User name can not be blank")
    @Size(min = 6, max = 30)
    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "secret")
    private String secret;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "additional_security")
    private boolean additionalSecurity;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "detail")
    private String detail;

    public User() {  }

    public Integer getId() {
        return id;
    }

    public void setId() {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAdditionalSecurity() {
        return additionalSecurity;
    }

    public void setAdditionalSecurity(boolean additionalSecurity) {
        this.additionalSecurity = additionalSecurity;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", secret='" + secret + '\'' +
                ", enabled=" + enabled +
                ", additionalSecurity=" + additionalSecurity +
                ", roleName='" + roleName + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }
}
