///*
// *                        SSG Public License Notice
// *
// * This software is the intellectual property of SSG. The program
// * may be used only in accordance with the terms of the license agreement you
// * entered into with SSG.
// *
// * 2019 SkillsFuture Singapore (SSG). All rights reserved.
// * 1 Marina Boulevard
// * #18-01 One Marina Boulevard
// * Singapore 018989
// */
//
//import org.awaitility.Awaitility;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.ExpectedException;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.mockito.invocation.InvocationOnMock;
//import org.mockito.stubbing.Answer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Example;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
//import javax.persistence.OptimisticLockException;
//
//import sg.gov.ssg.devportal.LoginUser;
//import sg.gov.ssg.devportal.common.RoleCategory;
//import sg.gov.ssg.devportal.common.SignupProgress;
//import sg.gov.ssg.devportal.common.UserAcctStatus;
//import sg.gov.ssg.devportal.common.UserIdentity;
//import sg.gov.ssg.devportal.common.UserType;
//import sg.gov.ssg.devportal.repositories.devportal.PrivilegeRepository;
//import sg.gov.ssg.devportal.repositories.devportal.RoleRepository;
//import sg.gov.ssg.devportal.repositories.devportal.UserListingRepository;
//import sg.gov.ssg.devportal.repositories.devportal.UserLoginRepository;
//import sg.gov.ssg.devportal.repositories.devportal.UserRepository;
//import sg.gov.ssg.devportal.repositories.devportal.entities.GithubUserEntity;
//import sg.gov.ssg.devportal.repositories.devportal.entities.RoleEntity;
//import sg.gov.ssg.devportal.repositories.devportal.entities.SysmUserEntity;
//import sg.gov.ssg.devportal.repositories.devportal.entities.SysmUserPasswordEntity;
//import sg.gov.ssg.devportal.repositories.devportal.entities.TykDeveloperEntity;
//import sg.gov.ssg.devportal.repositories.devportal.entities.UserEntity;
//import sg.gov.ssg.devportal.repositories.devportal.entities.UserListingEntity;
//import sg.gov.ssg.devportal.repositories.devportal.entities.UserLoginEntity;
//import sg.gov.ssg.devportal.services.JiraRestService.JiraServiceDeskRecordNotFoundException;
//import sg.gov.ssg.devportal.services.ServiceError.DataStaleException;
//import sg.gov.ssg.devportal.services.ServiceError.RecordNotFoundException;
//import sg.gov.ssg.devportal.services.ServiceError.TykApiException;
//import sg.gov.ssg.devportal.services.TykService.TykDeveloper;
//import sg.gov.ssg.devportal.services.UserService.User;
//import sg.gov.ssg.devportal.services.UserService.UserListing;
//import sg.gov.ssg.devportal.services.impl.UserServiceImpl;
//import sg.gov.ssg.devportal.utils.TykUtils;
//import sg.gov.ssg.devportal.utils.UserUtils;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
//import static org.junit.Assert.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.anyString;
//import static org.mockito.Mockito.when;
//
///**
// * Unit test on user service.
// *
// * @author EeKing
// */
//@RunWith( SpringRunner.class )
//@TestPropertySource( locations = "classpath:application-test.properties" )
//public class UserServiceTest
//{
//    @Value( "${spring.user.login.max.attempts}" )
//    private int maxLoginAttempts;
//
//    @Rule
//    public ExpectedException thrown = ExpectedException.none();
//
//    @Mock
//    private UserRepository userRepo;
//
//    @Mock
//    private RoleRepository roleRepo;
//
//    @Mock
//    private PrivilegeRepository privilegeRepo;
//
//    @Mock
//    private UserLoginRepository userLoginRepo;
//
//    @Mock
//    private TykService tykService;
//
//    @Mock
//    private JiraRestService jiraRestService;
//
//    @Mock
//    private UserListingRepository userListingRepository;
//
//    private UserServiceImpl userService;
//
//    @Before
//    public void setupMock()
//    {
//        MockitoAnnotations.initMocks( this );
//        userService = new UserServiceImpl( roleRepo, userRepo, userListingRepository, userLoginRepo, tykService,
//                jiraRestService );
//    }
//
//    /**
//     * New employee created in database.
//     *
//     * @return UserEntity
//     */
//    private SysmUserEntity newSysmUserEntity()
//    {
//        return newSysmUserEntity( null );
//    }
//
//    /**
//     * New employee created in database.
//     *
//     * @param ref
//     * @return EmployeeEntity
//     */
//    private SysmUserEntity newSysmUserEntity( UUID ref )
//    {
//        SysmUserEntity entity = new SysmUserEntity();
//        entity.setId( 1L );
//        entity.setRef( ref == null ? UUID.randomUUID() : ref );
//        entity.setFullName( "John Doe" );
//        entity.setEmail( "johndoe@gmail.com" );
//
//        entity.setMobileNo( "+6588888888" );
//
//        SysmUserPasswordEntity password = new SysmUserPasswordEntity();
//        password.setPassword( "password" );
//        entity.addPassword( password );
//
//        entity.setDesc( "Remark: New User" );
//        entity.setActivationStatus( UserAcctStatus.ACTIVATED );
//        entity.setEnabled( true );
//
//        RoleEntity role = new RoleEntity();
//        role.setId( 1L );
//        role.setRef( UUID.randomUUID() );
//        role.setName( "ROLE_ORG_DEVELOPER" );
//        role.setCategory( RoleCategory.EXTERNAL );
//        role.setCreatedAt( new Date() );
//        role.setCreatedBy( null );
//        role.setLastModifiedAt( null );
//        role.setLastModifiedBy( null );
//        role.setVersion( new Date() );
//        entity.addRole( role );
//
//        entity.setCreatedAt( new Date() );
//        entity.setCreatedBy( 1L );
//        entity.setLastModifiedAt( null );
//        entity.setLastModifiedBy( null );
//        entity.setVersion( new Date() );
//
//        return entity;
//    }
//
//    private UserLoginEntity newUserLoginEntity()
//    {
//        UserLoginEntity userLogin = new UserLoginEntity();
//        userLogin.setId( 1L );
//        userLogin.setLastSucceededAt( new Date() );
//        userLogin.setLastFailedAt( null );
//        userLogin.setFailedAttempts( 2 );
//        return userLogin;
//    }
//
//    private RoleEntity newRoleEntity( UUID ref, String name, RoleCategory category )
//    {
//        RoleEntity roleEntity = new RoleEntity();
//        roleEntity.setId( 1L );
//        roleEntity.setRef( ref );
//        roleEntity.setName( name );
//        roleEntity.setCategory( category );
//        roleEntity.setCreatedAt( new Date() );
//        roleEntity.setCreatedBy( null );
//        roleEntity.setLastModifiedAt( new Date() );
//        roleEntity.setLastModifiedBy( null );
//        roleEntity.setVersion( new Date() );
//
//        return roleEntity;
//    }
//
//    /**
//     * Get an existing employee with valid id.
//     */
//    @Test
//    public void scenario_01_001() throws RecordNotFoundException
//    {
//        SysmUserEntity entity = newSysmUserEntity();
//        when( userRepo.findOneByRef( any( UUID.class ) ) ).thenReturn( Optional.of( entity ) );
//        UserService.User user = userService.getUser( entity.getRef() );
//        assertEquals( entity.getRef(), user.getId() );
//        assertEquals( entity.getFullName(), user.getFullName() );
//        assertEquals( entity.getEmail(), user.getEmail() );
//        assertEquals( entity.getMobileNo(), user.getMobileNo() );
//        assertEquals( entity.getDesc(), user.getDesc() );
//        assertEquals( entity.getActivationStatus(), user.getActivationStatus() );
//        assertEquals( entity.getEnabled(), user.getEnabled() );
//
//        RoleEntity roleEntity = entity.getRoles().iterator().next();
//        assertNotNull( roleEntity );
//        assertEquals( roleEntity.getRef(), user.getRole().getId() );
//        assertEquals( roleEntity.getName(), user.getRole().getName() );
//
//        assertEquals( entity.getPasswords().iterator().next().getPassword(), user.getPwd() );
//        assertEquals( entity.getCreatedAt(), user.getCreatedAt() );
//        assertEquals( entity.getCreatedBy(), user.getCreatedBy() );
//        assertEquals( entity.getLastModifiedAt(), user.getLastModifiedAt() );
//        assertEquals( entity.getLastModifiedBy(), user.getLastModifiedBy() );
//        assertEquals( entity.getVersion(), user.getVersion() );
//    }
//
//    /**
//     * Get an employee with an ID doesn't exist.
//     *
//     * @throws RecordNotFoundException
//     */
//    @Test
//    public void scenario_01_002() throws RecordNotFoundException
//    {
//        thrown.expect( RecordNotFoundException.class );
//        thrown.expectMessage( "The user could not be found" );
//
//        when( userRepo.findOneByRef( any( UUID.class ) ) ).thenReturn( Optional.empty() );
//        userService.getUser( UUID.randomUUID() );
//    }
//
//    /**
//     * Get all users.
//     *
//     * @throws InterruptedException
//     */
//    @Test
//    public void scenario_02_001() throws InterruptedException
//    {
//        List<UserEntity> entities = new ArrayList<>();
//        for ( int i = 1; i <= 2; i++ )
//        {
//            long current = System.currentTimeMillis();
//
//            SysmUserEntity entity = new SysmUserEntity();
//            entity.setId( Long.valueOf( i ) );
//            entity.setRef( UUID.randomUUID() );
//            entity.setFullName( "John Doe" );
//            entity.setEmail( "johndoe" + i + "@gmail.com" );
//            entity.setMobileNo( "+6588888888" );
//
//            SysmUserPasswordEntity password = new SysmUserPasswordEntity();
//            password.setPassword( "password" );
//            entity.addPassword( password );
//
//            entity.setDesc( "Remark: New User" );
//            entity.setActivationStatus( UserAcctStatus.ACTIVATED );
//            entity.setEnabled( true );
//
//            RoleEntity role = new RoleEntity( "ROLE_ORG_DEVELOPER", RoleCategory.INTERNAL );
//            entity.addRole( role );
//
//            entity.setCreatedAt( new Date( current + 0 ) );
//            entity.setCreatedBy( 1L );
//            entity.setLastModifiedAt( new Date( current + 50 ) );
//            entity.setLastModifiedBy( 2L );
//            entity.setVersion( new Date( current + 100 ) );
//
//            entities.add( entity );
//
//            Awaitility.await().pollDelay( 100, TimeUnit.MILLISECONDS ).until( () -> true );
//        }
//
//        when( userRepo.findAll() ).thenReturn( entities );
//
//        List<UserService.User> users = userService.getUsers();
//
//        assertEquals( entities.size(), users.size() );
//        for ( int i = 0; i < users.size(); i++ )
//        {
//            UserEntity entity = entities.get( i );
//            UserService.User user = users.get( i );
//
//            assertEquals( entity.getRef(), user.getId() );
//            assertEquals( entity.getFullName(), user.getFullName() );
//            assertEquals( entity.getEmail(), user.getEmail() );
//            assertEquals( entity.getEnabled(), user.getEnabled() );
//            assertEquals( ( (SysmUserEntity) entity ).getMobileNo(), user.getMobileNo() );
//            assertEquals( ( (SysmUserEntity) entity ).getDesc(), user.getDesc() );
//            assertEquals( ( (SysmUserEntity) entity ).getActivationStatus(), user.getActivationStatus() );
//
//            RoleEntity roleEntity = entity.getRoles().iterator().next();
//            assertNotNull( roleEntity );
//            assertEquals( roleEntity.getRef(), user.getRole().getId() );
//            assertEquals( roleEntity.getName(), user.getRole().getName() );
//
//            assertEquals( ( (SysmUserEntity) entity ).getPasswords().iterator().next().getPassword(),
//                    user.getPwd() );
//            assertEquals( entity.getCreatedAt(), user.getCreatedAt() );
//            assertEquals( entity.getCreatedBy(), user.getCreatedBy() );
//            assertEquals( entity.getLastModifiedAt(), user.getLastModifiedAt() );
//            assertEquals( entity.getLastModifiedBy(), user.getLastModifiedBy() );
//            assertEquals( entity.getVersion(), user.getVersion() );
//        }
//    }
//
//    /**
//     * Creating a new user with required parameters.
//     *
//     * @throws TykApiException
//     */
//    @Test
//    public void scenario_03_001() throws RecordNotFoundException, TykApiException
//    {
//        Long id = 1L;
//        UUID ref = UUID.randomUUID();
//        String fullName = "admin";
//        String email = "admin@ufinity.com";
//        String password = "password@1";
//        Date createdAt = new Date();
//        Long createdBy = 1L;
//        Date version = new Date();
//        UserAcctStatus defaultStatus = UserAcctStatus.PENDING_ACTIVATION;
//        Boolean enabled = true;
//
//        UUID roleRef = UUID.randomUUID();
//        String roleName = "ROLE_SYSTEM_ADMIN";
//        RoleCategory roleCategory = RoleCategory.INTERNAL;
//
//        RoleEntity roleEntity = newRoleEntity( roleRef, roleName, roleCategory );
//        when( roleRepo.findByRef( any( UUID.class ) ) ).thenReturn( Optional.of( roleEntity ) );
//
//        when( userRepo.saveAndFlush( any( UserEntity.class ) ) ).then( new Answer<UserEntity>()
//        {
//            @Override
//            public UserEntity answer( InvocationOnMock invocation ) throws Throwable
//            {
//                UserEntity entity = invocation.getArgument( 0 );
//
//                // Simulate saveAndFlush operation for adding new record
//
//                entity.setId( id );
//                entity.setRef( ref );
//                entity.setCreatedAt( createdAt );
//                entity.setVersion( version );
//                return entity;
//            }
//        } );
//
//        UserService.User newUser = new UserService.User();
//        newUser.setFullName( fullName );
//        newUser.setEmail( email );
//        newUser.setPwd( password );
//        newUser.setRole( new UserService.User.Role( roleRef ) );
//        newUser.setCreatedBy( createdBy );
//        newUser.setEnabled( enabled );
//        newUser.setType( UserType.SYSM );
//        newUser.setActivationStatus( UserAcctStatus.PENDING_ACTIVATION );
//
//        ReflectionTestUtils.setField( userService, "adminUserRoleIds", List.of( UUID.randomUUID() ) );
//
//        UserService.User user = userService.createNewUser( newUser, false );
//
//        assertEquals( ref, user.getId() );
//        assertEquals( fullName, user.getFullName() );
//        assertEquals( email, user.getEmail() );
//        assertNull( null, user.getMobileNo() );
//        assertNull( null, user.getDesc() );
//        assertEquals( defaultStatus, user.getActivationStatus() );
//
//        assertNotNull( user.getRole() );
//        assertEquals( roleRef, user.getRole().getId() );
//        assertEquals( roleName, user.getRole().getName() );
//
//        assertEquals( password, user.getPwd() );
//        assertEquals( createdAt, user.getCreatedAt() );
//        assertEquals( createdBy, user.getCreatedBy() );
//        assertNull( null, user.getLastModifiedAt() );
//        assertNull( null, user.getLastModifiedBy() );
//        assertEquals( version, user.getVersion() );
//    }
//
//    /**
//     * Creating a new employee with invalid parameters.
//     *
//     * @throws TykApiException
//     */
//    @Test
//    public void scenario_03_002() throws RecordNotFoundException, TykApiException
//    {
//        thrown.expect( IllegalArgumentException.class );
//        thrown.expectMessage( "User object must not be null" );
//        userService.createNewUser( null, false );
//    }
//
//    /**
//     * Creating a new employee with invalid parameters(full name).
//     *
//     * @throws TykApiException
//     */
//    @Test
//    public void scenario_03_003() throws RecordNotFoundException, TykApiException
//    {
//        User newUser = new User();
//
//        newUser.setFullName( null );
//        newUser.setEmail( "johndoe@gmail.com" );
//        newUser.setRole( new UserService.User.Role( UUID.randomUUID() ) );
//        newUser.setCreatedBy( 1L );
//
//        thrown.expect( IllegalArgumentException.class );
//        thrown.expectMessage( "User full name must not be null" );
//        userService.createNewUser( newUser, false );
//    }
//
//    /**
//     * Creating a new employee with invalid parameters(email).
//     *
//     * @throws TykApiException
//     */
//    @Test
//    public void scenario_03_004() throws RecordNotFoundException, TykApiException
//    {
//        User newUser = new User();
//
//        newUser.setFullName( "John Doe" );
//        newUser.setEmail( null );
//        newUser.setRole( new UserService.User.Role( UUID.randomUUID() ) );
//        newUser.setCreatedBy( 1L );
//
//        thrown.expect( IllegalArgumentException.class );
//        thrown.expectMessage( "User email must not be null" );
//        userService.createNewUser( newUser, false );
//    }
//
//    /**
//     * Creating a new employee with invalid parameters(role).
//     *
//     * @throws TykApiException
//     */
//    @Test
//    public void scenario_03_005() throws RecordNotFoundException, TykApiException
//    {
//        User newUser = new User();
//
//        newUser.setFullName( "John Doe" );
//        newUser.setEmail( "johndoe@gmail.com" );
//        newUser.setRole( null );
//        newUser.setCreatedBy( 1L );
//
//        thrown.expect( IllegalArgumentException.class );
//        thrown.expectMessage( "User role object must not be null" );
//        userService.createNewUser( newUser, false );
//    }
//
//    /**
//     * Creating a new corppass user
//     *
//     * @throws TykApiException
//     */
//    @Test
//    public void scenario_03_006() throws RecordNotFoundException, TykApiException
//    {
//        User newUser = new User();
//
//        UUID roleRef = UUID.randomUUID();
//        String roleName = "ROLE_SYSTEM_ADMIN";
//        RoleCategory roleCategory = RoleCategory.INTERNAL;
//
//        RoleEntity roleEntity = newRoleEntity( roleRef, roleName, roleCategory );
//        when( roleRepo.findByRef( any( UUID.class ) ) ).thenReturn( Optional.of( roleEntity ) );
//
//        newUser.setFullName( "John Doe" );
//        newUser.setEmail( "johndoe@gmail.com" );
//        newUser.setRole( new UserService.User.Role( UUID.randomUUID() ) );
//        newUser.setCreatedBy( 1L );
//        newUser.setType( UserType.CORPPASS );
//        newUser.setEnabled( true );
//        newUser.setCorppassUserId( "corppassuserid" );
//        newUser.setCorppassEntityType( "entity type" );
//        newUser.setCorppassEntityId( "uen" );
//        newUser.setSignUpProgress( SignupProgress.PENDING );
//        userService.createNewUser( newUser, true );
//    }
//
////    /**
////     * Creating a new employee with invalid parameters(login user).
////     * TODO: created by automatically assigned by JPA
////     * @throws TykApiException
////     */
////    @Test
////    public void scenario_03_006() throws RecordNotFoundException, TykApiException
////    {
////        UUID roleRef = UUID.randomUUID();
////        String roleName = "ROLE_SYSTEM_ADMIN";
////        RoleCategory roleCategory = RoleCategory.INTERNAL;
////
////        RoleEntity roleEntity = newRoleEntity( roleRef, roleName, roleCategory );
////        when( roleRepo.findByRef( any( UUID.class ) ) ).thenReturn( Optional.of( roleEntity ) );
////
////        UserService.User.Role role = new UserService.User.Role();
////        role.loadEntity( roleEntity );
////
////        User newUser = new User();
////
////        newUser.setFullName( "John Doe" );
////        newUser.setEmail( "johndoe@gmail.com" );
////        newUser.setRole( role );
////        newUser.setCreatedBy( null );
////        newUser.setType( UserType.SYSM );
////        newUser.setEnabled( true );
////        newUser.setActivationStatus( UserAcctStatus.PENDING_ACTIVATION );
////
////        // TODO: created by automatically assigned by JPA
////        // thrown.expect( IllegalArgumentException.class );
////        // Assert.notNull( newUser.getCreatedBy(), "The id of user creator must
////        // not be null" );
////        // thrown.expectMessage( "The id of user creator must not be null" );
////        userService.createNewUser( newUser, false );
////    }
//
//    /**
//     * Update an existing user with valid & required parameters.
//     *
//     * @throws RecordNotFoundException
//     * @throws DataStaleException
//     * @throws InterruptedException
//     * @throws TykApiException
//     */
//    @Test
//    public void scenario_04_001()
//            throws RecordNotFoundException, DataStaleException, InterruptedException, TykApiException,
//            JiraRestService.JiraApiException
//    {
//        String roleName = "ROLE_ORG_ADMIN";
//
//        UserEntity entity = newSysmUserEntity();
//        when( userRepo.findOneByRef( any( UUID.class ) ) ).thenReturn( Optional.of( entity ) );
//
//        UserService.User existing = userService.getUser( entity.getRef() );
//        Awaitility.await().pollDelay( 100, TimeUnit.MILLISECONDS ).until( () -> true );
//
//        final Date lastModifiedAt = new Date();
//        final Date version = new Date();
//
//        UUID roleRef = UUID.randomUUID();
//
//        RoleEntity roleEntity = new RoleEntity();
//        roleEntity.setId( 1L );
//        roleEntity.setRef( roleRef );
//        roleEntity.setName( roleName );
//        roleEntity.setCategory( RoleCategory.INTERNAL );
//        roleEntity.setCreatedAt( new Date() );
//        roleEntity.setCreatedBy( null );
//        roleEntity.setLastModifiedAt( new Date() );
//        roleEntity.setLastModifiedBy( null );
//        roleEntity.setVersion( new Date() );
//        when( roleRepo.findByRef( any( UUID.class ) ) ).thenReturn( Optional.of( roleEntity ) );
//
//        when( userRepo.saveAndFlush( any( UserEntity.class ) ) ).then( new Answer<UserEntity>()
//        {
//            @Override
//            public UserEntity answer( InvocationOnMock invocation ) throws Throwable
//            {
//                UserEntity entity = invocation.getArgument( 0 );
//
//                // Simulate saveAndFlush operation updating an existing record
//                entity.setLastModifiedAt( lastModifiedAt );
//                entity.setVersion( version );
//
//                return entity;
//            }
//        } );
//
//        UserService.User changes = existing;
//        changes.setFullName( "Jane Doe" );
//        changes.setLastModifiedBy( 2L );
//
//        UserService.User updated = userService.updateUser( entity.getRef(), entity.getVersion(), changes );
//
//        assertEquals( entity.getRef(), updated.getId() );
//        assertEquals( entity.getCreatedAt(), updated.getCreatedAt() );
//        assertEquals( entity.getCreatedBy(), updated.getCreatedBy() );
//
//        assertEquals( changes.getFullName(), updated.getFullName() );
//        assertEquals( changes.getLastModifiedBy(), updated.getLastModifiedBy() );
//
//        assertEquals( lastModifiedAt, updated.getLastModifiedAt() );
//        assertEquals( version, updated.getVersion() );
//    }
//
//    /**
//     * Update an user with invalid parameters.
//     *
//     * @throws RecordNotFoundException
//     * @throws DataStaleException
//     * @throws TykApiException
//     */
//    @Test
//    public void scenario_04_002()
//            throws RecordNotFoundException, DataStaleException, TykApiException, JiraRestService.JiraApiException
//    {
//        thrown.expect( IllegalArgumentException.class );
//        thrown.expectMessage( "User id must not be null" );
//        userService.updateUser( null, null, null );
//    }
//
//    /**
//     * Update an user with invalid parameters(version).
//     *
//     * @throws RecordNotFoundException
//     * @throws DataStaleException
//     * @throws TykApiException
//     */
//    @Test
//    public void scenario_04_003()
//            throws RecordNotFoundException, DataStaleException, TykApiException, JiraRestService.JiraApiException
//    {
//        thrown.expect( IllegalArgumentException.class );
//        thrown.expectMessage( "User version must not be null" );
//        userService.updateUser( UUID.randomUUID(), null, null );
//    }
//
//    /**
//     * Update an user with invalid parameters(new user).
//     *
//     * @throws RecordNotFoundException
//     * @throws DataStaleException
//     * @throws TykApiException
//     */
//    @Test
//    public void scenario_04_004()
//            throws RecordNotFoundException, DataStaleException, TykApiException, JiraRestService.JiraApiException
//    {
//        thrown.expect( IllegalArgumentException.class );
//        thrown.expectMessage( "User object must not be null" );
//        userService.updateUser( UUID.randomUUID(), new Date(), null );
//    }
//
//    /**
//     * Updating a new employee with invalid parameters(full name).
//     *
//     * @throws TykApiException
//     */
//    @Test
//    public void scenario_04_005()
//            throws RecordNotFoundException, DataStaleException, TykApiException, JiraRestService.JiraApiException
//    {
//        User changes = new User();
//
//        UUID roleRef = UUID.randomUUID();
//        String roleName = "ROLE_ORG_ADMIN";
//        RoleCategory roleCategory = RoleCategory.EXTERNAL;
//
//        RoleEntity roleEntity = newRoleEntity( roleRef, roleName, roleCategory );
//        UserService.User.Role role = new UserService.User.Role();
//        role.loadEntity( roleEntity );
//
//        changes.setFullName( null );
//        changes.setEmail( "johndoe@gmail.com" );
//        changes.setRole( role );
//        changes.setCreatedBy( 1L );
//
//        thrown.expect( IllegalArgumentException.class );
//        thrown.expectMessage( "User full name must not be null" );
//        userService.updateUser( UUID.randomUUID(), new Date(), changes );
//    }
//
//    /**
//     * Updating a new employee with invalid parameters(email).
//     *
//     * @throws TykApiException
//     */
//    @Test
//    public void scenario_04_006()
//            throws RecordNotFoundException, DataStaleException, TykApiException, JiraRestService.JiraApiException
//    {
//        User changes = new User();
//
//        UUID roleRef = UUID.randomUUID();
//        String roleName = "ROLE_ORG_ADMIN";
//        RoleCategory roleCategory = RoleCategory.EXTERNAL;
//
//        RoleEntity roleEntity = newRoleEntity( roleRef, roleName, roleCategory );
//        UserService.User.Role role = new UserService.User.Role();
//        role.loadEntity( roleEntity );
//
//        changes.setFullName( "John Doe" );
//        changes.setEmail( null );
//        changes.setRole( role );
//        changes.setCreatedBy( 1L );
//
//        thrown.expect( IllegalArgumentException.class );
//        thrown.expectMessage( "User email must not be null" );
//        userService.updateUser( UUID.randomUUID(), new Date(), changes );
//    }
//
//    /**
//     * Update an user with invalid parameters(role).
//     *
//     * @throws RecordNotFoundException
//     * @throws DataStaleException
//     * @throws TykApiException
//     */
//    @Test
//    public void scenario_04_007()
//            throws RecordNotFoundException, DataStaleException, TykApiException, JiraRestService.JiraApiException
//    {
//        User changes = new User();
//        changes.setFullName( "John Doe" );
//        changes.setEmail( "johndoe@gmail.com" );
//        changes.setRole( null );
//        changes.setCreatedBy( 1L );
//
//        thrown.expect( IllegalArgumentException.class );
//        thrown.expectMessage( "User role must not be null" );
//        userService.updateUser( UUID.randomUUID(), new Date(), changes );
//    }
//
//    /**
//     * Update an employee with invalid ID.
//     *
//     * @throws RecordNotFoundException
//     * @throws DataStaleException
//     * @throws TykApiException
//     */
//    @Test
//    public void scenario_04_008()
//            throws RecordNotFoundException, DataStaleException, TykApiException, JiraRestService.JiraApiException
//    {
//        thrown.expect( RecordNotFoundException.class );
//        thrown.expectMessage( "The user could not be found" );
//
//        UUID roleRef = UUID.randomUUID();
//        String roleName = "ROLE_ORG_ADMIN";
//        RoleCategory roleCategory = RoleCategory.EXTERNAL;
//
//        RoleEntity roleEntity = newRoleEntity( roleRef, roleName, roleCategory );
//        UserService.User.Role role = new UserService.User.Role();
//        role.loadEntity( roleEntity );
//
//        User changes = new User();
//        changes.setFullName( "John Doe" );
//        changes.setEmail( "johndoe@gmail.com" );
//        changes.setRole( role );
//        changes.setLastModifiedBy( 1L );
//        changes.setActivationStatus( UserAcctStatus.ACTIVATED );
//        changes.setEnabled( true );
//
//        when( userRepo.findOneByRef( any( UUID.class ) ) ).thenReturn( Optional.empty() );
//        userService.updateUser( UUID.randomUUID(), new Date(), changes );
//    }
//
//    /**
//     * Update an existing user but it was updated by other user.
//     *
//     * @throws RecordNotFoundException
//     * @throws InterruptedException
//     * @throws DataStaleException
//     * @throws TykApiException
//     */
//    @Test
//    public void scenario_04_009()
//            throws RecordNotFoundException, InterruptedException, DataStaleException, TykApiException,
//            JiraRestService.JiraApiException
//    {
//        thrown.expect( DataStaleException.class );
//        thrown.expectMessage( "The user details retrieved previously already stale" );
//
//        UUID ref = UUID.randomUUID();
//
//        UserService.User existing = null;
//        {
//            UserEntity entity = newSysmUserEntity( ref );
//
//            when( userRepo.findOneByRef( any( UUID.class ) ) ).thenReturn( Optional.of( entity ) );
//
//            existing = userService.getUser( entity.getRef() );
//            Awaitility.await().pollDelay( 100, TimeUnit.MILLISECONDS ).until( () -> true );
//        }
//
//        UserService.User changes = existing;
//        changes.setFullName( "Jane Doe" );
//        changes.setLastModifiedBy( 1L );
//
//        UserEntity entity = newSysmUserEntity( ref );
//        entity.setLastModifiedAt( new Date() );
//        entity.setLastModifiedBy( 2L );
//        entity.setVersion( new Date() );
//
//        when( userRepo.findOneByRef( any( UUID.class ) ) ).thenReturn( Optional.of( entity ) );
//        userService.updateUser( changes.getId(), changes.getVersion(), changes );
//    }
//
//    /**
//     * Update an existing employee but it was updated by other user.
//     *
//     * @throws RecordNotFoundException
//     * @throws InterruptedException
//     * @throws DataStaleException
//     * @throws TykApiException
//     */
//    @Test
//    public void scenario_04_010()
//            throws RecordNotFoundException, InterruptedException, DataStaleException, TykApiException,
//            JiraRestService.JiraApiException
//    {
//        thrown.expect( DataStaleException.class );
//        thrown.expectMessage( "The user details retrieved previously already stale" );
//
//        UUID roleRef = UUID.randomUUID();
//        String roleName = "ROLE_ORG_ADMIN";
//        RoleCategory roleCategory = RoleCategory.EXTERNAL;
//
//        RoleEntity roleEntity = newRoleEntity( roleRef, roleName, roleCategory );
//        when( roleRepo.findByRef( any( UUID.class ) ) ).thenReturn( Optional.of( roleEntity ) );
//
//        UserEntity entity = newSysmUserEntity();
//        when( userRepo.findOneByRef( any( UUID.class ) ) ).thenReturn( Optional.of( entity ) );
//
//        when( userRepo.saveAndFlush( any( UserEntity.class ) ) ).then( new Answer<UserEntity>()
//        {
//            @Override
//            public UserEntity answer( InvocationOnMock invocation ) throws Throwable
//            {
//                throw new OptimisticLockException();
//            }
//        } );
//
//        UserService.User existing = userService.getUser( entity.getRef() );
//
//        UserService.User changes = existing;
//        changes.setFullName( "Jane Doe" );
//        changes.setLastModifiedBy( 1L );
//
//        userService.updateUser( changes.getId(), changes.getVersion(), changes );
//    }
//
//    /**
//     * Update an github user
//     *
//     * @throws RecordNotFoundException
//     * @throws InterruptedException
//     * @throws DataStaleException
//     * @throws TykApiException
//     */
//    @Test
//    public void scenario_04_011()
//            throws RecordNotFoundException, InterruptedException, DataStaleException, TykApiException,
//            JiraRestService.JiraApiException
//    {
//        String roleName = "ROLE_ORG_ADMIN";
//
//        UserEntity entity = UserUtils.createUserEntity( UserType.GITHUB );
//        when( userRepo.findOneByRef( any( UUID.class ) ) ).thenReturn( Optional.of( entity ) );
//
//        UserService.User existing = userService.getUser( entity.getRef() );
//        Awaitility.await().pollDelay( 100, TimeUnit.MILLISECONDS ).until( () -> true );
//
//        final Date lastModifiedAt = new Date();
//        final Date version = new Date();
//
//        UUID roleRef = UUID.randomUUID();
//
//        RoleEntity roleEntity = new RoleEntity();
//        roleEntity.setId( 1L );
//        roleEntity.setRef( roleRef );
//        roleEntity.setName( roleName );
//        roleEntity.setCategory( RoleCategory.INTERNAL );
//        roleEntity.setCreatedAt( new Date() );
//        roleEntity.setCreatedBy( null );
//        roleEntity.setLastModifiedAt( new Date() );
//        roleEntity.setLastModifiedBy( null );
//        roleEntity.setVersion( new Date() );
//        when( roleRepo.findByRef( any( UUID.class ) ) ).thenReturn( Optional.of( roleEntity ) );
//
//        when( userRepo.saveAndFlush( any( UserEntity.class ) ) ).then( new Answer<UserEntity>()
//        {
//            @Override
//            public UserEntity answer( InvocationOnMock invocation ) throws Throwable
//            {
//                UserEntity entity = invocation.getArgument( 0 );
//
//                // Simulate saveAndFlush operation updating an existing record
//                entity.setLastModifiedAt( lastModifiedAt );
//                entity.setVersion( version );
//
//                return entity;
//            }
//        } );
//
//        UserService.User changes = existing;
//        changes.setFullName( "Jane Doe" );
//        changes.setLastModifiedBy( 2L );
//        UserService.User.Role role = new UserService.User.Role();
//        role.setName( "role" );
//        role.setId( UUID.randomUUID() );
//        changes.setRole( role );
//
//        UserService.User updated = userService.updateUser( entity.getRef(), entity.getVersion(), changes );
//
//        assertEquals( entity.getRef(), updated.getId() );
//        assertEquals( entity.getCreatedAt(), updated.getCreatedAt() );
//        assertEquals( entity.getCreatedBy(), updated.getCreatedBy() );
//
//        assertEquals( changes.getFullName(), updated.getFullName() );
//        assertEquals( changes.getLastModifiedBy(), updated.getLastModifiedBy() );
//
//        assertEquals( lastModifiedAt, updated.getLastModifiedAt() );
//        assertEquals( version, updated.getVersion() );
//    }
//
//    /**
//     * Update an corppass user ( update tyk )
//     *
//     * @throws RecordNotFoundException
//     * @throws InterruptedException
//     * @throws DataStaleException
//     * @throws TykApiException
//     * @throws JiraServiceDeskRecordNotFoundException
//     */
//    @Test
//    public void scenario_04_012()
//            throws RecordNotFoundException, InterruptedException, DataStaleException, TykApiException,
//            JiraRestService.JiraApiException, JiraServiceDeskRecordNotFoundException
//    {
//        String roleName = "ROLE_ORG_ADMIN";
//
//        UserEntity userEntity = UserUtils.createUserEntity( UserType.CORPPASS );
//        TykDeveloperEntity tykDeveloper = TykUtils.createTykDeveloperEntity( userEntity );
//        userEntity.setTykDeveloper( tykDeveloper );
//        when( userRepo.findOneByRef( any( UUID.class ) ) ).thenReturn( Optional.of( userEntity ) );
//
//        UserService.User existing = userService.getUser( userEntity.getRef() );
//        Awaitility.await().pollDelay( 100, TimeUnit.MILLISECONDS ).until( () -> true );
//
//        final Date lastModifiedAt = new Date();
//        final Date version = new Date();
//
//        UUID roleRef = UUID.randomUUID();
//
//        RoleEntity roleEntity = new RoleEntity();
//        roleEntity.setId( 1L );
//        roleEntity.setRef( roleRef );
//        roleEntity.setName( roleName );
//        roleEntity.setCategory( RoleCategory.INTERNAL );
//        roleEntity.setCreatedAt( new Date() );
//        roleEntity.setCreatedBy( null );
//        roleEntity.setLastModifiedAt( new Date() );
//        roleEntity.setLastModifiedBy( null );
//        roleEntity.setVersion( new Date() );
//        when( roleRepo.findByRef( any( UUID.class ) ) ).thenReturn( Optional.of( roleEntity ) );
//        when( tykService.updateDeveloper( any( TykDeveloper.class ) ) ).thenReturn( TykUtils.createTykDeveloper() );
//        when( tykService.getDeveloper( any( UUID.class ), anyString() ) ).thenReturn( TykUtils.createTykDeveloper() );
//        when( userRepo.saveAndFlush( any( UserEntity.class ) ) ).then( new Answer<UserEntity>()
//        {
//            @Override
//            public UserEntity answer( InvocationOnMock invocation ) throws Throwable
//            {
//                UserEntity entity = invocation.getArgument( 0 );
//
//                // Simulate saveAndFlush operation updating an existing record
//                entity.setLastModifiedAt( lastModifiedAt );
//                entity.setVersion( version );
//
//                return entity;
//            }
//        } );
//        when( jiraRestService.getUser( any( UUID.class ) ) ).thenReturn( UserUtils.createJiraUser() );
//        when( jiraRestService.updateUser( any( JiraRestService.User.class ) ) ).thenReturn(
//                UserUtils.createJiraUser() );
//
//        UserService.User changes = existing;
//        changes.setFullName( "Jane Doe" );
//        changes.setLastModifiedBy( 2L );
//        UserService.User.Role role = new UserService.User.Role();
//        role.setName( "role" );
//        role.setId( UUID.randomUUID() );
//        changes.setRole( role );
//
//        UserService.User updated = userService.updateUser( userEntity.getRef(), userEntity.getVersion(), changes );
//
//        assertEquals( userEntity.getRef(), updated.getId() );
//        assertEquals( userEntity.getCreatedAt(), updated.getCreatedAt() );
//        assertEquals( userEntity.getCreatedBy(), updated.getCreatedBy() );
//
//        assertEquals( changes.getFullName(), updated.getFullName() );
//        assertEquals( changes.getLastModifiedBy(), updated.getLastModifiedBy() );
//
//        assertEquals( lastModifiedAt, updated.getLastModifiedAt() );
//        assertEquals( version, updated.getVersion() );
//    }
//
//    /**
//     * Check if user account is lockable
//     *
//     * @throws RecordNotFoundException
//     */
//    @Test
//    public void scenario_05_001() throws RecordNotFoundException
//    {
//        UUID ref = UUID.randomUUID();
//        UserEntity entity = newSysmUserEntity();
//        when( userRepo.findOneByRef( any( UUID.class ) ) ).thenReturn( Optional.of( entity ) );
//
//        UserIdentity loginUser = Mockito.mock( LoginUser.class );
//        when( loginUser.getUserId() ).thenReturn( ref );
//        boolean isUserDeleteable = userService.isUserAccountLockable( loginUser, entity.getRef() );
//
//        assertTrue( isUserDeleteable );
//    }
//
//    /**
//     * Check if user account is not lockable
//     *
//     * @throws RecordNotFoundException
//     */
//    @Test
//    public void scenario_05_002() throws RecordNotFoundException
//    {
//        UUID ref = UUID.randomUUID();
//        UserEntity entity = newSysmUserEntity( ref );
//        when( userRepo.findOneByRef( any( UUID.class ) ) ).thenReturn( Optional.of( entity ) );
//
//        UserIdentity loginUser = Mockito.mock( LoginUser.class );
//        when( loginUser.getUserId() ).thenReturn( ref );
//        boolean isUserDeleteable = userService.isUserAccountLockable( loginUser, entity.getRef() );
//
//        assertFalse( isUserDeleteable );
//    }
//
//    /**
//     * Check if user account is unlockable
//     *
//     * @throws RecordNotFoundException
//     */
//    @Test
//    public void scenario_05_003() throws RecordNotFoundException
//    {
//        UUID ref = UUID.randomUUID();
//        UserEntity entity = newSysmUserEntity();
//        when( userRepo.findOneByRef( any( UUID.class ) ) ).thenReturn( Optional.of( entity ) );
//
//        UserIdentity loginUser = Mockito.mock( LoginUser.class );
//        when( loginUser.getUserId() ).thenReturn( ref );
//        boolean isUserAccUnlockable = userService.isUserAccountUnlockable( loginUser, entity.getRef() );
//
//        assertTrue( isUserAccUnlockable );
//    }
//
//    /**
//     * Check if user account is not unlockable
//     *
//     * @throws RecordNotFoundException
//     */
//    @Test
//    public void scenario_05_004() throws RecordNotFoundException
//    {
//        UUID ref = UUID.randomUUID();
//        UserEntity entity = newSysmUserEntity( ref );
//        when( userRepo.findOneByRef( any( UUID.class ) ) ).thenReturn( Optional.of( entity ) );
//
//        UserIdentity loginUser = Mockito.mock( LoginUser.class );
//        when( loginUser.getUserId() ).thenReturn( ref );
//        boolean isUserAccUnlockable = userService.isUserAccountUnlockable( loginUser, entity.getRef() );
//
//        assertFalse( isUserAccUnlockable );
//    }
//
//    /**
//     * User logins successfully.
//     *
//     * @throws RecordNotFoundException
//     */
//    @Test
//    public void scenario_06_001() throws RecordNotFoundException
//    {
//        UUID ref = UUID.randomUUID();
//
//        SysmUserEntity user = newSysmUserEntity( ref );
//        UserLoginEntity userLogin = newUserLoginEntity();
//
//        when( userRepo.findOneByRefForUpdate( any( UUID.class ) ) ).thenReturn( Optional.of( user ) );
//        when( userLoginRepo.findById( any( Long.class ) ) ).thenReturn( Optional.of( userLogin ) );
//        ReflectionTestUtils.setField( userService, "maxLoginAttempts", 10 );
//        userService.loginAttempt( ref, true );
//
//        assertEquals( 0, userLogin.getFailedAttempts().intValue() );
//        assertEquals( UserAcctStatus.ACTIVATED, user.getActivationStatus() );
//        assertNull( userLogin.getLastFailedAt() );
//    }
//
//    /**
//     * User logins unsuccessfully with wrong password.
//     *
//     * @throws RecordNotFoundException
//     */
//    @Test
//    public void scenario_06_002() throws RecordNotFoundException
//    {
//        UUID ref = UUID.randomUUID();
//
//        SysmUserEntity user = newSysmUserEntity( ref );
//        UserLoginEntity userLogin = newUserLoginEntity();
//
//        when( userRepo.findOneByRefForUpdate( any( UUID.class ) ) ).thenReturn( Optional.of( user ) );
//        when( userLoginRepo.findById( any( Long.class ) ) ).thenReturn( Optional.of( userLogin ) );
//        ReflectionTestUtils.setField( userService, "maxLoginAttempts", 10 );
//        userService.loginAttempt( ref, false );
//
//        assertEquals( 3, userLogin.getFailedAttempts().intValue() );
//        assertEquals( UserAcctStatus.ACTIVATED, user.getActivationStatus() );
//        assertNotNull( userLogin.getLastFailedAt() );
//    }
//
//    /**
//     * User logins unsuccessfully with wrong password and reaches 10th fail
//     * attempt.
//     *
//     * @throws RecordNotFoundException
//     */
//    @Test
//    public void scenario_06_003() throws RecordNotFoundException
//    {
//        UUID ref = UUID.randomUUID();
//
//        SysmUserEntity user = newSysmUserEntity( ref );
//        UserLoginEntity userLogin = newUserLoginEntity();
//        userLogin.setFailedAttempts( 9 );
//
//        when( userRepo.findOneByRefForUpdate( any( UUID.class ) ) ).thenReturn( Optional.of( user ) );
//        when( userLoginRepo.findById( any( Long.class ) ) ).thenReturn( Optional.of( userLogin ) );
//        ReflectionTestUtils.setField( userService, "maxLoginAttempts", 10 );
//        userService.loginAttempt( ref, false );
//
//        assertEquals( 10, userLogin.getFailedAttempts().intValue() );
//        assertEquals( UserAcctStatus.ACTIVATED, user.getActivationStatus() );
//        assertNotNull( userLogin.getLastFailedAt() );
//    }
//
//    /**
//     * User logins unsuccessfully with wrong password and reaches 10th fail
//     * attempt.
//     *
//     * @throws RecordNotFoundException
//     */
//    @Test
//    public void scenario_06_004() throws RecordNotFoundException
//    {
//        when( userRepo.findOneByRefForUpdate( any( UUID.class ) ) ).thenReturn( Optional.empty() );
//
//        thrown.expect( RecordNotFoundException.class );
//
//        userService.loginAttempt( UUID.randomUUID(), false );
//    }
//
//    /**
//     * Create github user successfully.
//     *
//     * @throws RecordNotFoundException
//     * @throws TykApiException
//     */
//    @Test
//    public void scenario_07_001() throws RecordNotFoundException, TykApiException
//    {
//        Long id = 1L;
//        UUID ref = UUID.randomUUID();
//        String fullName = "John Doe";
//        String email = "johndoe@gmail.com";
//        Date createdAt = new Date();
//        Long createdBy = 1L;
//        Date version = new Date();
//        Boolean enabled = true;
//        Long githubUserId = 36831006L;
//
//        UUID roleRef = UUID.randomUUID();
//        String roleName = "ROLE_EXTERNAL_DEVELOPER";
//        RoleCategory roleCategory = RoleCategory.EXTERNAL;
//
//        RoleEntity roleEntity = newRoleEntity( roleRef, roleName, roleCategory );
//        when( roleRepo.findByRef( any( UUID.class ) ) ).thenReturn( Optional.of( roleEntity ) );
//
//        when( userRepo.saveAndFlush( any( GithubUserEntity.class ) ) ).then( new Answer<GithubUserEntity>()
//        {
//            @Override
//            public GithubUserEntity answer( InvocationOnMock invocation ) throws Throwable
//            {
//                GithubUserEntity entity = invocation.getArgument( 0 );
//
//                // Simulate saveAndFlush operation for adding new record
//
//                entity.setId( id );
//                entity.setRef( ref );
//                entity.setCreatedAt( createdAt );
//                entity.setVersion( version );
//                return entity;
//            }
//        } );
//
//        UserService.User newUser = new UserService.User();
//        newUser.setFullName( fullName );
//        newUser.setEmail( email );
//
//        newUser.setRole( new UserService.User.Role( roleRef ) );
//        newUser.setCreatedBy( createdBy );
//        newUser.setEnabled( enabled );
//        newUser.setType( UserType.GITHUB );
//        newUser.setGithubUserId( githubUserId );
//        newUser.setSignUpProgress( SignupProgress.COMPLETED );
//
//        UserService.User user = userService.createNewUser( newUser, true );
//
//        assertEquals( ref, user.getId() );
//        assertEquals( fullName, user.getFullName() );
//        assertEquals( email, user.getEmail() );
//        assertNotNull( user.getRole() );
//        assertEquals( roleRef, user.getRole().getId() );
//        assertEquals( roleName, user.getRole().getName() );
//        assertEquals( createdAt, user.getCreatedAt() );
//        assertEquals( createdBy, user.getCreatedBy() );
//        assertNull( null, user.getLastModifiedAt() );
//        assertNull( null, user.getLastModifiedBy() );
//        assertEquals( version, user.getVersion() );
//        assertEquals( SignupProgress.COMPLETED, user.getSignUpProgress() );
//        assertEquals( githubUserId, user.getGithubUserId() );
//    }
//
//    /**
//     * Create github user unsuccessfully.
//     *
//     * @throws RecordNotFoundException
//     * @throws TykApiException
//     */
//    @Test
//    public void scenario_07_002() throws IllegalArgumentException, RecordNotFoundException, TykApiException
//    {
//        thrown.expect( IllegalArgumentException.class );
//        thrown.expectMessage( "Github user id must not be null" );
//
//        String fullName = "John Doe";
//        String email = "johndoe@gmail.com";
//        Long createdBy = 1L;
//        Boolean enabled = true;
//
//        UUID roleRef = UUID.randomUUID();
//        String roleName = "ROLE_EXTERNAL_DEVELOPER";
//        RoleCategory roleCategory = RoleCategory.EXTERNAL;
//
//        RoleEntity roleEntity = newRoleEntity( roleRef, roleName, roleCategory );
//        when( roleRepo.findByRef( any( UUID.class ) ) ).thenReturn( Optional.of( roleEntity ) );
//
//        UserService.User newUser = new UserService.User();
//        newUser.setFullName( fullName );
//        newUser.setEmail( email );
//
//        newUser.setRole( new UserService.User.Role( roleRef ) );
//        newUser.setCreatedBy( createdBy );
//        newUser.setEnabled( enabled );
//        newUser.setType( UserType.GITHUB );
//        newUser.setSignUpProgress( SignupProgress.COMPLETED );
//
//        userService.createNewUser( newUser, true );
//
//    }
//
//    /**
//     * Test method find role by id record not found
//     *
//     * @throws RecordNotFoundException
//     */
//    @Test
//    public void scenario_08_001() throws RecordNotFoundException
//    {
//        when( roleRepo.findByRef( any( UUID.class ) ) ).thenReturn( Optional.empty() );
//
//        thrown.expect( RecordNotFoundException.class );
//
//        userService.getRole( UUID.randomUUID() );
//    }
//
//    /**
//     * Test method find role by id
//     *
//     * @throws RecordNotFoundException
//     */
//    @Test
//    public void scenario_08_002() throws RecordNotFoundException
//    {
//        RoleEntity roleEntity = UserUtils.createRoleEntity();
//
//        when( roleRepo.findByRef( any( UUID.class ) ) ).thenReturn( Optional.of( roleEntity ) );
//
//        UserService.User.Role role = userService.getRole( UUID.randomUUID() );
//
//        assertEquals( role.getName(), roleEntity.getName() );
//        assertEquals( role.getId(), roleEntity.getRef() );
//    }
//
//    /**
//     * Test method is username registered
//     *
//     * @throws RecordNotFoundException
//     */
//    @SuppressWarnings( "unchecked" )
//    @Test
//    public void scenario_09_001() throws RecordNotFoundException
//    {
//        when( userRepo.findOne( any( Example.class ) ) ).thenReturn( Optional.empty() );
//
//        thrown.expect( RecordNotFoundException.class );
//
//        userService.getRole( UUID.randomUUID() );
//    }
//
//    /**
//     * Test method is username registered
//     *
//     * @throws RecordNotFoundException
//     */
//    @Test
//    public void scenario_10_001() throws RecordNotFoundException
//    {
//        RoleEntity roleEntity = new RoleEntity();
//        roleEntity.setCategory( RoleCategory.INTERNAL );
//        roleEntity.setName( "role" );
//
//        List<RoleEntity> roleEntities = new ArrayList<>();
//        roleEntities.add( roleEntity );
//        when( roleRepo.findAll() ).thenReturn( roleEntities );
//
//        List<User.Role> userRoles = userService.getRoles();
//
//        for ( int index = 0; index < roleEntities.size(); index++ )
//        {
//            assertEquals( roleEntities.get( index ).getName(), userRoles.get( index ).getName() );
//        }
//    }
//
//    /**
//     * Test method is get roles
//     *
//     * @throws RecordNotFoundException
//     */
//    @Test
//    public void scenario_11_001() throws RecordNotFoundException
//    {
//        RoleEntity roleEntity = new RoleEntity();
//        roleEntity.setCategory( RoleCategory.INTERNAL );
//        roleEntity.setName( "role" );
//
//        List<RoleEntity> roleEntities = new ArrayList<>();
//        roleEntities.add( roleEntity );
//        when( roleRepo.findAll() ).thenReturn( roleEntities );
//
//        List<User.Role> userRoles = userService.getRoles();
//
//        for ( int index = 0; index < roleEntities.size(); index++ )
//        {
//            assertEquals( roleEntities.get( index ).getName(), userRoles.get( index ).getName() );
//        }
//    }
//
//    /**
//     * Test method find sysm user by email
//     *
//     * @throws RecordNotFoundException
//     */
//    @SuppressWarnings( "unchecked" )
//    @Test
//    public void scenario_12_001() throws RecordNotFoundException
//    {
//        SysmUserEntity sysmUserEntity = (SysmUserEntity) UserUtils.createUserEntity( UserType.SYSM );
//        when( userRepo.findOne( any( Example.class ) ) ).thenReturn( Optional.of( sysmUserEntity ) );
//
//        assertEquals( Optional.of( sysmUserEntity ), userService.findSystemUserByEmail( "email" ) );
//    }
//
//    /**
//     * Test method get sysm user ( user not found )
//     *
//     * @throws RecordNotFoundException
//     */
//    @SuppressWarnings( "unchecked" )
//    @Test
//    public void scenario_13_001() throws RecordNotFoundException
//    {
//        when( userRepo.findOne( any( Example.class ) ) ).thenReturn( Optional.empty() );
//
//        thrown.expect( RecordNotFoundException.class );
//
//        userService.getSysmUser( "username" );
//    }
//
//    /**
//     * Test method get sysm user
//     *
//     * @throws RecordNotFoundException
//     */
//    @SuppressWarnings( "unchecked" )
//    @Test
//    public void scenario_13_002() throws RecordNotFoundException
//    {
//        UserEntity expected = UserUtils.createUserEntity( UserType.SYSM );
//        when( userRepo.findOne( any( Example.class ) ) ).thenReturn( Optional.of( expected ) );
//
//        UserService.User actual = userService.getSysmUser( "username" );
//
//        assertEquals( expected.getEmail(), actual.getEmail() );
//        assertEquals( expected.getFullName(), actual.getFullName() );
//        assertEquals( expected.getEnabled(), actual.getEnabled() );
//    }
//
//    /**
//     * Test method get github user
//     *
//     * @throws RecordNotFoundException
//     */
//    @SuppressWarnings( "unchecked" )
//    @Test
//    public void scenario_14_001() throws RecordNotFoundException
//    {
//        UserEntity expected = UserUtils.createUserEntity( UserType.GITHUB );
//        when( userRepo.findOne( any( Example.class ) ) ).thenReturn( Optional.of( expected ) );
//
//        UserService.User actual = userService.getGithubUser( 1L );
//
//        assertEquals( expected.getEmail(), actual.getEmail() );
//        assertEquals( expected.getFullName(), actual.getFullName() );
//        assertEquals( expected.getEnabled(), actual.getEnabled() );
//    }
//
//    /**
//     * Test method get github user ( user not found )
//     *
//     * @throws RecordNotFoundException
//     */
//    @Test
//    public void scenario_14_002() throws RecordNotFoundException
//    {
//        UserEntity expected = UserUtils.createUserEntity( UserType.GITHUB );
//        when( userRepo.findOne( any( Example.class ) ) ).thenReturn( Optional.empty() );
//
//        thrown.expect( RecordNotFoundException.class );
//
//        userService.getGithubUser( 1L );
//    }
//
//    /**
//     * Test method get corppass user ( user not found )
//     *
//     * @throws RecordNotFoundException
//     */
//    @SuppressWarnings( "unchecked" )
//    @Test
//    public void scenario_15_001() throws RecordNotFoundException
//    {
//        when( userRepo.findOne( any( Example.class ) ) ).thenReturn( Optional.empty() );
//
//        thrown.expect( RecordNotFoundException.class );
//
//        userService.getCorpPassUser( "corppassID" );
//    }
//
//    /**
//     * Test method get corppass user ( user not found )
//     *
//     * @throws RecordNotFoundException
//     */
//    @Test
//    public void scenario_15_002() throws RecordNotFoundException
//    {
//        UserEntity expected = UserUtils.createUserEntity( UserType.CORPPASS );
//        when( userRepo.findOne( any( Example.class ) ) ).thenReturn( Optional.of( expected ) );
//
//        userService.getCorpPassUser( "corppassID" );
//    }
//
//    /**
//     * Test method get corppass user ( user not found )
//     *
//     * @throws RecordNotFoundException
//     */
//    @Test
//    public void scenario_16_001() throws RecordNotFoundException
//    {
//        UserListingEntity userListingEntity = new UserListingEntity();
//        userListingEntity.setActivationStatus( UserAcctStatus.ACTIVATED );
//        userListingEntity.setEmail( "hello@ds.com" );
//        userListingEntity.setEnabled( true );
//        userListingEntity.setFullName( "full name" );
//        userListingEntity.setMobileNo( "012" );
//        userListingEntity.setRef( UUID.randomUUID() );
//        userListingEntity.setRolesName( "platform-manager" );
//
//        List<UserListingEntity> userListingEntities = new ArrayList<>();
//        userListingEntities.add( userListingEntity );
//
//        when( userListingRepository.findAll( PageRequest.of( 0, 5 ) ) ).thenReturn(
//                new PageImpl<>( userListingEntities, PageRequest.of( 0, 5 ), 1 ) );
//
//        Page<UserListing> page = userService.getUsers( PageRequest.of( 0, 5 ) );
//
//        for ( int index = 0; index < userListingEntities.size(); index++ )
//        {
//            UserService.UserListing actual = page.getContent().get( index );
//            UserListingEntity expected = userListingEntities.get( index );
//            assertEquals( expected.getActivationStatus(), actual.getActivationStatus() );
//            assertEquals( expected.getEmail(), actual.getEmail() );
//            assertEquals( expected.getFullName(), actual.getFullName() );
//            assertEquals( expected.getMobileNo(), actual.getMobileNo() );
//        }
//    }
//
//    /**
//     * Test method get validate uen
//     *
//     * @throws RecordNotFoundException
//     */
//    @SuppressWarnings( "unchecked" )
//    @Test
//    public void scenario_17_001() throws RecordNotFoundException
//    {
//        when( userRepo.exists( any( Example.class ) ) ).thenReturn( true );
//
//        assertEquals( true, userService.validateUEN( "uen" ) );
//    }
//
//    /**
//     * Test method get validate uen
//     *
//     * @throws RecordNotFoundException
//     */
//    @SuppressWarnings( "unchecked" )
//    @Test
//    public void scenario_17_002() throws RecordNotFoundException
//    {
//        when( userRepo.exists( any( Example.class ) ) ).thenReturn( false );
//
//        assertEquals( false, userService.validateUEN( "uen" ) );
//    }
//
//    /**
//     * Test method is username registered
//     *
//     * @throws RecordNotFoundException
//     */
//    @SuppressWarnings( "unchecked" )
//    @Test
//    public void scenario_18_001() throws RecordNotFoundException
//    {
//        when( userRepo.findOne( any( Example.class ) ) ).thenReturn( Optional.empty() );
//
//        assertEquals( false, userService.isUsernameRegistered( "username" ) );
//    }
//
//    /**
//     * Test method iser username registered
//     *
//     * @throws RecordNotFoundException
//     */
//    @SuppressWarnings( "unchecked" )
//    @Test
//    public void scenario_18_002() throws RecordNotFoundException
//    {
//        when( userRepo.findOne( any( Example.class ) ) ).thenReturn(
//                Optional.of( UserUtils.createUserEntity( UserType.SYSM ) ) );
//
//        assertEquals( true, userService.isUsernameRegistered( "username" ) );
//    }
//
//}
