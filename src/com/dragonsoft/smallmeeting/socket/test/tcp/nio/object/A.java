package com.dragonsoft.smallmeeting.socket.test.tcp.nio.object;

import java.io.Serializable;

public class A implements Serializable{
	private static final long serialVersionUID = -48106122198399777L;
	private String arg1;
	private String arg2;
	
	public String getArg1() {
		return arg1;
	}

	public void setArg1(String arg1) {
		this.arg1 = arg1;
	}

	public String getArg2() {
		return arg2;
	}

	public void setArg2(String arg2) {
		this.arg2 = arg2;
	}

	@Override
	public String toString() {
		return "A [arg1=" + arg1 + ", arg2=" + arg2 + "]";
	}
}