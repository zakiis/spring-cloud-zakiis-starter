package com.zakiis.web.feign;

import java.io.IOException;
import java.util.List;

import feign.Client;
import feign.Request;
import feign.Request.Options;
import feign.Response;

/**
 * This client provides the ability to execute the custom logic for the class that implements the {@link FeignRequestFilter} interface.
 * Note that the {@link DelegateFeignRequestFilter} is the actual role that performs the HTTP request, It should be the last element of the filters field.
 * @author Liu Zhenghua
 * 2023年2月22日 下午2:20:47
 */
public class FilterChainFeignClient implements Client {
	
	List<FeignRequestFilter> filters;
	
	FilterChainFeignClient(List<FeignRequestFilter> filters) {
		this.filters = filters;
	}

	@Override
	public Response execute(Request request, Options options) throws IOException {
		FeignRequestFilterChain filterChain = new FeignRequestFilterChain(filters);
		return filterChain.doFilter(request, options);
	}

}
