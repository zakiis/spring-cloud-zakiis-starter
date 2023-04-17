package com.zakiis.auditlog.service;

import com.zakiis.auditlog.domain.message.AuditLogMessage;

/**
 * 审计日志发布接口
 * @author zlt
 * @date 2020/2/3
 */
public interface AuditLogPublishService {
	
	/**
	 * 发布审计日志
	 * @param auditLogMessage 审计日志消息
	 */
	void publish(AuditLogMessage auditLogMessage);
}
