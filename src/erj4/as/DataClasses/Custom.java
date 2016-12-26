package erj4.as.DataClasses;

import java.util.ArrayList;
import javafx.collections.ObservableList;
import erj4.as.Main;

public class Custom {
	private static ObservableList<Custom> allCustoms;
	private final int ID;
	private Template template;
	private String name;
	private ArrayList<Wallet> wallets = new ArrayList<Wallet>();
	
	public Custom(int ID, Template template, byte[] encryptedName, byte[] iv){
		this.ID=ID;
		this.setTemplate(template);
		this.setName(Main.encrypter.decrypt(encryptedName, iv));
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
}
