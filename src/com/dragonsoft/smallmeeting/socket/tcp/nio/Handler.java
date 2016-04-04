package com.dragonsoft.smallmeeting.socket.tcp.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;

interface Handler {

	void handleAccept(SelectionKey key) throws IOException;

	void handleRead(SelectionKey key) throws IOException;

	void handleWrite(SelectionKey key) throws IOException;
}