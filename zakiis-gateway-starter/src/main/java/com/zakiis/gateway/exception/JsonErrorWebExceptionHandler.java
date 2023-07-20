package com.zakiis.gateway.exception;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.zakiis.core.exception.BusinessException;
import com.zakiis.core.exception.web.StandardHttpException;
import com.zakiis.gateway.config.GatewayProperties.TraceIdConfig;
import com.zakiis.gateway.util.ExceptionUtil;

/**
 * Using JSON response if error throws.
 * @date 2023-03-13 15:13:51
 * @author Liu Zhenghua
 */
public class JsonErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {
	
	private final TraceIdConfig traceIdConfig;

	public JsonErrorWebExceptionHandler(ErrorAttributes errorAttributes, Resources resources,
			ErrorProperties errorProperties, ApplicationContext applicationContext, TraceIdConfig traceIdConfig) {
		super(errorAttributes, resources, errorProperties, applicationContext);
		this.traceIdConfig = traceIdConfig;
	}

	@Override
	protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
		return RouterFunctions.route(RequestPredicates.all(), super::renderErrorResponse);
	}

	@Override
	protected Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
		Map<String, Object> errorAttributes = new LinkedHashMap<>();
		errorAttributes.put("timestamp", new Date());
		Throwable t = getError(request);
		HttpStatus httpStatus = ExceptionUtil.getHttpStatus(t);
		errorAttributes.put("status", httpStatus.value());
		processUserDefinedField(errorAttributes, t, httpStatus, request);
		return errorAttributes;
	}

	/**
	 * set user-defined fields for response to conformed the framework.
	 * {@link com.zakiis.core.domain.dto.Resp}
	 * @param errorAttributes
	 * @param e
	 * @return
	 */
	private boolean processUserDefinedField(Map<String, Object> errorAttributes, Throwable e, HttpStatus httpStatus
			, ServerRequest request) {
		if (e instanceof StandardHttpException httpException) {
			errorAttributes.put("code", String.valueOf(httpException.getHttpStatusCode()));
			errorAttributes.put("message", httpException.getMessage());
			return true;
		} else if (e instanceof BusinessException bizException) {
			errorAttributes.put("code", bizException.getCode());
			errorAttributes.put("message", bizException.getMessage());
		} else {
			String errorCode = String.valueOf(httpStatus.value());
			String errorMsg = Optional.ofNullable(e.getMessage()).orElse(httpStatus.getReasonPhrase());
			errorAttributes.put("code", errorCode);
			errorAttributes.put("message", errorMsg);
		}
		errorAttributes.put("success", false);
		if (request instanceof ServerHttpRequest httpRequest) {
			errorAttributes.put("traceId", httpRequest.getHeaders().getFirst(traceIdConfig.getHttpHeaderKey()));
		}
		return false;
	}

	
	
}
