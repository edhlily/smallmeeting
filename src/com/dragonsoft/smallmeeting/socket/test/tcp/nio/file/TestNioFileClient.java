package com.dragonsoft.smallmeeting.socket.test.tcp.nio.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.dragonsoft.smallmeeting.socket.tcp.nio.NioClient;
import com.dragonsoft.smallmeeting.socket.tcp.nio.receiver.NioReceiver;
import com.dragonsoft.smallmeeting.socket.util.Logger;

public class TestNioFileClient {
	public static void main(String[] args) {
		NioReceiver nr = new NioReceiver() {
			
			@Override
			public int receive(SocketChannel socketChannel) throws IOException {
				return 0;
			}
		};
		
		final NioClient<byte[]> client = new NioClient<>("localhost", 19420, nr);

		client.start();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				if ("exit".equals(line)) {
					client.stop();
					break;
				} else {
					client.send(ByteBuffer.wrap(new byte[]{0,1,2,3}));
				}
			}
			Logger.d("over");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
