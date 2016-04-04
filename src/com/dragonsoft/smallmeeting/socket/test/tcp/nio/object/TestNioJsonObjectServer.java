package com.dragonsoft.smallmeeting.socket.test.tcp.nio.object;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketAddress;

import com.dragonsoft.smallmeeting.socket.core.dispatcher.JsonObjectDispatcher;
import com.dragonsoft.smallmeeting.socket.tcp.nio.NioServer;
import com.dragonsoft.smallmeeting.socket.tcp.nio.receiver.NioDispatcherReceiver;
import com.dragonsoft.smallmeeting.socket.util.Logger;
import com.google.gson.Gson;
/**
 *以json数据方式传递Object的服务端
 */
public class TestNioJsonObjectServer {
	public static void main(String[] args) {
		JsonObjectDispatcher<A> od = new JsonObjectDispatcher<A>() {

			@Override
			public void receiveData(SocketAddress socketAddress, A data) {
				System.out.println("收到客户端传来的对象 ：" + data);
			}

			@Override
			public A parse(SocketAddress socketAddress, byte[] datas) {
				return new Gson().fromJson(new String(datas), A.class);
			}

			@Override
			public byte[] getBytes(A data) {
				return new Gson().toJson(data).getBytes();
			}
			
			
		};
		NioDispatcherReceiver<A> ndr = new NioDispatcherReceiver<>(od);
		NioServer<A> server = new NioServer<>(ndr);
		server.start();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				if ("exit".equals(line)) {
					server.stop();
					break;
				} else {
					A a = new A();
					a.setArg1("msg from server:" + line.length());
					a.setArg2(line);
					server.sendAll(a);
				}
			}
			Logger.d("over");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
