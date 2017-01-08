package erj4.as;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

public class Encrypter {
	private byte[] key;
	private Cipher cipher;
	private final static String cipherType = "AES";
	
	public Encrypter(String key, byte[] salt) {
		try {
			cipher = Cipher.getInstance(cipherType);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {Main.fatalError(e, e.getMessage());} // Should not occur
		this.key = Main.encrypterHash(key, salt);
	}
	
	public byte[] encrypt(String data, byte[] iv){
		byte[] byteData = data.getBytes();
		// SRC stackoverflow.com/questions/1205135/how-to-encrypt-string-in-java
		SecretKeySpec keySpec = new SecretKeySpec(key, cipherType);
		//IvParameterSpec ivSpec = new IvParameterSpec(iv);
		try {
			cipher.init(Cipher.ENCRYPT_MODE, keySpec/*, ivSpec*/);
		} catch (InvalidKeyException/* | InvalidAlgorithmParameterException */e) {Main.fatalError(e, e.getMessage());} // Should not occur
		byte[] encryptedData = new byte[cipher.getOutputSize(byteData.length)];
		try {
			int encryptedLength = cipher.update(byteData, 0, byteData.length, encryptedData, 0);
			encryptedLength += cipher.doFinal(encryptedData, encryptedLength);
		} catch (IllegalBlockSizeException | ShortBufferException | BadPaddingException e) {Main.fatalError(e, e.getMessage());} // Should not occur
		return encryptedData;
	}
	
	public String decrypt(byte[] data, byte[] iv) {
		SecretKeySpec keySpec = new SecretKeySpec(key, cipherType);
		/*IvParameterSpec ivSpec = new IvParameterSpec(iv);*/
		try {
			cipher.init(Cipher.DECRYPT_MODE, keySpec/*, ivSpec*/);
		} catch (InvalidKeyException/* | InvalidAlgorithmParameterException*/ e) {Main.fatalError(e, e.getMessage());} // Should not occur
		byte[] decryptedData = new byte[cipher.getOutputSize(data.length)];
		try {
			int decryptedLength = cipher.update(data, 0, data.length, decryptedData, 0);
			decryptedLength += cipher.doFinal(decryptedData, decryptedLength);
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
