package com.zakiis.log.holder;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import com.zakiis.core.domain.constants.CommonConstants;

public class TraceIdHolder {
	
	private static String traceIdPrefix = "";
	
	public static void init(String traceIdPrefix) {
		if (traceIdPrefix != null) {
			TraceIdHolder.traceIdPrefix = traceIdPrefix;
		}
	}

	public static void set(String traceId) {
		if (StringUtils.isBlank(traceId)) {
			traceId = generateTraceId();
		}
		MDC.put(CommonConstants.TRACE_ID_PARAM_NAME, traceId);
	}
	
	public static String get() {
		String traceId = (String)MDC.get(CommonConstants.TRACE_ID_PARAM_NAME);
		if (StringUtils.isBlank(traceId)) {
			traceId = generateTraceId();
			MDC.put(CommonConstants.TRACE_ID_PARAM_NAME, traceId);
		}
		return traceId;
	}
	
	public static void clear() {
		MDC.clear();
	}
	
	public static String generateTraceId() {
		return traceIdPrefix + System.currentTimeMillis() + RandomStringUtils.randomAlphabetic(8);
	}
}
