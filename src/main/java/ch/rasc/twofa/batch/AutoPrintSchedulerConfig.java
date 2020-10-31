package ch.rasc.twofa.batch;


import org.quartz.JobDataMap;
import org.quartz.SimpleTrigger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@PropertySource("classpath:quartz.properties")
public class AutoPrintSchedulerConfig
{
    private static final String JOB_DETAIL_NAME = "autoPrint";

    private static final String JOB_DETAIL_GROUP = "autoPrintGroup";

    private static final String CRON_TRIGGER_NAME = "autoPrintJobTrigger";

    private static final String CRON_TRIGGER_GROUP = "autoPrintJobTriggerGroup";

    @Value( "${quartz.job.auto.print.cron.expression}" )
    private String cronExpression;

    /**
     * Get an instance of job factory for token revocation job
     * to be managed by spring
     *
     */
    @Bean( name = "autoPrintJob" )
    public JobDetailFactoryBean autoPrintJob()
    {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass( AutoPrintJob.class );

        Map<String, Object> jobDataBackupMap = new HashMap<>();
        jobDataBackupMap.put("k1", "v1");
        jobDataBackupMap.put("k2", "v2");

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
     */
//    @Bean( name = "autoPrintJobTrigger" )
//    public CronTriggerFactoryBean autoPrintJobTrigger()
//    {
//        CronTriggerFactoryBean triggerFactory = new CronTriggerFactoryBean();
//        triggerFactory.setGroup( CRON_TRIGGER_GROUP );
//        triggerFactory.setName( CRON_TRIGGER_NAME );
//        triggerFactory.setCronExpression( cronExpression ); // 0/30 * * ? * * *
//        triggerFactory.setJobDetail( Objects.requireNonNull( autoPrintJob().getObject() ) );
//
//        return triggerFactory;
//    }

    @Bean (name = "autoPrintJobTrigger")
    public SimpleTriggerFactoryBean trigger() {
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(Objects.requireNonNull(autoPrintJob().getObject()));
        trigger.setRepeatInterval(10000);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        return trigger;
    }
}

