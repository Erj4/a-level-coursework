package erj4.as.DataClasses;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CustomData {
	private static ObservableList<CustomData> allValues = FXCollections.observableArrayList();
	private Custom custom;
	private CustomColumn column;
	private byte[] encryptedData;
	private byte[] iv;
	
	public CustomData(Custom custom, CustomColumn column, byte[] encryptedData, byte[] iv) {
		this.setCustom(custom);
		this.setColumn(column);
		this.setEncryptedData(encryptedData);
		this.setIv(iv);
		allValues.add(this);
	}

	public static ObservableList<CustomData> getAllValues() {
		return allValues;
	}

	public static void setAllValues(ArrayList<CustomData> allValues) {
		CustomData.allValues.setAll(allValues);
	}

	public Custom getCustom() {
		return custom;
	}

	public void setCustom(Custom custom) {
		this.custom = custom;
	}

	public CustomColumn getColumn() {
		return column;
	}

	public void setColumn(CustomColumn column) {
		this.column = column;
	}

	public byte[] getEncryptedData() {
		return encryptedData;
	}

	public void setEncryptedData(byte[] encryptedData) {
		this.encryptedData = encryptedData;
	}

	public byte[] getIv() {
		return iv;
	}

	public void setIv(byte[] iv) {
		this.iv = iv;
	}
}
