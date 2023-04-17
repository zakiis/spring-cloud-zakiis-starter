package com.zakiis.auditlog.aspect;

import java.util.HashMap;
import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.zakiis.auditlog.annotation.AuditLog;
import com.zakiis.auditlog.config.AuditLogClientProperties;
import com.zakiis.auditlog.domain.constants.OperateStatus;
import com.zakiis.auditlog.domain.dto.AuditLogResponse;
import com.zakiis.auditlog.domain.message.AuditLogMessage;
import com.zakiis.auditlog.service.AuditLogPublishService;
import com.zakiis.auditlog.service.tool.AuditLogTool;
import com.zakiis.auditlog.util.SpelUtil;
import com.zakiis.core.util.JsonUtil;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Publish the audit log event.
 * @see AuditLog
 * @date 2023-04-04 17:23:52
 * @author Liu Zhenghua
 */
@Aspect
@RequiredArgsConstructor
public class AuditLogAspect {
    
	@NonNull
    private final AuditLogClientProperties auditLogClientProperties;
	@NonNull
	private final AuditLogTool auditLogTool;
	@NonNull
	private final AuditLogPublishService auditLogService;

    /**
     * 用于SpEL表达式解析.
     */
    private SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
    /**
     * 用于获取方法参数定义名字.
     */
    private DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();
    
    /**
     * @within 方法所属类如果包含了@AuditLog注解则会被拦截
     * @annotation 方法上如果包含了@AuditLog注解则会被拦截
     */
    @Pointcut("@within(com.zakiis.auditlog.annotation.AuditLog) || @annotation(com.zakiis.auditlog.annotation.AuditLog)")
    public void auditLogPointcut() {}

	@Around("auditLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    	Object result = null;
    	try {
    		result = joinPoint.proceed();
    	} catch (Throwable e) {
    		String remarks = Optional.ofNullable(e.getMessage())
    				.orElse(e.getClass().getName());
            sendAuditLogMessage(joinPoint, OperateStatus.FAIL, remarks);
    		throw e;
		}
    	String remarks = null;
    	OperateStatus operateStatus = OperateStatus.SUCCESS;
    	if (result != null && result instanceof AuditLogResponse auditLogResp) {
    		if (!auditLogResp.isSuccess()) {
    			operateStatus = OperateStatus.FAIL;
    		}
    	}
        sendAuditLogMessage(joinPoint, operateStatus, remarks);
    	return result;
    }

    /**
     * 解析spEL表达式
     * @throws SecurityException 
     * @throws NoSuchMethodException 
     */
    private String getValBySpEL(String spEL, MethodSignature methodSignature, Object[] args) throws NoSuchMethodException, SecurityException {
        //获取方法形参名数组
        String[] paramNames = nameDiscoverer.getParameterNames(methodSignature.getMethod());
        if (paramNames != null && paramNames.length > 0) {
            Expression expression = spelExpressionParser.parseExpression(spEL);
            EvaluationContext context = SpelUtil.getEvaluationContext();
            // 给上下文赋值
            for (int i = 0; i < args.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
            return Optional.ofNullable(expression.getValue(context))
            	.map(Object::toString)
            	.orElse(null);
        }
        return null;
    }

    /**
     * 发送审计日志对象到MQ
     * @throws SecurityException 
     * @throws NoSuchMethodException 
     */
    private void sendAuditLogMessage(JoinPoint joinPoint, OperateStatus operateStatus, String remark) throws NoSuchMethodException, SecurityException {
        if (auditLogClientProperties.isEnabled()) {
            AuditLog auditLogAnnotation = ((MethodSignature)joinPoint.getSignature()).getMethod().getDeclaredAnnotation(AuditLog.class);
            if (auditLogAnnotation == null) {
                // 获取类上的注解
                auditLogAnnotation = joinPoint.getTarget().getClass().getDeclaredAnnotation(AuditLog.class);
            }
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        	String operateTargetIdEL = auditLogAnnotation.operateTargetIdEL();
        	String operateTargetId = getValBySpEL(operateTargetIdEL, methodSignature, joinPoint.getArgs());
        	if (remark == null && !"nil".equals(auditLogAnnotation.remarkEL())) {
        		remark = getValBySpEL(auditLogAnnotation.remarkEL(), methodSignature, joinPoint.getArgs());
        	}
        	
        	AuditLogMessage auditLogMessage = auditLogTool.buildAuditLogMessage(auditLogAnnotation.operateGroup(), auditLogAnnotation.operateTarget()
        			, auditLogAnnotation.operateType(), operateTargetId, auditLogAnnotation.operateDesc(), operateStatus, remark,
        			methodSignature.getDeclaringTypeName(), methodSignature.getName(), buildParamsStr(joinPoint));
        	auditLogService.publish(auditLogMessage);
        }
    	
    }
    
    private String buildParamsStr(JoinPoint joinPoint) {
    	MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    	String[] parameterNames = methodSignature.getParameterNames();
    	Object[] args = joinPoint.getArgs();
    	HashMap<String, Object> paramMap = new HashMap<String, Object>();
    	for (int i = 0; i < parameterNames.length; i++) {
    		paramMap.put(parameterNames[i], args[i]);
    	}
    	return JsonUtil.toJson(paramMap);
    }
}
