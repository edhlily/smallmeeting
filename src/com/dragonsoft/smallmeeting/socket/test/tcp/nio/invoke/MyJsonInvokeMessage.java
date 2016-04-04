package com.dragonsoft.smallmeeting.socket.test.tcp.nio.invoke;

import java.io.Serializable;
import java.net.SocketAddress;

import com.dragonsoft.smallmeeting.socket.core.message.InvokeMessage;
import com.dragonsoft.smallmeeting.socket.core.message.JsonInvokeMessage;
import com.dragonsoft.smallmeeting.socket.core.message.MessageFormatException;

public class MyJsonInvokeMessage extends JsonInvokeMessage<String>{
	private static final long serialVersionUID = 1L;

	@Override
	public String toData(String json) {
		return json;
	}

	@Override
	public String toJson(String data) {
		return data;
	}
	
	public static MyJsonInvokeMessage getInstance(Class<?> controller, String method, String data, Class<? extends Serializable> dataType) throws MessageFormatException {
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
		MyJsonInvokeMessage mim = new MyJsonInvokeMessage();
		mim.setController(controller.getName());
		mim.setMethod(method);
		mim.setDataType(dataType.getName());
		mim.setData(data);
		return mim;
	}

}
