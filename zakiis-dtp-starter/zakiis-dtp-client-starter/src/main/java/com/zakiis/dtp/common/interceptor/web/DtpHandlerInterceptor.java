package com.zakiis.dtp.common.interceptor.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.zakiis.dtp.common.constants.DtpConstant;
import com.zakiis.dtp.common.context.DtpContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DtpHandlerInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String xid = DtpContext.getXid();
		String rpcXid = request.getHeader(DtpConstant.KEY_XID);
		if (log.isDebugEnabled()) {
			log.debug("xid in DtpContext {} xid in RpcContext {}", xid, rpcXid);
		}
		if (StringUtils.isBlank(xid) && StringUtils.isNotBlank(rpcXid)) {
			DtpContext.put(DtpConstant.KEY_XID, rpcXid);
		}
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		String xid = DtpContext.getXid();
		if (StringUtils.isNotBlank(xid)) {
			String rpcXid = request.getHeader(DtpConstant.KEY_XID);
			if (xid.equals(rpcXid)) {
				DtpContext.remove(DtpConstant.KEY_XID);
				log.debug("unbind {} from DtpContext", rpcXid);
			}
		}
	}

	
}
