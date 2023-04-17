package com.zakiis.auditlog.service;

import com.zakiis.auditlog.domain.message.AuditLogMessage;

/**
 * Audit log subscription service
 * @date 2023-04-04 17:19:50
 * @author Liu Zhenghua
 */
public interface AuditLogSubscriptionService {

	/**
	 * Subscription the audit log data
	 * default to persist it to elastic search.
	 * @param AuditLogMessage
	 */
	void subscription(AuditLogMessage auditLogMessage);
}
