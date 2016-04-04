package com.dragonsoft.smallmeeting.socket.tcp.nio;

import java.io.Serializable;
import java.net.SocketAddress;

public interface ServerNioListener<T extends Serializable> extends NioListener<T> {
	void disConnect(SocketAddress socketAddress);
	void connected(SocketAddress socketAddress);
}
