package com.dragonsoft.smallmeeting.socket.core.message;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	private short flag;

	public Message() {
	}

	public Message(short flag) {
		this.flag = flag;
	}

	public short getFlag() {
		return flag;
	}

	public void setFlag(short flag) {
		this.flag = flag;
	}
}
