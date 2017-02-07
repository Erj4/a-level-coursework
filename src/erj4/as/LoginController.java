package erj4.as;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class LoginController extends VBox implements Initializable{
	
	@FXML private TextField usernameField;
	@FXML private PasswordField passwordField;
	@FXML private Label validationField;

	public void initialize(URL u, ResourceBundle r) {
		validationField.managedProperty().bind(validationField.visibleProperty());
		usernameField.textProperty().addListener(x->clearInvalid());
		passwordField.textProperty().addListener(x->clearInvalid());
	}

	@FXML
	public void authenticate(){
		clearInvalid();
		PreparedStatement s = Main.db.newStatement("SELECT hskey, salt FROM Users WHERE username=?");
		try {
			s.setString(1, usernameField.getText());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		boolean resultFound = false;
		byte[] shPassword = null;
		byte[] salt = null;
		try {
			ResultSet results = Main.db.runQuery(s);
			resultFound = results.isBeforeFirst();
			if (resultFound){
				shPassword = results.getBytes("hskey");
				salt = results.getBytes("salt");
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		if(!resultFound) {
			System.out.println("User not found");
			invalid("User not found");
			return;
		}
		if (passwordIsCorrect(passwordField.getText(), salt, shPassword))
			logIn(usernameField.getText(), passwordField.getText(), salt);
		else {
			System.out.println("Incorrect password");
			invalid("Incorrect password");
		}
	}
	
	private void invalid(String message){
		validationField.setText(message);
		validationField.setVisible(true);
	}
	
	private void clearInvalid(){
		validationField.setText("");
		validationField.setVisible(false);
	}

	private boolean passwordIsCorrect(String password, byte[] salt, byte[] correctHash) {
		/*try {
			System.out.println("Comparing: " + new String(Main.hash(password,salt), "UTF-8"));
			System.out.println("     With: " + new String(correctHash, "UTF-8"));
			System.out.println("     Salt: " + new String(salt, "UTF-8"));
		} catch (UnsupportedEncodingException e){Main.fatalError(e, e.getMessage());}
		System.out.println(Main.hash(password,salt).length +" "+ correctHash.length);
		System.out.println( Arrays.equals(Main.hash(password,salt), correctHash) );*/

		return( Arrays.equals(Main.hash(password,salt), correctHash) );
	}

	@FXML
	public void newUser(){
		String fileName = "new_user.fxml";
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
		Stage stage = new Stage();
		stage.setTitle("Add new user");
		try {
			stage.setScene(new Scene(loader.load()));
		} catch (IOException e) {
			Main.fatalError(e, "An error occured while trying to load the resource "+fileName+", so the program must exit immediately");
		}
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(usernameField.getScene().getWindow());
		stage.showAndWait();
		String[] userAndPass = (String[]) ((Node) loader.getController()).getUserData();
		String username = userAndPass[0];
		String password = userAndPass[1];
		byte[] salt;
		try{
			salt = userAndPass[2].getBytes();
		}
		catch(NullPointerException e){
			return;
		}
		if (username!=null){
			logIn(username, password, salt);
		}
	}

	private void logIn(String username, String key, byte[] salt){
		Main.db.setUsername(username);
		Main.encrypter = new Encrypter(key, salt);
		Main.db.populate();
		index((Stage)usernameField.getScene().getWindow());
	}

	public void index(Stage stage) {
		String fileName = "index.fxml";
		Scene scene = null;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
			loader.setController(new CustomsIndexController());
			scene = new Scene((Parent)loader.load(), 800, 450);
		} catch (Exception e) {
			Main.fatalError(e, "An error occured while trying to load the resource "+fileName+", so the program must exit immediately");
		}
		stage.setTitle("Password Manager");
		stage.setScene(scene);
		stage.setOnCloseRequest(new EventHandler<WindowEvent>(){
			public void handle(WindowEvent we){
				we.consume();
				Main.quit();
			}
		});
		stage.show();
	}
}
