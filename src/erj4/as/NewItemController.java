package erj4.as;

import java.io.IOException;

import erj4.as.DataClasses.Template;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NewItemController {
	@FXML
	private ComboBox<Template> templateSelector;

	@FXML
	private Label placeholderText;

	@FXML
	private VBox inputContainer;

	@FXML
	private Button addItemFromScene;

	@FXML
	void newTemplate() {
		String fileName = "new_template.fxml";
		//
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
		Stage stage = new Stage();
		stage.setTitle("Add new template");
		try {
			stage.setScene(new Scene(loader.load()));
		} catch (IOException e) {
			Main.fatalError(e, "An error occured while trying to load the resource "+fileName+", so the program must exit immediately");
		}
		stage.setResizable(false);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(templateSelector.getScene().getWindow());
		stage.showAndWait();
		Template newTemplate = (Template) ((Node) loader.getController()).getUserData();
		if(newTemplate != null){
			templateSelector.getSelectionModel().select(newTemplate);
			templateSelected();
		}
	}

	@FXML
	void templateSelected() {

	}


}
