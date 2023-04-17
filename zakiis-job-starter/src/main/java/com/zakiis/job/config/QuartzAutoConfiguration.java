package com.zakiis.job.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import com.zakiis.job.service.QuartzService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfiguration
public class QuartzAutoConfiguration {

	@Bean
	public QuartzService quartzService() {
		log.info("Feature quartz service enabled.");
		return new QuartzService();
	}
}
