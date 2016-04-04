package com.dragonsoft.smallmeeting.socket.tcp.nio;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dragonsoft.smallmeeting.socket.core.SendListener;
import com.dragonsoft.smallmeeting.socket.tcp.nio.receiver.Receiver;
import com.dragonsoft.smallmeeting.socket.util.Logger;

public class NioServer<T extends Serializable> implements Runnable, ServerNioListener<T>, Handler {
	private static final String TAG = "Server";
	private int read_buffer_size = 8192;
	private int write_buffer_size = 8192;
	private int port = 19420;
	private Receiver<T> receiver;
	private Selector selector = null;
	private ServerSocketChannel serverSocketChannel = null;
	private Map<SocketAddress, SocketChannel> channels = new HashMap<>();
	private ServerNioListener<T> sendListener;
	private final Thread thread = new Thread(this);

	public NioServer(Receiver<T> receiver) {
		this.receiver = receiver;
	}

	public SendListener<T> getSendListener() {
		return sendListener;
	}

	public void setSendListener(ServerNioListener<T> sendListener) {
		this.sendListener = sendListener;
	}

	public int getRead_buffer_size() {
		return read_buffer_size;
	}

	public void setRead_buffer_size(int read_buffer_size) {
		this.read_buffer_size = read_buffer_size;
		if (receiver != null) {
			receiver.setReadBufferSize(read_buffer_size);
		}
	}

	public int getWrite_buffer_size() {
		return write_buffer_size;
	}

	public void setWrite_buffer_size(int write_buffer_size) {
		this.write_buffer_size = write_buffer_size;
	}

	public void start() {
		Logger.d(TAG, "starting event loop");
		open = true;
		thread.start();
	}

