package erj4.as;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import erj4.as.DataClasses.Custom;
import erj4.as.DataClasses.Data;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class IndexController extends SuperController implements Initializable{
	@FXML
	TextField searchBox;
	
	@FXML
	ListView<Custom> itemsList;
	
	@FXML
	VBox detailsPane;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		itemsList.setItems(Custom.getAllCustoms());
	}
	
	@FXML
	public void newItem() {
		String fileName = "new_item.fxml";
		//
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
		Stage stage = new Stage();
		stage.setTitle("Add new item");
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
	public void itemSelected() {
		detailsPane.getChildren().clear();
		Custom custom = itemsList.getSelectionModel().getSelectedItem();
		for (Data d:custom.getData()){
			Label columnLabel = new Label(d.getColumn().getName());
			columnLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize()));
			detailsPane.getChildren().addAll(columnLabel, new Label(Main.encrypter.decrypt(d.getEncryptedData(), d.getIv())));
		}
	}
}
