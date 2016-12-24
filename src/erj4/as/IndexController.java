package erj4.as;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class IndexController extends SuperController{
	@FXML
	TextField searchBox;
	
	@FXML
	public void newItem(){
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
}
