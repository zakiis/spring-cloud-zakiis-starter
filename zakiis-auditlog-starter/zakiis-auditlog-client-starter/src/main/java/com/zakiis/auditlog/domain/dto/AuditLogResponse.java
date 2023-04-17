package com.zakiis.auditlog.domain.dto;

import java.io.Serializable;

import com.zakiis.auditlog.aspect.AuditLogAspect;

public interface AuditLogResponse extends Serializable {

	/**
	 * It's used by {@link AuditLogAspect} to determine the result of this request
	 * @return true if the response is success.
	 */
	public boolean isSuccess();
	
}
