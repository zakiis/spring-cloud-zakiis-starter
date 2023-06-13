package com.zakiis.log.servlet;

import java.io.IOException;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class RepeatableReadServletRequest extends HttpServletRequestWrapper {
	
	private byte[] bodyBytes;

	public RepeatableReadServletRequest(HttpServletRequest request, byte[] bodyBytes) {
		super(request);
		this.bodyBytes = bodyBytes;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return new RepeatableReadServletInputStream(bodyBytes);
	}
	
	

}
