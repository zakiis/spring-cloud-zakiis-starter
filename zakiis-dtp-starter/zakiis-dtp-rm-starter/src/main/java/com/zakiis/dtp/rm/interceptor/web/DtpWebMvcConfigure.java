package com.zakiis.dtp.rm.interceptor.web;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class DtpWebMvcConfigure implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new DtpHandlerInterceptor()).addPathPatterns("/**");
	}

}
