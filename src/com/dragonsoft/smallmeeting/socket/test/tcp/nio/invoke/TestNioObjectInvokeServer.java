package com.dragonsoft.smallmeeting.socket.test.tcp.nio.invoke;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketAddress;

import com.dragonsoft.smallmeeting.socket.core.dispatcher.ObjectInvokeDispatcher;
import com.dragonsoft.smallmeeting.socket.core.message.MessageFormatException;
import com.dragonsoft.smallmeeting.socket.core.message.ObjectInvokeMessage;
import com.dragonsoft.smallmeeting.socket.tcp.nio.NioServer;
import com.dragonsoft.smallmeeting.socket.tcp.nio.receiver.NioDispatcherReceiver;
import com.dragonsoft.smallmeeting.socket.test.TestProcessor;
import com.dragonsoft.smallmeeting.socket.util.Logger;

public class TestNioObjectInvokeServer {
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
		NioServer<ObjectInvokeMessage<?>> server = new NioServer<>(ndr);
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
						ObjectInvokeMessage<String> im = ObjectInvokeMessage.getInstance(TestProcessor.class, "clientReceive", line, String.class);
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
