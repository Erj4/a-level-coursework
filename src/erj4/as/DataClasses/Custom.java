package erj4.as.DataClasses;

import erj4.as.Main;

public class Custom {
	final int ID;
	Template template;
	String name;
	
	public Custom(int ID, Template template, byte[] encryptedName, byte[] iv){
		this.ID=ID;
		this.template=template;
		this.name=Main.encrypter.decrypt(encryptedName, iv);
	}
}
