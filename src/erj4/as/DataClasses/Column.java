package erj4.as.DataClasses;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import erj4.as.Main;

public class Column {
	private static ObservableList<Column> allColumns = FXCollections.observableArrayList();
	private final int ID;
	private String name;
	private boolean isPassword;
	byte[] iv;

	public Column(int ID, Template template, byte[] encryptedColumnName, byte[] iv, boolean isPassword) {
		this.ID=ID;
		this.name=Main.encrypter.decrypt(encryptedColumnName, iv);
		this.iv=iv;
		this.isPassword=isPassword;
		template.addColumn(this);
	}

	public Column(Template template, String name, byte[] iv, boolean isPassword) {
		this.name=name;
		this.isPassword=isPassword;
		this.iv=iv;
		template.addColumn(this);
		int tempID = -1;
		PreparedStatement statement = Main.db.newStatement("INSERT INTO CustomColumns (encryptedColumnName, templateID, iv, isPassword) VALUES (?, ?, ?, ?);");
		try {
			statement.setBytes(1, Main.encrypter.encrypt(name, iv));
			statement.setInt(2, template.getID());
			statement.setBytes(3, iv);
			statement.setBoolean(4, isPassword);
        	statement.executeUpdate();
		}
		catch (SQLException e) {
			Main.fatalError(e, "Database access error, program must exit immediately");
		}
        try {
        	PreparedStatement idStatement = Main.db.newStatement("SELECT last_insert_rowid() As 'ID' FROM CustomColumns;");
            if (idStatement != null) {
                ResultSet results = Main.db.runQuery(idStatement);
                if (results != null) {
                    tempID=results.getInt("ID");
                }
            }
        }
        catch (SQLException e) {
            Main.fatalError(e, "Database access error, program must exit immediately");
        }
		this.ID=tempID;
		allColumns.add(this);
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
		PreparedStatement statement = Main.db.newStatement("UPDATE CustomColumns SET encryptedColumnName=? WHERE columnID=?");
		try {
			statement.setBytes(1, Main.encrypter.encrypt(name, this.iv));
			statement.setInt(2, this.ID);
			this.name = name;
		} catch (SQLException e) {
			Main.fatalError(e, "Database access error, program must exit immediately");
		}
	}

	public boolean isPassword() {
		return isPassword;
	}

	public void setIsPassword(boolean isPassword) {
		PreparedStatement statement = Main.db.newStatement("UPDATE CustomColumns SET isPassword=? WHERE columnID=?");
		try {
			statement.setBoolean(1, this.isPassword);
			statement.setInt(2, this.ID);
			this.isPassword = isPassword;
		} catch (SQLException e) {
			Main.fatalError(e, "Database access error, program must exit immediately");
		}
	}

	@Override
	public String toString(){
		return name;
	}
}
