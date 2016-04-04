package com.dragonsoft.smallmeeting.socket.core.dispatcher;

import java.io.IOException;
import java.net.SocketAddress;

import com.dragonsoft.smallmeeting.socket.core.message.InvokeMessage;
import com.dragonsoft.smallmeeting.socket.util.SerializableUtil;

public abstract class ObjectInvokeDispatcher<T extends InvokeMessage<?>> extends InvokeDispatcher<T> implements SerializeError{
	@Override
	public T parse(SocketAddress socketAddress, byte[] datas) {
		try {
			return SerializableUtil.toObject(datas, 0, datas.length);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			serializeError(socketAddress, datas);
		}
		return null;
	}

	@Override
	public byte[] getBytes(T data) {
		return SerializableUtil.toBytes(data);
	}
}
