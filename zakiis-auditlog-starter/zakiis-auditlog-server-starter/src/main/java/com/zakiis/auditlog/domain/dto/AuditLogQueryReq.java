package com.zakiis.auditlog.domain.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.zakiis.auditlog.domain.constants.OperateStatus;
import com.zakiis.core.domain.dto.CommonPageReq;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AuditLogQueryReq extends CommonPageReq {

	private static final long serialVersionUID = -7760713304138553729L;
	
	/**
	 * 操作分组
	 */
	private String operateGroup;
	/**
	 * 操作对象
	 */
	private String operateTarget;
	/**
     * 操作类型
     */
    private String operateType;
	/**
     * 操作对象Id
     */
    private String operateTargetId;
    /**
     * 操作说明
     */
    private String operateDesc;
	/**
     * 操作者IP
     */
    private String operatorIp;
    /**
     * 操作者用户ID
     */
    private String operatorUuid;
    /**
	 * 操作返回状态
	 */
	private OperateStatus operateStatus;
	/**
	 * 全局链路跟踪Id
	 */
	private String traceId;
	/**
     * 操作时间开始
     */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operateTimeStart;
    /**
     * 操作时间结束
     */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operateTimeEnd;
}
