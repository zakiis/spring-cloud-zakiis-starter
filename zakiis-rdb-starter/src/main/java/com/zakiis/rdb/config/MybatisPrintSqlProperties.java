package com.zakiis.rdb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.zakiis.core.domain.constants.ZakiisStarterConstants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = ZakiisStarterConstants.RDB_PRINT_SQL_PREFIX)
public class MybatisPrintSqlProperties {

	private boolean enabled = true;
	
}
