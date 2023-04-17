package com.zakiis.log.constant;

public enum DesensitizationEnum {

	/** 18681818181 -&gt; 186****8181 */
	REPLACE,
	/** 622612341234 -&gt; "************" */
	ERASE,
	/** drop the field */
	DROP,
	;
	
}
