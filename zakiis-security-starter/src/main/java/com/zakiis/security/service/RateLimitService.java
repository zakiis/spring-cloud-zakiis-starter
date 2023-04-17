package com.zakiis.security.service;

public interface RateLimitService {
	
	/**
	 * A new request goes in should call this method to check if it can be proceed.
	 * @param rateLimitKey
	 * @param maxRequestPerDay 
	 * @return
	 */
	void validate(String rateLimitKey, int maxRequestPerDay);
	
	/**
	 * Accumulate the request count.
	 * @param rateLimitKey
	 * @param minInterval
	 * @param maxRequestPerDay
	 */
	void request(String rateLimitKey, int minInterval, int maxRequestPerDay);

	/**
	 * Reset the request count.
	 * @param rateLimitKey
	 */
	void reset(String rateLimitKey);
}
