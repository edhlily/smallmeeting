package com.dragonsoft.smallmeeting.socket.tcp.nio;

import java.io.Serializable;
import java.net.SocketAddress;

import com.dragonsoft.smallmeeting.socket.core.SendListener;

public interface NioListener<T extends Serializable> extends SendListener<T> {
	void prepareWrite(SocketAddress socketAddress);
}
