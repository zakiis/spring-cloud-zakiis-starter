package com.zakiis.auditlog.service.impl;

import com.zakiis.auditlog.domain.entity.AuditLogPo;
import com.zakiis.auditlog.domain.mapper.AuditLogMapper;
import com.zakiis.auditlog.domain.message.AuditLogMessage;
import com.zakiis.auditlog.infra.repository.AuditLogRepository;
import com.zakiis.auditlog.service.AuditLogSubscriptionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DefaultAuditLogSubscriptionService implements AuditLogSubscriptionService {

	private final AuditLogRepository auditLogRepository;
	
	@Override
	public void subscription(AuditLogMessage auditLogMessage) {
		log.info("Receive audit log message:{}", auditLogMessage);
		AuditLogPo auditLogPo = AuditLogMapper.INSTANCE.convertToPo(auditLogMessage.getData());
		auditLogPo.setId(auditLogMessage.getSeqNo());
		auditLogRepository.save(auditLogPo);
	}

}
