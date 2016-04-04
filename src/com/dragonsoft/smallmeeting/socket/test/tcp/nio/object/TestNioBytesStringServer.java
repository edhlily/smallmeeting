package com.dragonsoft.smallmeeting.socket.test.tcp.nio.object;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketAddress;

import com.dragonsoft.smallmeeting.socket.core.dispatcher.ObjectDispatcher;
import com.dragonsoft.smallmeeting.socket.tcp.nio.NioServer;
import com.dragonsoft.smallmeeting.socket.tcp.nio.receiver.NioDispatcherReceiver;
import com.dragonsoft.smallmeeting.socket.util.Logger;
/**
 *传递String的服务端
 */
public class TestNioBytesStringServer {
	public static void main(String[] args) {
		ObjectDispatcher<String> od = new ObjectDispatcher<String>() {

			@Override
			public void serializeError(SocketAddress socketAddress, byte[] datas) {
			}

			@Override
			public void receiveData(SocketAddress socketAddress, String data) {
				System.out.println("收到客户端传来的字符串 ：" + data);
			}
		};
		NioDispatcherReceiver<String> ndr = new NioDispatcherReceiver<>(od);
		NioServer<String> server = new NioServer<>(ndr);
		server.start();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				if ("exit".equals(line)) {
					server.stop();
					break;
				} else {
					server.sendAll(line);
				}
			}
			Logger.d("over");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
