package com.zakiis.gateway.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.spi.LocationAwareLogger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import com.zakiis.core.exception.BusinessException;
import com.zakiis.core.exception.web.StandardHttpException;

public class ExceptionUtil {
	
	public static final String FQCN = ExceptionUtil.class.getName();

	public static HttpStatus getHttpStatus(Throwable t) {
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		if (t instanceof StandardHttpException e) {
			status = HttpStatus.valueOf(e.getHttpStatusCode());
		} else if (t instanceof HttpStatusCodeException e) {
			status = HttpStatus.valueOf(e.getStatusCode().value());
		} else if (t instanceof ResponseStatusException e) {
			status = HttpStatus.valueOf(e.getStatusCode().value());
		} else if (t instanceof WebClientResponseException e) {
			status = HttpStatus.valueOf(e.getStatusCode().value());
		} else if (t instanceof BusinessException e) {
			status = HttpStatus.OK;
		} else if (t instanceof MethodArgumentNotValidException e) {
			status = HttpStatus.BAD_REQUEST;
		}
		return status;
	}
	
	public static void printErrorLog(ReactiveLogger log, String traceId, Throwable t, HttpStatus httpStatus) {
		if (httpStatus.value() < 500) {
			if (log.getLog() instanceof LocationAwareLogger alog) {
				log.log(alog, traceId, FQCN, "{}, at:\n\t{}", LocationAwareLogger.WARN_INT, httpStatus, buildSimpleStatckTraceMsg(t));
			} else {
				log.warn(traceId, "{}, at:\n\t{}", httpStatus, buildSimpleStatckTraceMsg(t));		
			}
		} else {
			if (log.getLog() instanceof LocationAwareLogger alog) {
				log.log(alog, traceId, FQCN, "got an unknown error", LocationAwareLogger.ERROR_INT, t);
			} else {
				log.error(traceId, "got an unknown error", t);
			}
		}
	}
	
	private static String buildSimpleStatckTraceMsg(Throwable e) {
    	if (e.getStackTrace().length > 3) {
    		List<StackTraceElement> stacks = Arrays.asList(e.getStackTrace()[0], e.getStackTrace()[1], e.getStackTrace()[2]);
    		return StringUtils.join(stacks, "\n\t");
    	}
    	return StringUtils.join(e.getStackTrace(), "\n\t");
    }
}
