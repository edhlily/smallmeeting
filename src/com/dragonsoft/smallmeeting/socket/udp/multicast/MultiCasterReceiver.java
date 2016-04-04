package com.dragonsoft.smallmeeting.socket.udp.multicast;

import java.io.Serializable;

import com.dragonsoft.smallmeeting.socket.core.StateListener;
import com.dragonsoft.smallmeeting.socket.udp.receiver.Receiver;
import com.dragonsoft.smallmeeting.socket.util.Logger;

public class MultiCasterReceiver<T extends Serializable> implements MultiCaster, StateListener {
	private String multiCastIp = "239.0.0.1";
	private int receivePort = 45678;
	private StateListener stateListener;
	private Receiver<T> receiver;
	private ReceiveThread<T> receiveThread;
	private short openState = 0;

	public MultiCasterReceiver(Receiver<T> receiver) {
		this.receiver = receiver;
	}

	public MultiCasterReceiver(String multiCastIp, int receivePort, Receiver<T> dispatcher) {
		this.multiCastIp = multiCastIp;
		this.receivePort = receivePort;
		this.receiver = dispatcher;
	}

	public String getMultiCastIp() {
		return multiCastIp;
	}

	@Override
	public void open() {
		Logger.e("open");
		if (receiveThread == null || !receiveThread.isOpen()) {
			receiveThread = new ReceiveThread<T>(multiCastIp, receivePort, receiver);
			receiveThread.setStateListener(this);
			receiveThread.start();
		}
	}

	@Override
	public void close() {
		if (receiveThread != null) {
			receiveThread.close();
		}
	}

	public boolean isOpen() {
		return receiveThread != null && receiveThread.isOpen();
	}

	public StateListener getStateListener() {
		return stateListener;
	}

	public void setStateListener(StateListener stateListener) {
		this.stateListener = stateListener;
	}

	@Override
	public void closed() {
		if (stateListener != null) {
			stateListener.closed();
		}
	}

	@Override
	public void openSuccess() {
		openState = 1;
		if (stateListener != null) {
			stateListener.openSuccess();
		}
	}

	@Override
	public void openFailed() {
		openState = -1;
		if (stateListener != null) {
			stateListener.openFailed();
		}
	}

	public boolean isOpening() {
		return openState == 0;
	}

	public boolean isOpenSuccess() {
		return openState == 1;
	}
}
