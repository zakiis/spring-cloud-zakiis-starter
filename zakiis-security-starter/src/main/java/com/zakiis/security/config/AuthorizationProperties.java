package com.zakiis.security.config;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import com.zakiis.core.domain.constants.ZakiisStarterConstants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RefreshScope
@ConfigurationProperties(prefix = ZakiisStarterConstants.SECURITY_AUTHORIZATION_PREFIX)
public class AuthorizationProperties {

	private boolean enabled = true;
	private Set<String> skipPath;
	private String errorResponseText;
	
}
