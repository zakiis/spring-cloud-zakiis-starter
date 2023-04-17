package com.zakiis.web.feign;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import feign.Request;
import feign.Request.Options;
import feign.Response;

public class FeignRequestFilterChain {
	
	List<FeignRequestFilter> filters = new ArrayList<FeignRequestFilter>();
	int index = 0;

	public Response doFilter(Request request, Options options) throws IOException {
		if (index < filters.size()) {
			index = index + 1;
			return filters.get(index - 1).filter(request, options, this);
		}
		throw new RuntimeException("last filter didn't return a reponse object");
	}
	
	public FeignRequestFilterChain(List<FeignRequestFilter> filters) {
		this.filters = filters;
	}
}
