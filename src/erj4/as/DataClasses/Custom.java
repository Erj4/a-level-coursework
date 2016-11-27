package erj4.as.DataClasses;

import java.util.ArrayList;

import erj4.as.Main;

public class Custom {
	private static ArrayList<Custom> allCustoms;
	private final int ID;
	private Template template;
	private String name;
	
	public Custom(int ID, Template template, byte[] encryptedName, byte[] iv){
		this.ID=ID;
		this.setTemplate(template);
		this.setName(Main.encrypter.decrypt(encryptedName, iv));
	}

	public static ArrayList<Custom> getAllCustoms() {
		return allCustoms;
	}

	public static void setAllCustoms(ArrayList<Custom> allCustoms) {
		Custom.allCustoms = allCustoms;
	}

	public int getID() {
		return ID;
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
