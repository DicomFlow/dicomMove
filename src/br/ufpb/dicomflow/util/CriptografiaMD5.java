package br.ufpb.dicomflow.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * Essa classe pode ser usada tanto com o algoritmo MD5 como com o Sha-1. Uma
 * vez criptografado um dado, nao sera possivel conhecer o valor original. Para
 * saber se este dado corresponde a algum outro, voce deve comparar a string
 * resultante da criptografia de ambos.
 * 
 */
public class CriptografiaMD5 {

	private static MessageDigest md = null;

	static {
		try {
			// Pode-se usar o algoritmo Sha-1 tambem,
			// basta bustituir na linha abaixo.
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
	}

	private static char[] hexCodes(byte[] text) {
		char[] hexOutput = new char[text.length * 2];
		String hexString;
		for (int i = 0; i < text.length; i++) {
			hexString = "00" + Integer.toHexString(text[i]);
			hexString.toUpperCase().getChars(hexString.length() - 2,
					hexString.length(), hexOutput, i * 2);
		}
		return hexOutput;
	}

	public static String criptografar(String pwd) {
		if (md != null) {
			return new String(hexCodes(md.digest(pwd.getBytes())));
		}
		return null;
	}

	public static void main(String[] args) {
		String senha = "123456";
		System.out.println(CriptografiaMD5.criptografar(senha));

		senha = "132546";
		System.out.println(CriptografiaMD5.criptografar(senha));
	}
}