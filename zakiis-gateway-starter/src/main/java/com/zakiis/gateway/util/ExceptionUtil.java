package com.zakiis.gateway.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import com.zakiis.core.exception.BusinessException;
import com.zakiis.core.exception.web.StandardHttpException;

public class ExceptionUtil {

	public static HttpStatus getHttpStatus(Throwable t) {
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		if (t instanceof StandardHttpException e) {
			status = HttpStatus.valueOf(e.getHttpStatusCode());
		} else if (t instanceof HttpStatusCodeException e) {
			status = HttpStatus.valueOf(e.getStatusCode().value());
		} else if (t instanceof ResponseStatusException e) {
			status = HttpStatus.valueOf(e.getStatusCode().value());
		} else if (t instanceof BusinessException e) {
			status = HttpStatus.OK;
		} else if (t instanceof MethodArgumentNotValidException e) {
			status = HttpStatus.BAD_REQUEST;
		}
		return status;
	}
}
