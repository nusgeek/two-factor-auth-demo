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
//import org.quartz.JobDataMap;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
//import org.springframework.scheduling.quartz.JobDetailFactoryBean;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Objects;
//
///**
// * Token Revocation Trigger job related configuration
// *
// * @author XingJun
// */
//@Configuration
//public class TokenRevocationSchedulerConfig
//{
//    private static final String JOB_DETAIL_NAME = "tokenRevocationJob";
//
//    private static final String JOB_DETAIL_GROUP = "tokenRevocationJobGroup";
//
//    private static final String CRON_TRIGGER_NAME = "tokenRevocationJobTrigger";
//
//    private static final String CRON_TRIGGER_GROUP = "tokenRevocationJobTriggerGroup";
//
//    @Value( "${quartz.job.token.revocation.cron.expression}" )
//    private String cronExpression;
//
//    /**
//     * Get an instance of job factory for token revocation job
//     * to be managed by spring
//     *
//     * @return
//     */
//    @Bean( name = "tokenRevocationJob" )
//    public JobDetailFactoryBean tokenRevocationJob()
//    {
//        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
//        jobDetailFactory.setJobClass( TokenRevocationJob.class );
//
//        Map<String, Object> jobDataBackupMap = new HashMap<>();
//
//        JobDataMap jobDataMap = new JobDataMap( jobDataBackupMap );
//        jobDetailFactory.setJobDataMap( jobDataMap );
//        jobDetailFactory.setGroup( JOB_DETAIL_GROUP );
//        jobDetailFactory.setName( JOB_DETAIL_NAME );
//
//        jobDetailFactory.setRequestsRecovery( false );
//        jobDetailFactory.setDurability( true );
//        return jobDetailFactory;
//    }
//
//    /**
//     * Get an instance of cron trigger factory for FAILED token revocation job
//     * to be managed by spring
//     *
//     * @return
//     */
//    @Bean( name = "tokenRevocationJobTrigger" )
//    public CronTriggerFactoryBean tokenRevocationJobTrigger()
//    {
//        CronTriggerFactoryBean triggerFactory = new CronTriggerFactoryBean();
//        triggerFactory.setGroup( CRON_TRIGGER_GROUP );
//        triggerFactory.setName( CRON_TRIGGER_NAME );
//        triggerFactory.setCronExpression( cronExpression );
//        triggerFactory.setJobDetail( Objects.requireNonNull( tokenRevocationJob().getObject() ) );
//
//        return triggerFactory;
//    }
//}
