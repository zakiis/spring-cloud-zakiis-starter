package com.zakiis.dtp.common.context;

import com.zakiis.core.ThreadContext;
import com.zakiis.dtp.common.constants.DtpConstant;

/**
 * Distributed Transaction Processor Model Context
 * @date 2023-07-04 09:38:55
 * @author Liu Zhenghua
 */
public class DtpContext {

	private final static ThreadContext ctx = new ThreadContext();
	
	public static <T> T get(String key) {
		return ctx.get(key);
	}
	
	public static <T> void put(String key, T value) {
		ctx.put(key, value);
	}
	
	public static <T> void remove(String key) {
		
	}
	
	public static String getXid() {
		return get(DtpConstant.KEY_XID);
	}
}
