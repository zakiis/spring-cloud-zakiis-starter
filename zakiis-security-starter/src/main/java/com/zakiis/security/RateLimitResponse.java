package com.zakiis.security;

import java.io.Serializable;

import com.zakiis.security.annotation.RateLimit;
import com.zakiis.security.aspect.RateLimitAspect;

public interface RateLimitResponse extends Serializable {

	/**
	 * It's used by {@link RateLimitAspect} to determine whether to delete NX key in redis or not if you set {@link RateLimit#ignoreFailure()} = true
	 * if the response of the method has not implements the RateLimitResponse interface, RateLimitAspect would assume the method execute successfully if no errors thrown.
	 * @return true if the response is success.
	 */
	public boolean isSuccess();
}
