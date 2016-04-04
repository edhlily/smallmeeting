package com.dragonsoft.smallmeeting.socket.core.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketAddress;

import com.dragonsoft.smallmeeting.socket.core.dispatcher.InvokeDispatcher;
import com.dragonsoft.smallmeeting.socket.core.message.InvokeMessage;
import com.dragonsoft.smallmeeting.socket.util.Logger;

public class InvokeMessgeInvoker<T extends InvokeMessage<?>> {
	public void dispatch(InvokeDispatcher<T> dispather, SocketAddress socketAddress, T data) {
		Logger.e("invoke message from : " + socketAddress);
		String controllerName = data.getController();
		if (controllerName == null | data.getMethod() == null | data.getDataType() == null) {
			dispather.invokeError(socketAddress, data);
			return;
		}
		try {
			Class<?> controller = Class.forName(controllerName);
			Class<?> dataType = Class.forName(data.getDataType());
			Method method = controller.getDeclaredMethod(data.getMethod(), SocketAddress.class, InvokeMessage.class, dataType);
			method.invoke(controller.newInstance(), socketAddress, data, data.getData());
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException | ClassNotFoundException e) {
			e.printStackTrace();
			dispather.invokeError(socketAddress, data);
		}
	}
}
