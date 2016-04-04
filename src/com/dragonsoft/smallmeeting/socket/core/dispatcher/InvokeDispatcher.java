package com.dragonsoft.smallmeeting.socket.core.dispatcher;

import java.net.SocketAddress;

import com.dragonsoft.smallmeeting.socket.core.invoker.InvokeMessgeInvoker;
import com.dragonsoft.smallmeeting.socket.core.message.InvokeMessage;

public abstract class InvokeDispatcher<T extends InvokeMessage<?>> extends Dispatcher<T> implements InvokeError<T>{
	private InvokeMessgeInvoker<T> invoker = new InvokeMessgeInvoker<>();
	@Override
	public void receiveData(SocketAddress socketAddress, T data) {
		invoker.dispatch(this, socketAddress, data);
	}
}
