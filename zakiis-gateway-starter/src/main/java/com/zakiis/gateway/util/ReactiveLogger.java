package com.zakiis.gateway.util;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.spi.LocationAwareLogger;

import com.zakiis.core.domain.constants.CommonConstants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * method executed in Reactive env are cross threads, we need put the traceId to the MDC before log and clean it after done.
 * @date 2023-03-09 17:07:20
 * @author Liu Zhenghua
 */
@Getter
@RequiredArgsConstructor
public class ReactiveLogger {

	private final Logger log;
	public static final String FQCN = ReactiveLogger.class.getName();
	
	public void debug(String traceId, String format, Object... arguments) {
		MDC.put(CommonConstants.TRACE_ID_PARAM_NAME, traceId);
		if (log instanceof LocationAwareLogger alog) {
			log(alog, FQCN, format, LocationAwareLogger.DEBUG_INT, arguments);
		} else {
			log.debug(format, arguments);
		}
		MDC.clear();
	}
	
	public void info(String traceId, String format, Object... arguments) {
		MDC.put(CommonConstants.TRACE_ID_PARAM_NAME, traceId);
		if (log instanceof LocationAwareLogger alog) {
			log(alog, FQCN, format, LocationAwareLogger.INFO_INT, arguments);
		} else {
			log.info(format, arguments);
		}
		MDC.clear();
	}
	
	public void warn(String traceId, String format, Object... arguments) {
		MDC.put(CommonConstants.TRACE_ID_PARAM_NAME, traceId);
		if (log instanceof LocationAwareLogger alog) {
			log(alog, FQCN, format, LocationAwareLogger.WARN_INT, arguments);
		} else {
			log.warn(format, arguments);
		}
		MDC.clear();
	}
	
	public void error(String traceId, String format, Object... arguments) {
		MDC.put(CommonConstants.TRACE_ID_PARAM_NAME, traceId);
		if (log instanceof LocationAwareLogger alog) {
			log(alog, FQCN, format, LocationAwareLogger.ERROR_INT, arguments);
		} else {
			log.error(format, arguments);
		}
		MDC.clear();
	}
	
	public void log(LocationAwareLogger alog, String traceId, String fqcn, String format, int level, Object... arguments) {
		MDC.put(CommonConstants.TRACE_ID_PARAM_NAME, traceId);
		log(alog, fqcn, format, level, arguments);
		MDC.clear();
	}
	
	private void log(LocationAwareLogger alog, String fqcn, String format, int level, Object... arguments) {
		Throwable t = null;
		Object[] argArray = arguments;
		if (ArrayUtils.isNotEmpty(arguments)) {
			Object lastObj = arguments[arguments.length - 1];
			if (lastObj instanceof Throwable) {
				t = (Throwable)lastObj;
				argArray = ArrayUtils.subarray(arguments, 0, arguments.length - 1);
			}
		}
		alog.log(null, fqcn, level, format, argArray, t);
	}
}
