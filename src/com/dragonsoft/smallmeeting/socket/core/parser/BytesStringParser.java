package com.dragonsoft.smallmeeting.socket.core.parser;

import java.io.UnsupportedEncodingException;
import java.net.SocketAddress;

public class BytesStringParser extends ObjectParser<String> {
	String charset = "UTF-8";

	public BytesStringParser() {
		super();
	}

	public BytesStringParser(String charSet) {
		super();
		this.charset = charSet;
	}

	@Override
	public String receive(SocketAddress socketAddresses, byte[] datas) {
		try {
			return new String(datas, charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new String(datas);
		}
	}

	@Override
	public byte[] getBytes(String data) {
		try {
			return data.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return data.getBytes();
		}
	}
}
