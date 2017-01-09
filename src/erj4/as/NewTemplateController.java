package erj4.as;

import erj4.as.CustomElements.TemplateFieldInput;
import erj4.as.DataClasses.Column;
import erj4.as.DataClasses.Template;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NewTemplateController extends VBox {
	
	@FXML
    public Label placeholderText;

    @FXML
    public VBox inputContainer;
    
    @FXML
    public TextField nameField;

	@FXML
	public Button addButton;
    
    @FXML
    public Button deleteButton;
    
    @FXML
    public ImageView addFieldImage;
    
    public Template template;
	
	public NewTemplateController() {
		this.setUserData(null);
	}

    @FXML
    private void addField() {
    	placeholderText.setVisible(false);
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
    
    public void editMode(Template template){
    	this.template=template;
    	addButton.setText("SAVE TEMPLATE");
    	addButton.setOnAction(x->saveUpdate());
    	deleteButton.setVisible(true);
    	addFieldImage.setVisible(false);
    	placeholderText.setVisible(false);
    	nameField.setText(template.getName());
    	for(Column c:template.getColumns()) inputContainer.getChildren().add(new TemplateFieldInput(this, c));
    }
	
	private void saveUpdate() {
		template.setName(nameField.getText());
		for(Node x:inputContainer.getChildren()){
			TemplateFieldInput tfi = (TemplateFieldInput) x;
			tfi.getColumn().setName(tfi.getName());
			tfi.getColumn().setIsPassword(tfi.isPassword());
		}
    	this.setUserData(template);
    	((Stage) inputContainer.getScene().getWindow()).close();
	}

	@FXML
	void delete(){
		template.delete();
		((Stage) inputContainer.getScene().getWindow()).close();
	}
}
