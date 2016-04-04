package com.dragonsoft.smallmeeting.socket.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptionTool {
	public static final String MD5 = "md5";
	public static final String MD2 = "md2";
	public static final String SHA1 = "SHA-1";
	public static final String SHA256 = "SHA-256";
	public static final String SHA384 = "SHA-384";
	public static final String SHA512 = "SHA-512";

	public static final String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs;
	}

	/**
	 * 单向信息加密(信息摘要)，支持：md5、md2、SHA(SHA-1，SHA1)、SHA-256、SHA-384、SHA-512, <br>
	 * 生成时间：2014年5月2日 上午11:13:44 <br>
	 * 返回值：String 加密后的密文 <br>
	 * @param src 传入加密字符串(明文) <br>
	 * @param method 指定算法(md5、md2、SHA(SHA-1，SHA1)、SHA-256、SHA-384、SHA-512) <br>
	 * @return
	 */
	public static String encoding(String src, String method) {

		try {
			// 信息摘要算法
			MessageDigest md5 = MessageDigest.getInstance(method);
			md5.update(src.getBytes());
			byte[] encoding = md5.digest();
			// 使用64进行编码，一避免出现丢数据情景
			return new String(byte2hex(encoding));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e + "加密失败！！");
		}

	}
}