//package ch.rasc.twofa.batch;
//
//import org.quartz.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//@Controller
//@Configuration
//@EnableAutoConfiguration
//public class SpringQrtzScheduler {
//
//@Autowired
//private Scheduler scheduler;
//
////@Bean
////@QuartzDataSource
////public DataSource quartzDataSource() {
////        return DataSourceBuilder.create().build();
////}
//
//@PostMapping("/Quartz")
//@ResponseBody
//public Object SpringQrtzScheduler(@RequestParam("email")  String email) throws Exception {
//        Date start=new Date(System.currentTimeMillis() + 7 * 1000);//当前时间7秒之后
//
//        /**通过JobBuilder.newJob()方法获取到当前Job的具体实现(以下均为链式调用)
//         * 这里是固定Job创建，所以代码写死XXX.class
//         * 如果是动态的，根据不同的类来创建Job，则 ((Job)Class.forName("com.zy.job.TestJob").newInstance()).getClass()
//         * 即是 JobBuilder.newJob(((Job)Class.forName("com.zy.job.TestJob").newInstance()).getClass())
//         * */
//        JobDetail jobDetail = JobBuilder.newJob(JobA.class)
//        /**给当前JobDetail添加参数，K V形式*/
//        .usingJobData("email", email)
//        /**给当前JobDetail添加参数，K V形式，链式调用，可以传入多个参数，在Job实现类中，可以通过jobExecutionContext.getJobDetail().getJobDataMap().get("age")获取值*/
//        /**添加认证信息，有3种重写的方法，我这里是其中一种，可以查看源码看其余2种*/
//        .withIdentity(email)
//        .build();//执行
//
//
//        Trigger trigger = TriggerBuilder.newTrigger()
//        /**给当前JobDetail添加参数，K V形式，链式调用，可以传入多个参数，在Job实现类中，可以通过jobExecutionContext.getTrigger().getJobDataMap().get("orderNo")获取值*/
//        .usingJobData("email", email)
//        /**添加认证信息，有3种重写的方法，我这里是其中一种，可以查看源码看其余2种*/
//        .withIdentity(email)
//        /**立即生效*/
////      .startNow()
//        /**开始执行时间*/
//        .startAt(start)
//        /**结束执行时间*/
////        .endAt(start)
//        /**添加执行规则，SimpleTrigger、CronTrigger的区别主要就在这里*/
//        .withSchedule(
//        SimpleScheduleBuilder.simpleSchedule()
//        /**每隔1s执行一次*/
//        .withIntervalInMinutes(2)
//        /**一直执行，*/
//        .repeatForever()
//        )
//        .build();//执行
//
////CronTrigger  trigger = TriggerBuilder.newTrigger()
////        /**给当前JobDetail添加参数，K V形式，链式调用，可以传入多个参数，在Job实现类中，可以通过jobExecutionContext.getTrigger().getJobDataMap().get("orderNo")获取值*/
////        .usingJobData("orderNo", orderNo)
////        /**添加认证信息，有3种重写的方法，我这里是其中一种，可以查看源码看其余2种*/
////        .withIdentity(orderNo)
////        /**开始执行时间*/
////        .startAt(start)
////        /**结束执行时间*/
////        .endAt(start)
////        /**添加执行规则，SimpleTrigger、CronTrigger的区别主要就在这里*/
////        .withSchedule(CronScheduleBuilder.cronSchedule("* 30 10 ? * 1/5 2018"))
////        .build();//执行
//
//
//        /**添加定时任务*/
//        scheduler.scheduleJob(jobDetail, trigger);
//        if (!scheduler.isShutdown()) {
//        /**启动*/
//                scheduler.start();
//        }
//        System.err.println("--------schedule (" + email + ") starts successfully "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+" ------------");
//        return "ok";
//    }
//
//@PostMapping("/shutdown")
//@ResponseBody
//public Object shutdown(@RequestParam("email")  String email) throws IOException, SchedulerException {
//        scheduler.pauseTrigger(TriggerKey.triggerKey(email));//暂停Trigger
//        System.err.println("--------schedule (" + email + ") closes successfully "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+" ------------");
//        return "";
//        }
//
//@PostMapping("/resume")
//@ResponseBody
//public Object resume(@RequestParam("email")  String email) throws IOException, SchedulerException {
//        scheduler.resumeTrigger(TriggerKey.triggerKey(email));//恢复Trigger
//        System.err.println("--------schedule (" + email + ") resumes successfully "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+" ------------");
//        return "ok";
//        }
//
//@PostMapping("/del")
//@ResponseBody
//public Object del(@RequestParam("email")  String email) throws IOException, SchedulerException {
//        scheduler.pauseTrigger(TriggerKey.triggerKey(email));//暂停触发器
//        scheduler.unscheduleJob(TriggerKey.triggerKey(email));//移除触发器
//        scheduler.deleteJob(JobKey.jobKey(email));//删除Job
//        System.err.println("--------schedule (" + email + ") deleted successfully "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+" ------------");
//        return "ok";
//        }
//}