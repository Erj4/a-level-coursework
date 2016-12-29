package erj4.as.CustomInputs;

import erj4.as.DataClasses.Column;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class ItemFieldInput extends HBox {
	private Column column;
	private TextField input;
	
	public ItemFieldInput(Column column) {
		super(2);
		this.column=column;
		this.getChildren().add(new Label(column.getName()+": "));
		this.input = column.isPassword()?new PasswordField():new TextField();
		this.getChildren().add(input);
	}
	
	public Column getColumn(){
		return column;
	}
	
	public String getInput(){
		return input.getText();
	}

	public boolean isColumn(Column testColumn){
		return column==testColumn;
	}
}