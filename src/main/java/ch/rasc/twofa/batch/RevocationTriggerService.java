///*
// *                        SSG Public License Notice
// *
// *   This software is the intellectual property of SSG. The program
// *   may be used only in accordance with the terms of the license agreement you
// *   entered into with SSG.
// *
// *   2019 SkillsFuture Singapore (SSG). All rights reserved.
// *   1 Marina Boulevard
// *   #18-01 One Marina Boulevard
// *   Singapore 018989
// */
//
//package ch.rasc.twofa.batch;
//
//import java.util.Date;
//import java.util.List;
//
//
///**
// * Service to handle token revocation trigger record.
// *
// * @author XingJun
// */
//public interface RevocationTriggerService
//{
//    /**
//     * Get all revocation trigger record.
//     *
//     * @return
//     */
//    List<RevocationTriggerRecord> getAllRevocationTriggerRecord();
//
//    /**
//     * Update revocation trigger record.
//     *
//     * @return
//     */
//    RevocationTriggerRecord updateRevocationTriggerRecord( Long consentId, Integer failCount, Date triggeredAt )
//            throws ServiceError.RecordNotFoundException;
//
//    /**
//     * Delete revocation trigger record.
//     *
//     * @return
//     */
//    RevocationTriggerRecord deleteRevocationTriggerRecord( Long consentId ) throws ServiceError.RecordNotFoundException;
//
//    /**
//     * Revocation trigger record model
//     */
//    class RevocationTriggerRecord
//    {
//        private Long id;
//        private Long consentId;
//        private Integer failCount;
//        private Date createdAt;
//        private Date triggeredAt;
//
//        // Data required when doing revocation.
//        private String clientId;
//        @Sensitive
//        private String clientSecret;
//
//        public RevocationTriggerRecord()
//        {
//            this.id = null;
//            this.consentId = null;
//            this.failCount = null;
//            this.createdAt = null;
//            this.triggeredAt = null;
//            this.clientId = null;
//            this.clientSecret = null;
//        }
//
//        /**
//         * Get id.
//         *
//         * @return
//         */
//        public Long getId()
//        {
//            return id;
//        }
//
//        /**
//         * Set id.
//         *
//         * @param id
//         */
//        public void setId( Long id )
//        {
//            this.id = id;
//        }
//
//        /**
//         * Get consent id.
//         *
//         * @return
//         */
//        public Long getConsentId()
//        {
//            return consentId;
//        }
//
//        /**
//         * Set consent id.
//         *
//         * @param consentId
//         */
//        public void setConsentId( Long consentId )
//        {
//            this.consentId = consentId;
//        }
//
//        /**
//         * Get fail count.
//         *
//         * @return
//         */
//        public Integer getFailCount()
//        {
//            return failCount;
//        }
//
//        /**
//         * Set fail count.
//         *
//         * @param failCount
//         */
//        public void setFailCount( Integer failCount )
//        {
//            this.failCount = failCount;
//        }
//
//        /**
//         * Get create at date.
//         *
//         * @return
//         */
//        public Date getCreatedAt()
//        {
//            return DateUtils.clone( createdAt );
//        }
//
//        /**
//         * Set create at date.
//         *
//         * @param createdAt
//         */
//        public void setCreatedAt( Date createdAt )
//        {
//            this.createdAt = DateUtils.clone( createdAt );
//        }
//
//        /**
//         * Get triggered at date.
//         *
//         * @return
//         */
//        public Date getTriggeredAt()
//        {
//            return DateUtils.clone( triggeredAt );
//        }
//
//        /**
//         * Set triggered at date.
//         *
//         * @param triggeredAt
//         */
//        public void setTriggeredAt( Date triggeredAt )
//        {
//            this.triggeredAt = DateUtils.clone( triggeredAt );
//        }
//
//        /**
//         * Get client id.
//         *
//         * @return
//         */
//        public String getClientId()
//        {
//            return clientId;
//        }
//
//        /**
//         * Set client id.
//         *
//         * @param clientId
//         */
//        public void setClientId( String clientId )
//        {
//            this.clientId = clientId;
//        }
//
//        /**
//         * Get client secret.
//         *
//         * @return
//         */
//        public String getClientSecret()
//        {
//            return clientSecret;
//        }
//
//        /**
//         * Set client secret.
//         *
//         * @param clientSecret
//         */
//        public void setClientSecret( String clientSecret )
//        {
//            this.clientSecret = clientSecret;
//        }
//
//        /**
//         * Load entity to model.
//         *
//         * @param entity
//         */
//        public void loadRevocationTriggerRecord( RevocationTriggerEntity entity )
//        {
//            this.setId( entity.getId() );
//            this.setConsentId( entity.getConsent().getId() );
//            this.setCreatedAt( entity.getCreatedAt() );
//            this.setFailCount( entity.getFailCount() != null ? entity.getFailCount() : 0 );
//            this.setTriggeredAt( entity.getTriggeredAt() );
//            this.setClientId( entity.getConsent().getApp().getClientId() );
//        }
//
//        @Override
//        public String toString()
//        {
//            return "RevocationTriggerRecord{" +
//                    "id=" + id +
//                    ", consentId=" + consentId +
//                    ", createdAt=" + createdAt +
//                    ", triggeredAt=" + triggeredAt +
//                    ", failCount=" + failCount +
//                    ", clientId=" + clientId +
//                    ", clientSecret=" +
//                    ( clientSecret != null ? MaskSensitiveUtils.maskString( clientSecret, 0, clientSecret.length(),
//                            '*' ) : null ) + '\'' +
//                    '}';
//        }
//    }
//}
