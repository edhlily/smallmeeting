package com.dragonsoft.smallmeeting.socket.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class SerializableUtil {
	
	public static <T extends Serializable> byte[] toBytes(T object) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (IOException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T toObject(byte[] bytes, int offset, int length) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes, offset, length);
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(bais);
			T object = (T) ois.readObject();
			return object;
		} finally {
			try {
				ois.close();
			} catch (Exception e) {
			}
		}
	}

	public static byte[] receiveData(SocketChannel socketChannel) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		byte[] bytes;
		try {
			int size = 0;
			while ((size = socketChannel.read(buffer)) > 0) {
				buffer.flip();
				bytes = new byte[size];
				buffer.get(bytes);
				baos.write(bytes);
				buffer.clear();
			}
			bytes = baos.toByteArray();
		} finally {
			try {
				baos.close();
			} catch (Exception ex) {
			}
		}
		return bytes;
	}

	public static byte[] receiveData(DatagramPacket packet) throws IOException {
		return Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
	}
}