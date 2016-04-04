package com.dragonsoft.smallmeeting.socket.udp.receiver;

import java.net.SocketAddress;

import com.dragonsoft.smallmeeting.socket.core.dispatcher.BytesDispatcher;

public abstract class UdpReceiver extends Receiver<byte[]> {
	public UdpReceiver() {
		super(new BytesDispatcher() {
			@Override
			public void receiveData(SocketAddress socketAddress, byte[] data) {
			}
		});
	}
}
