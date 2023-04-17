package com.zakiis.auditlog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zakiis.auditlog.domain.dto.AuditLogDto;
import com.zakiis.auditlog.domain.dto.AuditLogQueryReq;
import com.zakiis.auditlog.service.AuditLogQueryService;
import com.zakiis.core.domain.dto.CommonPageResp;
import com.zakiis.core.domain.dto.CommonResp;
import com.zakiis.core.domain.dto.CommonResps;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/zakiis/audit-log")
public class AuditLogQueryController {
	
	private final AuditLogQueryService auditLogQueryService;

	@GetMapping("/list")
	public CommonResp<CommonPageResp<AuditLogDto>> list(@Valid AuditLogQueryReq req) {
		CommonPageResp<AuditLogDto> pageResp = auditLogQueryService.queryAuditLog(req);
		return CommonResp.of(CommonResps.SUCCESS, pageResp);
	}
}
