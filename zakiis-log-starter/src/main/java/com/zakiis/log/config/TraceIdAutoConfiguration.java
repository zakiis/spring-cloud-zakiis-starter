package com.zakiis.log.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.zakiis.core.domain.constants.ZakiisStarterConstants;
import com.zakiis.log.filter.TraceIdClientHttpRequestFilter;
import com.zakiis.log.filter.TraceIdFeignRequestFilter;
import com.zakiis.log.filter.TraceIdHttpRequestFilter;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfiguration
@ConditionalOnProperty(prefix = ZakiisStarterConstants.LOG_TRACE_ID_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(TraceIdProperties.class)
public class TraceIdAutoConfiguration {

	@Bean("zakiisTraceIdFilter")
	public TraceIdHttpRequestFilter traceIdFilter(TraceIdProperties props) {
		log.info("Trace id filter for http request enabled.");
		TraceIdHttpRequestFilter traceIdFilter = new TraceIdHttpRequestFilter(props);
		return traceIdFilter;
	}
	
	@Configuration
	@ConditionalOnClass(RequestInterceptor.class)
	protected static class FeignClientTraceIdConfiguration {

		@Bean
		public TraceIdFeignRequestFilter feignTraceIdFilter(TraceIdProperties props) {
			log.info("Trace id filter for feign client request enabled.");
			return new TraceIdFeignRequestFilter(props);
		}
	}
	
	@Configuration
	@ConditionalOnClass(RestTemplate.class)
	protected static class HttpClientTraceIdConfiguration {

		@Bean
		public TraceIdClientHttpRequestFilter clientHttpTraceIdFilter(TraceIdProperties props) {
			log.info("Trace id filter for http client request enabled.");
			return new TraceIdClientHttpRequestFilter(props);
		}
		
		@Bean
		public RestTemplateCustomizer traceIdRestTemplateCustomizer(TraceIdClientHttpRequestFilter filter) {
			return new RestTemplateCustomizer() {
				@Override
				public void customize(RestTemplate restTemplate) {
					if (!restTemplate.getInterceptors().contains(filter)) {
						restTemplate.getInterceptors().add(filter);
					}
				}
			};
		}
	}
}
