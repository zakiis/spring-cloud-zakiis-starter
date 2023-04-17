package com.zakiis.log.converter;

import com.zakiis.log.util.DesensitizationUtil;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Log desensitizaiton
 * add the following line to logback configuration file in configuration element: <pre>{@code
 *   <conversionRule conversionWord="msg" converterClass="com.zakiis.log.converter.DesensitizationConverter"/>
 * }</pre>
 * @author Liu Zhenghua
 * 2023年2月20日 下午6:25:35
 */
public class DesensitizationConverter extends MessageConverter {

	@Override
	public String convert(ILoggingEvent event) {
		String msg = super.convert(event);
		return DesensitizationUtil.convert(msg);
	}

}
