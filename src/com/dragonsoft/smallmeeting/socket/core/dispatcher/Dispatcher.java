package com.dragonsoft.smallmeeting.socket.core.dispatcher;

import java.io.Serializable;
import java.net.SocketAddress;
/**
 * 获取目标数据
 * @param <T>
 */
public abstract class Dispatcher<T extends Serializable> {
	/**
	 * 接收到消息时该方法会被调用
	 * @param socketAddress	远端地址
	 * @param data	接收到的目标数据
	 */
	public abstract void receiveData(SocketAddress socketAddress, T data);
	
	/**
	 * 将数组转化为目标对象
	 * @param socketAddress	远端地址
	 * @param datas	接收到的数据
	 * @return
	 */
	public abstract T parse(SocketAddress socketAddress,byte[] datas);
	
	/**
	 * 将目标对象转换为数组
	 * @param data	要转换的对象
	 * @return
	 */
	public abstract byte[] getBytes(T data);
	
	/**
	 * 原始数据转化为目标数据
	 * @param socketAddress	远端地址
	 * @param datas 原始数据
	 */
	public void receive(SocketAddress socketAddress, byte[] datas){
		receiveData(socketAddress, parse(socketAddress, datas));
	}
}
