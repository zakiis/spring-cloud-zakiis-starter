package com.zakiis.rdb.filter;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.BaseExecutor;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.ReflectionUtils;

import com.zakiis.security.CipherUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Intercepts({
	@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class}),
	@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
	@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
	@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
	
})
public class MybatisCipherInterceptor implements Interceptor {
	
	public MybatisCipherInterceptor(byte[] aesSecretKey, byte[] iv, boolean enableFuzzyQuery) {
		CipherUtil.init(aesSecretKey, iv, enableFuzzyQuery);
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object target = invocation.getTarget();
		if (target instanceof ResultSetHandler) { //result decrypt process
			Object result = invocation.proceed();
			if (result == null) {
				return result;
			}
			//bug fixed: association cause multiple decrypt
			if (getQueryStatck(target) > 1) {
				return result;
			}
			Collection<?> collection = (Collection<?>)result;
			collection.forEach(CipherUtil::decrypt);
			return result;
		} else { // parameter encrypt process
			Object param = invocation.getArgs()[1];
			if (param == null) {
				return invocation.proceed();
			}
			MappedStatement statement = (MappedStatement)invocation.getArgs()[0];
			boolean criteriaEncrypt = false;
			if (SqlCommandType.SELECT.equals(statement.getSqlCommandType())) {
				criteriaEncrypt = true;
			}
			if (param instanceof Collection) {
				Collection<?> collection = (Collection<?>)param;
				for (Object v : collection) {
					CipherUtil.encrypt(v, criteriaEncrypt);
				}
			} else if (param instanceof Map) { //ParamMap mostly, but Map perhaps when using PageHelper
				Map<String, ?> paramMap = (Map<String, ?>)param;
				if (SqlCommandType.UPDATE.equals(statement.getSqlCommandType())) {
					if (paramMap.containsKey("et")) {
						CipherUtil.encrypt(paramMap.get("et"), criteriaEncrypt);
					}
				} else if (SqlCommandType.SELECT.equals(statement.getSqlCommandType())) {
					if (paramMap.containsKey("param1")) { //Entity param need put in first element
						CipherUtil.encrypt(paramMap.get("param1"), criteriaEncrypt);
					}
				} else {
					log.error("Sql command type {} not processed by cipher method", statement.getSqlCommandType());
				}
			} else {
				CipherUtil.encrypt(param, criteriaEncrypt);
			}
			return invocation.proceed();
		}
	}
	
	private int getQueryStatck(Object obj) {
		if (obj instanceof DefaultResultSetHandler) {
			try {
				Field executorField = ReflectionUtils.findField(DefaultResultSetHandler.class, "executor", Executor.class);
				if (obj == null || executorField == null) {
					return 1;
				}
				ReflectionUtils.makeAccessible(executorField);
				Executor executor = (Executor) executorField.get(obj);
				Field baseExecutorField = ReflectionUtils.findField(CachingExecutor.class, "delegate", Executor.class);
				if (executor == null || baseExecutorField == null) {
					return 1;
				}
				ReflectionUtils.makeAccessible(baseExecutorField);
				BaseExecutor baseExecutor = (BaseExecutor)baseExecutorField.get(executor);
				Field queryStatckField = ReflectionUtils.findField(BaseExecutor.class, "queryStack", int.class);
				if (baseExecutor == null || queryStatckField == null) {
					return 1;
				}
				ReflectionUtils.makeAccessible(queryStatckField);
				return (int)queryStatckField.get(baseExecutor);
			} catch (Exception e) {
				log.error("get query stack got an error", e);
			}
		}
		return 1;
	}

}
