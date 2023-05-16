package com.zakiis.gateway.filter;

import java.nio.charset.StandardCharsets;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.zakiis.gateway.constant.GatewayConstant;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ParameterFilter implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		return DataBufferUtils.join(exchange.getRequest().getBody())
			.switchIfEmpty(chain.filter(exchange).then(Mono.empty()))
			.flatMap(dataBuffer -> {
				if (dataBuffer == null) {
					return chain.filter(exchange);
				}
				// 读取body
				byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                DataBufferUtils.release(dataBuffer);
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

}
