package com.zakiis.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import com.zakiis.core.domain.constants.ZakiisStarterConstants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RefreshScope
@ConfigurationProperties(prefix = ZakiisStarterConstants.GATEWAY_PREFIX)
public class GatewayProperties {

	/** log every request */
	@NestedConfigurationProperty
	private LogRequestConfig logRequest = new LogRequestConfig();
	/** traceId feature */
	@NestedConfigurationProperty
	private TraceIdConfig traceId = new TraceIdConfig();
	
	@Getter
	@Setter
	public static class LogRequestConfig {
		private boolean enabled = true;
	}
	
	@Getter
	@Setter
	public static class TraceIdConfig {
		
		private boolean enabled = true;
		/** header key of trace id, would retrieve from HTTP header or generate new one if not found */
		private String httpHeaderKey = "Trace-Id";
		/** if no trace id passed, a random trace id would generated using the prefix ,default is 'GW_' */
		private String prefix = "GW_";
	}
}
