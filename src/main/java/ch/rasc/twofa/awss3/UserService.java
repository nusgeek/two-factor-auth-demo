//package ch.rasc.twofa.awss3;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//
//import org.hibernate.Hibernate;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Date;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//import sg.gov.ssg.devportal.common.RoleCategory;
//import sg.gov.ssg.devportal.common.SignupProgress;
//import sg.gov.ssg.devportal.common.UserAcctStatus;
//import sg.gov.ssg.devportal.common.UserIdentity;
//import sg.gov.ssg.devportal.common.UserType;
//import sg.gov.ssg.devportal.common.logging.MaskSensitiveInfoToStringBuilder;
//import sg.gov.ssg.devportal.common.logging.Sensitive;
//import sg.gov.ssg.devportal.common.utils.DateUtils;
//import sg.gov.ssg.devportal.repositories.devportal.entities.CorpPassUserEntity;
//import sg.gov.ssg.devportal.repositories.devportal.entities.GithubUserEntity;
//import sg.gov.ssg.devportal.repositories.devportal.entities.PrivilegeEntity;
//import sg.gov.ssg.devportal.repositories.devportal.entities.RoleEntity;
//import sg.gov.ssg.devportal.repositories.devportal.entities.SysmUserEntity;
//import sg.gov.ssg.devportal.repositories.devportal.entities.SysmUserPasswordEntity;
//import sg.gov.ssg.devportal.repositories.devportal.entities.UserEntity;
//import sg.gov.ssg.devportal.services.ServiceError.DataStaleException;
//import sg.gov.ssg.devportal.services.ServiceError.RecordNotFoundException;
//import sg.gov.ssg.devportal.services.ServiceError.TykApiException;
//
///**
// * User related services.
// *
// * @author EeKing
// */
//public interface UserService
//{
//    /**
//     * Get the administrator role Ids.
//     *
//     * @return administrator role Ids
//     */
//    List<UUID> getAdminRoleId();
//
//    /**
//     * Get the specified user role by Id.
//     *
//     * @param id
//     * @return user role
//     * @throws RecordNotFoundException
//     */
//    User.Role getRole( UUID id ) throws RecordNotFoundException;
//
//    /**
//     * Get all available user roles.
//     *
//     * @return user roles
//     */
//    List<User.Role> getRoles();
//
//    /**
//     * Check if the user name was registered by an existing account.
//     *
//     * @param username user name
//     * @return boolean
//     */
//    boolean isUsernameRegistered( String username );
//
//    /**
//     * Check if the email was registered under system user.
//     *
//     * @param email
//     * @return User
//     */
//    Optional<SysmUserEntity> findSystemUserByEmail( String email );
//
//    /**
//     * Create a new user.
//     *
//     * @param newUser new user
//     * @return User
//     * @throws TykApiException
//     */
//    User createNewUser( User newUser, boolean isDeveloper ) throws RecordNotFoundException, TykApiException;
//
//    /**
//     * Get user details by reference Id.
//     *
//     * @param id
//     * @return user
//     * @throws RecordNotFoundException
//     */
//    User getUser( UUID id ) throws RecordNotFoundException;
//
//    /**
//     * Get user details by username.
//     *
//     * @param username
//     * @return user
//     * @throws RecordNotFoundException
//     */
//    User getSysmUser( String username ) throws RecordNotFoundException;
//
//    /**
//     * Get GitHub User by GitHub User ID.
//     *
//     * @param id
//     * @return user
//     * @throws RecordNotFoundException
//     */
//    User getGithubUser( Long id ) throws RecordNotFoundException;
//
//    /**
//     * Retrieve existing users.
//     *
//     * @return List<User>
//     */
//    List<User> getUsers();
//
//    /**
//     * Get sorted users with pagination.
//     *
//     * @param page
//     * @return
//     */
//    Page<UserListing> getUsers( Pageable page );
//
//    /**
//     * Update an existing user.
//     *
//     * @param id
//     * @param version the version of the retrieved record
//     * @param changes user changes
//     * @return updated user
//     * @throws RecordNotFoundException
//     * @throws DataStaleException
//     * @throws TykApiException
//     */
//    User updateUser( UUID id, Date version, User changes )
//            throws RecordNotFoundException, DataStaleException, TykApiException, JiraRestService.JiraApiException;
//
//    /**
//     * Check the access to lock user account
//     *
//     * @param loginUser
//     * @param id
//     * @return access to lock user account
//     * @throws RecordNotFoundException
//     */
//    boolean isUserAccountLockable( UserIdentity loginUser, UUID id ) throws RecordNotFoundException;
//
//    /**
//     * Check the access to unlock user account
//     *
//     * @param loginUser
//     * @param id
//     * @return access to unlock user account
//     * @throws RecordNotFoundException
//     */
//    boolean isUserAccountUnlockable( UserIdentity loginUser, UUID id ) throws RecordNotFoundException;
//
//    /**
//     * Capture user login attempt.
//     *
//     * @param id
//     * @param loginSucceeded
//     */
//    void loginAttempt( UUID id, boolean loginSucceeded ) throws RecordNotFoundException;
//
//    /**
//     * Get CorpPass User by CorpPass User ID.
//     *
//     * @param id
//     * @return
//     * @throws RecordNotFoundException
//     */
//    User getCorpPassUser( String id ) throws RecordNotFoundException;
//
//    /**
//     * validate UEN
//     *
//     * @param uen
//     * @return true if UEN is valid else false
//     */
//    boolean validateUEN( String uen );
//
//    /**
//     * User service model.
//     *
//     * @author EeKing
//     */
//    class User implements Serializable
//    {
//        private static final long serialVersionUID = 1L;
//
//        private UUID id;
//        private UserType type;
//        private String fullName;
//        private String username; // Login ID for System-managed user
//        private String email;
//        private String mobileNo;
//        private Date pwdLastModifiedDate;
//
//        private Long githubUserId;
//
//        private String corppassUserId;
//        private String corppassEntityType;
//        private String corppassEntityId;
//        private String jiraOrgId;
//        private String jiraUserId;
//
//        private SignupProgress signUpProgress;
//
//        // User current password
//        @Sensitive
//        private String pwd;
//
//        // User password history (including current password)
//        @Sensitive
//        private List<String> passwordHistory;
//
//        private UserAcctStatus activationStatus;
//        private Boolean enabled;
//        private Boolean twoFaEnabled;
//
//        private Role role;
//        private String desc;
//        private Date createdAt;
//        private Long createdBy;
//        private Date lastModifiedAt;
//        private Long lastModifiedBy;
//        private Date version;
//
//        /**
//         * Constructs a new instance.
//         */
//        public User()
//        {
//            this.id = null;
//            this.type = null;
//            this.fullName = null;
//            this.username = null;
//            this.email = null;
//            this.mobileNo = null;
//            this.pwdLastModifiedDate = null;
//            this.githubUserId = null;
//            this.signUpProgress = null;
//            this.pwd = null;
//            this.passwordHistory = new ArrayList<>();
//            this.activationStatus = null;
//            this.enabled = null;
//            this.twoFaEnabled = null;
//            this.corppassUserId = null;
//            this.corppassEntityType = null;
//            this.corppassEntityId = null;
//            this.jiraOrgId = null;
//            this.jiraUserId = null;
//            this.role = null;
//            this.desc = null;
//            this.createdAt = null;
//            this.createdBy = null;
//            this.lastModifiedAt = null;
//            this.lastModifiedBy = null;
//            this.version = null;
//        }
//
//        /**
//         * Constructs a new instance.
//         *
//         * @param user
//         */
//        public User( User user )
//        {
//            this.id = user.getId();
//            this.type = user.getType();
//            this.fullName = user.getFullName();
//            this.username = user.getUsername();
//            this.email = user.getEmail();
//            this.mobileNo = user.getMobileNo();
//            this.pwdLastModifiedDate = user.getPwdLastModifiedDate();
//            this.githubUserId = user.getGithubUserId();
//            this.signUpProgress = user.getSignUpProgress();
//            this.pwd = user.getPwd();
//            this.passwordHistory = new ArrayList<>( user.getPasswordHistory() );
//            this.activationStatus = user.getActivationStatus();
//            this.enabled = user.getEnabled();
//            this.twoFaEnabled = user.getTwoFaEnabled();
//            this.corppassUserId = user.getCorppassUserId();
//            this.corppassEntityType = user.getCorppassEntityType();
//            this.corppassEntityId = user.getCorppassUserId();
//            this.jiraOrgId = user.getJiraOrgId();
//            this.jiraUserId = user.getJiraUserId();
//            this.role = new Role( user.getRole() );
//            this.desc = user.getDesc();
//            this.createdAt = user.getCreatedAt();
//            this.createdBy = user.getCreatedBy();
//            this.lastModifiedAt = user.getLastModifiedAt();
//            this.lastModifiedBy = user.getLastModifiedBy();
//            this.version = user.getVersion();
//        }
//
//        /**
//         * Get user Id.
//         *
//         * @return UUID
//         */
//        public UUID getId()
//        {
//            return id;
//        }
//
//        /**
//         * Set user id.
//         *
//         * @param id id
//         */
//        public void setId( UUID id )
//        {
//            this.id = id;
//        }
//
//        /**
//         * Get user type.
//         *
//         * @return UserType
//         */
//        public UserType getType()
//        {
//            return type;
//        }
//
//        /**
//         * Set user type.
//         *
//         * @param type type
//         */
//        public void setType( UserType type )
//        {
//            this.type = type;
//        }
//
//        /**
//         * Get user assigned roles.
//         *
//         * @return Role
//         */
//        public Role getRole()
//        {
//            return role;
//        }
//
//        /**
//         * Set user assigned roles.
//         *
//         * @param role role
//         */
//        public void setRole( Role role )
//        {
//            this.role = role;
//        }
//
//        /**
//         * Get user description.
//         *
//         * @return String
//         */
//        public String getDesc()
//        {
//            return desc;
//        }
//
//        /**
//         * Set user description.
//         *
//         * @param desc description
//         */
//        public void setDesc( String desc )
//        {
//            this.desc = desc;
//        }
//
//        /**
//         * Get user full name.
//         *
//         * @return String
//         */
//        public String getFullName()
//        {
//            return fullName;
//        }
//
//        /**
//         * Set user full name.
//         *
//         * @param fullName name
//         */
//        public void setFullName( String fullName )
//        {
//            this.fullName = fullName;
//        }
//
//        /**
//         * Get user email.
//         *
//         * @return String
//         */
//        public String getEmail()
//        {
//            return email;
//        }
//
//        /**
//         * Set user email.
//         *
//         * @param email email
//         */
//        public void setEmail( String email )
//        {
//            this.email = email;
//        }
//
//        /**
//         * Get user mobile number.
//         *
//         * @return String
//         */
//        public String getMobileNo()
//        {
//            return mobileNo;
//        }
//
//        /**
//         * Set user mobile number.
//         *
//         * @param mobileNo mobile number
//         */
//        public void setMobileNo( String mobileNo )
//        {
//            this.mobileNo = mobileNo;
//        }
//
//        /**
//         * Get password last modified date.
//         *
//         * @return Date
//         */
//        public Date getPwdLastModifiedDate()
//        {
//            return DateUtils.clone( pwdLastModifiedDate );
//        }
//
//        /**
//         * Set password last modified date.
//         *
//         * @param pwdLastModifiedDate pwdLastModifiedDate
//         */
//        public void setPwdLastModifiedDate( Date pwdLastModifiedDate )
//        {
//            this.pwdLastModifiedDate = DateUtils.clone( pwdLastModifiedDate );
//        }
//
//        /**
//         * Get GitHub User Id.
//         *
//         * @return Long
//         */
//        public Long getGithubUserId()
//        {
//            return githubUserId;
//        }
//
//        /**
//         * Set GitHub User Id.
//         *
//         * @param gitHubUserId
//         */
//        public void setGithubUserId( Long gitHubUserId )
//        {
//            this.githubUserId = gitHubUserId;
//        }
//
//        /**
//         * Get GitHub User Sign Up Progress.
//         *
//         * @return SignupProgress
//         */
//        public SignupProgress getSignUpProgress()
//        {
//            return signUpProgress;
//        }
//
//        /**
//         * Set GitHub User Sign Up Progress.
//         *
//         * @param signUpProgress
//         */
//        public void setSignUpProgress( SignupProgress signUpProgress )
//        {
//            this.signUpProgress = signUpProgress;
//        }
//
//        /**
//         * Get user password.
//         *
//         * @return String
//         */
//        public String getPwd()
//        {
//            return pwd;
//        }
//
//        /**
//         * Set user password.
//         *
//         * @param pwd password
//         */
//        public void setPwd( String pwd )
//        {
//            this.pwd = pwd;
//        }
//
//        /**
//         * Get user password history.
//         *
//         * @return password history (including the current password)
//         */
//        public List<String> getPasswordHistory()
//        {
//            if ( passwordHistory == null )
//            {
//                return Collections.emptyList();
//            }
//
//            return Collections.unmodifiableList( passwordHistory );
//        }
//
//        /**
//         * Set user password history.
//         *
//         * @param passwords
//         */
//        public void setPasswordHistory( List<String> passwords )
//        {
//            if ( this.passwordHistory == null )
//            {
//                this.passwordHistory = new ArrayList<>();
//            }
//
//            this.passwordHistory.clear();
//            this.passwordHistory.addAll( passwords );
//        }
//
//        /**
//         * Get user activation status.
//         *
//         * @return UserAcctStatus
//         */
//        public UserAcctStatus getActivationStatus()
//        {
//            return activationStatus;
//        }
//
//        /**
//         * Set user activation status.
//         *
//         * @param activationStatus activationStatus
//         */
//        public void setActivationStatus( UserAcctStatus activationStatus )
//        {
//            this.activationStatus = activationStatus;
//        }
//
//        /**
//         * Get account enable status.
//         *
//         * @return Boolean
//         */
//        public Boolean getEnabled()
//        {
//            return enabled;
//        }
//
//        /**
//         * Set account enable status.
//         *
//         * @param enabled enabled
//         */
//        public void setEnabled( Boolean enabled )
//        {
//            this.enabled = enabled;
//        }
//
//        /**
//         * Get account twoFa enable status
//         *
//         * @return Boolean
//         */
//        public Boolean getTwoFaEnabled()
//        {
//            return twoFaEnabled;
//        }
//
//        /**
//         * Set account twoFa enable status
//         *
//         * @param twoFaEnabled twoFaEnabled
//         */
//        public void setTwoFaEnabled( Boolean twoFaEnabled )
//        {
//            this.twoFaEnabled = twoFaEnabled;
//        }
//
//        /**
//         * Get corppass user id.
//         *
//         * @return String
//         */
//        public String getCorppassUserId()
//        {
//            return corppassUserId;
//        }
//
//        /**
//         * Set corppass user id.
//         *
//         * @param corppassUserId corppassUserId
//         */
//        public void setCorppassUserId( String corppassUserId )
//        {
//            this.corppassUserId = corppassUserId;
//        }
//
//        /**
//         * Get corppass entity type.
//         *
//         * @return String
//         */
//        public String getCorppassEntityType()
//        {
//            return corppassEntityType;
//        }
//
//        /**
//         * Set corppass entity type.
//         *
//         * @param corppassEntityType corppassEntityType
//         */
//        public void setCorppassEntityType( String corppassEntityType )
//        {
//            this.corppassEntityType = corppassEntityType;
//        }
//
//        /**
//         * Get corppass entity id.
//         *
//         * @return String
//         */
//        public String getCorppassEntityId()
//        {
//            return corppassEntityId;
//        }
//
//        /**
//         * Set corppass entity id.
//         *
//         * @param corppassEntityId corppassEntityId
//         */
//        public void setCorppassEntityId( String corppassEntityId )
//        {
//            this.corppassEntityId = corppassEntityId;
//        }
//
//        /**
//         * Get Jira Org ID.
//         *
//         * @return String
//         */
//        public String getJiraOrgId()
//        {
//            return jiraOrgId;
//        }
//
//        /**
//         * Set Jira Org ID.
//         *
//         * @param jiraOrgId jiraOrgId
//         */
//        public void setJiraOrgId( String jiraOrgId )
//        {
//            this.jiraOrgId = jiraOrgId;
//        }
//
//        /**
//         * Get Jira User ID.
//         *
//         * @return String
//         */
//        public String getJiraUserId()
//        {
//            return jiraUserId;
//        }
//
//        /**
//         * Set Jira User ID.
//         *
//         * @param jiraUserId jiraUserId
//         */
//        public void setJiraUserId( String jiraUserId )
//        {
//            this.jiraUserId = jiraUserId;
//        }
//
//        /**
//         * {@inheritDoc}
//         */
//        public String getUsername()
//        {
//            return username;
//        }
//
//        /**
//         * Set user name.
//         *
//         * @param userName
//         */
//        public void setUsername( String username )
//        {
//            this.username = username;
//        }
//
//        /**
//         * Get user creation date/time.
//         *
//         * @return Date
//         */
//        public Date getCreatedAt()
//        {
//            return DateUtils.clone( createdAt );
//        }
//
//        /**
//         * Set user creation date/time.
//         *
//         * @param createdAt createdAt
//         */
//        public void setCreatedAt( Date createdAt )
//        {
//            this.createdAt = DateUtils.clone( createdAt );
//        }
//
//        /**
//         * Get record creator id.
//         *
//         * @return Long
//         */
//        public Long getCreatedBy()
//        {
//            return createdBy;
//        }
//
//        /**
//         * Set record creator id.
//         *
//         * @param createdBy record creator id
//         */
//        public void setCreatedBy( Long createdBy )
//        {
//            this.createdBy = createdBy;
//        }
//
//        /**
//         * Get user last modified date/time.
//         *
//         * @return Date
//         */
//        public Date getLastModifiedAt()
//        {
//            return DateUtils.clone( lastModifiedAt );
//        }
//
//        /**
//         * Set user last modified date/time.
//         *
//         * @param lastModifiedAt lastModifiedAt
//         */
//        public void setLastModifiedAt( Date lastModifiedAt )
//        {
//            this.lastModifiedAt = DateUtils.clone( lastModifiedAt );
//        }
//
//        /**
//         * Get user that last modified the record.
//         *
//         * @return Long
//         */
//        public Long getLastModifiedBy()
//        {
//            return lastModifiedBy;
//        }
//
//        /**
//         * Set user that last modified the record.
//         *
//         * @param lastModifiedBy
//         */
//        public void setLastModifiedBy( Long lastModifiedBy )
//        {
//            this.lastModifiedBy = lastModifiedBy;
//        }
//
//        /**
//         * Get version.
//         *
//         * @return Date
//         */
//        public Date getVersion()
//        {
//            return DateUtils.clone( version );
//        }
//
//        /**
//         * Set version.
//         *
//         * @param version
//         */
//        public void setVersion( Date version )
//        {
//            this.version = DateUtils.clone( version );
//        }
//
//        /**
//         * {@inheritDoc}
//         */
//        @Override
//        public String toString()
//        {
//            return MaskSensitiveInfoToStringBuilder.toString( this );
//        }
//
//        /**
//         * User role service model.
//         *
//         * @author Kelvin
//         */
//        public static class Role implements Serializable
//        {
//            private static final long serialVersionUID = 1L;
//
//            private UUID id;
//            private String name;
//            private RoleCategory category;
//            private List<Privilege> privileges;
//
//            /**
//             * Constructs a new instance.
//             */
//            public Role()
//            {
//                this( (UUID) null );
//            }
//
//            /**
//             * Constructs a new instance.
//             *
//             * @param id
//             */
//            public Role( UUID id )
//            {
//                this( id, null, null );
//            }
//
//            /**
//             * Constructs a new instance.
//             *
//             * @param id
//             * @param name
//             * @param category
//             */
//            public Role( UUID id, String name, RoleCategory category )
//            {
//                this( id, name, category, null );
//            }
//
//            /**
//             * Constructs a new instance.
//             *
//             * @param id
//             * @param name
//             * @param privileges
//             */
//            public Role( UUID id, String name, RoleCategory category, List<Privilege> privileges )
//            {
//                this.id = id;
//                this.name = name;
//                this.category = category;
//
//                this.privileges = new ArrayList<>();
//                if ( privileges != null )
//                {
//                    this.privileges.addAll( privileges );
//                }
//            }
//
//            /**
//             * Constructs a new instance.
//             *
//             * @param role
//             */
//            public Role( Role role )
//            {
//                this.id = role.getId();
//                this.name = role.getName();
//            }
//
//            /**
//             * Get role id.
//             *
//             * @return UUID
//             */
//            public UUID getId()
//            {
//                return id;
//            }
//
//            /**
//             * Set role id.
//             *
//             * @param id id
//             */
//            public void setId( UUID id )
//            {
//                this.id = id;
//            }
//
//            /**
//             * Get role name.
//             *
//             * @return String
//             */
//            public String getName()
//            {
//                return name;
//            }
//
//            /**
//             * Set role name.
//             *
//             * @param name name
//             */
//            public void setName( String name )
//            {
//                this.name = name;
//            }
//
//            /**
//             * Get category.
//             *
//             * @return RoleCategory
//             */
//            public RoleCategory getCategory()
//            {
//                return category;
//            }
//
//            /**
//             * Set category.
//             *
//             * @param category category
//             */
//            public void setCategory( RoleCategory category )
//            {
//                this.category = category;
//            }
//
//            /**
//             * Get role privileges.
//             *
//             * @return privileges
//             */
//            public List<Privilege> getPrivileges()
//            {
//                return Collections.unmodifiableList( privileges );
//            }
//
//            /**
//             * To load entity data.
//             *
//             * @param entity
//             */
//            public void loadEntity( RoleEntity entity )
//            {
//                this.id = entity.getRef();
//                this.name = entity.getName();
//                this.category = entity.getCategory();
//
//                this.privileges.addAll(
//                        entity.getPrivileges().stream().map( e -> new Privilege( e.getName() ) ).collect(
//                                Collectors.toList() ) );
//            }
//
//            /**
//             * Convert role & privileges to Spring Security GrantedAuthority
//             * object.
//             *
//             * @return granted authorities
//             */
//            @JsonIgnore
//            public Collection<GrantedAuthority> getAuthorities()
//            {
//                final Set<String> privilegeSet = new LinkedHashSet<>();
//
//                privilegeSet.add( this.name );
//                privilegeSet.addAll( this.privileges.stream().map( Privilege::getName ).collect( Collectors.toSet() ) );
//
//                return privilegeSet.stream().map( SimpleGrantedAuthority::new ).collect( Collectors.toSet() );
//            }
//
//            /**
//             * {@inheritDoc}
//             */
//            @Override
//            public String toString()
//            {
//                return MaskSensitiveInfoToStringBuilder.toString( this );
//            }
//
//            /**
//             * User privilege model.
//             *
//             * @author Kelvin
//             */
//            public static class Privilege implements Serializable
//            {
//                private static final long serialVersionUID = 1L;
//
//                private String name;
//
//                /**
//                 * Constructs a new instance.
//                 */
//                public Privilege()
//                {
//                    this( null );
//                }
//
//                /**
//                 * Constructs a new instance.
//                 *
//                 * @param name
//                 */
//                public Privilege( String name )
//                {
//                    this.name = name;
//                }
//
//                /**
//                 * Get privilege name.
//                 *
//                 * @return String
//                 */
//                public String getName()
//                {
//                    return name;
//                }
//
//                /**
//                 * Set privilege name.
//                 *
//                 * @param name name
//                 */
//                public void setName( String name )
//                {
//                    this.name = name;
//                }
//
//                /**
//                 * To load entity data.
//                 *
//                 * @param entity
//                 */
//                public void loadEntity( PrivilegeEntity entity )
//                {
//                    this.name = entity.getName();
//                }
//
//                /**
//                 * {@inheritDoc}
//                 */
//                @Override
//                public String toString()
//                {
//                    return MaskSensitiveInfoToStringBuilder.toString( this );
//                }
//            }
//        }
//
//        public void loadUserEntity( UserEntity entity )
//        {
//            entity = (UserEntity) Hibernate.unproxy( entity );
//
//            this.id = entity.getRef();
//
//            // Initialise to null first
//            this.type = null;
//
//            this.fullName = entity.getFullName();
//            this.email = entity.getEmail();
//
//            Collection<RoleEntity> roleEntities = entity.getRoles();
//            if ( !roleEntities.isEmpty() )
//            {
//                // Each user has only one role
//                RoleEntity roleEntity = roleEntities.iterator().next();
//
//                this.role = new Role();
//                this.role.loadEntity( roleEntity );
//            }
//
//            if ( entity instanceof SysmUserEntity )
//            {
//                this.type = UserType.SYSM;
//
//                SysmUserEntity sysmUserEntity = (SysmUserEntity) entity;
//
//                this.username = sysmUserEntity.getUserName();
//                this.mobileNo = sysmUserEntity.getMobileNo();
//                this.twoFaEnabled = sysmUserEntity.getTwoFaEnabled();
//
//                if ( sysmUserEntity.getPasswords().isEmpty() )
//                {
//                    this.pwd = null;
//                }
//                else
//                {
//                    SysmUserPasswordEntity currentPwd = sysmUserEntity.getPasswords().iterator().next();
//                    this.pwd = currentPwd.getPassword();
//                    this.pwdLastModifiedDate = currentPwd.getCreatedAt();
//                }
//
//                List<String> passwordHistoryList = new ArrayList<>();
//                sysmUserEntity.getPasswords().forEach( e -> passwordHistoryList.add( e.getPassword() ) );
//                this.passwordHistory = passwordHistoryList;
//
//                this.activationStatus = sysmUserEntity.getActivationStatus();
//                this.desc = sysmUserEntity.getDesc();
//            }
//
//            if ( entity instanceof GithubUserEntity )
//            {
//                this.type = UserType.GITHUB;
//
//                GithubUserEntity githubUserEntity = (GithubUserEntity) entity;
//
//                this.githubUserId = githubUserEntity.getGitHubUserId();
//                this.signUpProgress = githubUserEntity.getSignupProgress();
//            }
//            else if ( entity instanceof CorpPassUserEntity )
//            {
//                CorpPassUserEntity corppassUserEntity = (CorpPassUserEntity) entity;
//
//                this.type = UserType.CORPPASS;
//                this.signUpProgress = corppassUserEntity.getSignupProgress();
//                this.corppassUserId = corppassUserEntity.getCorpPassUserId();
//                this.corppassEntityType = corppassUserEntity.getCorpPassEntityType();
//                this.corppassEntityId = corppassUserEntity.getCorpPassEntityId();
//                this.jiraUserId = corppassUserEntity.getJiraUserId();
//                this.jiraOrgId = corppassUserEntity.getJiraOrgId();
//            }
//
//            this.enabled = entity.getEnabled();
//            this.createdBy = entity.getCreatedBy();
//            this.createdAt = entity.getCreatedAt();
//            this.createdBy = entity.getCreatedBy();
//            this.lastModifiedAt = entity.getLastModifiedAt();
//            this.lastModifiedBy = entity.getLastModifiedBy();
//            this.version = entity.getVersion();
//        }
//    }
//
//    /**
//     * User model used in table listing.
//     *
//     * @author Kelvin
//     */
//    public static class UserListing
//    {
//        private UUID ref;
//        private String fullName;
//        private String email;
//        private String mobileNo;
//        private String rolesName;
//        private UserAcctStatus activationStatus;
//        private Boolean enabled;
//        private Date createdAt;
//        private String desc;
//        private Date version;
//
//        /**
//         * Constructs a new instance.
//         */
//        public UserListing()
//        {
//            this.ref = null;
//            this.fullName = null;
//            this.email = null;
//            this.mobileNo = null;
//            this.rolesName = null;
//            this.activationStatus = null;
//            this.enabled = null;
//            this.createdAt = null;
//            this.desc = null;
//            this.version = null;
//        }
//
//        /**
//         * Get field.
//         *
//         * @return UUID
//         */
//        public UUID getRef()
//        {
//            return ref;
//        }
//
//        /**
//         * Set field.
//         *
//         * @param ref ref
//         */
//        public void setRef( UUID ref )
//        {
//            this.ref = ref;
//        }
//
//        /**
//         * Get version.
//         *
//         * @return Date
//         */
//        public Date getVersion()
//        {
//            return DateUtils.clone( version );
//        }
//
//        /**
//         * Set version.
//         *
//         * @param version
//         */
//        public void setVersion( Date version )
//        {
//            this.version = DateUtils.clone( version );
//        }
//
//        /**
//         * Get field.
//         *
//         * @return String
//         */
//        public String getFullName()
//        {
//            return fullName;
//        }
//
//        /**
//         * Set field.
//         *
//         * @param fullName fullName
//         */
//        public void setFullName( String fullName )
//        {
//            this.fullName = fullName;
//        }
//
//        /**
//         * Get field.
//         *
//         * @return String
//         */
//        public String getEmail()
//        {
//            return email;
//        }
//
//        /**
//         * Set field.
//         *
//         * @param email email
//         */
//        public void setEmail( String email )
//        {
//            this.email = email;
//        }
//
//        /**
//         * Get field.
//         *
//         * @return String
//         */
//        public String getMobileNo()
//        {
//            return mobileNo;
//        }
//
//        /**
//         * Set field.
//         *
//         * @param mobileNo mobileNo
//         */
//        public void setMobileNo( String mobileNo )
//        {
//            this.mobileNo = mobileNo;
//        }
//
//        /**
//         * Get field.
//         *
//         * @return String
//         */
//        public String getRolesName()
//        {
//            return rolesName;
//        }
//
//        /**
//         * Set field.
//         *
//         * @param rolesName rolesName
//         */
//        public void setRolesName( String rolesName )
//        {
//            this.rolesName = rolesName;
//        }
//
//        /**
//         * Get field.
//         *
//         * @return UserAcctStatus
//         */
//        public UserAcctStatus getActivationStatus()
//        {
//            return activationStatus;
//        }
//
//        /**
//         * Set field.
//         *
//         * @param activationStatus activationStatus
//         */
//        public void setActivationStatus( UserAcctStatus activationStatus )
//        {
//            this.activationStatus = activationStatus;
//        }
//
//        /**
//         * Get field.
//         *
//         * @return Boolean
//         */
//        public Boolean getEnabled()
//        {
//            return enabled;
//        }
//
//        /**
//         * Set field.
//         *
//         * @param enabled enabled
//         */
//        public void setEnabled( Boolean enabled )
//        {
//            this.enabled = enabled;
//        }
//
//        /**
//         * Get field.
//         *
//         * @return Date
//         */
//        public Date getCreatedAt()
//        {
//            return DateUtils.clone( createdAt );
//        }
//
//        /**
//         * Set field.
//         *
//         * @param createdAt createdAt
//         */
//        public void setCreatedAt( Date createdAt )
//        {
//            this.createdAt = DateUtils.clone( createdAt );
//        }
//
//        /**
//         * Get field.
//         *
//         * @return String
//         */
//        public String getDesc()
//        {
//            return desc;
//        }
//
//        /**
//         * Set field.
//         *
//         * @param desc desc
//         */
//        public void setDesc( String desc )
//        {
//            this.desc = desc;
//        }
//
//        /**
//         * {@inheritDoc}
//         */
//        @Override
//        public String toString()
//        {
//            return MaskSensitiveInfoToStringBuilder.toString( this );
//        }
//    }
//}
