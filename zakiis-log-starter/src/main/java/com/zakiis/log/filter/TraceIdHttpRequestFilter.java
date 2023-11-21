package com.zakiis.log.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.zakiis.log.config.TraceIdProperties;
import com.zakiis.log.holder.TraceIdHolder;
import com.zakiis.log.servlet.RepeatableReadServletRequest;

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
		String bodyStr = StringUtils.EMPTY;
		String contentType = request.getContentType();
		String path = request.getRequestURI();
		if (contentType == null || !contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
			// 由于request的InputStream只能读取一次，需要创建一个新的request对象
			byte[] bytes = request.getInputStream().readAllBytes();
			bodyStr = new String(bytes);
			request = new RepeatableReadServletRequest(request, bytes);
			Map<String,String[]> parameterMap = request.getParameterMap();
			if (!parameterMap.isEmpty() && !(request instanceof MultipartHttpServletRequest)) {
				List<String> params = new ArrayList<String>();
				for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
					params.add(String.format("%s=%s", entry.getKey(), StringUtils.join(entry.getValue(), ",")));
				}
				path += "?" + StringUtils.join(params, "&");
			}
		}
		long start = System.currentTimeMillis();
		try {
			String traceId = request.getHeader(traceIdProperties.getHeader());
			TraceIdHolder.set(traceId);
			log.info("{} {} start, request body:{}", request.getMethod(), path, bodyStr);
			filterChain.doFilter(request, response);
		} finally {
			long end = System.currentTimeMillis();
			log.info("{} {} end, status: {}, time elapse {} ms", request.getMethod()
					, path, response.getStatus(), end - start);
			TraceIdHolder.clear();
		}
	}
}
