package com.zakiis.gateway.filter;

import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.MDC;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.zakiis.core.domain.constants.CommonConstants;
import com.zakiis.gateway.config.GatewayProperties.TraceIdConfig;
import com.zakiis.gateway.constant.GatewayConstant;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class TraceIdFilter implements WebFilter, Ordered {
	
	private final TraceIdConfig traceIdProperties;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		exchange.getAttributes().put(ServerWebExchangeUtils.PRESERVE_HOST_HEADER_ATTRIBUTE, true);
		if (!traceIdProperties.isEnabled()) {
			return chain.filter(exchange);
		}
		String traceId = exchange.getRequest().getHeaders().getFirst(traceIdProperties.getHttpHeaderKey());
		if (StringUtils.isBlank(traceId)) {
			traceId = generateTraceId();
			exchange = exchange.mutate()
				.request(
					exchange.getRequest().mutate()
						.header(traceIdProperties.getHttpHeaderKey(), traceId)
						.build()
				).build();
		}
		MDC.put(CommonConstants.TRACE_ID_PARAM_NAME, traceId);
		AtomicReference<String> traceIdRefer = new AtomicReference<String>(traceId);
		return chain.filter(exchange)
			.contextWrite(ctx -> ctx
				.put(CommonConstants.TRACE_ID_PARAM_NAME, traceIdRefer.get())
			).doOnTerminate(() -> {
				// in compatible for classes that relies on thread local logger 
				MDC.remove(CommonConstants.TRACE_ID_PARAM_NAME);
			});
	}
	
	public String generateTraceId() {
		return traceIdProperties.getPrefix() + System.currentTimeMillis() + RandomStringUtils.randomAlphabetic(8);
	}

	@Override
	public int getOrder() {
		return GatewayConstant.ORDER_TRACE_ID_FILTER;
	}

}
