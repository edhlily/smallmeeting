package com.dragonsoft.smallmeeting.socket.util;

public class Logger {
	private static boolean enable = true;
	private static final String TAG = "Socket";

	public static void setEnableLog(boolean b) {
		Logger.enable = b;
	}

	public static void d(String tag, String msg) {
		out(tag + " : " + msg);
	}

	public static void e(String tag, String msg) {
		err(tag + " : " + msg);
	}

	public static void out(String msg) {
		if (enable) {
			System.out.println(msg);
		}
	}

	public static void err(String msg) {
		if (enable) {
			System.err.println(msg);
		}
	}

	public static void d(String msg) {
		out(TAG + " : " + msg);
	}

	public static void e(String msg) {
		err(TAG + " : " + msg);
	}
}
