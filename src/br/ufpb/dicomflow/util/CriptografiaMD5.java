/*
 * 	This file is part of DicomFlow.
 * 
 * 	DicomFlow is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 
 * 	You should have received a copy of the GNU General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

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