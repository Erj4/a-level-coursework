package erj4.as;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import erj4.as.DataClasses.Column;
import erj4.as.DataClasses.Template;

public class TemplatesIndexController extends IndexController {

	@FXML
	ListView<Template> itemsList;

	FilteredList<Template> filtered;

	@FXML
	VBox detailsPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize();
		filtered=Template.getAllTemplates().sorted().filtered(x->x.getName().toLowerCase().contains(searchBox.getText().toLowerCase()));
		itemsList.setItems(filtered);
	}

	@FXML
	public void newItem() {
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
		stage.initOwner(searchBox.getScene().getWindow());
		stage.showAndWait();
	}

	@FXML
	public void itemSelected(MouseEvent e) {
		detailsPane.getChildren().clear();
		Template template = itemsList.getSelectionModel().getSelectedItem();
		if (template==null) return;
		if (e==null||e.getClickCount()==1) {
			Separator sep = null;
			for (Column c : template.getColumns()) {
				Label columnLabel = new Label(c.getName());
				columnLabel.setFont(Font.font(Font.getDefault().getFamily(),
						FontWeight.BOLD, Font.getDefault().getSize()));
				sep = new Separator();
				CheckBox cb = new CheckBox();
				cb.setOpacity(0.8);
				cb.setSelected(c.isPassword());
				cb.setDisable(true);
				detailsPane.getChildren().addAll(columnLabel,
						new HBox(new Label("Hidden: "), cb), sep);
			}
			if (sep != null)
				detailsPane.getChildren().remove(sep);
		}
		else if (e.getClickCount()==2){
			String fileName = "new_template.fxml";
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
			Stage stage = new Stage();
			stage.setTitle("Edit template");
			try {
				stage.setScene(new Scene(loader.load()));
				((NewTemplateController) loader.getController()).editMode(template);
			} catch (IOException ex) {
				Main.fatalError(ex, "An error occured while trying to load the resource "+fileName+", so the program must exit immediately");
			}
			stage.setResizable(false);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(searchBox.getScene().getWindow());
			stage.showAndWait();
			itemSelected(null);
		}
	}
	
	public void updateListView() {
		filtered.setPredicate(x->x.getName().toLowerCase().contains(searchBox.getText().toLowerCase()));
	}
}
