package erj4.as;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import erj4.as.DataClasses.Custom;
import erj4.as.DataClasses.Wallet;

public class WalletsIndexController extends IndexController {
	@FXML
	TextField searchBox;

	@FXML
	ListView<Wallet> itemsList;

	@FXML
	ListView<Custom> detailsPane;

	public void initialize(URL location, ResourceBundle resources) {
		itemsList.setItems(Wallet.getAllWallets().sorted());
	}

	@FXML
	public void newItem() {
		String fileName = "new_wallet.fxml";
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
		Stage stage = new Stage();
		stage.setTitle("Add new wallet");
		try {
			stage.setScene(new Scene(loader.load()));
		} catch (IOException e) {
			Main.fatalError(e, "An error occured while trying to load the resource "+fileName+", so the program must exit immediately");
		}
		stage.setResizable(false);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(searchBox.getScene().getWindow());
		stage.showAndWait();
	}

	@FXML
	public void itemSelected(MouseEvent me) {
		if(me.getClickCount()==1) detailsPane.setItems(itemsList.getSelectionModel().getSelectedItem().getCustoms());
		else if(me.getClickCount()==2){
			Wallet wallet = itemsList.getSelectionModel().getSelectedItem();
			if (wallet==null) return;
			String fileName = "new_wallet.fxml";
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
			Stage stage = new Stage();
			stage.setTitle("Add new wallet");
			try {
				stage.setScene(new Scene(loader.load()));
				((NewWalletController) loader.getController()).editMode(wallet);
			} catch (IOException e) {
				Main.fatalError(e, "An error occured while trying to load the resource "+fileName+", so the program must exit immediately");
			}
			stage.setResizable(false);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(searchBox.getScene().getWindow());
			stage.showAndWait();
		}
	}
	
	@FXML
	public void customClicked(MouseEvent me) {
		if(me.getClickCount()==2){
			String fileName = "index.fxml";
			Scene scene = null;
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
				CustomsIndexController controller = new CustomsIndexController();
				loader.setController(controller);
				scene = new Scene((Parent)loader.load());
				controller.itemsList.getSelectionModel().select(detailsPane.getSelectionModel().getSelectedItem());
			} catch (Exception e) {
				Main.fatalError(e, "An error occured while trying to load the resource "+fileName+", so the program must exit immediately");
			}
			((Stage)searchBox.getScene().getWindow()).setScene(scene);
		}
	}
}
