package com.zakiis.log.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import com.zakiis.core.domain.constants.ZakiisStarterConstants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RefreshScope
@ConfigurationProperties(prefix = ZakiisStarterConstants.LOG_TRACE_ID_PREFIX)
public class TraceIdProperties {

	private boolean enabled = true;
	/** current application name*/
	private String appName;
	/** trace id header name*/
	private String header = "X-TRACE-ID";
	
}
