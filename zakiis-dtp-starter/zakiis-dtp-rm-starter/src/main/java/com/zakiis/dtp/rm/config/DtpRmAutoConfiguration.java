package com.zakiis.dtp.rm.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.zakiis.dtp.rm.interceptor.feign.DtpFeignBuilderBeanPostProcessor;
import com.zakiis.dtp.rm.interceptor.feign.DtpFeignRequestInterceptor;
import com.zakiis.dtp.rm.interceptor.rest.DtpRestTemplateCustomizer;
import com.zakiis.dtp.rm.interceptor.rest.DtpRestTemplateInterceptor;
import com.zakiis.dtp.rm.interceptor.web.DtpWebMvcConfigure;

import feign.Client;

@AutoConfiguration
public class DtpRmAutoConfiguration {
	
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(Client.class)
	public static class DtpFeignClientConfiguration {
		
		@Bean
		public DtpFeignBuilderBeanPostProcessor dtpFeignBuilderBeanPostProcessor() {
			return new DtpFeignBuilderBeanPostProcessor();
		}
		
		@Bean
		public DtpFeignRequestInterceptor dtpFeignRequestInterceptor() {
			return new DtpFeignRequestInterceptor();
		}
	}
	
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(RestTemplate.class)
	public static class DtpRestTemplateConfiguration {

		@Bean
		public DtpRestTemplateInterceptor dtpRestTemplateInterceptor() {
			return new DtpRestTemplateInterceptor();
		}
		
		@Bean
		public DtpRestTemplateCustomizer dtpRestTemplateCustomizer(DtpRestTemplateInterceptor dtpRestTemplateInterceptor) {
			return new DtpRestTemplateCustomizer(dtpRestTemplateInterceptor);
		}
	}
	
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnWebApplication
	public static class DtpWebMvcConfiguration {
		
		@Bean
		public DtpWebMvcConfigure dtpWebMvcConfigure() {
			return new DtpWebMvcConfigure();
		}
	}

}
