package com.dragonsoft.smallmeeting.socket.core.parser;

import java.io.Serializable;
import java.net.SocketAddress;

public interface Parser<T extends Serializable> {
	public T receive(SocketAddress socketAddresses, byte[] datas);

	public byte[] getBytes(T data);
}
