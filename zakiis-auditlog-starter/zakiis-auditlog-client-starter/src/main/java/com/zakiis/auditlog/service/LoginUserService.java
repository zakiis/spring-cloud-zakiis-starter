package com.zakiis.auditlog.service;

/**
 * Get user info that login.
 * @date 2023-04-03 14:10:51
 * @author Liu Zhenghua
 */
public interface LoginUserService {

	String getUserId();
	
	String getLoginAccount();
	
	String getRealName();
	
}
