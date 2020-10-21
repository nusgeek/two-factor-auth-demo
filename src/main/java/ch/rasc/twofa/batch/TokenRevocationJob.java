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
//import org.quartz.DisallowConcurrentExecution;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.quartz.QuartzJobBean;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import sg.gov.ssg.consentportal.common.logging.AuditLogger;
//import sg.gov.ssg.consentportal.services.RevocationTriggerService;
//import sg.gov.ssg.consentportal.services.ServiceError;
//import sg.gov.ssg.consentportal.services.TokenRevocationService;
//
///**
// * Job implementation for token revocation
// *
// * @author XingJun
// */
//@DisallowConcurrentExecution
//public class TokenRevocationJob extends QuartzJobBean
//{
//    private static final Logger LOGGER = LoggerFactory.getLogger( TokenRevocationJob.class );
//
//    @Autowired
//    private RevocationTriggerService revocationTriggerService;
//
//    @Autowired
//    private TokenRevocationService tokenService;
//
//    @Override
//    protected void executeInternal( JobExecutionContext context ) throws JobExecutionException
//    {
//        LOGGER.info( "Executing Job with key {}", context.getJobDetail().getKey() );
//
//        // Get list of revocation record to be triggered.
//        List<RevocationTriggerService.RevocationTriggerRecord> revocationTriggerRecords = revocationTriggerService.getAllRevocationTriggerRecord();
//
//        LOGGER.info( "The revocation records list to be triggered: {}", revocationTriggerRecords );
//
//        // If exist, start revocation activity.
//        if ( !revocationTriggerRecords.isEmpty() )
//        {
//            triggerRevocation( context, revocationTriggerRecords );
//        }
//
//        LOGGER.info( "Completed job with key {}", context.getJobDetail().getKey() );
//    }
//
//    private void triggerRevocation( JobExecutionContext context,
//            List<RevocationTriggerService.RevocationTriggerRecord> revocationTriggerRecords )
//    {
//        Map<Long, RevocationTriggerService.RevocationTriggerRecord> revocationSuccessRecords = new HashMap<>();
//        Map<Long, RevocationTriggerService.RevocationTriggerRecord> revocationFailRecords = new HashMap<>();
//
//        for ( RevocationTriggerService.RevocationTriggerRecord revocationTriggerRecord : revocationTriggerRecords )
//        {
//            // Call auth server to revoke token by consent id.
//            TokenRevocationService.TokenRevocationResult revokedToken = tokenService.revokeTokensByConsentId(
//                    String.valueOf( revocationTriggerRecord.getConsentId() ), revocationTriggerRecord.getClientId(),
//                    revocationTriggerRecord.getClientSecret() );
//
//            // If response from auth server is success, delete record from database.
//            if ( revokedToken.getResult().equals( TokenRevocationService.TokenRevocationResult.Result.SUCCESS ) )
//            {
//                LOGGER.info(
//                        "Token for Consent ID '{}' revoked successfully. Proceed to delete revocation trigger record.",
//                        revocationTriggerRecord.getConsentId() );
//                // Audit log response from auth server
//                AuditLogger.revokeToken( context.getJobDetail().getKey(), AuditLogger.ActionStatus.SUCCEEDED,
//                        revokedToken.getPrev(), revokedToken.getNext() );
//                try
//                {
//                    revocationTriggerService.deleteRevocationTriggerRecord( revocationTriggerRecord.getId() );
//                    revocationSuccessRecords.put( revocationTriggerRecord.getId(), revocationTriggerRecord );
//                }
//                catch ( ServiceError.RecordNotFoundException e )
//                {
//                    LOGGER.error( "Failed to delete revocation trigger record with id '{}'.",
//                            revocationTriggerRecord.getId(), e );
//                    revocationFailRecords.put( revocationTriggerRecord.getId(), revocationTriggerRecord );
//                }
//            }
//            // Response from auth server is fail, then update the fail count and trigger time of the revocation trigger record.
//            else
//            {
//                LOGGER.info(
//                        "Token for Consent ID '{}' revoked fail. Proceed to update revocation trigger record.",
//                        revocationTriggerRecord.getConsentId() );
//                // Audit log response from auth server
//                AuditLogger.revokeToken( context.getJobDetail().getKey(), AuditLogger.ActionStatus.FAILED,
//                        revokedToken.getPrev(), revokedToken.getNext() );
//                revocationFailRecords.put( revocationTriggerRecord.getId(), revocationTriggerRecord );
//                try
//                {
//                    revocationTriggerService.updateRevocationTriggerRecord( revocationTriggerRecord.getId(),
//                            revocationTriggerRecord.getFailCount() + 1, context.getFireTime() );
//                }
//                catch ( ServiceError.RecordNotFoundException e )
//                {
//                    LOGGER.error( "Failed to update revocation trigger record.", e );
//                }
//            }
//        }
//
//        LOGGER.info( "The failed revocation trigger records  Id: {}", revocationFailRecords.keySet() );
//        if ( !revocationFailRecords.isEmpty() )
//        {
//            AuditLogger.batchJob( context.getJobDetail().getKey(), AuditLogger.ActionStatus.FAILED,
//                    revocationFailRecords.values() );
//        }
//
//        LOGGER.info( "The succeed revocation trigger records  Id: {}", revocationSuccessRecords.keySet() );
//        if ( !revocationSuccessRecords.isEmpty() )
//        {
//            AuditLogger.batchJob( context.getJobDetail().getKey(), AuditLogger.ActionStatus.SUCCEEDED,
//                    revocationSuccessRecords.values() );
//        }
//    }
//}
