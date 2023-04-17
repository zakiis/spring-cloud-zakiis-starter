package com.zakiis.auditlog.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.zakiis.auditlog.domain.dto.AuditLogDto;
import com.zakiis.auditlog.domain.entity.AuditLogPo;
import org.mapstruct.Mapping;

@Mapper
public interface AuditLogMapper {

	AuditLogMapper INSTANCE = Mappers.getMapper(AuditLogMapper.class);
	
	@Mapping(target = "id", ignore = true)
	AuditLogPo convertToPo(AuditLogDto auditLogData);
}
