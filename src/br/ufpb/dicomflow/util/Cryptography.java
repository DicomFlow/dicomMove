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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

import javax.crypto.Cipher;
import javax.security.cert.X509Certificate;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Cryptography {
	private String CLIENT_KEYSTORE_TYPE = "JKS";
	private String inputFile = "C:/Users/Danilo/keystore.jks";
	private String CLIENT_KEYSTORE_PASSWORD = "pr0t0c0l0ap1";
	private String CLIENT_PRIVATE_KEY_PASSWORD = "pr0t0c0l0ap1";
	private String CLIENT_KEYSTORE_KEY_ALIAS = "dicomflow";

	private KeyStore.PrivateKeyEntry getKeyEntry() throws Exception {
		KeyStore clientKeyStore = KeyStore.getInstance(CLIENT_KEYSTORE_TYPE);
		InputStream is = new FileInputStream(new File(inputFile));
		clientKeyStore.load(is, CLIENT_KEYSTORE_PASSWORD.toCharArray());
		KeyStore.PasswordProtection keyPassword = new KeyStore.PasswordProtection(CLIENT_PRIVATE_KEY_PASSWORD.toCharArray());
		KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) clientKeyStore.getEntry(CLIENT_KEYSTORE_KEY_ALIAS, keyPassword);
		if (!X509Certificate.getInstance(pkEntry.getCertificate().getEncoded()).getNotAfter().after(new Date())) {
			throw new Exception("The identity certificate has expired");
		}
		return pkEntry;
	}

	private PrivateKey getPrivateKey() throws Exception {
		return getKeyEntry().getPrivateKey();
	}

	private PublicKey getPublicKey() throws Exception {
		return getKeyEntry().getCertificate().getPublicKey();
	}

	public String encrypt(String text) throws Exception {
		final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey());
		final byte[] cipherBytes = cipher.doFinal(text.getBytes());
		String cipherText = new BASE64Encoder().encode(cipherBytes);
		return cipherText;
	}

	public String decrypt(String cipherText) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, getPublicKey());
		byte[] results = cipher.doFinal(new BASE64Decoder()
				.decodeBuffer(cipherText));
		return new String(results);
	}

	public void test() throws Exception {
		String text = "hello world";
		String encryptedText = encrypt("test " + text);
		System.out.println("EncryptedText is: " + encryptedText);
		String decryptedText = decrypt(encryptedText);
		System.out.println("Result is: " + decryptedText);
	}

	public static void main(String[] args) {
		Cryptography p = new Cryptography();
		try {
			p.test();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
