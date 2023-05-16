package com.zakiis.gateway.filter;

import java.net.URI;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;

import com.zakiis.core.domain.constants.CommonConstants;
import com.zakiis.gateway.config.GatewayProperties.LogRequestConfig;
import com.zakiis.gateway.constant.GatewayConstant;
import com.zakiis.gateway.util.ReactiveLogger;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LogRequestFilter implements GlobalFilter, Ordered {
	
	private final LogRequestConfig logRequestProperties;
	private static final ReactiveLogger log = new ReactiveLogger(LoggerFactory.getLogger(LogRequestFilter.class));

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		return Mono.deferContextual(ctx -> {
			final String traceId = ctx.get(CommonConstants.TRACE_ID_PARAM_NAME);
			long start = System.currentTimeMillis();
			URI gatewayRequestUri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
			String path = Optional.ofNullable(gatewayRequestUri).map(Object::toString).orElse(exchange.getRequest().getURI().toString());
			String method = exchange.getRequest().getMethod().name();
			
			if (logRequestProperties.isEnabled()) {
				log.info(traceId, "{} {} start, request body:{}", method, path, exchange.getAttribute(GatewayConstant.ATTR_REQUEST_BODY));
			}
			return chain.filter(exchange)
				.doOnTerminate(() -> {
					long end = System.currentTimeMillis();
					if (logRequestProperties.isEnabled()) {
						log.info(traceId, "{} {} end, status: {}, time elapse {} ms", method, path, exchange.getResponse().getStatusCode(), end - start);
					}
				});
		});
	}

	/**
	 * execute after {@link ReactiveLoadBalancerClientFilter} to retrieve the request URL which contains destination machine
	 */
	@Override
	public int getOrder() {
		return ReactiveLoadBalancerClientFilter.LOAD_BALANCER_CLIENT_FILTER_ORDER + 1;
	}

}
