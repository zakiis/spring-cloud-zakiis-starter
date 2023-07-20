package com.zakiis.dtp.common.interceptor.feign;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import feign.Feign;
import feign.Retryer;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class DtpFeignBuilderBeanPostProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof Feign.Builder) {
			((Feign.Builder) bean).retryer(Retryer.NEVER_RETRY);
			log.info("change the retryer of the bean '{}' to 'Retryer.NEVER_RETRY'", beanName);
		}
		return bean;
	}

	
}
