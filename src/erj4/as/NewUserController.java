package erj4.as;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class NewUserController extends VBox implements Initializable {
	private final int MIN_PASSWORD_LENGTH  = 8;

	@FXML private TextField usernameField;
	@FXML private PasswordField passwordField;
	@FXML private PasswordField repeatPasswordField;
	@FXML private VBox validationBox;

	@Override
	public void initialize(URL location, ResourceBundle resources){
		this.setUserData(new String[]{null, null, null});
		usernameField.textProperty().addListener(x->clearInvalid());
		passwordField.textProperty().addListener(x->clearInvalid());
		repeatPasswordField.textProperty().addListener(x->clearInvalid());
	}

	@FXML
	private void newUser(){
		clearInvalid();
		PreparedStatement selectStatement = Main.db.newStatement("SELECT username FROM Users WHERE username=?");
		String username = usernameField.getText(); // For thread-safety
		String password = passwordField.getText(); // ^^
		// If using (password/username).getText() quickly, it is theoretically possible for the value to change between verification and user creation (a bad thing)
		
		boolean[] failures = {false, false, false, false};
		if (username.equals("")) failures[0] = true;
		try {
			selectStatement.setString(1, username);
		} catch (SQLException e) {Main.fatalError(e, e.getMessage());} // Should not occur
		boolean userFound = false;
		try {
			ResultSet results = Main.db.runQuery(selectStatement);
			userFound = results.isBeforeFirst();
		}
		catch (SQLException e){Main.fatalError(e, e.getMessage());} // Should not occur
		if (userFound) failures[1] = true;
		if (!password.equals(repeatPasswordField.getText())) failures[2] = true;
		if (!validPassword(password)) failures[3] = true;
		if (!validate(failures)){
			try {
				this.setUserData(new String[]{username, password, new String(createUser(username, password), "UTF-8")});
			} catch (UnsupportedEncodingException e) {e.printStackTrace();}
			((Stage)usernameField.getScene().getWindow()).close();
		}
		else {
			((Stage) validationBox.getScene().getWindow()).sizeToScene();
			System.out.println("Failed to create new user");
		}
	}

	private boolean validate(boolean[] failures) {
		boolean failed=false;
		if (failures[0]) {
			invalid("Username blank");
			failed=true;
		}
		if (failures[1]) {
			invalid("User already exists");
			failed=true;
		}
		if (failures[2]) {
			invalid("Passwords don't match");
			failed=true;
		}
		if (failures[3]) {
			invalid("User already exists");
			failed=true;
		}
		return failed;
	}

	private void invalid(String message){
		Label reason=new Label(message);
		reason.setTextFill(Color.RED);
		validationBox.getChildren().add(reason);
		validationBox.setVisible(true);
		
	}

	private void clearInvalid() {
		boolean shouldResize = !validationBox.getChildren().isEmpty();
		validationBox.setVisible(false);
		validationBox.getChildren().clear();
		if (shouldResize) ((Stage) validationBox.getScene().getWindow()).sizeToScene();
	}

	@FXML
	private void cancelPressed(){
		((Stage)usernameField.getScene().getWindow()).close();
	}

	private byte[] createUser(String username, String password) {
		PreparedStatement create = Main.db.newStatement("INSERT INTO Users (username, hskey, salt) VALUES (?, ?, ?);");
		try {
			create.setString(1, username);
			byte[] salt = Main.randomBytes(64);
			create.setBytes(2, Main.hash(password, salt));
			create.setBytes(3, salt);
			create.executeUpdate();
			verifyInsertion(username, Main.hash(password, salt), salt); // Check data in database is correct
			return salt;
		} catch (SQLException e) {e.printStackTrace();}
		return null;
	}

	private void verifyInsertion(String username, byte[] hash, byte[] salt){ // For debugging - checks data in database is same as values in variables
		PreparedStatement s = Main.db.newStatement("SELECT hskey, salt FROM Users WHERE username=?");
		try {
			s.setString(1, usernameField.getText());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		boolean resultFound = false;
		byte[] dbHash = null;
		byte[] dbSalt = null;
		try {
			ResultSet results = Main.db.runQuery(s);
			resultFound = results.isBeforeFirst();
			if (resultFound){
				dbHash = results.getBytes("hskey");
				dbSalt = results.getBytes("salt"); 
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		if(!resultFound) {
			System.out.print("Test failed");
			return;
		}
		if(!Arrays.equals(hash, dbHash)){
			try {
				System.out.println("Hash insertion failed:\n"+new String(hash, "UTF-8")+"\nvs\n"+new String(dbHash, "UTF-8"));
			} catch (UnsupportedEncodingException e) { e.printStackTrace(); }
		}
		if(!Arrays.equals(salt,  dbSalt)){
			try {
				System.out.println("Salt insertion failed:\n"+new String(salt, "UTF-8")+"\nvs\n"+new String(dbSalt, "UTF-8"));
			} catch (UnsupportedEncodingException e) { e.printStackTrace(); }
		}
		System.out.println("Insertion test complete");
	}

	public boolean validPassword(String password){
		if (password.length()<MIN_PASSWORD_LENGTH){
			return false;
		}
		if (password.toLowerCase().equals(password)||password.toUpperCase().equals(password)) {// check contains at least 1 upper & lower case letter
			return false;
		}
		// TODO check not all letters

		return true;
	}
}
