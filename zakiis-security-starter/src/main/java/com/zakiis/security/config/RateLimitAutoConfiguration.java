package com.zakiis.security.config;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.zakiis.core.domain.constants.ZakiisStarterConstants;
import com.zakiis.security.aspect.RateLimitAspect;
import com.zakiis.security.service.RateLimitService;
import com.zakiis.security.service.impl.RedisRateLimitService;
import com.zakiis.security.util.RateLimitUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(RateLimitProperties.class)
@ConditionalOnProperty(prefix = ZakiisStarterConstants.SECURITY_RATE_LIMIT_PREFIX , name = "enabled" , havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
public class RateLimitAutoConfiguration implements ApplicationContextAware {
	
	@Bean
	@ConditionalOnMissingBean
	public RateLimitAspect rateLimitAspect(RateLimitProperties rateLimitProperties,
			RateLimitService limitService) {
		log.info("Feature rate limit aspect enabled.");
		return new RateLimitAspect(rateLimitProperties, limitService);
	}

	@Bean
	@ConditionalOnMissingBean
	public RateLimitService rateLimitService(StringRedisTemplate redisTemplate) {
		return new RedisRateLimitService(redisTemplate);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		RateLimitUtil.init(applicationContext);
	}
}