	public void stop() {
		Logger.d(TAG, "stopping event loop");
		open = false;
		try {
			thread.interrupt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			selector.wakeup();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void bind() {
		try {
			serverSocketChannel.socket().bind(new InetSocketAddress(port));
		} catch (IOException e) {
			e.printStackTrace();
			port++;
			bind();
		}
	}

	private void configureServerChannel(ServerSocketChannel channel) throws IOException {
		channel.configureBlocking(false);
		channel.socket().setReceiveBufferSize(read_buffer_size);
		channel.socket().setReuseAddress(true);
		// channel.socket().setSoTimeout(0);
	}

	private void configureClientChannel(SocketChannel channel) throws IOException {
		channel.configureBlocking(false);
		channel.socket().setSendBufferSize(write_buffer_size);
		channel.socket().setReceiveBufferSize(read_buffer_size);
		channel.socket().setKeepAlive(true);
		channel.socket().setReuseAddress(true);
		// channel.socket().setSoLinger(false, 0);
		// channel.socket().setSoTimeout(0);
		channel.socket().setTcpNoDelay(true);
	}

	boolean open;

	public void run() {
		while (open) {
			try {
				selector = Selector.open();
				serverSocketChannel = ServerSocketChannel.open();
				configureServerChannel(serverSocketChannel);
				bind();
				serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
				openSuccess();
			} catch (IOException e1) {
				openFailed();
				e1.printStackTrace();
				try {
					if (serverSocketChannel != null)
						serverSocketChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					if (selector != null)
						selector.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				open = false;
				return;
			}
			int errorContinue = 0;
			while (open && serverSocketChannel.isOpen()) {
				int nKeys = 0;
				try {
					nKeys = selector.select();
				} catch (IOException e) {
					e.printStackTrace();
					errorContinue++;
					if (errorContinue < 10) {
						continue;
					} else {
						break;
					}
				}
				errorContinue = 0;
				if (nKeys > 0) {
					Set<SelectionKey> selectedKeys = selector.selectedKeys();
					Iterator<SelectionKey> it = selectedKeys.iterator();
					while (it.hasNext()) {
						SelectionKey key = it.next();
						it.remove();
						if (!key.isValid()) {
							continue;
						} else if (key.isAcceptable()) {
							Logger.d(TAG, "SelectionKey is acceptable.");
							handleAccept(key);
						} else if (key.isReadable()) {
							Logger.d(TAG, "SelectionKey is readable.");
							handleRead(key);
						} else if (key.isWritable()) {
							Logger.d(TAG, "SelectionKey is writable.");
							handleWrite(key);
						}
					}
				}
			}
		}
		open = false;
		for (SocketAddress socketAddress : channels.keySet()) {
			SocketChannel sc = channels.get(socketAddress);
			if (sc != null) {
				try {
					sc.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		try {
			if (serverSocketChannel != null)
				serverSocketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (selector != null)
				selector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		closed();
		Logger.d(TAG, "connection closed");
	}

	public void send(SocketAddress socketAddress, T data) {
		byte[] bytes = receiver.getDispatcher().getBytes(data);
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		send(socketAddress, buffer);
	}

	public boolean send(SocketAddress socketAddress, ByteBuffer buffer) {
		Logger.d("send " + socketAddress + " :" + buffer.limit());
		SocketChannel socketChannel = channels.get(socketAddress);
		if (socketChannel != null && socketChannel.isConnected()) {
			try {
				while (buffer.hasRemaining()) {
					socketChannel.write(buffer);
				}
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	public void sendAll(Iterable<SocketAddress> addresses, T data) {
		byte[] bytes = receiver.getDispatcher().getBytes(data);
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		sendAll(addresses, buffer);
	}

	List<SocketAddress> preRemovedAddresses = new ArrayList<>();

	public void sendAll(Iterable<SocketAddress> addresses, ByteBuffer buffer) {
		Logger.d("sendAll:" + buffer.limit());
		for (SocketAddress address : addresses) {
			buffer.rewind();
			if (!send(address, buffer)) {
				preRemovedAddresses.add(address);
			}
		}

		for (SocketAddress socketAddress : preRemovedAddresses) {
			channels.remove(socketAddress);
		}
		preRemovedAddresses.clear();
	}

	public void sendAll(T data) {
		sendAll(channels.keySet(), data);
	}

	public void sendAll(ByteBuffer buffer) {
		sendAll(channels.keySet(), buffer);
	}

	@Override
	public void openSuccess() {
		Logger.e(TAG, "openSuccess !");
		if (sendListener != null) {
			sendListener.openSuccess();
		}
	}

	@Override
	public void openFailed() {
		Logger.e(TAG, "openFailed !");
		if (sendListener != null) {
			sendListener.openFailed();
		}
	}

	@Override
	public void prepareWrite(SocketAddress socketAddress) {
		Logger.e(TAG, "prepareWrite");
		if (sendListener != null) {
			sendListener.prepareWrite(socketAddress);
		}
	}

	@Override
	public void sendSuccess(SocketAddress socketAddress, T data) {
		Logger.e(TAG, "sendSuccess : " + data);
		if (sendListener != null) {
			sendListener.sendSuccess(socketAddress, data);
		}
	}

	@Override
	public void sendFailed(SocketAddress socketAddress, T data) {
		Logger.e(TAG, "sendFailed : " + data);
		if (sendListener != null) {
			sendListener.sendFailed(socketAddress, data);
		}
	}

	@Override
	public void closed() {
		Logger.e(TAG, "closed ! ");
		if (sendListener != null) {
			sendListener.closed();
		}
	}

	@Override
	public void connected(SocketAddress socketAddress) {
		Logger.e(TAG, "client " + socketAddress.toString() + " connected! ");
		if (sendListener != null) {
			sendListener.connected(socketAddress);
		}
	}

	@Override
	public void disConnect(SocketAddress socketAddress) {
		Logger.e(TAG, "client " + socketAddress.toString() + " disConnect! ");
		if (sendListener != null) {
			sendListener.disConnect(socketAddress);
		}
	}

	@Override
	public void handleAccept(SelectionKey key) {
		try {
			SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
			configureClientChannel(socketChannel);
			channels.put(socketChannel.socket().getRemoteSocketAddress(), socketChannel);
			Logger.d(TAG, "handleAccept : " + socketChannel.socket().getRemoteSocketAddress() + " total : " + channels.size());
			connected(socketChannel.socket().getRemoteSocketAddress());
			socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		} catch (IOException e) {
			e.printStackTrace();
			try {
				key.channel().close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void handleRead(SelectionKey key) {
		Logger.d(TAG, "handleRead");
		SocketChannel sc = (SocketChannel) key.channel();
		try {
			int len = receiver.receive(sc);
			if (len < 0) {
				closeSocketChannel(sc);
			}
		} catch (IOException e) {
			e.printStackTrace();
			closeSocketChannel(sc);
		}
	}

	private void closeSocketChannel(SocketChannel sc) {
		Logger.d(TAG, "peer closed read channel");
		disConnect(sc.socket().getRemoteSocketAddress());
		try {
			sc.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void handleWrite(SelectionKey key) {
		SocketChannel sc = (SocketChannel) key.channel();
		prepareWrite(sc.socket().getRemoteSocketAddress());
		try {
			sc.register(selector, SelectionKey.OP_READ);
		} catch (ClosedChannelException e) {
			e.printStackTrace();
			try {
				key.channel().close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isOpen() {
		return open;
	}

	public SocketAddress getLocalSocketAddress() {
		return serverSocketChannel == null ? null : serverSocketChannel.socket() == null ? null : serverSocketChannel.socket().getLocalSocketAddress();
	}
}
