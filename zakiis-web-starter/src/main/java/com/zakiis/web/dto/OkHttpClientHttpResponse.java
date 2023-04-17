package com.zakiis.web.dto;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class OkHttpClientHttpResponse implements ClientHttpResponse {

	private final HttpHeaders headers;
	private final InputStream body;
	
	public OkHttpClientHttpResponse(InputStream body, HttpHeaders headers) {
		this.body = body;
		this.headers = headers;
	}

	@Override
	public InputStream getBody() throws IOException {
		return body;
	}
	@Override
	public HttpHeaders getHeaders() {
		return headers;
	}
	@Override
	public HttpStatus getStatusCode() throws IOException {
		return HttpStatus.OK;
	}
	@Override
	public int getRawStatusCode() throws IOException {
		return HttpStatus.OK.value();
	}
	@Override
	public String getStatusText() throws IOException {
		return HttpStatus.OK.getReasonPhrase();
	}
	@Override
	public void close() {
		try {
			getBody().close();
		} catch (IOException ex) {
			log.error("close reponse body got an exception", ex);
		}
	}
}
