package com.zakiis.gateway.filter;

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.zakiis.gateway.constant.GatewayConstant;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reading request body
 * GatewayClient -> GatewayHandlerMapping -> GatewayWebFilter -> Gateway Filter
 * @date 2023-06-19 15:34:19
 * @author Liu Zhenghua
 */
public class ParameterFilter implements WebFilter, Ordered {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		String contentLength = request.getHeaders().getFirst(HttpHeaders.CONTENT_LENGTH);
		String contentType = request.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
		// check content length so that can avoid create new empty request.
		if ("0".equals(contentLength)
				|| (StringUtils.isNotBlank(contentType) &&!contentType.startsWith(MediaType.APPLICATION_JSON_VALUE)) ) {
        	return chain.filter(exchange);
        }
		return DataBufferUtils.join(exchange.getRequest().getBody())
			.switchIfEmpty(Mono.defer(() -> {
				// request body had been read by DataBufferUtils.join, and the state changed to 'COMPLETE', need create new request to avoid IllegaStateException.
				ServerWebExchange emptyRequestExchange = buildEmptyRequestExchange(exchange);
				return chain.filter(emptyRequestExchange).then(Mono.empty());
			}))
			.flatMap(dataBuffer -> {
				if (dataBuffer == null) {
					return chain.filter(exchange);
				}
				// 读取body
				byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                exchange.getAttributes().put(GatewayConstant.ATTR_REQUEST_BODY, new String(bytes, StandardCharsets.UTF_8));
                // 创建新的 request
                Flux<DataBuffer> newBodyFlux = Flux.defer(() -> {
                	DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                	return Mono.just(buffer);
                });
                ServerHttpRequest newRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                    @Override
                    public Flux<DataBuffer> getBody() {
                        return newBodyFlux;
                    }
                }.mutate().header(HttpHeaders.CONTENT_LENGTH, String.valueOf(bytes.length)).build(); //coding issue may cause the content length changed.
                return chain.filter(exchange.mutate().request(newRequest).build());
			});
	}
	
	/**
	 * build an empty request body exchange.
	 * @param exchange
	 * @return
	 */
	private ServerWebExchange buildEmptyRequestExchange(ServerWebExchange exchange) {
		// empty request body
		Flux<DataBuffer> newBodyFlux = Flux.defer(() -> {
        	DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(new byte[0]);
        	return Mono.just(buffer);
        });
		ServerHttpRequest newRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public Flux<DataBuffer> getBody() {
                return newBodyFlux;
            }
        }.mutate().header(HttpHeaders.CONTENT_LENGTH, "0").build();
        return exchange.mutate().request(newRequest).build();
	}

	@Override
	public int getOrder() {
		return GatewayConstant.ORDER_PARAMETER_FILTER;
	}

}
