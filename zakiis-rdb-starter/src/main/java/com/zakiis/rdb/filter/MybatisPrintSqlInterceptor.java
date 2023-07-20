package com.zakiis.rdb.filter;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Intercepts({
	@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
	@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
	@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
	
})
public class MybatisPrintSqlInterceptor implements Interceptor {
	
	private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		MappedStatement statement = (MappedStatement)invocation.getArgs()[0];
		Object param = invocation.getArgs()[1];
		BoundSql boundSql = null;
		if (invocation.getArgs().length == 6) {
			boundSql = (BoundSql)invocation.getArgs()[5];	
		} else {
			boundSql = statement.getBoundSql(param);
		}
		String sql = getSql(statement.getConfiguration(), boundSql);
		log.info(sql);
		return invocation.proceed();
	}

	private String getSql(Configuration configuration, BoundSql boundSql) {
		//空格替换成1个
		String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
		Object parameterObject = boundSql.getParameterObject();
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		if (parameterObject != null && parameterMappings != null && parameterMappings.size() > 0) {
			TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
				sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(parameterObject)));
			} else {
				MetaObject metaObject = configuration.newMetaObject(parameterObject);
				for (ParameterMapping parameterMapping : parameterMappings) {
					String propertyName = parameterMapping.getProperty();
					if (metaObject.hasGetter(propertyName)) {
						Object paramValue = metaObject.getValue(propertyName);
						sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(paramValue)));
					} else if (boundSql.hasAdditionalParameter(propertyName)) {
						Object paramValue = boundSql.getAdditionalParameter(propertyName);
						sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(paramValue)));
					} else {
						sql = sql.replaceFirst("\\?", "missing");
					}
				}	
			}
		}
		return sql;
	}
	
	private String getParameterValue(Object paramValue) {
		if (paramValue == null) {
			return "null";
		}
		if (paramValue instanceof String) {
			return "'" + paramValue + "'";
		}
		if (paramValue instanceof Date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return "'" + sdf.format(paramValue) + "'";
		}
		if (paramValue instanceof TemporalAccessor ta) {
			return "'" + FORMATTER.format(ta) + "'";
		}
		return paramValue.toString();
	}

}
