package com.zakiis.gateway.constant;

import org.springframework.core.Ordered;

public interface GatewayConstant {

	/** request body string */
	String ATTR_REQUEST_BODY = "requestBodyStr";
	
	/** 
	 * filter execute order: traceId -> paramter -> logRequest
	 */
	int ORDER_TRACE_ID_FILTER = Ordered.HIGHEST_PRECEDENCE;
	int ORDER_PARAMETER_FILTER = ORDER_TRACE_ID_FILTER + 1;
	int ORDER_LOG_REQUEST_FILTER = ORDER_PARAMETER_FILTER + 1;
	
}
