package com.dragonsoft.smallmeeting.socket.core.dispatcher;

import java.net.SocketAddress;

import com.dragonsoft.smallmeeting.socket.core.message.InvokeMessage;

public interface InvokeError<T extends InvokeMessage<?>> {
	void invokeError(SocketAddress socketAddress, T data);
}
