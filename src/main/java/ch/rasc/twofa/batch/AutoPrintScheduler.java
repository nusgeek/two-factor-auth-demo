//package ch.rasc.twofa.batch;
//
//
//import org.quartz.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.config.PropertiesFactoryBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
//import org.springframework.scheduling.quartz.SchedulerFactoryBean;
//import org.springframework.scheduling.quartz.SpringBeanJobFactory;
//
//import javax.annotation.PostConstruct;
//import java.io.IOException;
//import java.util.Objects;
//import java.util.Properties;
//
//import static org.quartz.JobBuilder.newJob;
//import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
//import static org.quartz.TriggerBuilder.newTrigger;
//
//@Configuration
//@ConditionalOnExpression("'${using.spring.schedulerFactory}'=='false'")
//public class AutoPrintScheduler {
//
//    Logger logger = LoggerFactory.getLogger(getClass());
//
//    @Autowired
//    private ApplicationContext applicationContext;
//
//    @PostConstruct
//    public void init() {
//        logger.info("Hello world from Quartz...");
//    }
//
//    @Bean
//    public SpringBeanJobFactory springBeanJobFactory() {
//        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
//        logger.debug("Configuring Job factory");
//
//        jobFactory.setApplicationContext(applicationContext);
//        return jobFactory;
//    }
//
//    @Bean
//    public Scheduler scheduler(@Qualifier(value = "trigger1") Trigger trigger, @Qualifier(value = "jobDetail1") JobDetail job,
//                               @Qualifier(value = "factory1") SchedulerFactoryBean factory) throws SchedulerException {
//        logger.debug("Getting a handle to the Scheduler");
//        Scheduler scheduler = factory.getScheduler();
//        scheduler.scheduleJob(job, trigger);
//
//        logger.debug("Starting Scheduler threads");
//        scheduler.start();
//        return scheduler;
//    }
//
//    @Bean( name = "autoPrintJobTrigger" )
//    public CronTriggerFactoryBean autoPrintJobTrigger()
//    {
//        CronTriggerFactoryBean triggerFactory = new CronTriggerFactoryBean();
//        triggerFactory.setGroup( "CRON_TRIGGER_GROUP" );
//        triggerFactory.setName( "CRON_TRIGGER_NAME" );
//        triggerFactory.setCronExpression( "0/30 * * ? * * *" ); // 0/30 * * ? * * *
//        triggerFactory.setJobDetail( Objects.requireNonNull( jobDetail() ) );
//
//        return triggerFactory;
//    }
//
//    @Bean (value = "factory1")
//    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
//        SchedulerFactoryBean factory = new SchedulerFactoryBean();
//        factory.setJobFactory(springBeanJobFactory());
//        factory.setQuartzProperties(quartzProperties());
//        return factory;
//    }
//
//    public Properties quartzProperties() throws IOException {
//        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
//        propertiesFactoryBean.setLocation(new ClassPathResource("src/main/resources/quartz.properties"));
//        propertiesFactoryBean.afterPropertiesSet();
//        return propertiesFactoryBean.getObject();
//    }
//
//    @Bean (value = "jobDetail1")
//    public JobDetail jobDetail() {
//
//        return newJob().ofType(AutoPrintJob.class).storeDurably().withIdentity(JobKey.jobKey("Qrtz_Job_Detail")).withDescription("Invoke Sample Job service...").build();
//    }
//
//    @Bean (value = "trigger1")
//    public Trigger trigger(@Qualifier("jobDetail1") JobDetail job) {
//
//        int frequencyInSec = 10;
//        logger.info("Configuring trigger to fire every {} seconds", frequencyInSec);
//
//        return newTrigger().forJob(job).withIdentity(TriggerKey.triggerKey("Qrtz_Trigger")).withDescription("Sample trigger").withSchedule(simpleSchedule().withIntervalInSeconds(frequencyInSec).repeatForever()).build();
//    }
//}