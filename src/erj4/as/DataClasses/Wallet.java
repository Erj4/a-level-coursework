package erj4.as.DataClasses;

import erj4.as.Main;

public class Wallet {
	final int ID;
	String name;
	
	public Wallet(int ID, byte[] encryptedName, byte[] iv){
		this.ID=ID;
		this.name=Main.encrypter.decrypt(encryptedName, iv);
	}
}
