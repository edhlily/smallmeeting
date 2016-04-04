package com.dragonsoft.smallmeeting.socket.core.message;

import java.io.Serializable;
import java.net.SocketAddress;

public class ObjectInvokeMessage<T extends Serializable> extends InvokeMessage<T>{
	private static final long serialVersionUID = 1L;
	private T data;
	public void setData(T data) {
		this.data = data;
	}

	@Override
	public T getData() {
		return data;
	}
	
	public static <T extends Serializable> ObjectInvokeMessage<T> getInstance(Class<?> controller, String method, T data, Class<T> dataType) throws MessageFormatException {
		if (dataType == null) {
			throw new MessageFormatException("data type should not null");
		}
		if (data != null && !data.getClass().isAssignableFrom(dataType)) {
			throw new MessageFormatException(data.getClass().getName() + " can not convert to " + dataType.getName());
		}
		try {
			controller.getDeclaredMethod(method, SocketAddress.class, InvokeMessage.class, dataType);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new MessageFormatException(e);
		}
		ObjectInvokeMessage<T> im = new ObjectInvokeMessage<>();
		im.setController(controller.getName());
		im.setMethod(method);
		im.setDataType(dataType.getName());
		im.setData(data);
		return im;
	}
}
