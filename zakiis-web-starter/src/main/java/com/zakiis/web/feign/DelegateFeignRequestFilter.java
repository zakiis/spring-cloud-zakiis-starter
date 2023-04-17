package com.zakiis.web.feign;

import java.io.IOException;

import feign.Client;
import feign.Request;
import feign.Request.Options;
import feign.Response;

/**
 * The actual request logic of feign client, should be put at the last of filter chain
 * @author Liu Zhenghua
 * 2023年2月22日 下午2:18:45
 */
public class DelegateFeignRequestFilter implements FeignRequestFilter {

	Client delegate;
	
	public DelegateFeignRequestFilter(Client delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public Response filter(Request request, Options options, FeignRequestFilterChain filterChain) throws IOException {
		return delegate.execute(request, options);
	}

}
