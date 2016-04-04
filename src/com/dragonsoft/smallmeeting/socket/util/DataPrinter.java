package com.dragonsoft.smallmeeting.socket.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.DatagramPacket;

import com.dragonsoft.smallmeeting.socket.core.message.InvokeMessage;

public class DataPrinter {
	public static String asString(Serializable data) {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("Message[");
			java.lang.reflect.Field[] flds = data.getClass().getFields();
			if (flds != null) {
				for (Field f : flds) {
					sb.append(f.getName() + ":" + f.get(data)).append(";");
					System.out.println(f.getName() + " - " + f.get(data));
				}
			}
			sb.append("]");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static void print(InvokeMessage<?> msg) {
		Logger.out(asString(msg));
	}

	public static String asString(DatagramPacket packet) {
		return new String(packet.getData(), 0, packet.getLength());
	}

	public static void print(DatagramPacket packet) {
		Logger.out(asString(packet));
	}
}
