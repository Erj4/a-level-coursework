package erj4.as;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class IndexController extends StackPane{
	
	@FXML private AnchorPane menuPane;
	
	@FXML
	public void toggleMenu(){
		System.out.println("HI ED");
	}
}
