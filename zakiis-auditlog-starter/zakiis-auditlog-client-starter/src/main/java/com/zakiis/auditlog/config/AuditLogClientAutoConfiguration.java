package com.zakiis.auditlog.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

import com.zakiis.auditlog.aspect.AuditLogAspect;
import com.zakiis.auditlog.service.AuditLogPublishService;
import com.zakiis.auditlog.service.LoginUserService;
import com.zakiis.auditlog.service.RequestService;
import com.zakiis.auditlog.service.impl.DefaultAuditLogPublishService;
import com.zakiis.auditlog.service.tool.AuditLogTool;
import com.zakiis.core.domain.constants.ZakiisStarterConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfiguration
@ConditionalOnProperty(prefix = ZakiisStarterConstants.AUDIT_LOG_CLIENT_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(AuditLogClientProperties.class)
public class AuditLogClientAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public AuditLogTool auditLogTool(LoginUserService loginUserService, RequestService requestService) {
		return new AuditLogTool(loginUserService, requestService);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public AuditLogPublishService auditLogPublishService(StreamBridge streamBridge) {
		log.debug("MqAuditLogService loaded");
		return new DefaultAuditLogPublishService(streamBridge);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public AuditLogAspect auditLogAspect(AuditLogClientProperties auditLogProperties, AuditLogTool auditLogTool,
			AuditLogPublishService auditLogService) {
		log.info("Audit log aspect feature enabled.");
		return new AuditLogAspect(auditLogProperties, auditLogTool, auditLogService);
	}
}
