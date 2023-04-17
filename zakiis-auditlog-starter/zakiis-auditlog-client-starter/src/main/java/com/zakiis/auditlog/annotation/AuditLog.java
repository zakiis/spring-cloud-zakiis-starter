package com.zakiis.auditlog.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Audit log annotation
 * @date 2023-04-04 17:23:01
 * @author Liu Zhenghua
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {
	/**
	 * 操作分组
	 */
	String operateGroup();
	/**
	 * 操作对象
	 */
	String operateTarget();
	/**
	 * 操作对象Id的EL表达式
	 */
	String operateTargetIdEL();
	/**
	 * 操作类型
	 */
	String operateType();
	/**
	 * 操作说明
	 */
	String operateDesc() default "nil";
	/**
	 * 备注EL表达式
	 */
	String remarkEL() default "nil";
}
