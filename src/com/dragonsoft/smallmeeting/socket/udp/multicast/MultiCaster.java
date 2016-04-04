package com.dragonsoft.smallmeeting.socket.udp.multicast;

public interface MultiCaster {
	String getMultiCastIp();

	void open();

	void close();

	boolean isOpen();
}
