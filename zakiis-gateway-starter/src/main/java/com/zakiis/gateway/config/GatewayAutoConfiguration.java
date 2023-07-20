package com.zakiis.gateway.config;

import java.time.Duration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.DeferringLoadBalancerExchangeFilterFunction;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.result.view.ViewResolver;

import com.zakiis.core.domain.constants.CommonConstants;
import com.zakiis.core.domain.constants.ZakiisStarterConstants;
import com.zakiis.gateway.exception.JsonErrorWebExceptionHandler;
import com.zakiis.gateway.filter.LogRequestFilter;
import com.zakiis.gateway.filter.ParameterFilter;
import com.zakiis.gateway.filter.TraceIdFilter;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

@Slf4j
@AutoConfiguration
@AutoConfigureBefore(ErrorWebFluxAutoConfiguration.class)
@EnableConfigurationProperties(GatewayProperties.class)
public class GatewayAutoConfiguration {
	
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = ZakiisStarterConstants.GATEWAY_PREFIX, name = "log-request.enabled", havingValue = "true", matchIfMissing = true)
	public LogRequestFilter logRequestFilter(GatewayProperties properties) {
		log.info("Log request feature enabled.");
		return new LogRequestFilter(properties.getLogRequest());
	}
	
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = ZakiisStarterConstants.GATEWAY_PREFIX, name = "trace-id.enabled", havingValue = "true", matchIfMissing = true)
	public TraceIdFilter traceIdFilter(GatewayProperties properties) {
		log.info("Trace id feature enabled.");
		return new TraceIdFilter(properties.getTraceId());
	}
	
	@Bean
	@ConditionalOnMissingBean
	public ParameterFilter parameterFilter() {
		return new ParameterFilter();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public JsonErrorWebExceptionHandler jsonWebExceptionHandler(ErrorAttributes errorAttributes,
			WebProperties webProperties, ObjectProvider<ViewResolver> viewResolvers,
			ServerCodecConfigurer serverCodecConfigurer, ApplicationContext applicationContext,
			ServerProperties serverProperties, GatewayProperties gatewayProperties) {
		JsonErrorWebExceptionHandler exceptionHandler = new JsonErrorWebExceptionHandler(errorAttributes,
				webProperties.getResources(), serverProperties.getError(), applicationContext, gatewayProperties.getTraceId());
		exceptionHandler.setViewResolvers(viewResolvers.orderedStream().toList());
		exceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
		exceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());
		return exceptionHandler;
	}
	
	@Configuration(proxyBeanMethods = false)
	public static class WebClientConfiguration {
		/**
		 * WebClient can't change after build, You need add load balancer filter by yourself or inject WebClient.Builder Bean and add {@link LoadBalanced} annotation on the method.
		 * @see org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction
		 * @see org.springframework.cloud.loadbalancer.core.RandomLoadBalancer#choose(org.springframework.cloud.client.loadbalancer.Request)
		 * @see org.springframework.cloud.loadbalancer.core.CachingServiceInstanceListSupplier
		 * @see org.springframework.cloud.loadbalancer.core.DiscoveryClientServiceInstanceListSupplier
		 * @see org.springframework.cloud.loadbalancer.cache.DefaultLoadBalancerCache
		 * @see org.springframework.cloud.loadbalancer.cache.DefaultLoadBalancerCacheManager
		 * @param exchangeFilterFunction
		 * @return
		 */
		@Bean
		public WebClient webClient(DeferringLoadBalancerExchangeFilterFunction<? extends ExchangeFilterFunction> exchangeFilterFunction) {
			log.info("Customize web client feature enabled.");
			ConnectionProvider provider = ConnectionProvider.builder("http")
				.maxConnections(100)
				.pendingAcquireMaxCount(200) /** Default to {@code 2 * max connections}.*/
				.maxIdleTime(Duration.ofSeconds(30L))
				.build();
			LoopResources loopResources = LoopResources.create("reactor-http", 1, LoopResources.DEFAULT_IO_WORKER_COUNT, true);
			HttpClient httpClient = HttpClient.create(provider)
					.doOnConnected(conn -> {
						conn.addHandlerLast(new ReadTimeoutHandler(10))
							.addHandlerLast(new WriteTimeoutHandler(10));
					})
					.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
					.option(ChannelOption.TCP_NODELAY, true)
					.runOn(loopResources);
			return WebClient.builder()
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.filter(exchangeFilterFunction)
				.filter(traceIdFilter())
//				.filter(httpCodeFilter())
				.build();
		}
		
		private ExchangeFilterFunction traceIdFilter() {
			return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
				return Mono.deferContextual(ctx -> {
					String traceId = ctx.get(CommonConstants.TRACE_ID_PARAM_NAME);
					ClientRequest filteredRequest = ClientRequest.from(clientRequest)
						.header(CommonConstants.TRACE_ID_HEADER_NAME, traceId)
						.build();
					return Mono.just(filteredRequest);
				});
			});
		}
		
//		private ExchangeFilterFunction httpCodeFilter() {
//			return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
//				if (clientResponse.statusCode().isError()) {
//					return clientResponse.bodyToMono(new ParameterizedTypeReference<Resp>() {})
//							.flatMap(dto -> Mono.error(new BusinessException(dto.getCode(), dto.getMessage())));
//				} else {
//					return Mono.just(clientResponse);
//				}
//			});
//		}
	}
}
