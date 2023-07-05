package com.zakiis.dtp.rm.interceptor.rest;

import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DtpRestTemplateCustomizer implements RestTemplateCustomizer {
	
	private final DtpRestTemplateInterceptor dtpRestTemplateInterceptor;

	@Override
	public void customize(RestTemplate restTemplate) {
		restTemplate.getInterceptors().add(dtpRestTemplateInterceptor);
	}

}
