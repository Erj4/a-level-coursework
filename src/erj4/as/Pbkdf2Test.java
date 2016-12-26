package erj4.as;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Pbkdf2Test {

	public static void main(String[] args) {
		long time=0;
		int iFinal=0;
		for(int i=1000;time<1000;i+=1000){
			long start=System.currentTimeMillis();
			System.out.println(i+" iterations produced "+new String(PBKDF2(i))+" in "+(System.currentTimeMillis()-start)+"ms");
		}
	}
	
	public static byte[] PBKDF2(int iterations){
		try {
			final int keyLength = 512;
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			PBEKeySpec spec = new PBEKeySpec(null, new byte[1], iterations, keyLength);
			SecretKey key;
			key = skf.generateSecret(spec);
			return key.getEncoded();
		
		}
		catch(Exception e) {
			Main.fatalError(e, "Fatal exception in hashing function, so program must exit immediately");
			return null;
		}
	}

}
