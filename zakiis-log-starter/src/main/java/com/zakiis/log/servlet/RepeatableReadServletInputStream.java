package com.zakiis.log.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

public class RepeatableReadServletInputStream extends ServletInputStream {

	private final ByteArrayInputStream bis;
	
	public RepeatableReadServletInputStream(byte[] bytes) {
		bis = new ByteArrayInputStream(bytes);	
	}
	
	@Override
	public boolean isFinished() {
		return bis.available() > 0;
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void setReadListener(ReadListener readListener) {
	}

	@Override
	public int read() throws IOException {
		return bis.read();
	}

	@Override
	public synchronized void reset() throws IOException {
		bis.reset();
	}
	

}
