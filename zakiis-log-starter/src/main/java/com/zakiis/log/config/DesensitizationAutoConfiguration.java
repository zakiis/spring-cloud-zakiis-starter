package com.zakiis.log.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.zakiis.core.domain.constants.ZakiisStarterConstants;
import com.zakiis.log.util.DesensitizationUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfiguration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = ZakiisStarterConstants.LOG_DESENSITIZATION_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(DesensitizationProperties.class)
public class DesensitizationAutoConfiguration implements InitializingBean {
	
	private final DesensitizationProperties desensitizationProperties;

	/**
	 * need add following to logback.xml to make it work, note that fields msg represents the field in logback pattern.
	 * <pre> {@code
	 * 	  <conversionRule conversionWord="msg" converterClass="com.zakiis.security.logging.DesensitizationConverter"/>
	 * }</pre>
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if (!desensitizationProperties.isEnabled()) {
			return;
		}
		log.info("Log desensitization feature enabled");
		Set<String> replaceFieldSet = new HashSet<String>();
		if (StringUtils.isNotEmpty(desensitizationProperties.getReplaceFields())) {
			String[] replaceFields = desensitizationProperties.getReplaceFields().split(",");
			replaceFieldSet.addAll(Arrays.asList(replaceFields));
		}
		Set<String> erasseFieldSet = new HashSet<String>();
		if (StringUtils.isNotEmpty(desensitizationProperties.getEraseFields())) {
			String[] replaceFields = desensitizationProperties.getEraseFields().split(",");
			erasseFieldSet.addAll(Arrays.asList(replaceFields));
		}
		Set<String> dropFieldSet = new HashSet<String>();
		if (StringUtils.isNotEmpty(desensitizationProperties.getDropFields())) {
			String[] replaceFields = desensitizationProperties.getDropFields().split(",");
			dropFieldSet.addAll(Arrays.asList(replaceFields));
		}
		DesensitizationUtil.init(replaceFieldSet, erasseFieldSet, dropFieldSet);
		
	}
}
