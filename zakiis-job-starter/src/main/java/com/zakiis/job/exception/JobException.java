package com.zakiis.job.exception;

import com.zakiis.core.exception.ZakiisRuntimeException;

public class JobException extends ZakiisRuntimeException {

	private static final long serialVersionUID = 8176586351865381978L;

	public JobException() {
		super();
	}

	public JobException(String message, Throwable cause) {
		super(message, cause);
	}

	public JobException(String message) {
		super(message);
	}

	public JobException(Throwable cause) {
		super(cause);
	}

	
}
