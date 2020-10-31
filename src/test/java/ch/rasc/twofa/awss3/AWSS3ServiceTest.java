package ch.rasc.twofa.awss3;


import ch.rasc.twofa.Application;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


//@RunWith( SpringRunner.class )
@ContextConfiguration(classes = Application.class)
@TestPropertySource( locations = "classpath:application-test.properties" )
public class AWSS3ServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private AmazonS3 s3;

    private AWSS3Service s3Service;


//    private static final AWSCredentials credentials;

    @Before
    public void setupMock()
    {
        MockitoAnnotations.initMocks( this );
        s3Service = new AWSS3Service(s3);
    }

    @Test
    @DisplayName("method to check whether the specific bucket existing")
    public void doesBucketExist() {
        when(s3.doesBucketExistV2( "existed-bucket-name" )).thenReturn(true);
        when(s3.doesBucketExistV2( "not-existed-bucket-name" )).thenReturn( false );
        assertTrue(s3Service.doesBucketExist("existed-bucket-name"));;
        assertFalse(s3Service.doesBucketExist("not-existed-bucket-name"));;
    }

    @Test
    @DisplayName("method to create a specific bucket")
    public void methodToCreateASpecificBucket() throws AmazonServiceException, SdkClientException {
        Bucket bucket1 = new Bucket("bucket-not-exists");
        when(s3.createBucket("bucket-not-exists")).thenReturn(bucket1);
        assertEquals(s3Service.createBucket("bucket-not-exists"), bucket1);

        // bucket name is existed
        when(s3.createBucket("bucket-exists")).thenThrow(new SdkClientException("bucket name existed"));
        when(s3.createBucket("bucket-error")).thenThrow(new AmazonServiceException("amazon service exception"));
        assertThrows(SdkClientException.class, () -> s3Service.createBucket("bucket-exists"));
        assertThrows(AmazonServiceException.class, () -> s3Service.createBucket("bucket-error"));
    }

    @Test
    @DisplayName("test list bucket")
    public void testListBucket() {
        // mockup data

        List<Bucket> entities = new ArrayList<>();
        entities.add(new Bucket("bucket1"));
        entities.add(new Bucket("bucket2"));
        entities.add(new Bucket("bucket3"));

        /* mock service */
        AmazonS3 mockS3 = Mockito.mock( AmazonS3.class );
        AWSS3Service newService = new AWSS3Service(mockS3);

        // or AWSS3Service newService = new AWS3Service(s3);

        when(mockS3.listBuckets()).thenReturn( entities );
        List<Bucket> bucketList = newService.listBuckets();


        assertEquals(entities.size(), bucketList.size());
        for (int i = 0; i < entities.size(); i++) {
            assertEquals(entities.get(i), bucketList.get(i));
        }
    }
//     self defined class, should I provide a bucket if I want to test deleteBucket() method

    @Test
    @DisplayName("test delete bucket method")
    public void testDeleteBucketMethod() {
        // bucket name is existed
        when(s3.createBucket("bucket-exists")).thenThrow(new SdkClientException("bucket name existed"));
        when(s3.createBucket("bucket-error")).thenThrow(new AmazonServiceException("amazon service exception"));
        assertThrows(SdkClientException.class, () -> s3Service.createBucket("bucket-exists"));
        assertThrows(AmazonServiceException.class, () -> s3Service.createBucket("bucket-error"));
    }

    @Test
    @DisplayName("test put object method")
    public void putObject() {
        PutObjectResult result = new PutObjectResult();
        result.setVersionId("id");
        result.setETag("eTag");
        result.setExpirationTime(new Date());
        result.setExpirationTimeRuleId("expirationTimeRuleId");
        result.setContentMd5("contentMd5");
        result.setMetadata(new ObjectMetadata());
        result.setRequesterCharged(true);

        File file = Mockito.mock( File.class );
        when(s3.putObject("buckName", "key", file)).thenReturn(result);

        PutObjectResult methodResult = s3Service.putObject("buckName", "key", file);

        assertEquals(methodResult.getVersionId(), result.getVersionId());
        assertEquals(methodResult.getETag(), result.getETag());
        assertEquals(methodResult.getExpirationTime(), result.getExpirationTime());
        assertEquals(methodResult.getExpirationTime(), result.getExpirationTime());
        assertEquals(methodResult.getContentMd5(), result.getContentMd5());
        assertEquals(methodResult.getMetadata(), result.getMetadata());
        assertEquals(methodResult.isRequesterCharged(), result.isRequesterCharged());

    }

    @Test
    @DisplayName("test put objects method")
    public void testPutObjectsMethod() {
        // different point. focus on loop times.
    }

    @Test
    @DisplayName("test list objects according to bucket name")
    public void testListObjectsAccordingToBucketName() {
        ObjectListing objectListing = new ObjectListing();
        objectListing.setCommonPrefixes(new ArrayList<>());
        objectListing.setBucketName("bucket name");
        objectListing.setNextMarker("next marker");
        objectListing.setTruncated(true);
        objectListing.setPrefix("prefix");
        objectListing.setMarker("marker");
        objectListing.setMaxKeys(1);
        objectListing.setDelimiter("delimiter");
        objectListing.setEncodingType("encoding type");

        when(s3.listObjects("bucket-name")).thenReturn(objectListing);
        ObjectListing listing = s3Service.listObjects("bucket-name");

        assertEquals(objectListing.getCommonPrefixes(), listing.getCommonPrefixes());
        assertEquals(objectListing.getBucketName(), listing.getBucketName());
        assertEquals(objectListing.getNextMarker(), listing.getNextMarker());
        assertEquals(objectListing.getPrefix(), listing.getPrefix());
        assertTrue(listing.isTruncated());
        assertEquals(objectListing.getMarker(), listing.getMarker());
        assertEquals(objectListing.getMaxKeys(), listing.getMaxKeys());
        assertEquals(objectListing.getDelimiter(), listing.getDelimiter());
        assertEquals(objectListing.getEncodingType(), listing.getEncodingType());
    }

    @Test
    @DisplayName("test get object method")
    public void testGetObjectMethod() {

    }

    @Test
    public void copyObject() {
    }

    @Test
    public void deleteObject() {
    }

    @Test
    public void deleteObjects() {
        DeleteObjectsResult.DeletedObject deletedObject1 = new DeleteObjectsResult.DeletedObject();
        DeleteObjectsResult.DeletedObject deletedObject2 = new DeleteObjectsResult.DeletedObject();
        ArrayList<DeleteObjectsResult.DeletedObject> list = new ArrayList<>();
        list.add(deletedObject1);
        list.add(deletedObject2);

        /* two constructors, how to create?? */
        DeleteObjectsResult deleteObjectsResult = new DeleteObjectsResult(list);
        deleteObjectsResult.setRequesterCharged(false);

        DeleteObjectsRequest request = Mockito.mock(DeleteObjectsRequest.class);
        when(s3.deleteObjects(request)).thenReturn(deleteObjectsResult);

        DeleteObjectsResult result = s3Service.deleteObjects(request);

        assertFalse(result.isRequesterCharged());
        assertEquals(deleteObjectsResult.getDeletedObjects(), result.getDeletedObjects());

        // return status
    }
}
