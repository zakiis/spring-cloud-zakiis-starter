package com.zakiis.rdb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.zakiis.core.domain.constants.ZakiisStarterConstants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = ZakiisStarterConstants.RDB_CIPHER_PREFIX)
public class MybatisCipherProperties {

	private boolean enabled;
	/** AES secret key in hex format*/
	private String secret;
	/** Initialization vector in hex format*/
	private String iv;
	/** fuzzy query would make the encrypted content more bigger */
	private boolean enableFuzzyQuery;
	
}
