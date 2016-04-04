package com.dragonsoft.smallmeeting.socket.test.udp.multicast.bytes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.dragonsoft.smallmeeting.socket.udp.multicast.MultiCasterSender;
import com.dragonsoft.smallmeeting.socket.util.Logger;

public class TestMultiCastBytesSender {
	public static void main(String[] args) {
		MultiCasterSender<byte[]> casterSender = new MultiCasterSender<byte[]>(){

			@Override
			public byte[] getBytes(byte[] data) {
				return data;
			}
			
		};
		casterSender.open();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		try {
			while (casterSender.isOpen() && (line = br.readLine()) != null) {
				if ("exit".equals(line)) {
					casterSender.close();
					break;
				} else {
					casterSender.send(new byte[] { 5, 4, 3, 2, 1 });
				}
			}
			Logger.d("over");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
