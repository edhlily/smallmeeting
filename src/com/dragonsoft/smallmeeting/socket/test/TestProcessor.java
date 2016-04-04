package com.dragonsoft.smallmeeting.socket.test;

import java.net.SocketAddress;

import com.dragonsoft.smallmeeting.socket.core.message.InvokeMessage;
import com.dragonsoft.smallmeeting.socket.util.Logger;

public class TestProcessor {
	public void serverReceive(SocketAddress socketAddress, InvokeMessage<?> msg, String data) {
		Logger.e("服务端收到消息 : " + "\nmsg:"+msg +"\ndata:"+ data);
	}

	public void clientReceive(SocketAddress socketAddress, InvokeMessage<?> msg, String data) {
		Logger.e("客户端收到消息 : " + "\nmsg:"+msg +"\ndata:"+ data);
	}

	public void multiReceiveTest(SocketAddress socketAddress, InvokeMessage<?> msg, String data) {
		Logger.e("广播收到消息 : " + data);
	}
}
