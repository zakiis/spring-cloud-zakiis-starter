package com.zakiis.auditlog.domain.constants;

/**
 * 审计日志-操作结果状态
 * @author Liu Zhenghua
 */
public enum OperateStatus {

	SUCCESS("成功"),
	FAIL("失败"),
	;
	private OperateStatus(String desc) {
		this.desc = desc;
	}
	
	private String desc;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
