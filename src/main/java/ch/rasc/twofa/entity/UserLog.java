package ch.rasc.twofa.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "user_login_log")
public class UserLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer logId;

    @Column(name = "username")
    private String username;

    @Column(name = "login_time")
    private Timestamp loginTime;

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Timestamp loginTime) {
        this.loginTime = loginTime;
    }

    @Override
    public String toString() {
        return "UserLog{" +
                "logId=" + logId +
                ", username='" + username + '\'' +
                ", loginTime=" + loginTime +
                '}';
    }
}


