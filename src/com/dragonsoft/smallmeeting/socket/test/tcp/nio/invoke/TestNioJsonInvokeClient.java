package com.dragonsoft.smallmeeting.socket.test.tcp.nio.invoke;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketAddress;

import com.dragonsoft.smallmeeting.socket.core.dispatcher.JsonInvokeDispatcher;
import com.dragonsoft.smallmeeting.socket.core.message.MessageFormatException;
import com.dragonsoft.smallmeeting.socket.tcp.nio.ClientNioListener;
import com.dragonsoft.smallmeeting.socket.tcp.nio.NioClient;
import com.dragonsoft.smallmeeting.socket.tcp.nio.receiver.NioDispatcherReceiver;
import com.dragonsoft.smallmeeting.socket.test.TestProcessor;
import com.dragonsoft.smallmeeting.socket.util.Logger;
import com.google.gson.Gson;

public class TestNioJsonInvokeClient {
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
		
		final NioClient<MyJsonInvokeMessage> client = new NioClient<>("localhost", 19420, ndr);
		client.setNioListener(new ClientNioListener<MyJsonInvokeMessage>() {

			@Override
			public void openSuccess() {

			}

			@Override
			public void openFailed() {

			}

			@Override
			public void closed() {

			}

			@Override
			public void sendSuccess(SocketAddress socketAddress, MyJsonInvokeMessage data) {
			}

			@Override
			public void sendFailed(SocketAddress socketAddress, MyJsonInvokeMessage data) {

			}

			@Override
			public void prepareWrite(SocketAddress socketAddress) {
				try {
					MyJsonInvokeMessage im = MyJsonInvokeMessage.getInstance(TestProcessor.class, "serverReceive", "Hello Server !", String.class);
					client.send(im);
				} catch (MessageFormatException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void connected(SocketAddress socketAddress) {

			}

			@Override
			public void disConnect(SocketAddress socketAddress) {

			}

		});
		client.setRead_buffer_size(4096);
		client.setWrite_buffer_size(4096);
		client.start();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				if ("exit".equals(line)) {
					client.stop();
					break;
				} else {
					try {
						MyJsonInvokeMessage im = MyJsonInvokeMessage.getInstance(TestProcessor.class, "serverReceive", line, String.class);
						client.send(im);
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
