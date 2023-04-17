package com.zakiis.web.config;

import java.util.List;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.zakiis.core.domain.constants.ZakiisStarterConstants;
import com.zakiis.web.feign.FeignClientBuilderCustomizer;
import com.zakiis.web.feign.FeignRequestFilter;
import com.zakiis.web.filter.DamBoardFeignRequestFilter;
import com.zakiis.web.filter.DamboardClientHttpRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = ZakiisStarterConstants.WEB_DAMBOARD_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(DamBoardProperties.class)
public class DamBoardAutoConfiguration {
	
	@Configuration
	@ConditionalOnClass(name = "org.springframework.web.client.RestTemplate")
	protected static class HttpClientDamBoardConfiguration {
	
		@Bean
		@ConditionalOnMissingBean
		public DamboardClientHttpRequestFilter damboardClientHttpRequestFilter(DamBoardProperties properties) {
			log.info("Damboard http request filter enabled.");
			return new DamboardClientHttpRequestFilter(properties);
		}
		
		@Bean
		@ConditionalOnMissingBean
		public RestTemplateCustomizer damBoardRestTemplateCustomizer(DamboardClientHttpRequestFilter filter) {
			return new RestTemplateCustomizer() {
				@Override
				public void customize(RestTemplate restTemplate) {
					if (!restTemplate.getInterceptors().contains(filter)) {
						restTemplate.getInterceptors().add(filter);
					}
				}
			};
		}
		
		@Bean
		@ConditionalOnMissingBean
		public SmartInitializingSingleton restTemplateInitailizing(List<RestTemplateCustomizer> customizers, List<RestTemplate> restTemplates) {
			return new SmartInitializingSingleton() {
				@Override
				public void afterSingletonsInstantiated() {
					for (RestTemplate restTemplate : restTemplates) {
						for (RestTemplateCustomizer customizer : customizers) {
							customizer.customize(restTemplate);
						}
					}
				}
			};
		}
	}
	
	@Configuration
	@ConditionalOnClass(name = "feign.Feign")
	@AutoConfigureBefore(name = "org.springframework.cloud.openfeign.FeignAutoConfiguration")	
	protected static class FeignClientDamBoardConfiguration {
		
		@Bean
		@ConditionalOnMissingBean
		public DamBoardFeignRequestFilter damBoardFeignFilter(DamBoardProperties properties) {
			log.info("Damboard feign request filter enabled.");
			return new DamBoardFeignRequestFilter(properties);
		}
		
		@Bean
		public FeignClientBuilderCustomizer decoratorFeignClientBuilderCustomizer(List<FeignRequestFilter> filters) {
			return new FeignClientBuilderCustomizer(filters);
		}
	}
	
}
