package com.zakiis.web.util;

import com.zakiis.core.domain.dto.CommonResps;
import com.zakiis.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RestTemplateUtil {

    private static final int CONNECT_TIMEOUT = 10000;
    private static final int DEFAULT_READ_TIMEOUT = 60000;
    /** key 为read timeout */
    private static final Map<Integer, RestTemplate> REST_TEMPLATE_MAP = new ConcurrentHashMap<>();

    public static <T> T get(String url, MultiValueMap<String, String> headerMap, Map<String, String> queryParams
            , ParameterizedTypeReference<T> responseType) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);
        if (queryParams != null) {
            for (Map.Entry<String, String> paramEntry : queryParams.entrySet()) {
                uriBuilder.queryParam(paramEntry.getKey(), paramEntry.getValue());
            }
        }
        url = uriBuilder.build().toString();
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headerMap == null || !headerMap.containsKey("Content-Type")) {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        }
        if (headerMap != null) {
            httpHeaders.addAll(headerMap);
        }
        HttpEntity<T> httpEntity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<T> responseEntity = getRestTemplate(DEFAULT_READ_TIMEOUT).exchange(url, HttpMethod.GET, httpEntity, responseType);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException(responseEntity.getStatusCode().toString());
        }
        return responseEntity.getBody();
    }

    public static <R,P> R post(String url, MultiValueMap<String, String> headerMap , Map<String, String> queryParams
            , P body, Integer timeout, Class<R> responseType) {
        return post(url, headerMap, queryParams, body, timeout, ParameterizedTypeReference.forType(responseType));
    }

    public static <R,P> R post(String url, MultiValueMap<String, String> headerMap , Map<String, String> queryParams
            , P body, Integer timeout, ParameterizedTypeReference<R> responseType) {
        if (timeout == null || timeout <= 0) {
            timeout = DEFAULT_READ_TIMEOUT;
        }
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);
        if (queryParams != null) {
            for (Map.Entry<String, String> paramEntry : queryParams.entrySet()) {
                uriBuilder.queryParam(paramEntry.getKey(), paramEntry.getValue());
            }
        }
        url = uriBuilder.build().toString();
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headerMap == null || !headerMap.containsKey("Content-Type")) {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        }
        if (headerMap != null) {
            httpHeaders.addAll(headerMap);
        }
        HttpEntity<P> httpEntity = new HttpEntity<>(body, httpHeaders);
        try {
            ResponseEntity<R> responseEntity = getRestTemplate(timeout).exchange(url, HttpMethod.POST, httpEntity, responseType);
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                log.warn("post {} not success, response code:{}", url, responseEntity.getStatusCode().value());
                throw new BusinessException(CommonResps.RUNTIME_ERROR.getCode(), "Call external API error");
            }
            return responseEntity.getBody();
        } catch (HttpClientErrorException e) {
            log.warn("post {} got an exception, response code:{}, body:{}", url, e.getStatusCode().value()
                    , e.getResponseBodyAsString());
            throw new BusinessException(CommonResps.RUNTIME_ERROR.getCode(), "Call external API error");
        } catch (ResourceAccessException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                throw new BusinessException(CommonResps.RUNTIME_ERROR.getCode(), "Call external API timeout");
            }
            throw e;
        }
    }

    public static <R,P> R post(String url, MultiValueMap<String, String> headerMap , Map<String, String> queryParams
            , P body, ParameterizedTypeReference<R> responseType) {
        return post(url, headerMap, queryParams, body, DEFAULT_READ_TIMEOUT, responseType);
    }

    public static <R,P> R post(String url, MultiValueMap<String, String> headerMap , Map<String, String> queryParams
            , P body, Class<R> responseType) {
        return post(url, headerMap, queryParams, body, ParameterizedTypeReference.forType(responseType));
    }

    public static <R,P> R post(String url, MultiValueMap<String, String> headerMap, P body, Class<R> responseType) {
        return post(url, headerMap, null, body, responseType);
    }

    public static MultiValueMap<String, String> buildMuiltiValueMap(String... values) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        for (int i = 0; i < values.length; i++) {
            String key = values[i];
            if (++i < values.length) {
                headers.add(key, values[i]);
            }
        }
        return headers;
    }

    public static Map<String, String> buildQueryMap(String... values) {
        Map<String, String> queryParamMap = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            String key = values[i];
            if (++i < values.length) {
                queryParamMap.put(key, values[i]);
            }
        }
        return queryParamMap;
    }

    private static RestTemplate getRestTemplate(int readTimeout) {
        RestTemplate restTemplate = REST_TEMPLATE_MAP.get(readTimeout);
        if (restTemplate == null) {
            synchronized (RestTemplateUtil.class) {
                restTemplate = REST_TEMPLATE_MAP.get(readTimeout);
                if (restTemplate == null) {
                    SimpleClientHttpRequestFactory factory = createRequestFactory(readTimeout);
                    restTemplate = new RestTemplate(factory);
                    REST_TEMPLATE_MAP.put(readTimeout, restTemplate);
                }
            }
        }
        return restTemplate;
    }

    private static SimpleClientHttpRequestFactory createRequestFactory(int readTimeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT);
        factory.setReadTimeout(readTimeout);
        return factory;
    }
}
