package com.zakiis.auditlog.domain.message;

import java.io.Serializable;

import com.zakiis.auditlog.domain.dto.AuditLogDto;

import lombok.Data;

@Data
public class AuditLogMessage implements Serializable {
	
	private static final long serialVersionUID = -2624270487562836511L;
	private String seqNo;
	private AuditLogDto data;
	
	public static AuditLogMessage of(String seq, AuditLogDto data) {
		AuditLogMessage message = new AuditLogMessage();
		message.setSeqNo(seq);
		message.setData(data);
		return message;
	}

}
