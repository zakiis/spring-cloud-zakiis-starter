package com.zakiis.auditlog.service.impl;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import com.zakiis.auditlog.domain.constants.AuditLogConstant;
import com.zakiis.auditlog.domain.message.AuditLogMessage;
import com.zakiis.auditlog.service.AuditLogPublishService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultAuditLogPublishService implements AuditLogPublishService {

	@NonNull
	private final StreamBridge streamBridge;

	@Override
	public void publish(AuditLogMessage auditLogMessage) {
    	Message<AuditLogMessage> message = MessageBuilder.withPayload(auditLogMessage).build();
    	streamBridge.send(AuditLogConstant.AUDIT_LOG_OUTPUT, message);
	}
}
