package com.dragonsoft.smallmeeting.socket.udp.multicast;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.util.concurrent.ArrayBlockingQueue;

import com.dragonsoft.smallmeeting.socket.core.SendListener;
import com.dragonsoft.smallmeeting.socket.util.Logger;

public class SendThread<T extends Serializable> extends Thread implements SendListener<T> {
	ArrayBlockingQueue<T> datas = new ArrayBlockingQueue<>(100);
	MulticastSocket multicastSocket;
	InetAddress address;
	private String multiCastIp;
	private int receivePort;
	private boolean open;
	private boolean send;
	private SendListener<T> sendListener;
	MultiCasterSender<T> sender;
	public SendThread(MultiCasterSender<T> sender, String multiCastIp, int receivePort) {
		this.sender = sender;
		this.multiCastIp = multiCastIp;
		this.receivePort = receivePort;
		open = true;
		send = true;
	}

	public SendListener<T> getSendListener() {
		return sendListener;
	}

	public void setSendListener(SendListener<T> sendListener) {
		this.sendListener = sendListener;
	}

	public void send(T data) {
		try {
			if (open) {
				datas.put(data);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			sendFailed(null, data);
		}
	}

	public void run() {
		Logger.e("开始发送线程");
		try {
			multicastSocket = new MulticastSocket();
			address = InetAddress.getByName(multiCastIp); // 必须使用D类地址
			multicastSocket.joinGroup(address); // 以D类地址为标识，加入同一个组才能实现广播
			openSuccess();
		} catch (IOException e1) {
			openFailed();
			e1.printStackTrace();
			return;
		}

		DatagramPacket datagramPacket = null;
		T data = null;
		byte[] buffer = null;
		try {
			while (true) {
				data = datas.take();
				if (send) {
					buffer = sender.getBytes(data);
					datagramPacket = new DatagramPacket(buffer, buffer.length);
					datagramPacket.setAddress(address);
					datagramPacket.setPort(receivePort);
					multicastSocket.send(datagramPacket);
					Logger.e("发送线程发送数据：" + data.toString() + " 总长度 ： " + buffer.length);
					sendSuccess(null, data);
				} else {
					break;
				}
			}
			Logger.e("发送线程已经关闭");
		} catch (Exception e) {
			e.printStackTrace();
			Logger.e("发送线程异常");
			sendFailed(null, data);
		} finally {
			datas.clear();
			open = false;
			if (multicastSocket != null && address != null) {
				try {
					multicastSocket.leaveGroup(address);
					multicastSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					closed();
				}
			}
		}
	}

	@Override
	public void openSuccess() {
		Logger.e("发送广播-启动成功");
		if (sendListener != null) {
			sendListener.openSuccess();
		}
	}

	@Override
	public void openFailed() {
		Logger.e("发送广播-启动失败");
		if (sendListener != null) {
			sendListener.openFailed();
		}
	}

	@Override
	public void sendSuccess(SocketAddress socketAddress, T data) {
		Logger.d("发送广播-发送" + data + "成功");
		if (sendListener != null) {
			sendListener.sendSuccess(socketAddress, data);
		}
	}

	@Override
	public void sendFailed(SocketAddress socketAddress, T data) {
		Logger.e("发送广播-发送" + data + "失败");
		if (sendListener != null) {
			sendListener.sendFailed(socketAddress, data);
		}
	}

	@Override
	public void closed() {
		Logger.e("发送广播-发送线程关闭");
		if (sendListener != null) {
			sendListener.closed();
		}
	}

	public boolean isOpen() {
		return open;
	}

	public void close() {
		send = false;
	}
}
