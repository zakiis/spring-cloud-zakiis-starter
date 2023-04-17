package com.zakiis.log.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import com.zakiis.core.domain.constants.ZakiisStarterConstants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RefreshScope
@ConfigurationProperties(prefix = ZakiisStarterConstants.LOG_DESENSITIZATION_PREFIX)
public class DesensitizationProperties {

	private boolean enabled = true;
	/** separated by comma, replace center value to * */
	private String replaceFields;
	/** separated by comma, replace value to * */
	private String eraseFields;
	/** separated by comma, drop value */
	private String dropFields;
	
}
