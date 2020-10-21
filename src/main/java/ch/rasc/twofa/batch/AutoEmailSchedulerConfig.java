package ch.rasc.twofa.batch;


import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@PropertySource("classpath:quartz.properties")
public class AutoEmailSchedulerConfig
{
    private static final String JOB_DETAIL_NAME = "autoEmailJob";

    private static final String JOB_DETAIL_GROUP = "autoEmailJobGroup";

    private static final String CRON_TRIGGER_NAME = "autoEmailJobTrigger";

    private static final String CRON_TRIGGER_GROUP = "autoEmailJobTriggerGroup";

    @Value( "${quartz.job.auto.email.cron.expression}" )
    private String cronExpression;

    /**
     * Get an instance of job factory for token revocation job
     * to be managed by spring
     *
     * @return
     */
    @Bean( name = "autoEmailJob" )
    public JobDetailFactoryBean autoEmailJob()
    {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass( AutoEmailJob.class );

        Map<String, Object> jobDataBackupMap = new HashMap<>();

        JobDataMap jobDataMap = new JobDataMap( jobDataBackupMap );
        jobDetailFactory.setJobDataMap( jobDataMap );
        jobDetailFactory.setGroup( JOB_DETAIL_GROUP );
        jobDetailFactory.setName( JOB_DETAIL_NAME );

        jobDetailFactory.setRequestsRecovery( false );
        jobDetailFactory.setDurability( true );
        return jobDetailFactory;
    }

    /**
     * Get an instance of cron trigger factory for FAILED token revocation job
     * to be managed by spring
     *
     * @return
     */
    @Bean( name = "autoEmailJobTrigger" )
    public CronTriggerFactoryBean autoEmailJobTrigger()
    {
        CronTriggerFactoryBean triggerFactory = new CronTriggerFactoryBean();
        triggerFactory.setGroup( CRON_TRIGGER_GROUP );
        triggerFactory.setName( CRON_TRIGGER_NAME );
        triggerFactory.setCronExpression( cronExpression ); // 0/30 * * ? * * *
        triggerFactory.setJobDetail( Objects.requireNonNull( autoEmailJob().getObject() ) );

        return triggerFactory;
    }
}

