package com.dragonsoft.smallmeeting.socket.core;

import java.io.Serializable;
import java.net.SocketAddress;

public interface SendListener<T extends Serializable> extends StateListener {
	void sendSuccess(SocketAddress socketAddress, T data);

	void sendFailed(SocketAddress socketAddress, T data);
}