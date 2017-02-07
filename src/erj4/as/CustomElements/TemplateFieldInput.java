package erj4.as.CustomElements;

import erj4.as.NewTemplateController;
import erj4.as.DataClasses.Column;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TemplateFieldInput extends HBox {
	private NewTemplateController controller;
	private TextField nameField;
	private CheckBox isPasswordField;
	private Column column;

	public TemplateFieldInput(NewTemplateController controller) {
		super(5);
		
		this.controller=controller;


		Button removeButton = new Button("Remove");
		this.getChildren().add(removeButton);
		removeButton.setOnAction(x->remove());
		
		VBox form = new VBox(2);
		HBox nameH = new HBox(2);
		nameH.getChildren().add(new Label("Field name:"));
		nameField=new TextField();
		nameH.getChildren().add(nameField);
		form.getChildren().add(nameH);
		
		HBox isPasswordH = new HBox(2);
		isPasswordH.getChildren().add(new Label("Hide text:"));
		isPasswordField=new CheckBox();
		isPasswordH.getChildren().add(isPasswordField);
		form.getChildren().add(isPasswordH);
		
		this.getChildren().add(form);
	}
	
	public TemplateFieldInput(NewTemplateController controller, Column c) {
		super(5);
		
		this.controller=controller;
		this.column=c;
		
		VBox form = new VBox(2);
		HBox nameH = new HBox(2);
		nameH.getChildren().add(new Label("Field name:"));
		nameField=new TextField(c.getName());
		nameH.getChildren().add(nameField);
		form.getChildren().add(nameH);
		
		HBox isPasswordH = new HBox(2);
		isPasswordH.getChildren().add(new Label("Hide text:"));
		isPasswordField=new CheckBox();
		isPasswordField.selectedProperty().set(c.isPassword());
		isPasswordH.getChildren().add(isPasswordField);
		form.getChildren().add(isPasswordH);
		
		this.getChildren().add(form);
	}

	public String getName(){
		return nameField.getText();
	}
	
	public Boolean isPassword(){
		return isPasswordField.isSelected();
	}
	
	private void remove(){
		if(controller.inputContainer.getChildren().isEmpty()){
			controller.placeholderText.setVisible(true);
		}
		controller.inputContainer.getChildren().remove(this);
	}
	
	public Column getColumn(){
		return column;
	}
}
