package ch.rasc.twofa.batch;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

@DisallowConcurrentExecution
public class JobA extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context ) throws JobExecutionException {
        printMethod("this is a good one.");
    }


    private void printMethod(String text) {
        System.err.println(text);
    }
}