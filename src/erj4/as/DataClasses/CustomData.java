package erj4.as.DataClasses;

public class CustomData {
	Custom custom;
	CustomColumn column;
	byte[] encryptedData;
	byte[] iv;
	
	public CustomData(Custom custom, CustomColumn column, byte[] encryptedData, byte[] iv) {
		this.custom = custom;
		this.column = column;
		this.encryptedData = encryptedData;
		this.iv = iv;
	}
}
