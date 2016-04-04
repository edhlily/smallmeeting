package com.dragonsoft.smallmeeting.socket.core.parser;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketAddress;

import com.dragonsoft.smallmeeting.socket.util.SerializableUtil;

public abstract class BytesObjectParser<T extends Serializable> extends ObjectParser<T> {

	@Override
	public T receive(SocketAddress socketAddress, byte[] datas) {
		try {
			return SerializableUtil.toObject(datas, 0, datas.length);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			error(socketAddress, datas);
		}
		return null;
	}

	@Override
	public byte[] getBytes(T data) {
		return SerializableUtil.toBytes(data);
	}

	public abstract void error(SocketAddress socketAddress, byte[] datas);
}
