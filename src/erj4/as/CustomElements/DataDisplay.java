package erj4.as.CustomElements;

import erj4.as.Main;
import erj4.as.DataClasses.Data;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class DataDisplay extends HBox {
	private Label plainTextLabel;
	private Label asteriskLabel;
	public DataDisplay(Data d) {
		String value=Main.encrypter.decrypt(d.getEncryptedData(), d.getIv());
		plainTextLabel=new Label(value);
		if(!d.getColumn().isPassword()){
			this.getChildren().add(plainTextLabel);
			return;
		}
		
		String asterisks = new String();
		for(int i=0;i<value.length();i++) asterisks+="✱";
		asteriskLabel=new Label(asterisks);
		this.getChildren().add(asteriskLabel);
		
		this.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if(event.getButton()!=MouseButton.PRIMARY) return;
				if (event.getClickCount()==1){
					ClipboardContent cc = new ClipboardContent();
					cc.putString(plainTextLabel.getText());
					Clipboard.getSystemClipboard().setContent(cc);
				}
				else if (event.getClickCount()==2){
					Clipboard.getSystemClipboard().setContent(null);
					if (getChildren().contains(asteriskLabel)) {
						getChildren().remove(asteriskLabel);
						getChildren().add(plainTextLabel);
						}
					else {
						getChildren().remove(plainTextLabel);
						getChildren().add(asteriskLabel);
					}
				}
			}
			
		});
	}
}
