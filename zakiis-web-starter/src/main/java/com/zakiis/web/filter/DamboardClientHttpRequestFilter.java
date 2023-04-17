package com.zakiis.web.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.zakiis.web.config.DamBoardProperties;
import com.zakiis.web.dto.OkHttpClientHttpResponse;

/**
 * Interceptor for {@link org.springframework.web.client.RestTemplate} that provides the ability for return the specified text in HTTP response.
 * @author Liu Zhenghua
 * 2023-02-22 14:55:08
 */
public class DamboardClientHttpRequestFilter implements ClientHttpRequestInterceptor {
	
	private final DamBoardProperties properties;
	/** key is the path, values is the response text */
	Map<String, String> ruleMap;
	/** key is the path regex, values is the response text */
	Map<Pattern, String> rulePatternMap;
	final static Logger log = LoggerFactory.getLogger(DamboardClientHttpRequestFilter.class);
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		if (!properties.isEnabled()) {
			execution.execute(request, body);
		}
		String path = request.getURI().getPath();
		String damboardContent = null;
		if (ruleMap.containsKey(path)) { // if can full match, do not use regular expression. 
			damboardContent = ruleMap.get(path);
		} else {
			for (Map.Entry<Pattern, String> entry : rulePatternMap.entrySet()) {
				if (entry.getKey().matcher(path).find()) {
					damboardContent = entry.getValue();
					break;
				}
			}
		}
		if (damboardContent != null) {
			byte[] bodyBytes = damboardContent.getBytes(StandardCharsets.UTF_8);
			request.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
			request.getHeaders().setContentLength(bodyBytes.length);
			return new OkHttpClientHttpResponse(new ByteArrayInputStream(bodyBytes), request.getHeaders());	
		} else {
			return execution.execute(request, body);
		}
	}
	
	public DamboardClientHttpRequestFilter(DamBoardProperties properties) {
		this.properties = properties;
		this.ruleMap = properties.ruleMap();
		rulePatternMap = new HashMap<Pattern, String>(ruleMap.size());
		for (Map.Entry<String, String> entry : ruleMap.entrySet()) {
			Pattern pattern = Pattern.compile(entry.getKey());
			rulePatternMap.put(pattern, entry.getValue());
		}
	}
}
