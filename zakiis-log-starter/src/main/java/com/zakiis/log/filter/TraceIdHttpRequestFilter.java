package com.zakiis.log.filter;

import java.io.IOException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.zakiis.log.config.TraceIdProperties;
import com.zakiis.log.holder.TraceIdHolder;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class TraceIdHttpRequestFilter extends OncePerRequestFilter {

	private final TraceIdProperties traceIdProperties;
	
	@PostConstruct
	public void init() {
		TraceIdHolder.init(traceIdProperties.getAppName() + "_");
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (!traceIdProperties.isEnabled()) {
			filterChain.doFilter(request, response);
			return;
		}
		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
		try {
			String traceId = request.getHeader(traceIdProperties.getHeader());
			TraceIdHolder.set(traceId);
			log.info("Request {} start", request.getRequestURI());
			filterChain.doFilter(requestWrapper, response);
		} finally {
			log.info("Request {} end, request body:{}", request.getRequestURI(), new String(requestWrapper.getContentAsByteArray()));
			TraceIdHolder.clear();
		}
	}
}
