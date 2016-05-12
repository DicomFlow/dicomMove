package br.ufpb.dicomflow.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 
 * Essa classe usa Base64 e permite a descriptografia, usando-se a chave
 * correta.
 * 
 */
public final class CriptografiaBase64 {
	private static SecretKey skey;
	private static KeySpec ks;
	private static PBEParameterSpec ps;
	private static final String algorithm = "PBEWithMD5AndDES";
	private static BASE64Encoder enc = new BASE64Encoder();
	private static BASE64Decoder dec = new BASE64Decoder();
	
	
	static {
		try {
			SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm);

			ps = new PBEParameterSpec(new byte[] { 3, 1, 4, 1, 5, 9, 2, 6 }, 20);

			// Esta e a chave que voce quer manter secreta.
			ks = new PBEKeySpec("EAlGeEen3/m8/YkO".toCharArray());

			skey = skf.generateSecret(ks);
		} catch (java.security.NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		} catch (java.security.spec.InvalidKeySpecException ex) {
			ex.printStackTrace();
		}
	}

	public static final String encrypt(final String text)
			throws BadPaddingException, NoSuchPaddingException,
			IllegalBlockSizeException, 
			NoSuchAlgorithmException, InvalidAlgorithmParameterException {

		final Cipher cipher = Cipher.getInstance(algorithm);
		try {
			cipher.init(Cipher.ENCRYPT_MODE, skey, ps);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return enc.encode(cipher.doFinal(text.getBytes()));
	}

	public static final String decrypt(final String text)
			throws BadPaddingException, NoSuchPaddingException,
			IllegalBlockSizeException, 
			NoSuchAlgorithmException, InvalidAlgorithmParameterException {

		final Cipher cipher = Cipher.getInstance(algorithm);
		
		String ret = null;
		try {
			cipher.init(Cipher.DECRYPT_MODE, skey, ps);
			ret = new String(cipher.doFinal(dec.decodeBuffer(text)));
		} catch (Exception ex) {
		}
		return ret;
	}

	public static void main(String[] args) throws Exception {
		String password = "raissa";
		String encoded = CriptografiaBase64.encrypt(password);
		System.out.println("\nString: " + password);
		System.out.println("String criptografada: " + encoded);
		System.out.println("String descriptografada: "
				+ CriptografiaBase64.decrypt(encoded));

		password = "123459";
		encoded = CriptografiaBase64.encrypt(password);
		System.out.println("\nString: " + password);
		System.out.println("String criptografada: " + encoded);
		System.out.println("String descriptografada: "
				+ CriptografiaBase64.decrypt(encoded));
	}
}