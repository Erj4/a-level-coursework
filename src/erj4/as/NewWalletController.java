package erj4.as;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.controlsfx.control.CheckListView;

import erj4.as.DataClasses.Custom;
import erj4.as.DataClasses.Wallet;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NewWalletController extends VBox implements Initializable{

	@FXML
	public CheckListView<Custom> inputContainer;

	@FXML
	public TextField nameField;

	@FXML
	public Button addButton;

	private Wallet wallet;

	public NewWalletController() {
		this.setUserData(null);
	}

	public void editMode(Wallet w) {
		this.wallet = w;
		for (Custom c:w.getCustoms()) inputContainer.getCheckModel().check(c);
		nameField.setText(w.getName());
		addButton.setText("SAVE WALLET");
		addButton.setOnAction(e->saveUpdate());
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		inputContainer.getItems().setAll(Custom.getAllCustoms().sorted());
	}

	@FXML
	private void addWalletFromScene() {
		Wallet w = new Wallet(nameField.getText(), Main.getIV());
		for(Custom x:inputContainer.getCheckModel().getCheckedItems()){
			w.addCustom(x);
		}
		this.setUserData(w);
		((Stage) inputContainer.getScene().getWindow()).close();
	}

	public void saveUpdate(){
		wallet.setName(nameField.getText());
		ArrayList<Custom> toRemove = new ArrayList<>();
		
		for (Custom x:wallet.getCustoms()) if (!inputContainer.getCheckModel().getCheckedItems().contains(x)) toRemove.add(x);
		for(Custom x:toRemove) wallet.removeCustom(x); // Remove new unchecked
		
		for (Custom y:inputContainer.getCheckModel().getCheckedItems()) if (!wallet.getCustoms().contains(y)) wallet.addCustom(y); // Add new checked
		
		this.setUserData(wallet);
		((Stage) inputContainer.getScene().getWindow()).close();
	}
}
