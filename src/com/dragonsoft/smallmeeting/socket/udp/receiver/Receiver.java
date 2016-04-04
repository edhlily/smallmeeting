package com.dragonsoft.smallmeeting.socket.udp.receiver;

import java.io.Serializable;
import java.net.DatagramPacket;

import com.dragonsoft.smallmeeting.socket.core.dispatcher.Dispatcher;

public abstract class Receiver<T extends Serializable> {
	private int readBufferSize = 1024;
	private Dispatcher<T> dispatcher;

	public Receiver(Dispatcher<T> dispatcher, int readBufferSize) {
		this.dispatcher = dispatcher;
		this.readBufferSize = readBufferSize;
	}

	public Receiver(Dispatcher<T> dispatcher) {
		this.dispatcher = dispatcher;
	}

	public Dispatcher<T> getDispatcher() {
		return dispatcher;
	}

	public void setDispatcher(Dispatcher<T> dispatcher) {
		this.dispatcher = dispatcher;
	}

	public int getReadBufferSize() {
		return readBufferSize;
	}

	public void setReadBufferSize(int readBufferSize) {
		this.readBufferSize = readBufferSize;
	}
	
	public abstract void receive(DatagramPacket packet);
}
