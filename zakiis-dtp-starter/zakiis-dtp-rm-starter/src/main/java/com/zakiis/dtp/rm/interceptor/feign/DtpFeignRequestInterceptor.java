package com.zakiis.dtp.rm.interceptor.feign;

import org.apache.commons.lang3.StringUtils;

import com.zakiis.dtp.rm.constants.DtpConstant;
import com.zakiis.dtp.rm.context.DtpContext;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class DtpFeignRequestInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate template) {
		String xid = DtpContext.getXid();
		if (StringUtils.isBlank(xid)) {
			return;
		}
		template.header(DtpConstant.KEY_XID, xid);
	}

}
