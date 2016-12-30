package erj4.as.CustomElements;

import erj4.as.NewTemplateController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

	public TemplateFieldInput(NewTemplateController controller) {
		super(5);
		
		this.controller=controller;
		
		//TODO proper remove item button
		Button removeButton = new Button("Remove");
		this.getChildren().add(removeButton);
		removeButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				remove();
			}
		});
		
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
}
