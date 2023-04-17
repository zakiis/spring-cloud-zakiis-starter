package com.zakiis.log.filter;

import com.zakiis.log.config.TraceIdProperties;
import com.zakiis.log.holder.TraceIdHolder;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TraceIdFeignRequestFilter implements RequestInterceptor {

	private final TraceIdProperties traceIdProperties;
	
	@PostConstruct
	public void init() {
		TraceIdHolder.init(traceIdProperties.getAppName() + "_");
	}
	
	@Override
	public void apply(RequestTemplate template) {
		template.header(traceIdProperties.getHeader(), TraceIdHolder.get());
	}

}
