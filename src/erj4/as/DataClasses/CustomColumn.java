package erj4.as.DataClasses;

import java.util.ArrayList;

import erj4.as.Main;

public class CustomColumn {
	private static ArrayList<CustomColumn> allColumns;
	private final int ID;
	private Template template;
	private String name;
	//TODO column options eg. sensitive (hide with asterisks)
	
	public CustomColumn(int ID, Template template, byte[] encryptedColumnName, byte[] iv) {
		this.ID=ID;
		this.setTemplate(template);
		this.setName(Main.encrypter.decrypt(encryptedColumnName, iv));
		allColumns.add(this);
	}

	public static ArrayList<CustomColumn> getAllColumns() {
		return allColumns;
	}

	public static void setAllColumns(ArrayList<CustomColumn> allColumns) {
		CustomColumn.allColumns = allColumns;
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
