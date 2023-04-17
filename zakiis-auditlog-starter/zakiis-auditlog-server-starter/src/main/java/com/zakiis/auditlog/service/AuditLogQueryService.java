package com.zakiis.auditlog.service;

import com.zakiis.auditlog.domain.dto.AuditLogDto;
import com.zakiis.auditlog.domain.dto.AuditLogQueryReq;
import com.zakiis.core.domain.dto.CommonPageResp;

import jakarta.validation.Valid;

public interface AuditLogQueryService {

	/**
	 * paging query audit log 
	 * @param req
	 * @return
	 */
	CommonPageResp<AuditLogDto> queryAuditLog(@Valid AuditLogQueryReq req);
}
