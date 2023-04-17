package com.zakiis.security.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface OptimisticLock {

	String NULL_STRING = "nil";
	
	/** default we use class, method and parameters to generate lock key, you can specified the synchronize key EL if needed */
	String lockKeyEL() default NULL_STRING;

	/** if NX key still exists after lock time out, access would be allowed. default is 300 seconds */
	int lockTimeout() default 300;
	
	/** After method execute done would delete NX key by default,
	 *  but in some special case you would like keep the key for a while to prevent method execute again
	 *  For example in MQ consume scenario, you may need this to prevent message consumed for many times. */
	boolean deleteNxKeyAfterExecuted() default true;
}
