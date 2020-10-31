//package ch.rasc.twofa.awss3;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Example;
//import org.springframework.data.domain.ExampleMatcher;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.Assert;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//import javax.persistence.OptimisticLockException;
//
//import sg.gov.ssg.devportal.common.UserIdentity;
//import sg.gov.ssg.devportal.common.UserType;
//import sg.gov.ssg.devportal.repositories.devportal.RoleRepository;
//import sg.gov.ssg.devportal.repositories.devportal.UserListingRepository;
//import sg.gov.ssg.devportal.repositories.devportal.UserLoginRepository;
//import sg.gov.ssg.devportal.repositories.devportal.UserRepository;
//import sg.gov.ssg.devportal.repositories.devportal.entities.CorpPassUserEntity;
//import sg.gov.ssg.devportal.repositories.devportal.entities.GithubUserEntity;
//import sg.gov.ssg.devportal.repositories.devportal.entities.RoleEntity;
//import sg.gov.ssg.devportal.repositories.devportal.entities.SysmUserEntity;
//import sg.gov.ssg.devportal.repositories.devportal.entities.SysmUserPasswordEntity;
//import sg.gov.ssg.devportal.repositories.devportal.entities.UserEntity;
//import sg.gov.ssg.devportal.repositories.devportal.entities.UserListingEntity;
//import sg.gov.ssg.devportal.repositories.devportal.entities.UserLoginEntity;
//import sg.gov.ssg.devportal.services.JiraRestService;
//import sg.gov.ssg.devportal.services.ServiceError.DataStaleException;
//import sg.gov.ssg.devportal.services.ServiceError.RecordNotFoundException;
//import sg.gov.ssg.devportal.services.ServiceError.TykApiException;
//import sg.gov.ssg.devportal.services.TykService;
//import sg.gov.ssg.devportal.services.TykService.TykDeveloper;
//import sg.gov.ssg.devportal.services.UserService;
//
///**
// * Implementation of user related services.
// *
// * @author EeKing
// */
//@Service
//public class UserServiceImpl implements UserService
//{
//    private static final Logger LOGGER = LoggerFactory.getLogger( UserServiceImpl.class );
//
//    private static final String ROLE_NOT_FOUND = "The role could not be found";
//    private static final String USER_NOT_FOUND = "The user could not be found";
//
//    @Value( "${spring.application.role.administrator}" )
//    private List<UUID> adminUserRoleIds;
//
//    @Value( "${spring.user.password.history.count}" )
//    private int pwdHistoryCount;
//
//    @Value( "${spring.user.login.max.attempts}" )
//    private int maxLoginAttempts;
//
//    private RoleRepository roleRepo;
//    private UserRepository userRepo;
//    private UserListingRepository userListRepo;
//    private UserLoginRepository userLoginRepo;
//    private TykService tykService;
//    private JiraRestService jiraRestService;
//
//    /**
//     * Constructs a new instance.
//     *
//     * @param roleRepo
//     * @param userRepo
//     * @param userListRepo
//     * @param userLoginRepo
//     */
//    public UserServiceImpl( RoleRepository roleRepo, UserRepository userRepo, UserListingRepository userListRepo,
//            UserLoginRepository userLoginRepo, TykService tykService, JiraRestService jiraRestService )
//    {
//        this.roleRepo = roleRepo;
//        this.userRepo = userRepo;
//        this.userListRepo = userListRepo;
//        this.userLoginRepo = userLoginRepo;
//        this.tykService = tykService;
//        this.jiraRestService = jiraRestService;
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public List<UUID> getAdminRoleId()
//    {
//        return Collections.unmodifiableList( this.adminUserRoleIds );
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Transactional( transactionManager = "devportal-txnmgr", readOnly = true )
//    public User.Role getRole( UUID id ) throws RecordNotFoundException
//    {
//        Optional<RoleEntity> optional = roleRepo.findByRef( id );
//        if ( !optional.isPresent() )
//        {
//            throw new RecordNotFoundException( ROLE_NOT_FOUND );
//        }
//
//        User.Role role = new User.Role();
//        role.loadEntity( optional.get() );
//        return role;
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Transactional( transactionManager = "devportal-txnmgr", readOnly = true )
//    public List<User.Role> getRoles()
//    {
//        List<RoleEntity> entities = roleRepo.findAll();
//
//        List<User.Role> roles = entities.stream().map( entity -> {
//            User.Role role = new User.Role();
//            role.loadEntity( entity );
//            return role;
//        } ).collect( Collectors.toList() );
//
//        return roles;
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Transactional( transactionManager = "devportal-txnmgr", readOnly = true )
//    public boolean isUsernameRegistered( String username )
//    {
//        SysmUserEntity condition = new SysmUserEntity();
//        condition.setUserName( username );
//
//        Optional<UserEntity> optional = userRepo.findOne(
//                Example.of( condition, ExampleMatcher.matching().withIgnoreCase() ) );
//
//        return optional.isPresent();
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Transactional( transactionManager = "devportal-txnmgr", readOnly = true )
//    public Optional<SysmUserEntity> findSystemUserByEmail( String email )
//    {
//        SysmUserEntity condition = new SysmUserEntity();
//        condition.setEmail( email );
//
//        Optional<SysmUserEntity> optional = userRepo.findOne(
//                Example.of( condition, ExampleMatcher.matching().withIgnoreCase() ) );
//
//        return optional;
//    }
//
//    /**
//     * {@inheritDoc}
//     *
//     * @throws TykApiException
//     */
//    @Override
//    @Transactional( transactionManager = "devportal-txnmgr", readOnly = false )
//    public User createNewUser( User newUser, boolean isDeveloper ) throws RecordNotFoundException, TykApiException
//    {
//        Assert.notNull( newUser, "User object must not be null" );
//        Assert.notNull( newUser.getFullName(), "User full name must not be null" );
//        Assert.notNull( newUser.getEmail(), "User email must not be null" );
//        Assert.notNull( newUser.getRole(), "User role object must not be null" );
//        Assert.notNull( newUser.getRole().getId(), "User role id must not be null" );
//        // TODO: created by automatically assigned by JPA, check not null of
//        // created by after that
//        Assert.notNull( newUser.getEnabled(), "Enabled must not be null" );
//
//        UserEntity entity = null;
//        if ( UserType.SYSM == newUser.getType() )
//        {
//            Assert.notNull( newUser.getActivationStatus(), "User status must not be null" );
//
//            entity = new SysmUserEntity();
//        }
//        else if ( UserType.GITHUB == newUser.getType() )
//        {
//            Assert.notNull( newUser.getGithubUserId(), "Github user id must not be null" );
//            Assert.notNull( newUser.getSignUpProgress(), "User sign up process must not be null" );
//
//            entity = new GithubUserEntity();
//        }
//        else if ( UserType.CORPPASS == newUser.getType() )
//        {
//            Assert.notNull( newUser.getCorppassUserId(), "CorpPass user id must not be null" );
//            Assert.notNull( newUser.getCorppassEntityType(), "CorpPass entity type must not be null" );
//            Assert.notNull( newUser.getCorppassEntityId(), "CorpPass entity id must not be null" );
//            Assert.notNull( newUser.getSignUpProgress(), "User sign up progress must not be null" );
//
//            entity = new CorpPassUserEntity();
//        }
//        else
//        {
//            throw new IllegalArgumentException( "Unrecognised user type - " + newUser.getType() );
//        }
//
//        populateForNewUserAccount( newUser, entity );
//
//        userRepo.saveAndFlush( entity );
//
//        if ( isDeveloper )
//        {
//            LOGGER.info( "Creating developer account in Tyk..." );
//
//            TykDeveloper developer = new TykDeveloper();
//            developer.loadUser( newUser );
//            tykService.createDeveloper( entity.getRef(), developer );
//        }
//
//        User user = new User();
//        user.loadUserEntity( entity );
//        LOGGER.info( "New user {} is created successfully.", user.getId() );
//
//        return user;
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Transactional( transactionManager = "devportal-txnmgr", readOnly = true )
//    public User getUser( UUID ref ) throws RecordNotFoundException
//    {
//        Optional<UserEntity> optional = userRepo.findOneByRef( ref );
//        if ( !optional.isPresent() )
//        {
//            throw new RecordNotFoundException( USER_NOT_FOUND );
//        }
//        User user = new User();
//        user.loadUserEntity( optional.get() );
//
//        return user;
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Transactional( transactionManager = "devportal-txnmgr", readOnly = true )
//    public User getSysmUser( String username ) throws RecordNotFoundException
//    {
//        SysmUserEntity condition = new SysmUserEntity();
//        condition.setUserName( username );
//
//        Optional<UserEntity> optional = userRepo.findOne(
//                Example.of( condition, ExampleMatcher.matching().withIgnoreCase() ) );
//        if ( !optional.isPresent() )
//        {
//            throw new RecordNotFoundException( USER_NOT_FOUND );
//        }
//
//        User user = new User();
//        user.loadUserEntity( optional.get() );
//
//        return user;
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Transactional( transactionManager = "devportal-txnmgr", readOnly = true )
//    public User getGithubUser( Long id ) throws RecordNotFoundException
//    {
//        GithubUserEntity condition = new GithubUserEntity();
//        condition.setGitHubUserId( id );
//
//        Optional<GithubUserEntity> optional = userRepo.findOne( Example.of( condition ) );
//        if ( !optional.isPresent() )
//        {
//            throw new RecordNotFoundException( USER_NOT_FOUND );
//        }
//
//        User user = new User();
//        user.loadUserEntity( optional.get() );
//
//        return user;
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Transactional( transactionManager = "devportal-txnmgr", readOnly = true )
//    public User getCorpPassUser( String id ) throws RecordNotFoundException
//    {
//        CorpPassUserEntity condition = new CorpPassUserEntity();
//        condition.setCorpPassUserId( id );
//
//        Optional<CorpPassUserEntity> optional = userRepo.findOne( Example.of( condition ) );
//        if ( optional.isEmpty() )
//        {
//            throw new RecordNotFoundException( USER_NOT_FOUND );
//        }
//
//        User user = new User();
//        user.loadUserEntity( optional.get() );
//
//        return user;
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Transactional( transactionManager = "devportal-txnmgr", readOnly = true )
//    public List<User> getUsers()
//    {
//        List<UserEntity> entities = userRepo.findAll();
//
//        List<User> users = entities.stream().map( entity -> {
//            User user = new User();
//            user.loadUserEntity( entity );
//            return user;
//        } ).collect( Collectors.toList() );
//
//        return users;
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Transactional( transactionManager = "devportal-txnmgr", readOnly = true )
//    public Page<UserListing> getUsers( Pageable page )
//    {
//        Page<UserListingEntity> results = userListRepo.findAll( page );
//
//        List<UserListing> users = results.getContent().stream().map( entity -> {
//            UserListing user = new UserListing();
//            loadUserListingEntity( user, entity );
//            return user;
//        } ).collect( Collectors.toList() );
//
//        return new PageImpl<>( users, results.getPageable(), results.getTotalElements() );
//    }
//
//    /**
//     * {@inheritDoc}
//     *
//     * @throws TykApiException
//     */
//    @Override
//    @Transactional( transactionManager = "devportal-txnmgr", readOnly = false )
//    public User updateUser( UUID id, Date version, User changes )
//            throws RecordNotFoundException, DataStaleException, TykApiException, JiraRestService.JiraApiException
//    {
//        Assert.notNull( id, "User id must not be null" );
//        Assert.notNull( version, "User version must not be null" );
//        Assert.notNull( changes, "User object must not be null" );
//        Assert.notNull( changes.getFullName(), "User full name must not be null" );
//        Assert.notNull( changes.getEmail(), "User email must not be null" );
//        Assert.notNull( changes.getRole(), "User role must not be null" );
//        Assert.notNull( changes.getRole().getId(), "Role id must not be null" );
//
//        // TODO: created by automatically assigned by JPA, check not null of
//        // lastModifiedBy after that
//        Assert.notNull( changes.getEnabled(), "Enabled must not be null" );
//
//        if ( changes.getType() == UserType.SYSM )
//        {
//            Assert.notNull( changes.getActivationStatus(), "User status must not be null" );
//        }
//        else if ( changes.getType() == UserType.GITHUB )
//        {
//            Assert.notNull( changes.getGithubUserId(), "Github user id must not be null" );
//            Assert.notNull( changes.getSignUpProgress(), "User sign up process must not be null" );
//        }
//        else if ( changes.getType() == UserType.CORPPASS )
//        {
//            Assert.notNull( changes.getCorppassUserId(), "CorpPass user id must not be null" );
//            Assert.notNull( changes.getCorppassEntityType(), "CorpPass entity type must not be null" );
//            Assert.notNull( changes.getCorppassEntityId(), "CorpPass entity id must not be null" );
//            Assert.notNull( changes.getSignUpProgress(), "User sign up progress must not be null" );
//        }
//
//        Optional<UserEntity> optional = userRepo.findOneByRef( id );
//        if ( !optional.isPresent() )
//        {
//            throw new RecordNotFoundException( USER_NOT_FOUND );
//        }
//
//        UserEntity entity = optional.get();
//
//        if ( entity.getVersion().compareTo( version ) != 0 )
//        {
//            throw new DataStaleException( "The user details retrieved previously already stale" );
//        }
//
//        boolean jiraUserUpdateRequired = UserType.CORPPASS == changes.getType() &&
//                ( !Objects.equals( entity.getEmail(), changes.getEmail() ) ||
//                        !Objects.equals( entity.getFullName(), changes.getFullName() ) );
//
//        populateForUpdateUserAccount( changes, entity, pwdHistoryCount );
//
//        try
//        {
//            userRepo.saveAndFlush( entity );
//        }
//        catch ( OptimisticLockException ex )
//        {
//            throw new DataStaleException( "The user details retrieved previously already stale", ex );
//        }
//
//        if ( entity.getTykDeveloper() != null )
//        {
//            TykDeveloper tykDeveloper = tykService.getDeveloper( id, changes.getEmail() );
//            if ( tykDeveloper != null )
//            {
//                tykDeveloper.loadUser( changes );
//                tykService.updateDeveloper( tykDeveloper );
//            }
//        }
//
//        if ( jiraUserUpdateRequired )
//        {
//            try
//            {
//                JiraRestService.User jiraUser = jiraRestService.getUser( entity.getRef() );
//                jiraUser.setDisplayName(
//                        JiraRestService.USER_DISPLAY_NAME_TEMPLATE.apply( entity.getFullName(),
//                                ( (CorpPassUserEntity) entity ).getCorpPassEntityId() ) );
//                jiraUser.setEmailAddress( changes.getEmail() );
//
//                jiraRestService.updateUser( jiraUser );
//            }
//            catch ( JiraRestService.JiraServiceDeskRecordNotFoundException e )
//            {
//                LOGGER.info( "Jira user account for this corpPass user " + entity.getRef() +
//                        " does not exist, no update required.", e );
//            }
//        }
//
//        User user = new User();
//        user.loadUserEntity( entity );
//        LOGGER.info( "User {} is updated successfully.", user.getId() );
//
//        return user;
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Transactional( transactionManager = "devportal-txnmgr", readOnly = true )
//    public boolean isUserAccountLockable( UserIdentity loginUser, UUID id ) throws RecordNotFoundException
//    {
//        User user = getUser( id );
//
//        boolean isUserAccountLockable = !loginUser.getUserId().equals( user.getId() );
//
//        if ( !isUserAccountLockable )
//        {
//            LOGGER.info( "User is not allowed to lock own account" );
//        }
//
//        return isUserAccountLockable;
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Transactional( transactionManager = "devportal-txnmgr", readOnly = true )
//    public boolean isUserAccountUnlockable( UserIdentity loginUser, UUID id ) throws RecordNotFoundException
//    {
//        User user = getUser( id );
//
//        boolean isUserAccountUnlockable = !loginUser.getUserId().equals( user.getId() );
//
//        if ( !isUserAccountUnlockable )
//        {
//            LOGGER.info( "User is not allowed to unlock own account" );
//        }
//
//        return isUserAccountUnlockable;
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Transactional( transactionManager = "devportal-txnmgr", readOnly = false )
//    public void loginAttempt( UUID id, boolean loginSucceeded ) throws RecordNotFoundException
//    {
//        // Using row lock on UserEntity table to eliminate concurrent update
//        Optional<UserEntity> optionalUser = userRepo.findOneByRefForUpdate( id );
//        if ( !optionalUser.isPresent() )
//        {
//            throw new RecordNotFoundException( USER_NOT_FOUND );
//        }
//
//        UserEntity userEntity = optionalUser.get();
//
//        UserLoginEntity userLoginEntity = new UserLoginEntity( userEntity.getId(), null, 0, null );
//        Optional<UserLoginEntity> optionalUserLogin = userLoginRepo.findById( userEntity.getId() );
//        if ( optionalUserLogin.isPresent() )
//        {
//            userLoginEntity = optionalUserLogin.get();
//        }
//
//        if ( loginSucceeded )
//        {
//            userLoginEntity.setLastSucceededAt( new Date() );
//            userLoginEntity.setFailedAttempts( 0 );
//        }
//        else
//        {
//            userLoginEntity.setLastFailedAt( new Date() );
//            userLoginEntity.setFailedAttempts( userLoginEntity.getFailedAttempts() + 1 );
//
//            LOGGER.info( "User '{}' failed login attempts increased to {}.", userEntity.getEmail(),
//                    userLoginEntity.getFailedAttempts() );
//        }
//
//        if ( userLoginEntity.getFailedAttempts() >= maxLoginAttempts )
//        {
//            ( (SysmUserEntity) userEntity ).setEnabled( false );
//
//            userRepo.saveAndFlush( userEntity );
//        }
//
//        userLoginRepo.saveAndFlush( userLoginEntity );
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Transactional( transactionManager = "devportal-txnmgr", readOnly = true )
//    public boolean validateUEN( String uen )
//    {
//        CorpPassUserEntity condition = new CorpPassUserEntity();
//        condition.setCorpPassEntityId( uen );
//
//        if ( userRepo.exists( Example.of( condition ) ) )
//        {
//            return true;
//        }
//
//        return false;
//    }
//
//    /**
//     * To load entity data.
//     *
//     * @param user
//     * @param entity
//     */
//    private void loadUserListingEntity( UserListing user, UserListingEntity entity )
//    {
//        user.setRef( entity.getRef() );
//        user.setVersion( entity.getVersion() );
//        user.setFullName( entity.getFullName() );
//        user.setEmail( entity.getEmail() );
//        user.setMobileNo( entity.getMobileNo() );
//        user.setEnabled( entity.getEnabled() );
//        user.setCreatedAt( entity.getCreatedAt() );
//        user.setRolesName( entity.getRolesName() );
//        user.setActivationStatus( entity.getActivationStatus() );
//        user.setDesc( entity.getDesc() );
//    }
//
//    /**
//     * Populate the specified entity for creating a new user.
//     *
//     * @param user   user model to create
//     * @param entity new user entity
//     * @throws RecordNotFoundException
//     */
//    private void populateForNewUserAccount( User user, UserEntity entity ) throws RecordNotFoundException
//    {
//        entity.setFullName( user.getFullName() );
//        entity.setEmail( user.getEmail() );
//        entity.setEnabled( Boolean.TRUE );
//
//        entity.clearRoles();
//        if ( user.getRole() != null && user.getRole().getId() != null )
//        {
//            Optional<RoleEntity> re = roleRepo.findByRef( user.getRole().getId() );
//            if ( !re.isPresent() )
//            {
//                throw new RecordNotFoundException( "Undefined user role - " + user.getRole().getId() );
//            }
//
//            entity.addRole( re.get() );
//        }
//
//        entity.setCreatedBy( user.getCreatedBy() );
//
//        if ( UserType.SYSM == user.getType() )
//        {
//            SysmUserEntity sysmUserEntity = (SysmUserEntity) entity;
//            sysmUserEntity.setMobileNo( user.getMobileNo() );
//            sysmUserEntity.setDesc( user.getDesc() );
//            sysmUserEntity.setActivationStatus( user.getActivationStatus() );
//            sysmUserEntity.setUserName( user.getEmail() );
//            sysmUserEntity.setTwoFaEnabled( Boolean.FALSE );
//
//            if ( user.getPwd() != null )
//            {
//                SysmUserPasswordEntity passwordEntity = new SysmUserPasswordEntity();
//                passwordEntity.setPassword( user.getPwd() );
//
//                sysmUserEntity.addPassword( passwordEntity );
//            }
//        }
//        else if ( user.getType() == UserType.GITHUB )
//        {
//            GithubUserEntity githubUserEntity = (GithubUserEntity) entity;
//            githubUserEntity.setGitHubUserId( user.getGithubUserId() );
//            githubUserEntity.setSignupProgress( user.getSignUpProgress() );
//        }
//        else if ( user.getType() == UserType.CORPPASS )
//        {
//            CorpPassUserEntity corpPassUserEntity = (CorpPassUserEntity) entity;
//            corpPassUserEntity.setCorpPassUserId( user.getCorppassUserId() );
//            corpPassUserEntity.setCorpPassEntityType( user.getCorppassEntityType() );
//            corpPassUserEntity.setCorpPassEntityId( user.getCorppassEntityId() );
//            corpPassUserEntity.setSignupProgress( user.getSignUpProgress() );
//        }
//    }
//
//    /**
//     * Populate the specified entity for updating an existing user.
//     *
//     * @param user            user model to update
//     * @param entity          current user entity
//     * @param pwdHistoryCount
//     * @throws RecordNotFoundException
//     */
//    private void populateForUpdateUserAccount( User user, UserEntity entity, int pwdHistoryCount )
//            throws RecordNotFoundException
//    {
//        entity.setLastModifiedBy( user.getLastModifiedBy() );
//
//        entity.setFullName( user.getFullName() );
//        entity.setEmail( user.getEmail() );
//        entity.setEnabled( user.getEnabled() );
//
//        entity.clearRoles();
//        if ( user.getRole() != null && user.getRole().getId() != null )
//        {
//            Optional<RoleEntity> re = roleRepo.findByRef( user.getRole().getId() );
//            if ( !re.isPresent() )
//            {
//                throw new RecordNotFoundException( "Undefined user role - " + user.getRole().getId() );
//            }
//
//            entity.addRole( re.get() );
//        }
//
//        if ( entity instanceof SysmUserEntity )
//        {
//            SysmUserEntity sysmUserEntity = (SysmUserEntity) entity;
//            sysmUserEntity.setMobileNo( user.getMobileNo() );
//            if ( user.getPwd() != null )
//            {
//                SysmUserPasswordEntity newPasswordEntity = new SysmUserPasswordEntity();
//                newPasswordEntity.setPassword( user.getPwd() );
//                newPasswordEntity.setCreatedAt( new Date() );
//                if ( !sysmUserEntity.getPasswords().isEmpty() )
//                {
//                    SysmUserPasswordEntity prevPasswordEntity = sysmUserEntity.getPasswords().iterator().next();
//                    if ( !user.getPwd().equals( prevPasswordEntity.getPassword() ) )
//                    {
//                        List<SysmUserPasswordEntity> passwords = new ArrayList<>();
//                        passwords.add( newPasswordEntity );
//                        passwords.addAll( sysmUserEntity.getPasswords() );
//                        if ( passwords.size() < pwdHistoryCount )
//                        {
//                            passwords = passwords.subList( 0, passwords.size() );
//                        }
//                        else
//                        {
//                            passwords = passwords.subList( 0, pwdHistoryCount );
//                        }
//
//                        sysmUserEntity.clearPasswords();
//                        passwords.stream().forEachOrdered( sysmUserEntity::addPassword );
//                    }
//                }
//                else
//                {
//                    sysmUserEntity.addPassword( newPasswordEntity );
//                }
//            }
//
//            sysmUserEntity.setDesc( user.getDesc() );
//            sysmUserEntity.setActivationStatus( user.getActivationStatus() );
//        }
//        else if ( entity instanceof GithubUserEntity )
//        {
//            GithubUserEntity githubUserEntity = (GithubUserEntity) entity;
//            githubUserEntity.setSignupProgress( user.getSignUpProgress() );
//        }
//        else if ( entity instanceof CorpPassUserEntity )
//        {
//            CorpPassUserEntity corpPassUserEntity = (CorpPassUserEntity) entity;
//            corpPassUserEntity.setSignupProgress( user.getSignUpProgress() );
//            corpPassUserEntity.setJiraUserId( user.getJiraUserId() );
//            corpPassUserEntity.setJiraOrgId( user.getJiraOrgId() );
//        }
//    }
//}
