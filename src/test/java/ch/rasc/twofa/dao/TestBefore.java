package ch.rasc.twofa.dao;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith( SpringRunner.class )
@TestPropertySource( locations = "classpath:application-test.properties" )
public class TestBefore {

    @BeforeClass
    public static void beforeClass() {
        System.out.println("@BeforeClass");
    }

    @Before
    public void before() {
        System.out.println("@Before");
    }

    @Test
    public void test() {
        System.out.println("@Test");
    }

    @After
    public void after() {
        System.out.println("@After");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("@AfterClass");
    }

}