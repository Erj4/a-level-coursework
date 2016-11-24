package erj4.as.DataClasses;

import erj4.as.Main;

public class Template {
	final int ID;
	String name;
	
	public Template(int ID, byte[] encryptedName, byte[] iv){
		this.ID=ID;
		this.name=Main.encrypter.decrypt(encryptedName, iv);
	}
}
