package erj4.as;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
		PreparedStatement selectStatement = Main.db.newStatement("SELECT User FROM Users WHERE User=?");
		String username = usernameField.getText(); // For thread-safety
		String password = passwordField.getText(); // ^^
		// If using (password/username).getText(), it is theoretically possible for the value to change between verification and user creation (a bad thing)
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
		PreparedStatement create = Main.db.newStatement("INSERT INTO Users (User, SHPass, Salt) VALUES (?, ?, ?);");
		try {
			create.setString(1, username);
			byte[] salt = Main.randomBytes(64);
			create.setString(2, new String(Main.hash(password, salt), "UTF-8"));
			create.setString(3, new String(salt, "UTF-8"));
			create.executeUpdate();
		} catch (SQLException e) {e.printStackTrace();} 
		catch (UnsupportedEncodingException e){e.printStackTrace();}
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
