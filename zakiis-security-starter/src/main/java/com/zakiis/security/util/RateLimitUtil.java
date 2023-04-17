package com.zakiis.security.util;

import java.lang.annotation.Annotation;
import java.util.Calendar;
import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.zakiis.core.util.JsonUtil;
import com.zakiis.security.MD5Util;

public class RateLimitUtil {
	
	static SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
	static DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();
	static BeanFactoryResolver beanFactoryResolver = null;
	
	public static void init(ApplicationContext context) {
		if (beanFactoryResolver == null) {
			beanFactoryResolver = new BeanFactoryResolver(context);
		}
	}

	/**
	 * Generate Rate Limit key
	 * @return rate limit key
	 */
	public static String genRateLimitKey(String className, String methodName, Object params) {
		String limitKey = MD5Util.digestAsHex((className + methodName + JsonUtil.toJson(params)).getBytes());
		return limitKey;
	}
	
	/**
	 * Get value by spring EL expression.
	 * @param spEL
	 * @param methodSignature
	 * @param args
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static String getStrValBySpEL(String spEL, MethodSignature methodSignature, Object[] args) throws NoSuchMethodException, SecurityException {
        String[] paramNames = nameDiscoverer.getParameterNames(methodSignature.getMethod());
        if (paramNames != null && paramNames.length > 0) {
            Expression expression = spelExpressionParser.parseExpression(spEL);
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setBeanResolver(beanFactoryResolver);
            for (int i = 0; i < args.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
            return Optional.ofNullable(expression.getValue(context))
            	.map(Object::toString)
            	.orElse(null);
        }
        return null;
    }
	
	@SuppressWarnings("unchecked")
	public static <T> T getValBySpEL(String spEL, MethodSignature methodSignature, Object[] args) throws NoSuchMethodException, SecurityException {
        String[] paramNames = nameDiscoverer.getParameterNames(methodSignature.getMethod());
        if (paramNames != null && paramNames.length > 0) {
            Expression expression = spelExpressionParser.parseExpression(spEL);
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setBeanResolver(beanFactoryResolver);
            for (int i = 0; i < args.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
            return (T)expression.getValue(context);
        }
        return null;
    }
	
	public static <T extends Annotation> T getAnnotation(JoinPoint joinPoint, Class<T> clazz) {
		T declaredAnnotation = ((MethodSignature)joinPoint.getSignature()).getMethod().getDeclaredAnnotation(clazz);
		if (declaredAnnotation == null) {
			declaredAnnotation = joinPoint.getTarget().getClass().getDeclaredAnnotation(clazz);
		}
		return declaredAnnotation;
	}
	
	public static long getTodayLeftSeconds() {
		Calendar now = Calendar.getInstance();
		Calendar todayEnd = Calendar.getInstance();
		todayEnd.set(Calendar.HOUR_OF_DAY, 23);
		todayEnd.set(Calendar.MINUTE, 59);
		todayEnd.set(Calendar.SECOND, 59);
		return (todayEnd.getTimeInMillis() - now.getTimeInMillis()) / 1000;
	}
	
}
