package com.zakiis.auditlog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import com.zakiis.core.domain.constants.ZakiisStarterConstants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RefreshScope
@ConfigurationProperties(prefix = ZakiisStarterConstants.AUDIT_LOG_SERVER_PREFIX)
public class AuditLogServerProperties {

	/** determine if the feature of publish audit log message should be enabled, default is true */
	private boolean enabled = true;
	/** the index name in ES for storing audit log */
	private String logIndexName = "zakiis.audit-log";
	
}
