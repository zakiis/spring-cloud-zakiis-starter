package com.zakiis.auditlog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import com.zakiis.core.domain.constants.ZakiisStarterConstants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RefreshScope
@ConfigurationProperties(prefix = ZakiisStarterConstants.AUDIT_LOG_CLIENT_PREFIX)
public class AuditLogClientProperties {

	/** determine if the feature of publish audit log message should be enabled, default is true */
	private boolean enabled = true;
	
}
