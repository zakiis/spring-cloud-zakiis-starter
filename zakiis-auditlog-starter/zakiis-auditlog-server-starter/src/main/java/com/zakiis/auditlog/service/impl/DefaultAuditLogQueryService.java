package com.zakiis.auditlog.service.impl;

import org.apache.commons.lang3.StringUtils;

import com.zakiis.auditlog.config.AuditLogServerProperties;
import com.zakiis.auditlog.domain.dto.AuditLogDto;
import com.zakiis.auditlog.domain.dto.AuditLogQueryReq;
import com.zakiis.auditlog.service.AuditLogQueryService;
import com.zakiis.auditlog.util.PageUtil;
import com.zakiis.core.domain.dto.CommonPageResp;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class DefaultAuditLogQueryService implements AuditLogQueryService {
	
	private final ElasticsearchClient esClient;
	private final AuditLogServerProperties auditLogServerProperties;

	@Override
	@SneakyThrows
	public CommonPageResp<AuditLogDto> queryAuditLog(@Valid AuditLogQueryReq req) {
		// API上说大于10000条以后的数据需要用search_after参数，待测试：
		SearchResponse<AuditLogDto> searchResponse = esClient.search(s -> s
			.index(auditLogServerProperties.getLogIndexName())
			.ignoreUnavailable(true)
			.sort(sort -> sort.field(f -> f.field("operateTime").order(SortOrder.Desc)))
			.query(auditLogQuery(req))
			.from(req.getOffset())
			.size(req.getPageSize())
			, AuditLogDto.class);
		return PageUtil.extractData(searchResponse, req);
	}

	private Query auditLogQuery(AuditLogQueryReq req) {
		BoolQuery.Builder boolQueryBuiler = new BoolQuery.Builder();

		if (StringUtils.isNotBlank(req.getOperatorUuid())) {
			boolQueryBuiler = boolQueryBuiler
					.filter(f -> f.term(t -> t.field("operatorUuid").value(req.getOperatorUuid())));
		}
		if (req.getOperateGroup() != null) {
			boolQueryBuiler = boolQueryBuiler
					.filter(f -> f.term(t -> t.field("operateGroup").value(req.getOperateGroup())));
		}
		if (req.getOperateTarget() != null) {
			boolQueryBuiler = boolQueryBuiler
					.filter(f -> f.term(t -> t.field("operateTarget").value(req.getOperateTarget())));
		}
		if (req.getOperateType() != null) {
			boolQueryBuiler = boolQueryBuiler
					.filter(f -> f.term(t -> t.field("operateType").value(req.getOperateType())));
		}
		if (StringUtils.isNotBlank(req.getOperateTargetId())) {
			boolQueryBuiler = boolQueryBuiler
					.filter(f -> f.term(t -> t.field("operateTargetId").value(req.getOperateTargetId())));
		}
		if (StringUtils.isNotBlank(req.getOperateDesc())) {
			boolQueryBuiler = boolQueryBuiler
					.filter(f -> f.matchPhrase(t -> t.field("operateDesc").query(req.getOperateDesc())));
		}
		if (StringUtils.isNotBlank(req.getTraceId())) {
			boolQueryBuiler = boolQueryBuiler
					.filter(f -> f.matchPhrase(t -> t.field("traceId").query(req.getTraceId())));
		}
		if (StringUtils.isNotBlank(req.getOperatorIp())) {
			boolQueryBuiler = boolQueryBuiler
					.filter(f -> f.term(t -> t.field("operatorIp").value(req.getOperatorIp())));
		}
		if (req.getOperateStatus() != null) {
			boolQueryBuiler = boolQueryBuiler
					.filter(f -> f.term(t -> t.field("operateStatus").value(req.getOperateStatus().name())));
		}
		if (req.getOperateTimeStart() != null) {
			boolQueryBuiler = boolQueryBuiler
					.filter(f -> f.range(t -> t.field("operateTime").gte(JsonData.of(req.getOperateTimeStart()))));
		}
		if (req.getOperateTimeEnd() != null) {
			boolQueryBuiler = boolQueryBuiler
					.filter(f -> f.range(t -> t.field("operateTime").lte(JsonData.of(req.getOperateTimeEnd()))));
		}
		return new Query.Builder()
				.bool(boolQueryBuiler.build())
				.build();
	}
}
