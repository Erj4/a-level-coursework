package erj4.as.CustomInputs;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TemplateFieldInput extends VBox {
	TextField nameField;
	CheckBox isPasswordField;

	public TemplateFieldInput() {
		super(2);
		
		HBox nameH = new HBox(2);
		nameH.getChildren().add(new Label("Field name:"));
		nameField=new TextField();
		nameH.getChildren().add(nameField);
		this.getChildren().add(nameH);
		
		HBox isPasswordH = new HBox(2);
		isPasswordH.getChildren().add(new Label("Hide text:"));
		isPasswordField=new CheckBox();
		isPasswordH.getChildren().add(isPasswordField);
	}
	
	public String getName(){
		return nameField.getText();
	}
	
	public Boolean isPassword(){
		return isPasswordField.isSelected();
	}
}
