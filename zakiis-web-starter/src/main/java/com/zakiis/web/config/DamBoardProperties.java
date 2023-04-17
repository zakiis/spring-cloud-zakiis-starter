package com.zakiis.web.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.zakiis.core.domain.constants.ZakiisStarterConstants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = ZakiisStarterConstants.WEB_DAMBOARD_PREFIX)
public class DamBoardProperties {

	private boolean enabled = true;
	private List<DamBoard> rules;

	@Getter
	@Setter
	public static class DamBoard {
		private String path;
		private String response;
	}
	
	public Map<String, String> ruleMap() {
		HashMap<String, String> ruleMap = new HashMap<String, String>();
		if (rules != null && rules.size() > 0) {
			for (DamBoard damBoard : rules) {
				ruleMap.put(damBoard.getPath(), damBoard.getResponse());
			}
		}
		return ruleMap;
	}
}
