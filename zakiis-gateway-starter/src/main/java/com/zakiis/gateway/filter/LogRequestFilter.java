package com.zakiis.gateway.filter;

import java.net.URI;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.zakiis.core.domain.constants.CommonConstants;
import com.zakiis.core.domain.dto.CommonResp;
import com.zakiis.core.domain.dto.Resp;
import com.zakiis.core.util.JsonUtil;
import com.zakiis.gateway.config.GatewayProperties.LogRequestConfig;
import com.zakiis.gateway.constant.GatewayConstant;
import com.zakiis.gateway.util.ExceptionUtil;
import com.zakiis.gateway.util.ReactiveLogger;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LogRequestFilter implements WebFilter, Ordered {
	
	private final LogRequestConfig logRequestProperties;
	private static final ReactiveLogger log = new ReactiveLogger(LoggerFactory.getLogger(LogRequestFilter.class));

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		return Mono.deferContextual(ctx -> {
			final String traceId = ctx.get(CommonConstants.TRACE_ID_PARAM_NAME);
			long start = System.currentTimeMillis();
			String path = exchange.getRequest().getURI().getPath();
			String method = exchange.getRequest().getMethod().name();
			if (logRequestProperties.isEnabled()) {
				log.info(traceId, "{} {} start, request body:{}", method, path, exchange.getAttribute(GatewayConstant.ATTR_REQUEST_BODY));
			}
			return chain.filter(exchange)
				.onErrorResume(t -> handleEmptyResponseStatusException(exchange, t))
				.doOnError(t -> {
					HttpStatus httpStatus = ExceptionUtil.getHttpStatus(t);
					exchange.getResponse().setStatusCode(httpStatus);
					printEndLog(path, method, httpStatus, start, traceId);
				})
				.then(Mono.defer(() -> {
					URI gatewayRequestUri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
					String routedPath = Optional.ofNullable(gatewayRequestUri).map(Object::toString).orElse(path);
					printEndLog(routedPath, method, exchange.getResponse().getStatusCode(), start, traceId);
					return Mono.empty();
				}));
		});
	}
	
	public void printEndLog(String path, String method, HttpStatusCode statusCode, long start, String traceId) {
		long end = System.currentTimeMillis();
		if (logRequestProperties.isEnabled()) {
			log.info(traceId, "{} {} end, status: {}, time elapse {} ms", method, path, statusCode, end - start);
		}
	}
	
	/**
	 * write body for ReponseStatusException while the response body is empty
	 * @param exchange
	 * @param t
	 * @return
	 */
	public Mono<Void> handleEmptyResponseStatusException(ServerWebExchange exchange, Throwable t) {
		if (t instanceof ResponseStatusException e) {
			Long contentLength = Optional.of(exchange.getResponse()).map(ServerHttpResponse::getHeaders)
				.map(h -> h.getFirst(HttpHeaders.CONTENT_LENGTH)).filter(StringUtils::isNotBlank)
				.map(Long::valueOf).orElse(0L);
			if (!exchange.getResponse().isCommitted() && contentLength == 0) {
				HttpStatus httpStatus = ExceptionUtil.getHttpStatus(t);
				exchange.getResponse().setStatusCode(httpStatus);
				exchange.getResponse().getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
				Resp resp = CommonResp.fail(String.valueOf(httpStatus.value()), httpStatus.getReasonPhrase());
				DataBuffer dataBuffer = exchange.getResponse().bufferFactory().wrap(JsonUtil.toJson(resp).getBytes());
				return exchange.getResponse().writeWith(Mono.just(dataBuffer));
			}
		} 
		return Mono.error(t);
	}

	/**
	 * execute after {@link ReactiveLoadBalancerClientFilter} to retrieve the request URL which contains destination machine
	 */
	@Override
	public int getOrder() {
		return GatewayConstant.ORDER_LOG_REQUEST_FILTER;
	}

}
