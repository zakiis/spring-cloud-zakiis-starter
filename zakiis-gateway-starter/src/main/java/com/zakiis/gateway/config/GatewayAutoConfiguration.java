package com.zakiis.gateway.config;

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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import com.zakiis.core.domain.constants.ZakiisStarterConstants;
import com.zakiis.gateway.exception.JsonErrorWebExceptionHandler;
import com.zakiis.gateway.filter.LogRequestFilter;
import com.zakiis.gateway.filter.ParameterFilter;
import com.zakiis.gateway.filter.TraceIdFilter;

import lombok.extern.slf4j.Slf4j;

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
}
