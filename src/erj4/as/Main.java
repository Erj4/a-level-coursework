package erj4.as;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.security.SecureRandom; // For salting and IVs (new ..., nextBytes(outArray)
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import erj4.as.DatabaseConnection;

public class Main extends Application {

	public static DatabaseConnection db;
	public static Encrypter encrypter;

	public static void main(String[] args){
		launch();
	}

	public void start(Stage stage) {
		db = new DatabaseConnection("data.db");
		Parent root = null;
		String fileName = "login.fxml";
		Scene scene = null;
		try {
			root = (Parent)FXMLLoader.load(getClass().getResource(fileName));
			scene = new Scene(root);
		} catch (Exception e) {
			fatalError(e, "An error occured while trying to load the resource "+fileName+", so the program must exit immediately");
		}
		stage.setTitle("Login");
		stage.setScene(scene);
		stage.setOnCloseRequest(new EventHandler<WindowEvent>(){
			public void handle(WindowEvent we){
				we.consume();
				Main.quit();
			}
		});
		stage.show();
	}

	public static void fatalError(Exception e, String message){
		e.printStackTrace();
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Fatal error");
		alert.setHeaderText("A fatal error occured");
		alert.setContentText(message);
		alert.showAndWait();
		db.disconnect();
		System.exit(1);
	}

	public static void quit(){
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Quit");
		alert.setHeaderText("Really quit?");
		alert.setContentText("Are you sure you want to quit? (Don't worry, any changes made will be kept)");
		ButtonType result = alert.showAndWait().get();
		if(result==ButtonType.OK){
			db.disconnect();
			System.exit(0);
		}
	}

	public static byte[] randomBytes(int length){
		SecureRandom sr = new SecureRandom();
		byte[] bytes = new byte[length];
		sr.nextBytes(bytes);
		return bytes;
	}

	public static byte[] hash(String password, byte[] salt) { // Wrapper for hashing algorithm
		return PBKDF2(password, salt, 50000, 512);
	}

	public static byte[] encrypterHash(String password, byte[] salt) {
		return PBKDF2(password, salt, 10000, 128);
	}

	//SRC adapted from https://www.owasp.org/index.php/Hashing_Java
	private static byte[] PBKDF2(final String password, final byte[] salt, int iterations, int keyLength){
		try {
			final char[] passwordChars = password.toCharArray();
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			PBEKeySpec spec = new PBEKeySpec(passwordChars, salt, iterations, keyLength);
			SecretKey key;
			key = skf.generateSecret(spec);
			return key.getEncoded();

		}
		catch(Exception e) {
			Main.fatalError(e, "Fatal exception in hashing function, so program must exit immediately");
			return null;
		}
	}

	public void vmShutDown(){
		// TODO Work out if unexpected, if so leave info
	}

	public static byte[] getIV() {
		return randomBytes(16);
	}
}
