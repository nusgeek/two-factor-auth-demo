package ch.rasc.twofa.awss3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:aws.properties")
public class ClientConnection {

    @Value("${accessKey}")
    private static String accessKey;

    @Value("${secretKey}")
    private static String secretKey;

    static AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

    static AmazonS3 s3client = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.AP_SOUTHEAST_1)
            .build();

    public static void main(String[] args) {
        String bucketName = "second-bucket-test";

        if(s3client.doesBucketExistV2(bucketName)) {
            System.err.println("The bucket name is existed.");
            return;
        }

        s3client.createBucket(bucketName);

    }

}
