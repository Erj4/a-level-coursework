package erj4.as;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encrypter {
	private String key; // TODO test very long keys
	private Cipher cipher;
	private String cipherType = "AES";
	
	public Encrypter(String key) {
		try {
			cipher = Cipher.getInstance(cipherType);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {Main.fatalError(e, e.getMessage());} // Should not occur
		this.key = key;
	}
	
	public String encrypt(String data, String iv){
		byte[] byteData = data.getBytes();
		byte[] byteIv = iv.getBytes();
		byte[] byteKey = key.getBytes();
		// SRC stackoverflow.com/questions/1205135/how-to-encrypt-string-in-java
		SecretKeySpec keySpec = new SecretKeySpec(byteKey, cipherType);
		IvParameterSpec ivSpec = new IvParameterSpec(byteIv);
		try {
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {Main.fatalError(e, e.getMessage());} // Should not occur
		byte[] encryptedData = new byte[cipher.getOutputSize(byteData.length)];
		try {
			int enc_len = cipher.update(byteData, 0, byteData.length, encryptedData, 0);
			enc_len += cipher.doFinal(encryptedData, enc_len);
		} catch (IllegalBlockSizeException | ShortBufferException | BadPaddingException e) {Main.fatalError(e, e.getMessage());} // Should not occur
		try{
			return new String(encryptedData, "UTF-8");
		}
		catch(UnsupportedEncodingException e){
			Main.fatalError(e, e.getMessage());
			return null;
		}
	}
	
	public String decrypt(String data, String iv) {
		byte[] byteData = data.getBytes();
		byte[] byteIv = iv.getBytes();
		byte[] byteKey = key.getBytes();
		// SRC stackoverflow.com/questions/1205135/how-to-encrypt-string-in-java
		SecretKeySpec keySpec = new SecretKeySpec(byteKey, cipherType);
		IvParameterSpec ivSpec = new IvParameterSpec(byteIv);
		try {
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {Main.fatalError(e, e.getMessage());} // Should not occur
		byte[] decryptedData = new byte[cipher.getOutputSize(byteData.length)];
		try {
			int dec_len = cipher.update(byteData, 0, byteData.length, decryptedData, 0);
			dec_len += cipher.doFinal(decryptedData, dec_len);
		} catch (IllegalBlockSizeException | ShortBufferException | BadPaddingException e) {
			e.printStackTrace();
		}
		try{
			return new String(decryptedData, "UTF-8");
		}
		catch(UnsupportedEncodingException e){
			System.err.println(e);
			return null;
		}
	}
}
