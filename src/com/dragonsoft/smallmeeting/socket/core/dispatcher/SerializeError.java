package com.dragonsoft.smallmeeting.socket.core.dispatcher;

import java.net.SocketAddress;

public interface SerializeError {
	void serializeError(SocketAddress socketAddress, byte[] datas);
}
