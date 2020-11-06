package ch.rasc.twofa.entity;

import java.sql.Timestamp;

public class UserLoginReportRecord {
    private String username;
    private String roleName;
    private boolean is2FA;
    private Timestamp loginTime;

    public UserLoginReportRecord() {}

    public UserLoginReportRecord(String username, String roleName, String is2FA, Timestamp loginTime) {
        this.username = username;
        this.roleName = roleName;
        if (is2FA == null || is2FA.equals("")) {
            this.is2FA = false;
        } else {
            this.is2FA = true;
        }
        this.loginTime = loginTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public boolean isIs2FA() {
        return is2FA;
    }

    public void setIs2FA(boolean is2FA) {
        this.is2FA = is2FA;
    }

    public Timestamp getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Timestamp loginTime) {
        this.loginTime = loginTime;
    }

    public String createOneRecord() {
        return username + "," + roleName + "," + is2FA + "," + loginTime.toString() + "\n";
    }
}
