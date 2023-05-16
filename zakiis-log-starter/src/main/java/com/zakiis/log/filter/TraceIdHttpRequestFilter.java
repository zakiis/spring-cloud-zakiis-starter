package com.zakiis.log.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import com.zakiis.log.config.TraceIdProperties;
import com.zakiis.log.holder.TraceIdHolder;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
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
		// 由于request的InputStream只能读取一次，需要创建一个新的request对象
		byte[] bytes = request.getInputStream().readAllBytes();
		HttpServletRequestWrapper newRequest = new HttpServletRequestWrapper(request) {
			@Override
			public ServletInputStream getInputStream() throws IOException {
				ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
				return new ServletInputStream() {
					@Override
					public int read() throws IOException {
						return bis.read();
					}
					@Override
					public void setReadListener(ReadListener readListener) {
						
					}
					@Override
					public boolean isReady() {
						return true;
					}
					@Override
					public boolean isFinished() {
						return false;
					}
				};
			}
		};
		long start = System.currentTimeMillis();
		try {
			String traceId = request.getHeader(traceIdProperties.getHeader());
			TraceIdHolder.set(traceId);
			log.info("{} {} start, request body:{}", request.getMethod(), request.getRequestURI(), new String(bytes, StandardCharsets.UTF_8));
			filterChain.doFilter(newRequest, response);
		} finally {
			long end = System.currentTimeMillis();
			log.info("{} {} end, status: {}, time elapse {} ms", request.getMethod()
					, request.getRequestURI(), response.getStatus(), end - start);
			TraceIdHolder.clear();
		}
	}
}
