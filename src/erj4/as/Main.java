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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom; // For salting and IVs (new ..., nextBytes(outArray)
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

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
			scene = new Scene(root, 300, 130);
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

	public static byte[] hash(String password, byte[] salt){ // Wrapper for hashing algorithm (SHA-512)
		//TODO switch to pbkdf2
		return SHA512(password, salt);
	}

	private static byte[] SHA512(String password, byte[] salt) {
		byte[] passwordBytes = null;
		try {
			passwordBytes = password.getBytes("UTF-8");
		} catch(UnsupportedEncodingException e){e.printStackTrace();}
		byte[] saltedPassword = new byte[salt.length+passwordBytes.length];
		System.arraycopy(salt, 0, saltedPassword, 0, salt.length);
		System.arraycopy(passwordBytes, 0, saltedPassword, salt.length, passwordBytes.length);
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-512");
		} catch(NoSuchAlgorithmException e){e.printStackTrace();}
		md.update(saltedPassword);
		return md.digest();
	}

	//SRC adapted from https://www.owasp.org/index.php/Hashing_Java
	public static byte[] PBKDF2(String password, final byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		final char[] passwordChars = password.toCharArray();
		final int iterations = 1000;
		final int keyLength = 512;
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		PBEKeySpec spec = new PBEKeySpec(passwordChars, salt, iterations, keyLength);
		SecretKey key = skf.generateSecret(spec);
		return key.getEncoded();
	}
}
