package com.zakiis.security.aspect;

import java.time.Duration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.zakiis.core.exception.ZakiisRuntimeException;
import com.zakiis.security.annotation.OptimisticLock;
import com.zakiis.security.config.OptimisticLockProperties;
import com.zakiis.security.util.OptimisticLockUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 乐观锁，同时请求只允许一个被处理
 * @author Liu Zhenghua
 * 2023年1月10日 下午2:54:46
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class OptimisticLockAspect {

	private final OptimisticLockProperties optimisticLockProperties;
	private final StringRedisTemplate redisTemplate;
	
	/**
    * @within 方法所属类如果包含了注解则会被拦截
    * @annotation 方法上如果包含了注解则会被拦截
    */
	@Pointcut("@within(com.zakiis.security.annotation.OptimisticLock) || @annotation(com.zakiis.security.annotation.OptimisticLock)")
    public void optimisticLockPointcut() {}

	@Around("optimisticLockPointcut()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		if (!optimisticLockProperties.isEnabled()) {
			log.debug("Feature optimistic lock pointcut not enabled, skip logic.");
			return joinPoint.proceed();
		}
		OptimisticLock annotation = OptimisticLockUtil.getAnnotation(joinPoint, OptimisticLock.class);
    	if (annotation == null) {
    		throw new ZakiisRuntimeException("Can't find @OptimisticLock annotation.");
    	}
    	String lockKey;
    	String lockKeyEL = annotation.lockKeyEL();
    	MethodSignature signature = (MethodSignature)joinPoint.getSignature();
    	if (OptimisticLock.NULL_STRING.equals(lockKeyEL)) {
    		lockKey = OptimisticLockUtil.genLockKey(signature.getDeclaringTypeName(), signature.getName(), joinPoint.getArgs());
    	} else {
    		lockKey = OptimisticLockUtil.getValBySpEL(lockKeyEL, signature, joinPoint.getArgs());
    	}
    	if (lockKey == null) {
    		throw new ZakiisRuntimeException("Can't generate lock key");
    	}
    	Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", Duration.ofSeconds(annotation.lockTimeout()));
    	boolean executeSuccess = false;
    	try {
    		if (setIfAbsent != null && setIfAbsent == true) {
    			Object result = joinPoint.proceed();
    			executeSuccess = true;
    			return result;
    		} else {
    			if (annotation.deleteNxKeyAfterExecuted()) {
    				throw new ZakiisRuntimeException("Request is processing, please try it later");
    			} else {
    				//do nothing
    				log.warn("To keep the idempotence of that method, this request ignored.");
    				throw new ZakiisRuntimeException("Repeated request");
    			}
    		}
    	} finally {
			if (setIfAbsent != null && setIfAbsent == true) {
				if (annotation.deleteNxKeyAfterExecuted() || !executeSuccess) {
					redisTemplate.delete(lockKey);
				}
			}
		}

	}

}
