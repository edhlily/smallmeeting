package com.dragonsoft.smallmeeting.socket.test.tcp.nio.invoke;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketAddress;

import com.dragonsoft.smallmeeting.socket.core.dispatcher.JsonInvokeDispatcher;
import com.dragonsoft.smallmeeting.socket.core.message.MessageFormatException;
import com.dragonsoft.smallmeeting.socket.tcp.nio.NioServer;
import com.dragonsoft.smallmeeting.socket.tcp.nio.receiver.NioDispatcherReceiver;
import com.dragonsoft.smallmeeting.socket.test.TestProcessor;
import com.dragonsoft.smallmeeting.socket.util.Logger;
import com.google.gson.Gson;

public class TestNioJsonInvokeServer {
	public static void main(String[] args) {
		JsonInvokeDispatcher<MyJsonInvokeMessage> jid = new JsonInvokeDispatcher<MyJsonInvokeMessage>() {

			@Override
			public void invokeError(SocketAddress socketAddress, MyJsonInvokeMessage data) {
			}

			@Override
			public MyJsonInvokeMessage parse(SocketAddress socketAddress, byte[] datas) {
				return new Gson().fromJson(new String(datas), MyJsonInvokeMessage.class);
			}

			@Override
			public byte[] getBytes(MyJsonInvokeMessage data) {
				return new Gson().toJson(data).getBytes();
			}
		};
		
		NioDispatcherReceiver<MyJsonInvokeMessage> ndr = new NioDispatcherReceiver<MyJsonInvokeMessage>(jid);
		
		NioServer<MyJsonInvokeMessage> server = new NioServer<>(ndr);
		server.start();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				if ("exit".equals(line)) {
					server.stop();
					// break;
				} else if ("start".equals(line)) {
					server.start();
				} else {
					try {
						MyJsonInvokeMessage im = MyJsonInvokeMessage.getInstance(TestProcessor.class, "clientReceive", line, String.class);
						server.sendAll(im);
					} catch (MessageFormatException e) {
						e.printStackTrace();
					}
				}
			}
			Logger.d("over");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
