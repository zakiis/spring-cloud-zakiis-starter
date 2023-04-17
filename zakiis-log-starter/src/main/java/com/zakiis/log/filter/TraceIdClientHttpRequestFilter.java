package com.zakiis.log.filter;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.zakiis.log.config.TraceIdProperties;
import com.zakiis.log.holder.TraceIdHolder;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * Interceptor for RestTemplate to pass traceId in header.
 * @author Liu Zhenghua
 * 2023-02-23 18:34:33
 */
@RequiredArgsConstructor
public class TraceIdClientHttpRequestFilter implements ClientHttpRequestInterceptor {

	private final TraceIdProperties traceIdProperties;
	
	@PostConstruct
	public void init() {
		TraceIdHolder.init(traceIdProperties.getAppName() + "_");
	}
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		request.getHeaders().add(traceIdProperties.getHeader(), TraceIdHolder.get());
		return execution.execute(request, body);
	}

}
