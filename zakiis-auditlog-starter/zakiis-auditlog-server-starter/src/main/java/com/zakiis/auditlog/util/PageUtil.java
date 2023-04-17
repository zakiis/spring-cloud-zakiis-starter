package com.zakiis.auditlog.util;

import java.util.List;
import java.util.stream.Collectors;

import com.zakiis.core.domain.dto.CommonPageReq;
import com.zakiis.core.domain.dto.CommonPageResp;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

public class PageUtil {

	public static <T> CommonPageResp<T> extractData(SearchResponse<T> searchResponse, CommonPageReq pageReq) {
		List<T> dataList =  searchResponse.hits().hits()
			.stream()
			.map(Hit::source)
			.collect(Collectors.toList());
		return CommonPageResp.of(dataList, pageReq, searchResponse.hits().total().value());
	}
	
}
