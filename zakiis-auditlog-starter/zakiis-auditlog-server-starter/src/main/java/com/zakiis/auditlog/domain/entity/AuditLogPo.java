package com.zakiis.auditlog.domain.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import com.zakiis.auditlog.domain.constants.OperateStatus;
import com.zakiis.core.domain.constants.ZakiisStarterConstants;

import lombok.Data;

@Data
@Document(indexName = "#{@'" + ZakiisStarterConstants.AUDIT_LOG_SERVER_PREFIX + "-"
		+ "com.zakiis.auditlog.config.AuditLogServerProperties'" + ".getLogIndexName()}")
@Setting(shards = 1, replicas = 1, settingPath = "comma_analyzer.json")
public class AuditLogPo {

	/** ES Id */
	@Id
	@Field(type = FieldType.Keyword)
    private String id;
	/**
	 * 当前操作所属应用
	 */
	@Field(type = FieldType.Keyword)
	private String applicationName;
    /**
     * 操作者用户ID
     */
	@Field(type = FieldType.Keyword)
    private String operatorId;
    /**
     * 操作者用户名
     */
	@Field(type = FieldType.Keyword)
    private String operatorUserName;
    /**
     * 操作者姓名
     */
	@Field(type = FieldType.Text)
    private String operatorRealName;
    /**
     * 操作者IP
     */
	@Field(type = FieldType.Ip)
    private String operatorIp;
    /**
     * 操作时间
     */
	@Field(type = FieldType.Date)
    private Date operateTime;
    /**
     * 操作分组
     */
	@Field(type = FieldType.Keyword)
    private String operateGroup;
    /**
	 * 操作对象
	 */
	@Field(type = FieldType.Keyword)
	private String operateTarget;
	/**
     * 操作类型
     */
	@Field(type = FieldType.Keyword)
    private String operateType;
	/**
     * 操作对象Id
     */
	@Field(type = FieldType.Text, analyzer = "comma", searchAnalyzer = "comma")
    private String operateTargetId;
    /**
     * 操作说明
     */
    @Field(type = FieldType.Text)
    private String operateDesc;
    /**
	 * 操作返回状态
	 */
    @Field(type = FieldType.Keyword)
	private OperateStatus operateStatus;
	/**
	 * 备注
	 */
    @Field(type = FieldType.Text)
	private String remark;
	/**
	 * 客户端程序
	 */
    @Field(type = FieldType.Text)
	private String userAgent;
	/**
	 * 全局链路跟踪Id
	 */
    @Field(type = FieldType.Text)
	private String traceId;
	/**
	 * 类名
	 */
    @Field(type = FieldType.Text)
	private String className;
	/**
	 * 方法名
	 */
    @Field(type = FieldType.Text)
	private String methodName;
    /**
	 * 方法参数：以JSON格式存储
	 */
    @Field(type = FieldType.Text)
	private String methodParams;
}
