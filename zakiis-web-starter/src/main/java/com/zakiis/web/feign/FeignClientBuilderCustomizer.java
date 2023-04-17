package com.zakiis.web.feign;

import java.lang.reflect.Field;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.util.ReflectionUtils;

import feign.Client;
import feign.Feign.Builder;

/**
 * Customize the feign client, using {@link FilterChainFeignClient} to provides the ability of filter chain.
 * @author Liu Zhenghua
 * 2023年2月22日 下午2:20:16
 */
public class FeignClientBuilderCustomizer implements FeignBuilderCustomizer {

	List<FeignRequestFilter> filters;
	Logger log = LoggerFactory.getLogger(FeignRequestFilterChain.class);
	
	public FeignClientBuilderCustomizer(List<FeignRequestFilter> filters) {
		this.filters = filters;
	}
	
	@Override
	public void customize(Builder builder) {
		try {
			Field field = Builder.class.getDeclaredField("client");
			ReflectionUtils.makeAccessible(field);
			Client client = (Client)field.get(builder);
			DelegateFeignRequestFilter delegateFeignFilter = new DelegateFeignRequestFilter(client);
			filters.add(delegateFeignFilter);
			FilterChainFeignClient filterChainFeignClient = new FilterChainFeignClient(filters);
			builder.client(filterChainFeignClient);
		} catch (Exception e) {
			log.error("set feign client decorator got an exception.", e);
		}
	}

}
