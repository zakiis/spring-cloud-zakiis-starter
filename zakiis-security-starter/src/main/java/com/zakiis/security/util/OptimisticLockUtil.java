package com.zakiis.security.util;

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.zakiis.core.util.JsonUtil;
import com.zakiis.security.MD5Util;

public class OptimisticLockUtil {

	static SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
	static DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

	public static <T extends Annotation> T getAnnotation(JoinPoint joinPoint, Class<T> clazz) {
		T declaredAnnotation = ((MethodSignature)joinPoint.getSignature()).getMethod().getDeclaredAnnotation(clazz);
		if (declaredAnnotation == null) {
			declaredAnnotation = joinPoint.getTarget().getClass().getDeclaredAnnotation(clazz);
		}
		return declaredAnnotation;
	}
	
	public static String genLockKey(String className, String methodName, Object params) {
		String limitKey = MD5Util.digestAsHex((className + methodName + JsonUtil.toJson(params)).getBytes());
		return limitKey;
	}

	public static String getValBySpEL(String spEL, MethodSignature methodSignature, Object[] args) throws NoSuchMethodException, SecurityException {
        String[] paramNames = nameDiscoverer.getParameterNames(methodSignature.getMethod());
        if (paramNames != null && paramNames.length > 0) {
            Expression expression = spelExpressionParser.parseExpression(spEL);
            StandardEvaluationContext context = new StandardEvaluationContext();
            for (int i = 0; i < args.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
            return Optional.ofNullable(expression.getValue(context))
            	.map(Object::toString)
            	.orElse(null);
        }
        return null;
	}
}
