package com.dragonsoft.smallmeeting.socket.core.dispatcher;

import java.net.SocketAddress;

public abstract class BytesDispatcher extends Dispatcher<byte[]> {
	public BytesDispatcher() {
		super();
	}

	@Override
	public byte[] parse(SocketAddress socketAddress, byte[] datas) {
		return datas;
	}

	@Override
	public byte[] getBytes(byte[] data) {
		return data;
	}
}
