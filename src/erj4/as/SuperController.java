package erj4.as;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public abstract class SuperController extends AnchorPane {
	@FXML private AnchorPane sideMenuPane;
	@FXML private VBox mainPane;

	public void initialize(){ // Called by FXMLLoader *after* @fxml annotation injection
		sideMenuPane.widthProperty().addListener(new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) { // Called when tray width changes TODO animate if possible without jerk?
				System.out.println("Changed by "+((Double)oldValue-(Double)newValue));
			}
		});
	}

	@FXML
	public void showMenu(MouseEvent ae){
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().addAll(
				new KeyFrame(Duration.ZERO, new KeyValue(sideMenuPane.translateXProperty(), -sideMenuPane.getWidth())),
				new KeyFrame(new Duration(150), new KeyValue(sideMenuPane.translateXProperty(), 0, Interpolator.EASE_OUT)), //200 for entrance animations

				new KeyFrame(Duration.ZERO, new KeyValue(mainPane.opacityProperty(), 1.0)),
				new KeyFrame(new Duration(150), new KeyValue(mainPane.opacityProperty(), 0.5))
				);
		timeline.play();
		sideMenuPane.setVisible(true);
		ae.consume();
	}
	@FXML
	public void hideMenu(){
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().addAll(
				new KeyFrame(Duration.ZERO, new KeyValue(sideMenuPane.translateXProperty(), 0)),
				new KeyFrame(new Duration(150), new KeyValue(sideMenuPane.translateXProperty(), -sideMenuPane.getWidth(), Interpolator.EASE_BOTH)), //150ms for exit animations
				
				new KeyFrame(Duration.ZERO, new KeyValue(mainPane.opacityProperty(), 0.5)),
				new KeyFrame(new Duration(150), new KeyValue(mainPane.opacityProperty(), 1.0))
				);
		timeline.onFinishedProperty().set(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent ae){
				sideMenuPane.setVisible(false);
			}
		});
		timeline.play();
	}
}
