package br.ufpb.dicomflow.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

/**
 * This class defines common routines for encoding data in CDN requests.
 */
public class Encoding {
	
public static String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz+-";
	
	/*
	 *  Incrementa um numero representado por um string usando o alfabeto Base64
	 * Atencao: nao aumenta o numero de simbolos no numero. Ou seja, quando o numero precisa de mais um digito, ele volta para zero.
	 * Exemplo: "00", "01", "02", ..., "+z", "+-", "--", "00"
	 * 
	 * */
	public static String incrementBase64(String numStr) {		
		
		char[] num = numStr.toCharArray();
		
		int alphabetLength = alphabet.length();
		int len = num.length;
		
		while (len-- > 0) {
			int index = 0;

			//Encontra a posição do simbolo de mais baixa ordem no vetor de simbolos
			if (num[len] == '-') {
				index = 63;
			} else if (num[len] == '+') {
				index = 62;
			} else if (num[len] >= 'a') {
				index = num[len] - 'a' + 36;
			} else if (num[len] >= 'A') {
				index = num[len] - 'A' + 10;
			} else {
				index = num[len] - '0';
			}

			//Incrementa o simbolo							
			if (index == alphabetLength - 1) {
				//Acabaram os simbolos: vamos fazer o "vai 1" (carry)
				num[len] = '0';
			} else {
				//Incrementa o simbolo (pega o proximo simbolo disponivel)
				num[len] = alphabet.charAt(index + 1);
				break;
			}

		}
						
		return new String(num);
		
	}

	/**
	 * Performs base64-encoding of input bytes.
	 * 
	 * @param rawData
	 *            * Array of bytes to be encoded.
	 * @return * The base64 encoded string representation of rawData.
	 */
	public static String EncodeBase64(byte[] rawData) {
		return Base64.encodeBase64String(rawData);
	}
	
	
	/**
	 * Performs MD5-encoding of input String.
	 * 
	 * @param data
	 *            * String to be encoded
	 * @return * The MD5 encoded string representation of data.
	 */
	public static String EncodeMD5(String data) {
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(data.getBytes(), 0, data.length());  
			String hashedPass = new BigInteger(1,messageDigest.digest()).toString(16);  
			if (hashedPass.length() < 32) {
			   hashedPass = "0" + hashedPass; 
			}
			
			return hashedPass;

		} catch (NoSuchAlgorithmException e) {
			return null;
		}  
	}
	
	

}