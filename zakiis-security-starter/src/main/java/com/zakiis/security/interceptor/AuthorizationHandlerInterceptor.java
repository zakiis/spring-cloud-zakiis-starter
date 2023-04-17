package com.zakiis.security.interceptor;

import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.zakiis.security.PermissionUtil;
import com.zakiis.security.Realm;
import com.zakiis.security.annotation.Permission;
import com.zakiis.security.config.AuthorizationProperties;
import com.zakiis.security.exception.NoPermissionException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthorizationHandlerInterceptor implements HandlerInterceptor {
	
	private final Realm realm;
	private final AuthorizationProperties authorizationProperties;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (!authorizationProperties.isEnabled()) {
			return HandlerInterceptor.super.preHandle(request, response, handler);
		}
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod)handler;
			Permission permission = handlerMethod.getMethodAnnotation(Permission.class);
			Set<String> functions = realm.getFunctions(request);
			try {
				PermissionUtil.checkPrivileges(functions, permission);
			} catch (NoPermissionException e) {
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
				response.getWriter().write(authorizationProperties.getErrorResponseText());
				return false;
			}
			return true;
		} else {
			return HandlerInterceptor.super.preHandle(request, response, handler);
		}
	}

}
