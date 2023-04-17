package com.zakiis.auditlog.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.zakiis.core.util.JsonUtil;

/**
 * Spring EL工具类
 * @author Liu Zhenghua
 */
public class SpelUtil {
	
	/**
	 * 获取Spring EL表达式上下文对象
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static EvaluationContext getEvaluationContext() throws NoSuchMethodException, SecurityException {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.registerFunction("join", SpelUtil.class.getDeclaredMethod("join", Object[].class));
        context.registerFunction("joinList", SpelUtil.class.getDeclaredMethod("joinList", List.class));
        context.registerFunction("json", SpelUtil.class.getDeclaredMethod("json", Object.class));
        return context;
	}

	public static String join(Object[] obj) {
    	return StringUtils.join(obj, ",");
    }
	public static String joinList(@SuppressWarnings("rawtypes") List obj) {
    	return StringUtils.join(obj, ",");
    }

	public static String json(Object obj) {
		return JsonUtil.toJson(obj);
	}
}
