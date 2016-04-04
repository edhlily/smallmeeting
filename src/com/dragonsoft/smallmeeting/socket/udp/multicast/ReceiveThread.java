package com.dragonsoft.smallmeeting.socket.udp.multicast;

import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import com.dragonsoft.smallmeeting.socket.core.StateListener;
import com.dragonsoft.smallmeeting.socket.udp.receiver.Receiver;
import com.dragonsoft.smallmeeting.socket.util.Logger;

public class ReceiveThread<T extends Serializable> extends Thread implements StateListener {
	private MulticastSocket multicastSocket;
	private InetAddress address;
	private String multiCastIp = "239.0.0.1";
	private int receivePort = 45678;
	private boolean open;
	private Receiver<T> receiver;
	private StateListener stateListener;

	public ReceiveThread() {

	}

	public ReceiveThread(String multiCastIp, int receivePort, Receiver<T> receiver) {
		this.multiCastIp = multiCastIp;
		this.receivePort = receivePort;
		this.receiver = receiver;
	}

	@Override
	public void run() {
		try {
			Logger.e("开始接收线程");
			open = true;
			multicastSocket = new MulticastSocket(receivePort);
			address = InetAddress.getByName(multiCastIp);
			multicastSocket.joinGroup(address);
		} catch (Exception e) {
			e.printStackTrace();
			openFailed();
			return;
		}
		openSuccess();
		DatagramPacket datagramPacket;
		byte[] buf = new byte[4096];
		open = true;
		int exceptionTime = 0;
		while (open) {
			try {
				datagramPacket = new DatagramPacket(buf, buf.length);
				multicastSocket.receive(datagramPacket);
				if (open) {
					receiver.receive(datagramPacket);
				}
				exceptionTime = 0;
			} catch (Exception e) {
				exceptionTime++;
				if (exceptionTime > 100) {
					open = false;
				}
				e.printStackTrace();
			}
		}
		Logger.e("接收线程已经关闭！");
		open = false;
		closed();
		try {
			if (multicastSocket != null && address != null) {
				multicastSocket.leaveGroup(address);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public StateListener getStateListener() {
		return stateListener;
	}

	public void setStateListener(StateListener stateListener) {
		this.stateListener = stateListener;
	}

	public boolean isOpen() {
		return open;
	}

	public void close() {
		open = false;
		multicastSocket.close();
	}

	@Override
	public void openSuccess() {
		Logger.e("接收广播-启动成功");
		if (stateListener != null) {
			stateListener.openSuccess();
		}
	}

	@Override
	public void openFailed() {
		Logger.e("接收广播-启动失败");
		if (stateListener != null) {
			stateListener.openFailed();
		}
	}

	@Override
	public void closed() {
		Logger.e("接收广播-关闭");
		if (stateListener != null) {
			stateListener.closed();
		}
	}

}