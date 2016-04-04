package com.dragonsoft.smallmeeting.socket.core;

public interface StateListener {
	void openSuccess();

	void openFailed();

	void closed();
}
