package com.zakiis.dtp.rm.context;

import com.zakiis.core.ThreadContext;

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
}
