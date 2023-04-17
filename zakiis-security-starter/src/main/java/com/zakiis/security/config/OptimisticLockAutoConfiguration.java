package com.zakiis.security.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.zakiis.core.domain.constants.ZakiisStarterConstants;
import com.zakiis.security.aspect.OptimisticLockAspect;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(OptimisticLockProperties.class)
@ConditionalOnProperty(prefix = ZakiisStarterConstants.SECURITY_OPTIMISTIC_LOCK_PREFIX , name = "enabled" , havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
public class OptimisticLockAutoConfiguration {
	
	@Bean
	@ConditionalOnMissingBean
	public OptimisticLockAspect optimisticLockAspect(OptimisticLockProperties optimisticLockProperties,
			StringRedisTemplate redisTemplate) {
		log.info("Feature optimistic lock aspect enabled.");
		return new OptimisticLockAspect(optimisticLockProperties, redisTemplate);
	}
}
