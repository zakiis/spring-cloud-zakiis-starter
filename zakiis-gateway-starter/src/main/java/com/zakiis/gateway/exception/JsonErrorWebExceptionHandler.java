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
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.zakiis.core.exception.BusinessException;
import com.zakiis.core.exception.web.StandardHttpException;
import com.zakiis.gateway.config.GatewayProperties.TraceIdConfig;

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
		Throwable error = getError(request);
		boolean isBuiltInError = processBuiltInError(errorAttributes, error);
		if (!isBuiltInError) {
			HttpStatus errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			if (error instanceof NotFoundException) {
				errorStatus = HttpStatus.NOT_FOUND;
			}
			errorAttributes.put("status", errorStatus.value());
			String errorCode = "999999";
			String errorMsg = Optional.ofNullable(error.getMessage()).orElse(errorStatus.getReasonPhrase());
			// set user-defined fields, com.zakiis.core.domain.dto.Resp
			errorAttributes.put("code", errorCode);
			errorAttributes.put("message", errorMsg);
			errorAttributes.put("success", false);
		}
		errorAttributes.put("traceId", request.exchange().getRequest().getHeaders().getFirst(traceIdConfig.getHttpHeaderKey()));
		return errorAttributes;
	}

	private boolean processBuiltInError(Map<String, Object> errorAttributes, Throwable e) {
		if (e instanceof StandardHttpException httpException) {
			// status field represents HTTP status code
			errorAttributes.put("status", httpException.getHttpStatusCode());
			errorAttributes.put("code", String.valueOf(httpException.getHttpStatusCode()));
			errorAttributes.put("message", httpException.getMessage());
			errorAttributes.put("success", false);
			return true;
		} else if (e instanceof BusinessException bizException) {
			errorAttributes.put("status", HttpStatus.OK.value());
			errorAttributes.put("code", bizException.getCode());
			errorAttributes.put("message", bizException.getMessage());
			errorAttributes.put("success", false);
		}
		return false;
	}

	
	
}
