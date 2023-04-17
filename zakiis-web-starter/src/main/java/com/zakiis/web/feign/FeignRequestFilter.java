package com.zakiis.web.feign;

import java.io.IOException;

import feign.Request;
import feign.Request.Options;
import feign.Response;

/**
 * Provides the ability of request intercept of feign client.
 * For example, if you would rather return the specified text than the HTTP response, you can use the following code:
 * <pre>{@code
 * 		Response.builder().request(request).status(HttpStatus.OK.value()).headers(headers).body("the text you want to return", StandardCharsets.UTF_8).build();
 * }</pre>
 * if you want using the HTTP response, just using:
 * <pre>{@code
 * 		return filterChain.doFilter(request, options);
 * }</pre>
 * 
 * @author Liu Zhenghua
 * 2023-02-22 14:36:48
 */
public interface FeignRequestFilter {

	/**
	 * @param request
	 * @param options
	 * @return 
	 * @throws IOException 
	 */
	Response filter(Request request, Options options, FeignRequestFilterChain filterChain) throws IOException;
}
