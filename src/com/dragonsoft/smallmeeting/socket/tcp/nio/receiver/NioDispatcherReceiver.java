package com.dragonsoft.smallmeeting.socket.tcp.nio.receiver;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.dragonsoft.smallmeeting.socket.core.dispatcher.Dispatcher;

public class NioDispatcherReceiver<T extends Serializable> extends Receiver<T>{


	public NioDispatcherReceiver(Dispatcher<T> dispatcher) {
		super(dispatcher);
	}

	public NioDispatcherReceiver(Dispatcher<T> dispatcher, int readBufferSize) {
		super(dispatcher, readBufferSize);
	}

	public int receive(SocketChannel socketChannel) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(getReadBufferSize());
		int len = socketChannel.read(buffer);
		buffer.flip();
		byte[] bytes = new byte[buffer.limit()];
		buffer.get(bytes);
		getDispatcher().receive(socketChannel.socket().getRemoteSocketAddress(), bytes);
		return len;
	}
}
