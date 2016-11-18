package erj4.as;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class LoginController extends VBox{
	@FXML private TextField usernameField;
	@FXML private PasswordField passwordField;
	
	@FXML
	public void authenticate(){
		PreparedStatement s = Main.db.newStatement("SELECT hskey, salt FROM Users WHERE username=?");
		try {
			s.setString(1, usernameField.getText());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		boolean resultFound = false;
		String shPassword = "";
		String salt = "";
		try {
			ResultSet results = Main.db.runQuery(s);
			resultFound = results.isBeforeFirst();
			if (resultFound){
				shPassword = results.getString("SHPass");
				salt = results.getString("Salt");
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		if(!resultFound) {
			System.out.print("User not found");
			return;
		}
		try {
			if (passwordIsCorrect(passwordField.getText(), salt.getBytes("UTF-8"), shPassword.getBytes("UTF-8"))){
				logIn(usernameField.getText(), passwordField.getText());
			}
			else {
				System.out.println("Incorrect password");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private boolean passwordIsCorrect(String password, byte[] salt, byte[] correctHash) {
		salt = new byte[64];
		try {
			System.out.println("Comparing: " + new String(Main.hash(password,salt), "UTF-8"));
			System.out.println("     With: " + new String(correctHash, "UTF-8"));
			System.out.println("     Salt: " + new String(salt, "UTF-8"));
		} catch (UnsupportedEncodingException e){Main.fatalError(e, e.getMessage());}
		byte[] result=Main.hash(password, salt);
		System.out.println(Main.hash(password,salt).length +" "+ correctHash.length);
		System.out.println( Arrays.equals(Main.hash(password,salt), correctHash) );
		
		return( Arrays.equals(Main.hash(password,salt), correctHash) );
	}
	
	@FXML
	public void newUser(){
		String fileName = "new_user.fxml";
		//
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
        Stage stage = new Stage();
        stage.setTitle("Add new item");
        try {
			stage.setScene(new Scene(loader.load()));
		} catch (IOException e) {
			Main.fatalError(e, "An error occured while trying to load the resource "+fileName+", so the program must exit immediately");
		}
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(usernameField.getScene().getWindow());
        stage.showAndWait();
        String[] userAndPass = (String[]) ((Node) loader.getController()).getUserData();
        String username = userAndPass[0];
        String password = userAndPass[1];
        if (username!=null){
        	logIn(username, password);
        }
        
	}
	
	private void logIn(String username, String key){
		Main.db.setUsername(username);
		Main.encrypter = new Encrypter(key);
		index((Stage)usernameField.getScene().getWindow());
	}
	
	public void index(Stage stage) {
		Parent root = null;
		String fileName = "index.fxml";
		Scene scene = null;
		try {
			root = FXMLLoader.load(getClass().getResource(fileName));
	        scene = new Scene(root, 300, 275);
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
