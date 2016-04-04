package com.dragonsoft.smallmeeting.socket.udp.receiver;

import java.io.Serializable;
import java.net.DatagramPacket;

import com.dragonsoft.smallmeeting.socket.core.dispatcher.Dispatcher;

public class UdpDispatcherReceiver<T extends Serializable> extends Receiver<T> {

	public UdpDispatcherReceiver(Dispatcher<T> dispatcher, int readBufferSize) {
		super(dispatcher, readBufferSize);
	}

	public UdpDispatcherReceiver(Dispatcher<T> dispatcher) {
		super(dispatcher);
	}

	@Override
	public void receive(DatagramPacket packet) {
		byte[] buf = new byte[packet.getLength()];
		System.arraycopy(packet.getData(), 0, buf, 0, buf.length);
		getDispatcher().receive(packet.getSocketAddress(), buf);
	}
}
