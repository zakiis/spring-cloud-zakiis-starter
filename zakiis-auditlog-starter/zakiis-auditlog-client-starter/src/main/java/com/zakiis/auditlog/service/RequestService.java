package com.zakiis.auditlog.service;

public interface RequestService {

	String getIp();
	
	/**
	 * Retrieves from HTTP header: {@value User-Agent}
	 * @return
	 */
	String getUserAgent();
	
	String getTraceId();
}
