package com.coursework.comsec;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

/**
 * 
 * @author Smit
 * Steps:
 * 1) openssl genrsa -des3 -out private.pem 2048
 * 2) openssl rsa -in private.pem -outform PEM -pubout -out public.pem
 * # convert private Key to PKCS#8 format (so Java can read it)
 * 3) openssl pkcs8 -topk8 -inform PEM -outform DER -in private.pem -out private_key.der -nocrypt
 * 4) openssl rsa -in private.pem -pubout -outform DER -out public_key.der
 * 
 * URL: https://blog.jonm.dev/posts/rsa-public-key-cryptography-in-java/
 */

public class RSAOpenSSLKeys {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		PrivateKey privateKey = getPrivateKey("private_key.der");
		PublicKey publicKey = getPublicKey("public_key.der");

		String message = args.length > 0 ? args[0] : "Hello World!";

		// test

		String encrypted = encrypt(message, publicKey);
		String decrypted = decrypt(encrypted, privateKey);

		System.out.println("Printing the message: " + message + " \n " + "Encrypted message " + encrypted + "\n"
				+ "Decrypted message " + decrypted);
		
		
		String signature = sign(privateKey, message);
		boolean isCorrect = verify(publicKey, message, signature);
		
		System.out.println("Generating signature with PrivateKey and Message: "+ signature + "\n"
				+" verfication results in: "+ isCorrect);
	}

	public static PrivateKey getPrivateKey(String filename) throws Exception {

		File f = new File(filename);
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);
		byte[] keyBytes = new byte[(int) f.length()];
		dis.readFully(keyBytes);
		dis.close();

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}

	public static PublicKey getPublicKey(String filename) throws Exception {

		File f = new File(filename);
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);
		byte[] keyBytes = new byte[(int) f.length()];
		dis.readFully(keyBytes);
		dis.close();

		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}

	public static String sign(PrivateKey privateKey, String message)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
		Signature sign = Signature.getInstance("SHA1withRSA");
		sign.initSign(privateKey);
		sign.update(message.getBytes("UTF-8"));
		return new String(Base64.encodeBase64(sign.sign()), "UTF-8");
	}

	public static boolean verify(PublicKey publicKey, String message, String signature)
			throws SignatureException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
		Signature sign = Signature.getInstance("SHA1withRSA");
		sign.initVerify(publicKey);
		sign.update(message.getBytes("UTF-8"));
		return sign.verify(Base64.decodeBase64(signature.getBytes("UTF-8")));
	}

	public static String encrypt(String rawText, PublicKey publicKey) throws IOException, GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return Base64.encodeBase64String(cipher.doFinal(rawText.getBytes("UTF-8")));
	}

	public static String decrypt(String cipherText, PrivateKey privateKey)
			throws IOException, GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return new String(cipher.doFinal(Base64.decodeBase64(cipherText)), "UTF-8");
	}

}
