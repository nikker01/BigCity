package com.doubleservice.bigcitynavigation;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;
import android.util.Log;

public class AES {

	//private String stringKey = "DScity2014passds";
	private String stringKey = "5f9oI8rTmmUlXFbo";

	public void selfDecode(String str) throws Exception {

		// Log.i("AES", "Try selfDecode");

		try {

			String key = stringKey;
			byte[] raw = key.getBytes("UTF-8");

			SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");

			Cipher cipher;

			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, keySpec);
			String s = new String(cipher.doFinal(Base64.decode(str,
					android.util.Base64.NO_WRAP)));

			//Log.i("AES", "selfDecode s = " + s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String decrypt_AES(String src) throws Exception {
		String key = stringKey;
		//Log.i("srcForDecrpt_AES", src);
		try {
			if (key == null) {
				Log.e("decrypt_aes_error", "key == null");
				return null;
			}

			if (key.length() != 16) {
				Log.e("decrypt_aes_error", "key.length() != 16");
				return null;
			}

			byte[] raw = key.getBytes("UTF-8");

			SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");

			// Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

			IvParameterSpec iv = new IvParameterSpec(
					"0102030405060708".getBytes());

			cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
			byte[] encrypted1 = Base64.decode(src, Base64.DEFAULT);
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original, "UTF-8");

			//Log.i("decrypt_aes_result", originalString);

			return originalString;
		} catch (Exception ex) {
			Log.e("decrypt_aes_error", "Exception");

			return null;
		}

	}

	public String encrypt_AES(String src) throws Exception {
		String key = stringKey;
		if (key == null) {
			Log.e("encrypt_aes_error", "Key���ର��");
			return null;
		}

		if (key.length() != 16) {
			Log.e("encrypt_aes_error", "Key��פ��O16��");
			return null;
		}

		src = toUtf8(src);

		byte[] raw = key.getBytes();
		SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
		// Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Cipher cipher = Cipher.getInstance("AES/CBC/ZeroBytePadding"); // �t��k/�Ҧ�/�ɼƤ覡
		IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
		byte[] encrypted = cipher.doFinal(src.getBytes());
		Log.v("encrypt_aes_result", encrypted.toString());

		String stringResult_AES = Base64.encodeToString(encrypted,
				Base64.DEFAULT);

		String stringReturn = "";
		for (char c : stringResult_AES.toCharArray()) {
			if (c == '+') {
				c = '!';
			}
			stringReturn = stringReturn + c;
		}

		return stringReturn;
	}

	public String decrypt_AES_ParsingSomeChar(String src) throws Exception {
		String key = stringKey;

		// Log.i("srcForDecrpt_AES", src);

		String stringNormal_AES = "";
		for (char c : src.toCharArray()) {
			if (c == '!') {
				c = '+';
			}
			stringNormal_AES = stringNormal_AES + c;
		}

		src = stringNormal_AES;

		try {
			if (key == null) {
				Log.e("decrypt_aes_error", "Key null");
				return null;
			}

			if (key.length() != 16) {
				Log.e("decrypt_aes_error", "Key length != 16");
				return null;
			}

			byte[] raw = key.getBytes("UTF-8");

			SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
			// Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding")
			Cipher cipher = Cipher.getInstance("AES/CBC/ZeroBytePadding"); // �t��k/�Ҧ�/�ɼƤ覡
			IvParameterSpec iv = new IvParameterSpec(
					"0102030405060708".getBytes());
			cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
			byte[] encrypted1 = Base64.decode(src, Base64.DEFAULT);
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original);
			//Log.i("decrypt_aes_result", originalString);

			return originalString;
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e("decrypt_aes_error", "Exception");

			return null;
		}

	}

	public String PlusToExclamationMark(String src) {
		String stringReturn = "";
		for (char c : src.toCharArray()) {
			if (c == '+') {
				c = '!';
			}
			stringReturn = stringReturn + c;
		}

		return stringReturn;
	}

	private static String toUtf8(String str) throws Exception {
		Log.d("initInputString", str);
		String stringUtf8 = new String(str.getBytes("UTF-8"), "UTF-8");
		Log.d("utf8MethodString", stringUtf8);
		return stringUtf8;
	}

}
