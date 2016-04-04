package com.dragonsoft.smallmeeting.socket.udp.multicast;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;

import com.dragonsoft.smallmeeting.socket.core.SendListener;

public abstract class MultiCasterSender<T extends Serializable> implements MultiCaster, SendListener<T> {
	private MulticastSocket multicastSocket;
	private InetAddress address;
	private String multiCastIp = "239.0.0.1";
	private int receivePort = 45678;
	private SendListener<T> senderListener;
	private SendThread<T> sendThread;
	private short openState = 0;
	
	public MultiCasterSender(){
		
	}
	
	public MultiCasterSender(String multiCastIp, int receivePort) {
		this.multiCastIp = multiCastIp;
		this.receivePort = receivePort;
	}

	public String getMultiCastIp() {
		return multiCastIp;
	}

	@Override
	public void close() {
		if (sendThread != null) {
			sendThread.close();
		}
	}

	@Override
	public void open() {
		if (sendThread == null || !sendThread.isOpen()) {
			sendThread = new SendThread<>(this,multiCastIp, receivePort);
			sendThread.setSendListener(this);
			sendThread.start();
		}
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public void setMultiCastIp(String multiCastIp) {
		this.multiCastIp = multiCastIp;
	}

	public MulticastSocket getMulticastSocket() {
		return multicastSocket;
	}

	public void setMulticastSocket(MulticastSocket multicastSocket) {
		this.multicastSocket = multicastSocket;
	}

	public int getReceivePort() {
		return receivePort;
	}

	public void setReceivePort(int receivePort) {
		this.receivePort = receivePort;
	}

	public boolean isOpen() {
		return sendThread.isOpen();
	}

	public SendListener<T> getSenderListener() {
		return senderListener;
	}

	public void setSenderListener(SendListener<T> senderListener) {
		this.senderListener = senderListener;
	}

	public void send(T data) {
		if (sendThread != null) {
			sendThread.send(data);
		}
	}

	@Override
	public void closed() {
		if (senderListener != null) {
			senderListener.closed();
		}
	}

	@Override
	public void openSuccess() {
		openState = 1;
		if (senderListener != null) {
			senderListener.openSuccess();
		}
	}

	@Override
	public void openFailed() {
		openState = -1;
		if (senderListener != null) {
			senderListener.openFailed();
		}
	}

	@Override
	public void sendSuccess(SocketAddress socketAddress, T data) {
		if (senderListener != null) {
			senderListener.sendSuccess(socketAddress, data);
		}
	}

	@Override
	public void sendFailed(SocketAddress socketAddress, T data) {
		if (senderListener != null) {
			senderListener.sendFailed(socketAddress, data);
		}
	}

	public boolean isOpening() {
		return openState == 0;
	}

	public boolean isOpenSuccess() {
		return openState == 1;
	}
	
	public abstract byte[] getBytes(T data);
}
