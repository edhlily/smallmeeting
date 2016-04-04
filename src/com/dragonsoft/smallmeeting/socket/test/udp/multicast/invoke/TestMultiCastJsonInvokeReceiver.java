package com.dragonsoft.smallmeeting.socket.test.udp.multicast.invoke;

import java.net.SocketAddress;

import com.dragonsoft.smallmeeting.socket.core.StateListener;
import com.dragonsoft.smallmeeting.socket.core.dispatcher.JsonInvokeDispatcher;
import com.dragonsoft.smallmeeting.socket.test.tcp.nio.invoke.MyJsonInvokeMessage;
import com.dragonsoft.smallmeeting.socket.udp.multicast.MultiCasterReceiver;
import com.dragonsoft.smallmeeting.socket.udp.receiver.UdpDispatcherReceiver;
import com.google.gson.Gson;

public class TestMultiCastJsonInvokeReceiver {
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
		UdpDispatcherReceiver<MyJsonInvokeMessage> udr = new UdpDispatcherReceiver<>(jid);
		MultiCasterReceiver<MyJsonInvokeMessage> casterReceiver = new MultiCasterReceiver<>(udr);
		casterReceiver.setStateListener(new StateListener() {

			@Override
			public void openSuccess() {
			}

			@Override
			public void openFailed() {
			}

			@Override
			public void closed() {
			}
		});
		casterReceiver.open();
	}
}
