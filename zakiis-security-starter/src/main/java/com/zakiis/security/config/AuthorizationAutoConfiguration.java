package com.zakiis.security.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.zakiis.core.domain.constants.ZakiisStarterConstants;
import com.zakiis.security.Realm;
import com.zakiis.security.interceptor.AuthorizationHandlerInterceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(AuthorizationProperties.class)
@ConditionalOnProperty(prefix = ZakiisStarterConstants.SECURITY_AUTHORIZATION_PREFIX , name = "enabled" , havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(name = "org.springframework.web.servlet.HandlerInterceptor")
public class AuthorizationAutoConfiguration {
	
	@Bean
	public AuthorizationHandlerInterceptor authorizationHandlerInterceptor(AuthorizationProperties authorizationProperties, Realm realm) {
		log.info("Feature authorization handler interceptor enabled.");
		return new AuthorizationHandlerInterceptor(realm, authorizationProperties);
	}
	
	@Configuration
	@RequiredArgsConstructor
	protected static class WebMvcConfigure implements WebMvcConfigurer {

		private final AuthorizationProperties authorizationProperties;
		private final AuthorizationHandlerInterceptor authorizationHandlerInterceptor;
		
		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			List<String> skipPathList = new ArrayList<String>(authorizationProperties.getSkipPath());
			skipPathList.add("/error");
			registry.addInterceptor(authorizationHandlerInterceptor).addPathPatterns("/**")
					.excludePathPatterns(skipPathList);
		}
		
	}
}
