package erj4.as;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import erj4.as.CustomElements.ItemFieldInput;
import erj4.as.DataClasses.Column;
import erj4.as.DataClasses.Custom;
import erj4.as.DataClasses.Data;
import erj4.as.DataClasses.Template;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NewItemController extends VBox implements Initializable {
	@FXML
	private ComboBox<Template> templateSelector;

	@FXML
	private Label placeholderText;

	@FXML
	private VBox inputContainer;
	
	@FXML
	private TextField nameField;

	@Override
	public void initialize(URL location, ResourceBundle resources){
		templateSelector.setItems(Template.getAllTemplates());
	}

	@FXML
	void newTemplate() {
		String fileName = "new_template.fxml";
		
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
		}
	}

	@FXML
	void templateSelected() {
		inputContainer.getChildren().clear();
		if(templateSelector.getSelectionModel().isSelected(-1))
		{
			placeholderText.setVisible(true);
		}
		else {
			placeholderText.setVisible(false);
			for(Column column:templateSelector.getSelectionModel().getSelectedItem().getColumns()){
				inputContainer.getChildren().add(new ItemFieldInput(column));
			}
		}
	}

	@FXML
	void addItemFromScene() {
		Custom custom = new Custom(templateSelector.getSelectionModel().getSelectedItem(), nameField.getText(), Main.getIV());
		for (Node x:inputContainer.getChildren()){
			ItemFieldInput ifi = (ItemFieldInput) x;
			byte[] iv = Main.getIV();
			new Data(custom, ifi.getColumn(), ifi.getInput(), iv);
		}
		((Stage) inputContainer.getScene().getWindow()).close();
	}
	
}
