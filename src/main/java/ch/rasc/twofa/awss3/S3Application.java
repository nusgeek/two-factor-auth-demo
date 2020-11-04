package ch.rasc.twofa.awss3;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@PropertySource("classpath:aws.properties")
public class S3Application {
    @Value("${accessKey}")
    private static String accessKey;

    @Value("${secretKey}")
    private static String secretKey;

    private static final AWSCredentials credentials;
    private static String bucketName;

    static {
        //put your accesskey and secretkey here
        credentials = new BasicAWSCredentials(accessKey, secretKey);
    }

    public static void createBucket(String bucketName, AWSS3Service service) {
        //creating a bucket
        if(service.doesBucketExist(bucketName)) {
            System.err.println("Bucket name is not available."
                    + " Try again with a different Bucket name.");
            return;
        }
        service.createBucket(bucketName);
    }

    public static void main(String[] args) throws IOException {
        //set-up the client
        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.AP_SOUTHEAST_1)
                .build();

        AWSS3Service awsService = new AWSS3Service(s3client);

        bucketName = "myawsbucket-test-test-test";


        //creating a bucket
        createBucket(bucketName, awsService);

        //list all the buckets
        for(Bucket s : awsService.listBuckets() ) {
            System.out.println(s.getName());
        }

        //deleting bucket
        awsService.deleteBucket(bucketName);

        //uploading object
        String fileLocation = "src/main/resources/images/test.png";
        awsService.putObject(
                bucketName,
                "test.png",
                new File(fileLocation)
        );

        // uploading objects
        /**
         * @author: kl
         */
        Map<String, File> objectsMap = new HashMap<>();
        String defaultDir = "src/main/resources/images/";
        objectsMap.put("HTML/test.html", new File(defaultDir + "test.html"));
        objectsMap.put("Images/test.png", new File(defaultDir + "test.png"));
        objectsMap.put("Images/test-downloaded.png", new File(defaultDir + "test-downloaded.png"));
        awsService.putObjects(bucketName, objectsMap);

        //listing objects
        ObjectListing objectListing = awsService.listObjects(bucketName);
        for(S3ObjectSummary os : objectListing.getObjectSummaries()) {
            System.out.println(os.getKey());
        }

        //downloading an object
        S3Object s3object = awsService.getObject(bucketName, "test.png");
        try (S3ObjectInputStream inputStream = s3object.getObjectContent();
            OutputStream outputStream = new FileOutputStream("src/main/resources/images/test-downloaded2.png")
        ) {
            byte[] buf = new byte[1024];
            int count;
            while((count = inputStream.read(buf)) != -1) {
                if (Thread.interrupted()) {
                    throw new InterruptedIOException();
                }
                outputStream.write(buf, 0, count);
            }
        }

        //copying an object
        awsService.copyObject(
                bucketName,
                "test.png",
                "my-bucket-test-identical",
                "Document/picture.png"
        );

        //deleting an object
        DeleteObjectRequest request = new DeleteObjectRequest(bucketName, "demo.zip");
        s3client.deleteObject(request);

        //deleting multiple objects
        String objkeyArr[] = {
                "lou-springboot-03.zip",
                "test.png"
        };

        DeleteObjectsRequest delObjReq = new DeleteObjectsRequest("myawsbucket-test-test")
                .withKeys(objkeyArr);
        awsService.deleteObjects(delObjReq);
    }
}
