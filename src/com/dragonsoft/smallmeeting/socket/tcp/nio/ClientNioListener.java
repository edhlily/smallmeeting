package com.dragonsoft.smallmeeting.socket.tcp.nio;

import java.io.Serializable;
import java.net.SocketAddress;

public interface ClientNioListener<T extends Serializable> extends NioListener<T> {
	void connected(SocketAddress socketAddress);

	void disConnect(SocketAddress socketAddress);
}
