package erj4.as;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public abstract class IndexController extends SuperController implements Initializable {
	@FXML
	TextField searchBox;
	
	@Override
	public abstract void initialize(URL location, ResourceBundle resources);
	
	@FXML
	public abstract void newItem();
	
	@FXML
	public abstract void itemSelected(MouseEvent e);
	
	public abstract void updateListView();
	
	public void initialize() {
		searchBox.textProperty().addListener((ChangeListener<String>)(x,y,z)->updateListView());
	}
	
	@FXML
	public void refresh(){
		Main.db.populate();
		initialize(null, null);
	}
	
	@FXML
	public void toItems(){
		String fileName = "index.fxml";
		Scene scene = null;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
			loader.setController(new CustomsIndexController());
			scene = new Scene((Parent)loader.load());
		} catch (Exception e) {
			Main.fatalError(e, "An error occured while trying to load the resource "+fileName+", so the program must exit immediately");
		}
		((Stage)searchBox.getScene().getWindow()).setScene(scene);
	}
	
	@FXML
	public void toTemplates(){
		String fileName = "template_index.fxml";
		Scene scene = null;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
			loader.setController(new TemplatesIndexController());
			scene = new Scene((Parent)loader.load());
		} catch (Exception e) {
			e.printStackTrace();
			Main.fatalError(e, "An error occured while trying to load the resource "+fileName+", so the program must exit immediately");
		}
		((Stage)searchBox.getScene().getWindow()).setScene(scene);
	}
	
	@FXML
	public void toWallets(){
		String fileName = "wallet_index.fxml";
		Scene scene = null;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
			scene = new Scene((Parent)loader.load());
		} catch (Exception e) {
			Main.fatalError(e, "An error occured while trying to load the resource "+fileName+", so the program must exit immediately");
		}
		((Stage)searchBox.getScene().getWindow()).setScene(scene);
	}
}
