package erj4.as.DataClasses;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import erj4.as.Main;

public class Column {
	private static ObservableList<Column> allColumns = FXCollections.observableArrayList();
	private final int ID;
	private String name;
	private boolean isPassword;
	//TODO column options eg. sensitive (hide with asterisks)
	
	public Column(int ID, Template template, byte[] encryptedColumnName, byte[] iv, boolean isPassword) {
		this.ID=ID;
		this.name=Main.encrypter.decrypt(encryptedColumnName, iv);
		this.setPassword(isPassword);
		template.addColumn(this);
	}

	public static ObservableList<Column> getAllColumns() {
		return allColumns;
	}

	public static void setAllColumns(ArrayList<Column> allColumns) {
		Column.allColumns.setAll(allColumns);
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

	public boolean isPassword() {
		return isPassword;
	}

	public void setPassword(boolean isPassword) {
		this.isPassword = isPassword;
	}
	
	@Override
	public String toString(){
		return name;
	}
}
