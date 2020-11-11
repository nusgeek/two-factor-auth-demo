package ch.rasc.twofa.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "topic")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "topic_name")
    private String topicName;

    @Column(name = "topic_type")
    private String topicType;

    @Column(name = "topic_arn")
    private String topicArn;

    @Column(name = "create_date")
    private Timestamp timestamp;

    public Topic() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicType() {
        return topicType;
    }

    public void setTopicType(String topicType) {
        this.topicType = topicType;
    }

    public String getTopicArn() {
        return topicArn;
    }

    public void setTopicArn(String topicArn) {
        this.topicArn = topicArn;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "id=" + id +
                ", topicName='" + topicName + '\'' +
                ", topicType='" + topicType + '\'' +
                ", topicArn='" + topicArn + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
