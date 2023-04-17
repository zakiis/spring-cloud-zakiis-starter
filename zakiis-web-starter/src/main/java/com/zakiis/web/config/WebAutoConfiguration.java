package com.zakiis.web.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import com.zakiis.web.exception.GlobalExceptionAdvice;
import com.zakiis.web.holder.ApplicationContextHolder;

@AutoConfiguration
public class WebAutoConfiguration {

	@Bean("zakiisApplicationContextHolder")
	@ConditionalOnMissingBean
	public ApplicationContextHolder applicationContextHolder() {
		return new ApplicationContextHolder();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public GlobalExceptionAdvice globalExceptionAdvice() {
		return new GlobalExceptionAdvice();
	}
}
