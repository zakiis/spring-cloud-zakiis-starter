package com.zakiis.security.service.impl;

import java.time.Duration;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.zakiis.core.exception.ZakiisRuntimeException;
import com.zakiis.security.service.RateLimitService;
import com.zakiis.security.util.RateLimitUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RedisRateLimitService implements RateLimitService {

	private final static String INTERVAL_KEY_PREFIX = "rate_limit_interval_";
	private final static String DAY_KEY_PREFIX = "rate_limit_day_";
	private final StringRedisTemplate redisTemplate;

	@Override
	public void validate(String rateLimitKey, int maxRequestPerDay) {
		
		if (StringUtils.isBlank(rateLimitKey)) {
			throw new ZakiisRuntimeException("Rate limit key can't be empty.");
		}
		String intervalLimitKey = INTERVAL_KEY_PREFIX + rateLimitKey;
		Long expire = redisTemplate.getExpire(intervalLimitKey);
		if (expire != null && expire > 0) {
			log.warn("Request too frequently, rate limit key {} will expired in {} seconds.", rateLimitKey, expire);
			throw new ZakiisRuntimeException("Request too frequently, please try it later.");
		}
		if (maxRequestPerDay != 0) {
			String countLimitKey = DAY_KEY_PREFIX + rateLimitKey;
			String requestedCount = redisTemplate.opsForValue().get(countLimitKey);
			if (StringUtils.isNotBlank(requestedCount) && Long.valueOf(requestedCount) >= maxRequestPerDay) {
				throw new ZakiisRuntimeException("请求次数已超过当日最大限制：" + maxRequestPerDay + ", 请明天再试.");
			}
		}
	}

	@Override
	public void request(String rateLimitKey, int minInterval, int maxRequestPerDay) {
		String intervalLimitKey = INTERVAL_KEY_PREFIX + rateLimitKey;
		redisTemplate.opsForValue().set(intervalLimitKey, "1", Duration.ofSeconds(minInterval));
		if (maxRequestPerDay != 0) {
			String countLimitKey = DAY_KEY_PREFIX + rateLimitKey;
			Boolean hasKey = redisTemplate.hasKey(countLimitKey);
			if (!hasKey) {
				long todayLeftSeconds = RateLimitUtil.getTodayLeftSeconds();
				redisTemplate.opsForValue().set(countLimitKey, "0", Duration.ofSeconds(todayLeftSeconds));
			}
			redisTemplate.opsForValue().increment(countLimitKey);
		}
	}
	
	@Override
	public void reset(String rateLimitKey) {
		String intervalLimitKey = INTERVAL_KEY_PREFIX + rateLimitKey;
		String countLimitKey = DAY_KEY_PREFIX + rateLimitKey;
		redisTemplate.delete(Arrays.asList(intervalLimitKey, countLimitKey));
	}
}
