package com.dragonsoft.smallmeeting.socket.test.udp.multicast.bytes;

import java.net.SocketAddress;
import java.util.Arrays;

import com.dragonsoft.smallmeeting.socket.core.StateListener;
import com.dragonsoft.smallmeeting.socket.core.dispatcher.BytesDispatcher;
import com.dragonsoft.smallmeeting.socket.udp.multicast.MultiCasterReceiver;
import com.dragonsoft.smallmeeting.socket.udp.receiver.UdpDispatcherReceiver;

public class TestMultiCastBytesReceiver {
	public static void main(String[] args) {
		BytesDispatcher bd = new BytesDispatcher() {
			
			@Override
			public void receiveData(SocketAddress socketAddress, byte[] data) {
				System.out.println("广播收到消息："+Arrays.toString(data));
			}
		};
		
		UdpDispatcherReceiver<byte[]> udr = new UdpDispatcherReceiver<byte[]>(bd);
		MultiCasterReceiver<byte[]> casterReceiver = new MultiCasterReceiver<>(udr);
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
