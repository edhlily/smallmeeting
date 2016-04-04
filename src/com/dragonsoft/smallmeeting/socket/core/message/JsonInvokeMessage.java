package com.dragonsoft.smallmeeting.socket.core.message;

import java.io.Serializable;

public abstract class JsonInvokeMessage<T extends Serializable> extends InvokeMessage<T> {
	private static final long serialVersionUID = 1L;
	private String data;

	protected JsonInvokeMessage() {
	}

	public void setData(T data) {
		this.data = toJson(data);
	}
	
	@Override
	public T getData() {
		return toData(data);
	}

	public abstract T toData(String json);

	public abstract String toJson(T data);
}
