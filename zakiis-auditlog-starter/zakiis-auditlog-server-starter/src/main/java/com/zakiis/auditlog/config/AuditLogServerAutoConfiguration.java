package com.zakiis.auditlog.config;

import java.util.function.Consumer;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import com.zakiis.auditlog.controller.AuditLogQueryController;
import com.zakiis.auditlog.domain.message.AuditLogMessage;
import com.zakiis.auditlog.infra.repository.AuditLogRepository;
import com.zakiis.auditlog.service.AuditLogQueryService;
import com.zakiis.auditlog.service.AuditLogSubscriptionService;
import com.zakiis.auditlog.service.impl.DefaultAuditLogQueryService;
import com.zakiis.auditlog.service.impl.DefaultAuditLogSubscriptionService;
import com.zakiis.core.domain.constants.ZakiisStarterConstants;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfiguration
@ConditionalOnProperty(prefix = ZakiisStarterConstants.AUDIT_LOG_SERVER_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableElasticsearchRepositories(basePackages = "com.zakiis.auditlog.infra.repository")
@EnableConfigurationProperties(AuditLogServerProperties.class)
public class AuditLogServerAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public AuditLogSubscriptionService auditLogSubscriptionService(AuditLogRepository auditLogRepository) {
		log.info("Default audit log subscription service loaded.");
		return new DefaultAuditLogSubscriptionService(auditLogRepository);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public Consumer<AuditLogMessage> auditLog(AuditLogSubscriptionService auditLogSubscriptionService) {
		return AuditLogMessage -> auditLogSubscriptionService.subscription(AuditLogMessage);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public AuditLogQueryService auditLogQueryService(ElasticsearchClient elasticsearchClient,
			AuditLogServerProperties auditLogServerProperties) {
		return new DefaultAuditLogQueryService(elasticsearchClient, auditLogServerProperties);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public AuditLogQueryController auditLogQueryController(AuditLogQueryService auditLogQueryService) {
		return new AuditLogQueryController(auditLogQueryService);
	}
}
