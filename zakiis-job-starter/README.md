## Job
### Introduction
Job module integrates quartz on spring boot framework. It provides a service class help quick using quartz and a test class to demonstrate how to use it.

### How to use it
1. add job on your project dependency
```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>io.github.zakiis</groupId>
      <artifactId>zakiis-dependencies</artifactId>
      <version>0.0.4</version>
      <scope>import</scope>
      <type>pom</type>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <dependency>
    <groupId>io.github.zakiis</groupId>
    <artifactId>zakiis-job</artifactId>
  </dependency>
</dependencies>

<!-- data source and spring boot dependency -->
...
```

2. create the table that quartz need in <b>`db/db.sql`</b>
3. create a job class extends QuartzJobBean and write your business code on it

```java
public class HelloWorldJob extends QuartzJobBean {
	
	Logger log = LoggerFactory.getLogger(HelloWorldJob.class);

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		Trigger trigger = context.getTrigger();
		Date fireTime = context.getFireTime();
		log.info("job executed, trigger:{} , fire time:{}, data map:{}", trigger, fireTime, jobDataMap);
        // your business code
	}
}
```

4. configure your job with a `Cron` expression through `QuartzService` class after the project start 

```java
@Component
public class JobInitialize implements CommandLineRunner {

	@Autowired
	QuartzService quartzService;
	
	@Override
	public void run(String... args) throws Exception {
		// job data is optional
		HashMap<String, Object> jobData = new HashMap<String, Object>();
		jobData.put("name", "Jack");
		quartzService.addCronJob(HelloWorldJob.class, "hello-job", "groupName", "0 0/1 * * * ?", jobData);
	}
}
```

### Advanced API

There are many advanced API in QuartzService like the follwings:

- queryAllJob
- deleteJob
- pauseJob/resumeJob
- triggerJobNow
- etc

if you are interested on that, the best way of learning API is reading the code, you can find the using on following classes:

- QuartzService on job module
- QuartzServiceTest on test module

### Key Tables

Quartz store data in database, knowing key tables can help you find the problems if your program not run as you expected.

For example, If you want to delete a job manually, you should delete the following tables:

- QRTZ_CRON_TRIGGERS - store the cron experssion of your job
- QRTZ_TRIGGERS - store the scheduler of your job
- QRTZ_JOB_DETAILS - store the job info
