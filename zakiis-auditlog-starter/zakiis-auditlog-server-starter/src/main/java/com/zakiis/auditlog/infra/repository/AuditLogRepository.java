package com.zakiis.auditlog.infra.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.zakiis.auditlog.domain.entity.AuditLogPo;

public interface AuditLogRepository extends ElasticsearchRepository<AuditLogPo, String> {

}
