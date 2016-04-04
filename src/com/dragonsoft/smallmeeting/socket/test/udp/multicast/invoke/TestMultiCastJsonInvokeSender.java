package com.dragonsoft.smallmeeting.socket.test.udp.multicast.invoke;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.dragonsoft.smallmeeting.socket.core.message.MessageFormatException;
import com.dragonsoft.smallmeeting.socket.test.TestProcessor;
import com.dragonsoft.smallmeeting.socket.test.tcp.nio.invoke.MyJsonInvokeMessage;
import com.dragonsoft.smallmeeting.socket.udp.multicast.MultiCasterSender;
import com.dragonsoft.smallmeeting.socket.util.Logger;
import com.google.gson.Gson;

public class TestMultiCastJsonInvokeSender {
	public static void main(String[] args) {
		MultiCasterSender<MyJsonInvokeMessage> casterSender = new MultiCasterSender<MyJsonInvokeMessage>(){

			@Override
			public byte[] getBytes(MyJsonInvokeMessage data) {
				return new Gson().toJson(data).getBytes();
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
					try {
						MyJsonInvokeMessage mjim = MyJsonInvokeMessage.getInstance(TestProcessor.class, "multiReceiveTest", line, String.class);
						casterSender.send(mjim);
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
