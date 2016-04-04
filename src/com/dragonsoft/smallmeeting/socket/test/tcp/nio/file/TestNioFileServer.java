package com.dragonsoft.smallmeeting.socket.test.tcp.nio.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.dragonsoft.smallmeeting.socket.tcp.nio.NioServer;
import com.dragonsoft.smallmeeting.socket.tcp.nio.receiver.NioReceiver;
import com.dragonsoft.smallmeeting.socket.util.Logger;

public class TestNioFileServer {
	public static void main(String[] args) {
		NioReceiver nr = new NioReceiver() {
			
			@Override
			public int receive(SocketChannel socketChannel) throws IOException {
				return 0;
			}
		};
		NioServer<byte[]> server = new NioServer<>(nr);
		server.start();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				if ("exit".equals(line)) {
					server.stop();
					break;
				} else {
					server.sendAll(ByteBuffer.wrap(new byte[]{0,1,2,3}));
				}
			}
			Logger.d("over");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
