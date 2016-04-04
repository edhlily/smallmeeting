package com.dragonsoft.smallmeeting.socket.core.parser;

import java.net.SocketAddress;

public class BytesParser implements Parser<byte[]> {
	@Override
	public byte[] receive(SocketAddress socketAddresses,byte[] datas) {
		return datas;
	}
	
	@Override
	public byte[] getBytes(byte[] data) {
		return data;
	}
}
