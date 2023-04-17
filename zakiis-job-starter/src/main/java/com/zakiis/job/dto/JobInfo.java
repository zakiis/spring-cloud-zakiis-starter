package com.zakiis.job.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobInfo implements Serializable {

	private static final long serialVersionUID = 7340640653421762652L;

	String jobName;
	String jobGroupName;
	String triggerKey;
	String jobStatus;
	String cron;
	
}
