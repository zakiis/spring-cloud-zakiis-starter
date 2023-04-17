package com.zakiis.auditlog.domain.dto;

import java.io.Serializable;
import java.util.Date;

import com.zakiis.auditlog.domain.constants.OperateStatus;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuditLogDto implements Serializable {
	
	private static final long serialVersionUID = -164600471798881781L;
	/**
	 * 当前操作所属应用
	 */
	private String applicationName;
    /**
     * 操作者用户ID
     */
    private String operatorId;
    /**
     * 操作者用户名
     */
    private String operatorUserName;
    /**
     * 操作者姓名
     */
    private String operatorRealName;
    /**
     * 操作者IP
     */
    private String operatorIp;
    /**
     * 操作时间
     */
    private Date operateTime;
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
	 * 操作返回状态
	 */
	private OperateStatus operateStatus;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 客户端程序
	 */
	private String userAgent;
	/**
	 * 全局链路跟踪Id
	 */
	private String traceId;
	/**
	 * 类名(非业务需要字段)
	 */
	private String className;
	/**
	 * 方法名(非业务需要字段)
	 */
	private String methodName;
	/**
	 * 方法参数：以JSON格式存储
	 */
	private String methodParams;
}
