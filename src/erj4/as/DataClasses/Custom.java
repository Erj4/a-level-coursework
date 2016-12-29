package erj4.as.DataClasses;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import erj4.as.Main;

public class Custom {
	private static ObservableList<Custom> allCustoms = FXCollections.observableArrayList();
	private ObservableList<Data> data = FXCollections.observableArrayList();
	private final int ID;
	private Template template;
	private String name;
	private ArrayList<Wallet> wallets = new ArrayList<Wallet>();
	
	public Custom(int ID, Template template, byte[] encryptedName, byte[] iv) {
		this.ID=ID;
		this.setTemplate(template);
		this.setName(Main.encrypter.decrypt(encryptedName, iv));
		allCustoms.add(this);
	}
	
	public Custom(Template template, String name, byte[] iv) {
		this.template=template;
		this.name=name;
		int tempID = -1;
		PreparedStatement statement = Main.db.newStatement("INSERT INTO Customs (templateID, encryptedCustomName, iv) VALUES (?, ?, ?);");
		try {
			statement.setInt(1, template.getID());
			statement.setBytes(2, Main.encrypter.encrypt(name, iv));
			statement.setBytes(3, iv);
        	statement.executeUpdate();
		}
		catch (SQLException e) {
			Main.fatalError(e, "Database access error, program must exit immediately");
		}
        try {
        	PreparedStatement idStatement = Main.db.newStatement("SELECT last_insert_rowid() As 'ID' FROM Customs;");
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
		allCustoms.add(this);
	}

	public static ObservableList<Custom> getAllCustoms() {
		return allCustoms;
	}

	public static void setAllCustoms(ArrayList<Custom> allCustoms) {
		Custom.allCustoms.setAll(allCustoms);
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
	
	public ArrayList<Wallet> getWallets(){
		return wallets;
	}
	
	public void addToWallet(Wallet wallet){
		wallets.add(wallet);
	}
	
	public boolean removeWallet(Wallet wallet){
		if (wallets.contains(wallet)){
			wallets.remove(wallet);
			return true;
		}
		else return false;
	}
	
	public void addData(Data d){
		data.add(d);
	}
	
	public ObservableList<Data> getData(){
		return data;
	}
	
	@Override
	public String toString(){
		return name;
	}
}
