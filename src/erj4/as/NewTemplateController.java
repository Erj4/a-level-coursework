package erj4.as;

import erj4.as.CustomInputs.TemplateFieldInput;
import erj4.as.DataClasses.Column;
import erj4.as.DataClasses.Template;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NewTemplateController extends VBox {
	
	public NewTemplateController() {
		this.setUserData(null);
	}
	
	@FXML
    public Label placeholderText;

    @FXML
    public VBox inputContainer;
    
    @FXML
    public TextField nameField;

    @FXML
    private void addField() {
    	inputContainer.getChildren().add(new TemplateFieldInput(this));
    }

    @FXML
    private void addTemplateFromScene() {
    	Template t = new Template(nameField.getText(), Main.getIV());
    	for(Node x:inputContainer.getChildren()){
    		TemplateFieldInput tfi=(TemplateFieldInput) x;
    		new Column(t, tfi.getName(), Main.getIV(), tfi.isPassword());
    	}
    	this.setUserData(t);
    	((Stage) inputContainer.getScene().getWindow()).close();
    }
}
