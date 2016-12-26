package erj4.as.DataClasses;

import java.util.ArrayList;

import erj4.as.Main;

public class Template {
	private static ArrayList<Template> allTemplates;
	private final int ID;
	private String name;
	
	public Template(int ID, byte[] encryptedName, byte[] iv){
		this.ID=ID;
		this.name=Main.encrypter.decrypt(encryptedName, iv);
		allTemplates.add(this);
	}
	
	public static ArrayList<Template> getAllTemplates(){
		return allTemplates;
	}
	
	public static void setAllTemplates(ArrayList<Template> allTemplates){
		Template.allTemplates=allTemplates;
	}

	public int getID() {
		return ID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
