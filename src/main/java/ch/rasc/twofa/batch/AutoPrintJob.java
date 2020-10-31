package ch.rasc.twofa.batch;


import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

@DisallowConcurrentExecution
public class AutoPrintJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger( AutoPrintJob.class );

    private int time = 1;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        LOGGER.info("Job starts");
        printCurrentTime();
        LOGGER.info("This is the {} output.", time++);
    }

    private void printCurrentTime() {
        LOGGER.info("Current time: {}", new Date());
    }

}
