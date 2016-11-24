package erj4.as.DataClasses;

import erj4.as.Main;

public class CustomColumn {
	final int ID;
	Template template;
	String name;
	//TODO column options eg. sensitive (hide with asterisks)
	
	public CustomColumn(int ID, Template template, byte[] encryptedColumnName, byte[] iv) {
		this.ID=ID;
		this.template = template;
		this.name = Main.encrypter.decrypt(encryptedColumnName, iv);
	}
}
