package com.zakiis.auditlog.service.tool;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

import com.zakiis.auditlog.domain.constants.OperateStatus;
import com.zakiis.auditlog.domain.dto.AuditLogDto;
import com.zakiis.auditlog.domain.message.AuditLogMessage;
import com.zakiis.auditlog.service.LoginUserService;
import com.zakiis.auditlog.service.RequestService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuditLogTool {

	@Value("${spring.application.name}")
    private String applicationName;
	@NonNull
	private final LoginUserService loginUserService;
	@NonNull
	private final RequestService requestService;
	
	public AuditLogMessage buildAuditLogMessage(String operateGroup, String operateTarget, String operateType,
			String operateTargetId, String operateDesc, OperateStatus operateStatus, String remark,
			String className, String methodName, String methodParams) {
		AuditLogDto auditLog = new AuditLogDto()
    		.setOperateTime(new Date())
    		.setApplicationName(applicationName)
    		.setOperateGroup(operateGroup)
    		.setOperateTarget(operateTarget)
    		.setOperateType(operateType);
    	if (!"nil".equals(operateDesc)) {
    		auditLog.setOperateDesc(operateDesc);
    	}
    	auditLog.setClassName(className)
    		.setMethodName(methodName)
    		.setMethodParams(methodParams)
    		.setOperateTargetId(operateTargetId)
    		.setOperatorIp(requestService.getIp())
    		.setUserAgent(requestService.getUserAgent())
    		.setTraceId(requestService.getTraceId())
    		.setOperatorId(loginUserService.getUserId())
    		.setOperatorUserName(loginUserService.getLoginAccount())
    		.setOperatorRealName(loginUserService.getRealName())
    		.setOperateStatus(operateStatus)
    		.setRemark(remark);
    	return AuditLogMessage.of(UUID.randomUUID().toString(), auditLog);
	}
}
