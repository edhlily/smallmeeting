package com.dragonsoft.smallmeeting.socket.test.tcp.nio.object;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketAddress;

import com.dragonsoft.smallmeeting.socket.core.dispatcher.ObjectDispatcher;
import com.dragonsoft.smallmeeting.socket.tcp.nio.NioClient;
import com.dragonsoft.smallmeeting.socket.tcp.nio.receiver.NioDispatcherReceiver;
import com.dragonsoft.smallmeeting.socket.util.Logger;

/**
 * 传递String的客户端
 */
public class TestNioBytesStringClient {
	public static void main(String[] args) {
		ObjectDispatcher<String> od = new ObjectDispatcher<String>() {

			@Override
			public void serializeError(SocketAddress socketAddress, byte[] datas) {
			}

			@Override
			public void receiveData(SocketAddress socketAddress, String data) {
				System.out.println("收到服务端传来的字符串 ：" + data);
			}
		};
		NioDispatcherReceiver<String> ndr = new NioDispatcherReceiver<>(od);
		final NioClient<String> client = new NioClient<>("localhost", 19420, ndr);

		client.start();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				if ("exit".equals(line)) {
					client.stop();
					break;
				} else {
					client.send(line);
				}
			}
			Logger.d("over");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
