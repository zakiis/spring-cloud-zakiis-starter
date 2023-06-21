package com.zakiis.web.exception;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.zakiis.core.domain.dto.CommonResp;
import com.zakiis.core.domain.dto.CommonResps;
import com.zakiis.core.domain.dto.Resp;
import com.zakiis.core.exception.BusinessException;
import com.zakiis.core.exception.ZakiisRuntimeException;
import com.zakiis.core.exception.web.StandardHttpException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ResponseBody
@RestControllerAdvice
public class GlobalExceptionAdvice {

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Throwable.class)
	public Resp handleThrowable(Throwable e) {
		log.error("got an unknown error", e);
		String msg = Optional.ofNullable(e.getMessage()).orElse(CommonResps.UNKNOWN_ERROR.getMessage());
		return CommonResp.fail(CommonResps.UNKNOWN_ERROR.getCode(), msg);
	}
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(ZakiisRuntimeException.class)
	public Resp handleException(ZakiisRuntimeException e) {
		String msg = Optional.ofNullable(e.getMessage()).orElse(CommonResps.RUNTIME_ERROR.getMessage());
    	log.warn("{}, at:\n\t{}", msg, buildSimpleStatckTraceMsg(e));
		return CommonResp.fail(CommonResps.RUNTIME_ERROR.getCode(), msg);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(BusinessException.class)
	public Resp handleException(BusinessException e) {
    	log.warn("{}:{}, at:\n\t{}", e.getCode(), e.getMessage(), buildSimpleStatckTraceMsg(e));
		return CommonResp.fail(e.getCode(), e.getMessage());
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Resp handleException(MethodArgumentNotValidException e) {
		String message = Optional.of(e).map(MethodArgumentNotValidException::getAllErrors)
				.map(l -> l.size() > 0 ? l.get(0).getDefaultMessage() : "parameter not valid").orElse("parameter not valid");
    	log.warn("{}:{}, at:\n\t{}", HttpStatus.BAD_REQUEST.value(), message, buildSimpleStatckTraceMsg(e));
		return CommonResp.fail(String.valueOf(HttpStatus.BAD_REQUEST.value()), message);
	}
	
	@ExceptionHandler(StandardHttpException.class)
	public ResponseEntity<Resp> handleException(StandardHttpException e) {
    	log.warn("{}:{}, at:\n\t{}", e.getHttpStatusCode(), e.getMessage(), buildSimpleStatckTraceMsg(e));
    	CommonResp<Object> resp = CommonResp.fail(String.valueOf(e.getHttpStatusCode()), e.getMessage());
		return ResponseEntity.status(e.getHttpStatusCode()).body(resp);
	}

	private String buildSimpleStatckTraceMsg(Throwable e) {
    	if (e.getStackTrace().length > 3) {
    		List<StackTraceElement> stacks = Arrays.asList(e.getStackTrace()[0], e.getStackTrace()[1], e.getStackTrace()[2]);
    		return StringUtils.join(stacks, "\n\t");
    	}
    	return StringUtils.join(e.getStackTrace(), "\n\t");
    }
}
