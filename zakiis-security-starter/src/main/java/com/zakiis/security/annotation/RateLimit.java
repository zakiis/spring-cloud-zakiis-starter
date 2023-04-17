package com.zakiis.security.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.zakiis.security.aspect.RateLimitAspect;

/**
 * Using this annotation to limit the count or frequency of the method.
 * method should throw exception if not success to make sure can access next time.
 * @see {@link RateLimitAspect}
 * @author Liu Zhenghua
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RateLimit {
	
	String NULL_STRING = "nil";

	/** Minimum interval seconds between two request. */
	String minIntervalEL();
	
	/** Determine whether failure request should ignored or not, default true */
	boolean ignoreFailure() default true;
	
	/** The max amount that would be allowed for the request per day, zero represents no limit */
	String maxRequestPerDayEL() default "0";
	
	/** default we use class, method and parameters to generate limit key, you can specified the limit key EL for specified purpose, for example phone and email */
	String limitKeyEL() default NULL_STRING;
}
