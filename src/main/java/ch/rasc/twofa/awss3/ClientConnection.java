//package ch.rasc.twofa.awss3;
//
//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.regions.Regions;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
//
//public class ClientConnection {
//
//    AWSCredentials credentials = new BasicAWSCredentials(
//            "AKIAUHW7W74B4YMPK4MM",
//            "=+lVuP/YHGHY400qJ5CHSDVUCW/M6XGgTb95P7YAo"
//    );
//
//    AmazonS3 s3client = AmazonS3ClientBuilder
//            .standard()
//            .withCredentials(new AWSStaticCredentialsProvider(credentials))
//            .withRegion(Regions.AP_SOUTHEAST_1)
//            .build();
//
//    public void main(String[] args) {
//        String bucketName = "first-bucket-test";
//
//        if(s3client.doesBucketExistV2(bucketName)) {
//            LOG.info("Bucket name is not available."
//                    + " Try again with a different Bucket name.");
//            return;
//        }
//
//        s3client.createBucket(bucketName);
//
//    }
//
//}
