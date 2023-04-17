package com.zakiis.security;

import java.util.Set;

import com.zakiis.security.interceptor.AuthorizationHandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;

public interface Realm {

	/**
	 * Get user roles from current request. {@link AuthorizationHandlerInterceptor} will check if current user has right to the method.
	 * @param request
	 * @return
	 */
	Set<String> getFunctions(HttpServletRequest request);
}
