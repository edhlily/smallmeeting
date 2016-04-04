package com.dragonsoft.smallmeeting.socket.core.message;

import java.io.Serializable;

public abstract class InvokeMessage<T extends Serializable> extends Message {
	private static final long serialVersionUID = 1L;
	private String controller;
	private String method;
	private String dataType;
	public String getController() {
		return controller;
	}
	public void setController(String controller) {
		this.controller = controller;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public abstract T getData();
	public abstract void setData(T data);
}
