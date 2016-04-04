package com.dragonsoft.smallmeeting.socket.tcp.nio;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import com.dragonsoft.smallmeeting.socket.tcp.nio.receiver.Receiver;
import com.dragonsoft.smallmeeting.socket.util.Logger;

public class NioClient<T extends Serializable> implements Runnable, ClientNioListener<T> {
	private static final String TAG = "Client";
	private static final long INITIAL_RECONNECT_INTERVAL = 0;
	private static final long MAXIMUM_RECONNECT_INTERVAL = 1500;
	private int read_buffer_size = 8192;
	private int write_buffer_size = 8192;
	private long reconnectInterval = INITIAL_RECONNECT_INTERVAL;
	private final Thread thread = new Thread(this);
	private SocketAddress address;
	private Selector selector;
	private SocketChannel channel;
	private Receiver<T> receiver;
	private ClientNioListener<T> nioListener;

	public NioClient(String ip, int port, Receiver<T> receiver) {
		address = new InetSocketAddress(ip, port);
		this.receiver = receiver;
	}

	public NioClient(SocketAddress socketAddress, Receiver<T> receiver) {
		address = socketAddress;
		this.receiver = receiver;
	}

	public NioListener<T> getNioListener() {
		return nioListener;
	}

	public void setNioListener(ClientNioListener<T> nioListener) {
		this.nioListener = nioListener;
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
		Logger.d(TAG, "starting event loop:");
		thread.start();
	}

	public void stop() {
		Logger.d(TAG, "stopping event loop");
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

	private void close() {
		try {
			if (channel != null)
				channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (selector != null)
				selector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void configureChannel(SocketChannel channel) throws IOException {
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

	@Override
	public void run() {
		open = true;
		Logger.d(TAG, "event loop running");
		while (open && !Thread.interrupted()) {
			try {
				selector = Selector.open();
				channel = SocketChannel.open();
				configureChannel(channel);
				channel.connect(address);
				channel.register(selector, SelectionKey.OP_CONNECT);
				if (reconnectInterval == INITIAL_RECONNECT_INTERVAL) {
					openSuccess();
				}
			} catch (IOException e1) {
				openFailed();
				close();
				open = false;
				e1.printStackTrace();
				return;
			}
			try {
				while (!thread.isInterrupted() && channel.isOpen()) {
					Logger.d(TAG, "select");
					if (selector.select() > 0) {
						Iterator<SelectionKey> itr = selector.selectedKeys().iterator();
						while (itr.hasNext()) {
							SelectionKey key = (SelectionKey) itr.next();
							if (key.isReadable())
								processRead(key);
							if (key.isWritable())
								processWrite(key);
							if (key.isConnectable())
								processConnect(key);
							if (key.isAcceptable())
								;
							itr.remove();
						}
					}
				}
			} catch (ConnectedFailedException e) {

			} catch (IOException e) {
				disConnect(channel.socket().getRemoteSocketAddress());
				e.printStackTrace();
				Logger.d(TAG, "chanel exception");
			}

			try {
				Thread.sleep(reconnectInterval);
				if (reconnectInterval < MAXIMUM_RECONNECT_INTERVAL) {
					reconnectInterval += 500;
				} else {
					stop();
				}
				Logger.d(TAG, "reconnecting to " + address);
			} catch (InterruptedException e) {
				break;
			}
		}
		open = false;
		close();
		closed();
		Logger.d(TAG, "event loop terminated");
	}

	private void processConnect(SelectionKey key) throws ConnectedFailedException {
		try {
			if (channel.isConnectionPending()) {
				channel.finishConnect();
			}
			Logger.d(TAG, "connected to " + address);
			channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			reconnectInterval = INITIAL_RECONNECT_INTERVAL;
			connected(channel.socket().getRemoteSocketAddress());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ConnectedFailedException();
		}
	}

	private void processRead(SelectionKey key) throws IOException {
		SocketChannel sc = (SocketChannel) key.channel();
		int len = receiver.receive(sc);
		if (len < 0) {
			throw new IOException("peer closed read channel");
		}
	}

	private void processWrite(SelectionKey key) throws IOException {
		prepareWrite(channel.socket().getRemoteSocketAddress());
		channel.register(selector, SelectionKey.OP_READ);
	}

	public SocketAddress getAddress() {
		return address;
	}

	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}

	@Override
	public void openSuccess() {
		Logger.e(TAG, "openSuccess !");
		if (nioListener != null) {
			nioListener.openSuccess();
		}
	}

	@Override
	public void openFailed() {
		Logger.e(TAG, "openFailed !");
		if (nioListener != null) {
			nioListener.openFailed();
		}
	}

	@Override
	public void prepareWrite(SocketAddress socketAddress) {
		Logger.e(TAG, "prepareWrite : ");
		if (nioListener != null) {
			nioListener.prepareWrite(socketAddress);
		}
	}

	@Override
	public void connected(SocketAddress socketAddress) {
		Logger.e(TAG, "connected : ");
		if (nioListener != null) {
			nioListener.connected(socketAddress);
		}
	}

	@Override
	public void sendSuccess(SocketAddress socketAddress, T data) {
		Logger.e(TAG, "sendSuccess : " + data);
		if (nioListener != null) {
			nioListener.sendSuccess(socketAddress, data);
		}
	}

	@Override
	public void sendFailed(SocketAddress socketAddress, T data) {
		Logger.e(TAG, "sendFailed : " + data);
		if (nioListener != null) {
			nioListener.sendFailed(socketAddress, data);
		}
	}

	@Override
	public void closed() {
		Logger.e(TAG, " closed ! ");
		if (nioListener != null) {
			nioListener.closed();
		}
	}

	@Override
	public void disConnect(SocketAddress socketAddress) {
		Logger.e(TAG, " disConnect:" + socketAddress);
		if (nioListener != null) {
			nioListener.disConnect(socketAddress);
		}
	}

	public void send(ByteBuffer buffer) throws IOException {
		while (buffer.hasRemaining()) {
			channel.write(buffer);
		}
	}

	public void send(T data) {
		try {
			if (channel.isConnected()) {
				byte[] bytes = receiver.getDispatcher().getBytes(data);
				Logger.d(TAG, "send : " + bytes.length);
				ByteBuffer buffer = ByteBuffer.wrap(bytes);
				send(buffer);
				sendSuccess(channel.socket().getRemoteSocketAddress(), data);
			}
		} catch (IOException e) {
			e.printStackTrace();
			sendFailed(channel.socket().getRemoteSocketAddress(), data);
		}
	}

	public boolean isOpen() {
		return open;
	}

	public SocketAddress getSocketAddress() {
		return address;
	}

	public SocketAddress getLocalSocketAddress() {
		return channel == null ? null : channel.socket() == null ? null : channel.socket().getLocalSocketAddress();
	}
}
