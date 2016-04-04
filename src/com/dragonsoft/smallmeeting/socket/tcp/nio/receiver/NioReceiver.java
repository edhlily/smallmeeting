package com.dragonsoft.smallmeeting.socket.tcp.nio.receiver;

import java.net.SocketAddress;

import com.dragonsoft.smallmeeting.socket.core.dispatcher.BytesDispatcher;

public abstract class NioReceiver extends Receiver<byte[]> {
	public NioReceiver() {
		super(new BytesDispatcher() {

			@Override
			public void receiveData(SocketAddress socketAddress, byte[] data) {
				
			}
		});
	}
}
