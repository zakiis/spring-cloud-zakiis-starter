package com.zakiis.rdb.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zakiis.core.domain.constants.ZakiisStarterConstants;
import com.zakiis.rdb.filter.MybatisCipherInterceptor;
import com.zakiis.rdb.filter.MybatisPrintSqlInterceptor;
import com.zakiis.security.codec.HexUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfiguration
@ConditionalOnClass(name = "org.apache.ibatis.plugin.Invocation")
public class MybatisAutoConfiguration {

	
	@Configuration
	@ConditionalOnProperty(prefix = ZakiisStarterConstants.RDB_CIPHER_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = false)
	@EnableConfigurationProperties(MybatisCipherProperties.class)
	protected static class MybatisCipherConfiguration {
		
		@Bean
		public MybatisCipherInterceptor mybatisCipherInterceptor(MybatisCipherProperties mybatisCipherProperties) {
			log.info("Feature mybatis cipher interceptor enabled");
			byte[] secret = HexUtil.toByteArray(mybatisCipherProperties.getSecret());
			byte[] iv = HexUtil.toByteArray(mybatisCipherProperties.getIv());
			return new MybatisCipherInterceptor(secret, iv, mybatisCipherProperties.isEnableFuzzyQuery());
		}
	}
	
	@Configuration
	@AutoConfigureBefore(MybatisCipherConfiguration.class)
	@ConditionalOnProperty(prefix = ZakiisStarterConstants.RDB_PRINT_SQL_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
	@EnableConfigurationProperties(MybatisPrintSqlProperties.class)
	protected static class MybatisPrintSqlConfiguration {
		
		@Bean
		public MybatisPrintSqlInterceptor mybatisPrintSqlInterceptor() {
			log.info("Feature mybatis print sql interceptor enabled");
			return new MybatisPrintSqlInterceptor();
		}

	}
	
}
