package erj4.as.DataClasses;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import erj4.as.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Data {
	private static ObservableList<Data> allValues = FXCollections.observableArrayList();
	private Column column;
	private byte[] encryptedData;
	private byte[] iv;
	
	public Data(Custom custom, Column column, byte[] encryptedData, byte[] iv) {
		custom.addData(this);
		this.setColumn(column);
		this.encryptedData=encryptedData;
		this.setIv(iv);
		allValues.add(this);
	}
	
	public Data(Custom custom, Column column, String data, byte[] iv) {
		this(custom, column, Main.encrypter.encrypt(data, iv), iv);
		PreparedStatement statement = Main.db.newStatement("INSERT INTO CustomData (customID, columnID, encryptedData, iv) VALUES (?, ?, ?, ?);");
		try {
			statement.setInt(1, custom.getID());
			statement.setInt(2, column.getID());
			statement.setBytes(3, Main.encrypter.encrypt(data, iv));
			statement.setBytes(4, iv);
			if (statement != null) {
				Main.db.runUpdate(statement);
			}
		}
		catch (SQLException e) {
			Main.fatalError(e, "Database access error, program must exit immediately");
		}
		
		allValues.add(this);
	}

	public static ObservableList<Data> getAllValues() {
		return allValues;
	}

	public static void setAllValues(ArrayList<Data> allValues) {
		Data.allValues.setAll(allValues);
	}

	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	public byte[] getEncryptedData() {
		return encryptedData;
	}

	public void setEncryptedData(byte[] encryptedData, Custom custom) {
		System.out.println("HERE NOW");
		PreparedStatement statement = Main.db.newStatement("UPDATE CustomData SET encryptedData=? WHERE customID=? AND columnID=?");
		try {
			statement.setBytes(1, encryptedData);
			statement.setInt(2, custom.getID());
			statement.setInt(3, this.getColumn().getID());
			statement.executeUpdate();
			this.encryptedData = encryptedData;
		} catch (SQLException e) {
			Main.fatalError(e, "Database access error, program must exit immediately");
		}
	}

	public byte[] getIv() {
		return iv;
	}

	public void setIv(byte[] iv) {
		this.iv = iv;
	}
	
	public void delete(Custom custom){
		PreparedStatement statement = Main.db.newStatement("DELETE FROM CustomData WHERE customID=? AND columnID=?;");
		try {
			statement.setInt(1, custom.getID());
			statement.setInt(2, this.column.getID());
			statement.executeUpdate();
		}
		catch (SQLException e) {
			Main.fatalError(e, "Database access error, program must exit immediately");
		}
	}
}
