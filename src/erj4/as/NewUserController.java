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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NewUserController extends VBox implements Initializable{
	private final int MIN_PASSWORD_LENGTH  = 8;

	@FXML private TextField usernameField;
	@FXML private PasswordField passwordField;
	@FXML private PasswordField repeatPasswordField;

	@Override
	public void initialize(URL location, ResourceBundle resources){
		this.setUserData(new String[]{null, null});
	}

	@FXML
	private void newUser(){
		PreparedStatement selectStatement = Main.db.newStatement("SELECT username FROM Users WHERE username=?");
		String username = usernameField.getText(); // For thread-safety
		String password = passwordField.getText(); // ^^
		// If using (password/username).getText() repeatedly, it is theoretically possible for the value to change between verification and user creation (a bad thing)
		boolean[] failures = {false, false, false, false};
		boolean failed = false;
		if (username.equals("")){
			System.out.println("Username blank");
			failures[0] = true;
			failed = true;
		}
		try {
			selectStatement.setString(1, username);
		} catch (SQLException e) {Main.fatalError(e, e.getMessage());} // Should not occur
		boolean userFound = false;
		try {
			ResultSet results = Main.db.runQuery(selectStatement);
			userFound = results.isBeforeFirst();
		}
		catch (SQLException e){Main.fatalError(e, e.getMessage());} // Should not occur
		if (userFound){
			System.out.println("User already exists");
			failures[1] = true;
			failed = true;
		}
		if (!password.equals(repeatPasswordField.getText())){
			System.out.println("Passwords don't match");
			failures[2] = true;
			failed = true;
		}
		if (!validPassword(password)){
			System.out.println("Unsuitable password");
			failures[3] = true;
			failed = true;
		}
		if (!failed){
			createUser(username, password);
			this.setUserData(new String[]{username, password});
			((Stage)usernameField.getScene().getWindow()).close();
		}
		else {
			// TODO deal with failures
			System.out.println("Failed to create new user");
		}
	}

	@FXML
	private void cancelPressed(){
		((Stage)usernameField.getScene().getWindow()).close();
	}

	private void createUser(String username, String password) {
		PreparedStatement create = Main.db.newStatement("INSERT INTO Users (username, hskey, salt) VALUES (?, ?, ?);");
		try {
			create.setString(1, username);
			byte[] salt = Main.randomBytes(64);
			create.setBytes(2, Main.hash(password, salt));
			create.setBytes(3, salt);
			create.executeUpdate();
			verifyInsertion(username, Main.hash(password, salt), salt); // Check data in database is correct
		} catch (SQLException e) {e.printStackTrace();}
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
