package ch.rasc.twofa.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "subscription")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "subscription_id")
    private String subscriptionId;

    @Column(name = "endpoint")
    private String endpoint;

    @Column(name ="status")
    private boolean status;

    @Column(name = "protocol")
    private String protocol;

    @Column(name = "create_date")
    private Timestamp creationDate;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "topic_id")
    private Integer topicId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", subscriptionId='" + subscriptionId + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", status=" + status +
                ", protocol='" + protocol + '\'' +
                ", creationDate=" + creationDate +
                ", isDeleted=" + isDeleted +
                ", topicId=" + topicId +
                '}';
    }
}
