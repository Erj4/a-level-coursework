package erj4.as.DataClasses;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import erj4.as.Main;

public class Template {
	private static ObservableList<Template> allTemplates = FXCollections.observableArrayList();
	private ObservableList<Column> columns = FXCollections.observableArrayList();
	private final int ID;
	private String name;
	private byte[] iv;
	
	public Template(int ID, byte[] encryptedName, byte[] iv){
		this.ID=ID;
		this.name=Main.encrypter.decrypt(encryptedName, iv);
		this.iv=iv;
		allTemplates.add(this);
	}
	
	public Template(String name, byte[] iv){
		this.name=name;
		this.iv=iv;
		int tempID = -1;
		PreparedStatement insertStatement = Main.db.newStatement("INSERT INTO Templates (encryptedTemplateName, owner, iv) VALUES (?, ?, ?);");
        try {
        	insertStatement.setBytes(1, Main.encrypter.encrypt(name, iv));
        	insertStatement.setString(2, Main.db.getUsername());
        	insertStatement.setBytes(3, iv);
        	insertStatement.executeUpdate();
        	}
        catch (SQLException e) {
        	Main.fatalError(e, "Database access error, program must exit immediately");
        }
        try {
        	PreparedStatement idStatement = Main.db.newStatement("SELECT last_insert_rowid() As 'ID' FROM Templates;");
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
        allTemplates.add(this);
	}
	
	public static ObservableList<Template> getAllTemplates(){
		return allTemplates;
	}
	
	public static void setAllTemplates(ArrayList<Template> allTemplates){
		Template.allTemplates.setAll(allTemplates);
	}

	public int getID() {
		return ID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		PreparedStatement statement = Main.db.newStatement("UPDATE Templates SET encryptedTemplateName=? WHERE templateID=?");
		try {
			statement.setBytes(1, Main.encrypter.encrypt(name, this.iv));
			statement.setInt(2, this.ID);
			statement.executeUpdate();
			this.name = name;
		} catch (SQLException e) {
			Main.fatalError(e, "Database access error, program must exit immediately");
		}
	}

	public void addColumn(Column c) {
		columns.add(c);
	}
	
	public ObservableList<Column> getColumns(){
		return columns;
	}
	
	public void delete(){
		PreparedStatement statement = Main.db.newStatement("DELETE FROM Templates WHERE templateID=?;");
		try {
			statement.setInt(1, this.getID());
			statement.executeUpdate();
			allTemplates.remove(this);
		}
		catch (SQLException e) {
			Main.fatalError(e, "Database access error, program must exit immediately");
		}
	}
	
	@Override
	public String toString(){
		return name;
	}
}
