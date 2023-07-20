package com.zakiis.dtp.common.interceptor.rest;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.zakiis.dtp.common.constants.DtpConstant;
import com.zakiis.dtp.common.context.DtpContext;

public class DtpRestTemplateInterceptor implements ClientHttpRequestInterceptor {

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		String xid = DtpContext.getXid();
		if (StringUtils.isBlank(xid)) {
			request.getHeaders().add(DtpConstant.KEY_XID, xid);
		}
		return execution.execute(request, body);
	}

}
