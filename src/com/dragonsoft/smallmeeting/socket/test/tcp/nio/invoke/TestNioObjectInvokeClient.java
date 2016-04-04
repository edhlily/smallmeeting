package com.dragonsoft.smallmeeting.socket.test.tcp.nio.invoke;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketAddress;

import com.dragonsoft.smallmeeting.socket.core.dispatcher.ObjectInvokeDispatcher;
import com.dragonsoft.smallmeeting.socket.core.message.MessageFormatException;
import com.dragonsoft.smallmeeting.socket.core.message.ObjectInvokeMessage;
import com.dragonsoft.smallmeeting.socket.tcp.nio.ClientNioListener;
import com.dragonsoft.smallmeeting.socket.tcp.nio.NioClient;
import com.dragonsoft.smallmeeting.socket.tcp.nio.receiver.NioDispatcherReceiver;
import com.dragonsoft.smallmeeting.socket.test.TestProcessor;
import com.dragonsoft.smallmeeting.socket.util.Logger;

public class TestNioObjectInvokeClient {
	public static void main(String[] args) {
		ObjectInvokeDispatcher<ObjectInvokeMessage<?>> oid = new ObjectInvokeDispatcher<ObjectInvokeMessage<?>>() {

			@Override
			public void serializeError(SocketAddress socketAddress, byte[] datas) {
			}

			@Override
			public void invokeError(SocketAddress socketAddress, ObjectInvokeMessage<?> data) {
			}
		};
		NioDispatcherReceiver<ObjectInvokeMessage<?>> ndr = new NioDispatcherReceiver<>(oid);
		final NioClient<ObjectInvokeMessage<?>> client = new NioClient<>("localhost", 19420, ndr);
		client.setNioListener(new ClientNioListener<ObjectInvokeMessage<?>>() {

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
			public void sendSuccess(SocketAddress socketAddress, ObjectInvokeMessage<?> data) {
			}

			@Override
			public void sendFailed(SocketAddress socketAddress, ObjectInvokeMessage<?> data) {

			}

			@Override
			public void prepareWrite(SocketAddress socketAddress) {
				try {
					ObjectInvokeMessage<String> im = ObjectInvokeMessage.getInstance(TestProcessor.class, "serverReceive", "Hello Server !", String.class);
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
						ObjectInvokeMessage<String> im = ObjectInvokeMessage.getInstance(TestProcessor.class, "serverReceive", line, String.class);
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
