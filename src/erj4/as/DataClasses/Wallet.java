package erj4.as.DataClasses;

import java.util.ArrayList;

import erj4.as.Main;

public class Wallet {
	private static ArrayList<Wallet> allWallets;
	private final int ID;
	private String name;
	
	public Wallet(int ID, byte[] encryptedName, byte[] iv){
		this.ID=ID;
		this.setName(Main.encrypter.decrypt(encryptedName, iv));
		allWallets.add(this);
	}

	public static ArrayList<Wallet> getAllWallets() {
		return allWallets;
	}

	public static void setAllWallets(ArrayList<Wallet> allWallets) {
		Wallet.allWallets = allWallets;
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
