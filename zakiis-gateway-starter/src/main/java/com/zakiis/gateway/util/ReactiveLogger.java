package com.zakiis.gateway.util;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.zakiis.core.domain.constants.CommonConstants;

import lombok.RequiredArgsConstructor;

/**
 * method executed in Reactive env are cross threads, we need put the traceId to the MDC before log and clean it after done.
 * @date 2023-03-09 17:07:20
 * @author Liu Zhenghua
 */
@RequiredArgsConstructor
public class ReactiveLogger {

	private final Logger log;
	
	public void debug(String traceId, String format, Object... arguments) {
		MDC.put(CommonConstants.TRACE_ID_PARAM_NAME, traceId);
		log.debug(format, arguments);
		MDC.clear();
	}
	
	public void info(String traceId, String format, Object... arguments) {
		MDC.put(CommonConstants.TRACE_ID_PARAM_NAME, traceId);
		log.info(format, arguments);
		MDC.clear();
	}
	
	public void warn(String traceId, String format, Object... arguments) {
		MDC.put(CommonConstants.TRACE_ID_PARAM_NAME, traceId);
		log.warn(format, arguments);
		MDC.clear();
	}
	
	public void error(String traceId, String format, Object... arguments) {
		MDC.put(CommonConstants.TRACE_ID_PARAM_NAME, traceId);
		log.error(format, arguments);
		MDC.clear();
	}
}
