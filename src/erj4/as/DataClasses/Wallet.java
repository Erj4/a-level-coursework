package erj4.as.DataClasses;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import erj4.as.Main;

public class Wallet {
	private static ObservableList<Wallet> allWallets = FXCollections.observableArrayList();
	private final int ID;
	private String name;
	private ObservableList<Custom> customs = FXCollections.observableArrayList();
	private byte[] iv;

	public Wallet(int ID, byte[] encryptedName, byte[] iv){
		this.ID=ID;
		this.name=Main.encrypter.decrypt(encryptedName, iv);
		this.iv=iv;
		allWallets.add(this);
	}

	public Wallet(String name, byte[] iv) {
		this.name=name;
		this.iv=iv;
		int tempID=-1;
		PreparedStatement statement = Main.db.newStatement("INSERT INTO Wallets (encryptedWalletName, owner, iv) VALUES (?, ?, ?);");
		try {
			statement.setBytes(1, Main.encrypter.encrypt(name, iv));
			statement.setString(2, Main.db.getUsername());
			statement.setBytes(3, iv);
			statement.executeUpdate();
		}
		catch (SQLException e) {
			Main.fatalError(e, "Database access error, program must exit immediately");
		}
		try {
			PreparedStatement idStatement = Main.db.newStatement("SELECT last_insert_rowid() As 'ID' FROM Wallets;");
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
		allWallets.add(this);
	}

	public static ObservableList<Wallet> getAllWallets() {
		return allWallets;
	}

	public static void setAllWallets(ArrayList<Wallet> allWallets) {
		Wallet.allWallets.setAll(allWallets);
	}

	public int getID() {
		return ID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		PreparedStatement statement = Main.db.newStatement("UPDATE Wallets SET encryptedWalletName=? WHERE walletID=?");
		try {
			statement.setBytes(1, Main.encrypter.encrypt(name, this.iv));
			statement.setInt(2, this.ID);
			this.name = name;
		} catch (SQLException e) {
			Main.fatalError(e, "Database access error, program must exit immediately");
		}
	}

	public void addCustom(Custom custom) {
		PreparedStatement statement = Main.db.newStatement("INSERT INTO CustominWallet (customID, walletID) VALUES (?, ?);");
		try {
			statement.setInt(1, custom.getID());
			statement.setInt(2, this.getID());
			statement.executeUpdate();
			this.customs.add(custom);
		}
		catch (SQLException e) {
			Main.fatalError(e, "Database access error, program must exit immediately");
		}
	}
	
	public void addCustomFromDbStatement(Custom custom) {
		this.customs.add(custom);
	}

	public void removeCustom(Custom custom) {
		PreparedStatement statement = Main.db.newStatement("DELETE FROM CustominWallet WHERE customID=? AND walletID=?;");
		try {
			statement.setInt(1, custom.getID());
			statement.setInt(2, this.getID());
			statement.executeUpdate();
			this.customs.remove(custom);
		}
		catch (SQLException e) {
			Main.fatalError(e, "Database access error, program must exit immediately");
		}
	}

	public ObservableList<Custom> getCustoms(){
		return this.customs;
	}
	
	public void delete(){
		PreparedStatement delinkStatement = Main.db.newStatement("DELETE FROM CustominWallet WHERE walletID=?;");
		try {
			delinkStatement.setInt(1, this.getID());
			delinkStatement.executeUpdate();
			this.customs.clear();
		}
		catch (SQLException e) {
			Main.fatalError(e, "Database access error, program must exit immediately");
		}
		PreparedStatement deleteStatement = Main.db.newStatement("DELETE FROM Wallets WHERE walletID=?;");
		try {
			deleteStatement.setInt(1, this.getID());
			deleteStatement.executeUpdate();
			allWallets.remove(this);
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
