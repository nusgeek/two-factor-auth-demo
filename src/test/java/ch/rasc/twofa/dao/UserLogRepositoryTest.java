package ch.rasc.twofa.dao;

import ch.rasc.twofa.Application;
import ch.rasc.twofa.awss3.AWSS3Service;
import com.amazonaws.services.s3.AmazonS3;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = Application.class)
@TestPropertySource( locations = "classpath:application-test.properties" )
public class UserLogRepositoryTest {


    @Mock
    private AmazonS3 s3;

    private AWSS3Service s3Service;

    @Before
    public void setupMock()
    {
        MockitoAnnotations.initMocks( this );
        s3Service = new AWSS3Service(s3);
    }

    @Test
    public void findByLoginTimeBetween() {

    }
}