package com.zakiis.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import com.zakiis.core.domain.constants.ZakiisStarterConstants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RefreshScope
@ConfigurationProperties(prefix = ZakiisStarterConstants.SECURITY_OPTIMISTIC_LOCK_PREFIX)
public class OptimisticLockProperties {

	private boolean enabled = true;
}
