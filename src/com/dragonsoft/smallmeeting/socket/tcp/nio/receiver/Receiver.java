package com.dragonsoft.smallmeeting.socket.tcp.nio.receiver;

import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.SocketChannel;

import com.dragonsoft.smallmeeting.socket.core.dispatcher.Dispatcher;

public abstract class Receiver<T extends Serializable> {
	private int readBufferSize = 1024;
	private Dispatcher<T> dispatcher;
	
	public Receiver(Dispatcher<T> dispatcher){
		this.dispatcher = dispatcher;
	}
	
	public Receiver(Dispatcher<T> dispatcher,int readBufferSize){
		this.dispatcher = dispatcher;
		this.readBufferSize = readBufferSize;
	}
	
	public Dispatcher<T> getDispatcher(){
		return dispatcher;
	}

	public int getReadBufferSize() {
		return readBufferSize;
	}
	
	public void setReadBufferSize(int readBufferSize) {
		this.readBufferSize = readBufferSize;
	}
	
	public abstract int receive(SocketChannel socketChannel) throws IOException;
}
