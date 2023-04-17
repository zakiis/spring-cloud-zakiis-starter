package com.zakiis.job.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.zakiis.job.dto.JobInfo;
import com.zakiis.job.exception.JobException;

public class QuartzService {

	@Autowired
	private Scheduler scheduler;
	
	/**
     * add a simple job
     * @param jobClass
     * @param jobName 
     * @param jobGroupName
     * @param intervalInSeconds
     * @param repeatTimes
     * @param jobData Extra data for this job
     */
    public void addSimpleJob(Class<? extends QuartzJobBean> jobClass, String jobName, String jobGroupName, int intervalInSeconds,
                       int repeatTimes, Map<? extends String, ?> jobData) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName)
                    .build();
            if(jobData!= null && jobData.size()>0){
                jobDetail.getJobDataMap().putAll(jobData);
            }
            Trigger trigger = null;
            if (repeatTimes < 0) {
                trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroupName)
                        .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(1).withIntervalInSeconds(intervalInSeconds))
                        .startNow().build();
            } else {
                trigger = TriggerBuilder
                        .newTrigger().withIdentity(jobName, jobGroupName).withSchedule(SimpleScheduleBuilder
                                .repeatSecondlyForever(1).withIntervalInSeconds(intervalInSeconds).withRepeatCount(repeatTimes))
                        .startNow().build();
            }
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
        	throw new JobException(String.format("add simple job fail, job name:%s, job group:%s", jobName, jobGroupName), e);
        }
    }
    
    /**
     * add a cron job
     * @param jobClass
     * @param jobName
     * @param jobGroupName
     * @param cron Execute expression, for example: 0 0/1 * * ? represents executed per minute
     * @param jobData Extra data for this job
     */
	public void addCronJob(Class<? extends QuartzJobBean> jobClass, String jobName, String jobGroupName, String cron, Map<? extends String, ?> jobData) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName)
                    .build();
            if(jobData!= null && jobData.size()>0){
                jobDetail.getJobDataMap().putAll(jobData);
            }
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroupName)
                    .startAt(DateBuilder.futureDate(1, IntervalUnit.SECOND))
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron)).startNow().build();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
        	throw new JobException(String.format("add cron job fail, job name:%s, job group:%s", jobName, jobGroupName), e);
        }
    }
	
	/**
	 * update job's cron expression
	 * @param jobName
	 * @param jobGroupName
	 * @param cron Execute expression, for example: 0 0/1 * * ? represents executed per minute
	 */
	public void updateJobCron(String jobName, String jobGroupName, String cron) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroupName);
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
            scheduler.rescheduleJob(triggerKey, trigger);
        } catch (SchedulerException e) {
        	throw new JobException(String.format("update job cron expression fail, job name:%s, job group:%s, cron:%s", jobName, jobGroupName, cron), e);
        }
    }

	public void deleteJob(String jobName, String jobGroupName) {
		try {
			scheduler.deleteJob(new JobKey(jobName, jobGroupName));
		} catch (Exception e) {
			throw new JobException(String.format("delete job fail, job name:%s, job group:%s", jobName, jobGroupName), e);
		}
	}
	
	public void pauseJob(String jobName, String jobGroupName) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
        	throw new JobException(String.format("pause job fail, job name:%s, job group:%s", jobName, jobGroupName), e);
        }
    }
	
	public void resumeJob(String jobName, String jobGroupName) {
		try {
			JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
			scheduler.resumeJob(jobKey);
		} catch (SchedulerException e) {
			throw new JobException(String.format("resume job fail, job name:%s, job group:%s", jobName, jobGroupName), e);
		}
	}
	
	public void triggerJobNow(String jobName, String jobGroupName) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
            scheduler.triggerJob(jobKey);
        } catch (SchedulerException e) {
        	throw new JobException(String.format("trigger job fail, job name:%s, job group:%s", jobName, jobGroupName), e);
        }
    }
	
	public List<JobInfo> queryJob(String jobName, String jobGroupName) {
		List<JobInfo> jobList = null;
		try {
			JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
			List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
			jobList = new ArrayList<JobInfo>(triggers.size());
			for (Trigger trigger : triggers) {
				JobInfo jobDTO = new JobInfo();
				jobDTO.setJobName(jobKey.getName());
				jobDTO.setJobGroupName(jobKey.getGroup());
				jobDTO.setTriggerKey(trigger.getKey().toString());
				Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
				jobDTO.setJobStatus(triggerState.name());
				if (trigger instanceof CronTrigger) {
					CronTrigger cronTrigger = (CronTrigger) trigger;
					jobDTO.setCron(cronTrigger.getCronExpression());
				}
				jobList.add(jobDTO);
			}
		} catch (SchedulerException e) {
			throw new JobException("query job list fail", e);
		}
		return jobList;
	}
	
	public List<JobInfo> queryAllJob() {
		List<JobInfo> jobList = null;
		try {
			GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
			Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
			jobList = new ArrayList<JobInfo>(jobKeys.size());
			for (JobKey jobKey : jobKeys) {
				List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
				for (Trigger trigger : triggers) {
					JobInfo jobDTO = new JobInfo();
					jobDTO.setJobName(jobKey.getName());
					jobDTO.setJobGroupName(jobKey.getGroup());
					jobDTO.setTriggerKey(trigger.getKey().toString());
					Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
					jobDTO.setJobStatus(triggerState.name());
					if (trigger instanceof CronTrigger) {
						CronTrigger cronTrigger = (CronTrigger) trigger;
						jobDTO.setCron(cronTrigger.getCronExpression());
					}
					jobList.add(jobDTO);
				}
			}
		} catch (SchedulerException e) {
			throw new JobException("query job list fail", e);
		}
		return jobList;
	}
	
	public List<JobInfo> queryRunJob() {
		List<JobInfo> jobList = null;
        try {
            List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
            jobList = new ArrayList<JobInfo>(executingJobs.size());
            for (JobExecutionContext executingJob : executingJobs) {
                JobDetail jobDetail = executingJob.getJobDetail();
                JobKey jobKey = jobDetail.getKey();
                Trigger trigger = executingJob.getTrigger();
                JobInfo jobDTO = new JobInfo();
				jobDTO.setJobName(jobKey.getName());
				jobDTO.setJobGroupName(jobKey.getGroup());
				jobDTO.setTriggerKey(trigger.getKey().toString());
				Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
				jobDTO.setJobStatus(triggerState.name());
				if (trigger instanceof CronTrigger) {
					CronTrigger cronTrigger = (CronTrigger) trigger;
					jobDTO.setCron(cronTrigger.getCronExpression());
				}
				jobList.add(jobDTO);
            }
        } catch (SchedulerException e) {
        	throw new JobException("query currently executing job list fail", e);
        }
        return jobList;
    }

}
