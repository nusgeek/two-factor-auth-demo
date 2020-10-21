package ch.rasc.twofa.entity;

import java.util.Date;

public class UserEmailContent {
    private User user;
    private String CSVFile;
    private Date date;

    public UserEmailContent () {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCSVFile() {
        return CSVFile;
    }

    public void setCSVFile(String CSVFile) {
        this.CSVFile = CSVFile;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "UserEmailContent{" +
                "user=" + user +
                ", CSVFile='" + CSVFile + '\'' +
                ", date=" + date +
                '}';
    }
}
