package erj4.as;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;

import erj4.as.DataClasses.*;

public class DatabaseConnection {

	private Connection connection = null;
	private String[] dbCreationStatement = {
			"PRAGMA foreign_keys = off;\r\n",
			"BEGIN TRANSACTION;\r\n",
			"CREATE TABLE IF NOT EXISTS UserSettings (\r\n"
					+ "    username CHAR NOT NULL,\r\n"
					+ "    PRIMARY KEY (\r\n" + "        username\r\n"
					+ "    ),\r\n" + "    FOREIGN KEY (\r\n"
					+ "        username\r\n" + "    )\r\n"
					+ "    REFERENCES Users (username) ON DELETE CASCADE \r\n" + ");",
			"CREATE TABLE IF NOT EXISTS Users (\r\n"
					+ "    username CHAR      NOT NULL,\r\n"
					+ "    hsKey    BLOB		NOT NULL,\r\n"
					+ "    salt     BLOB		NOT NULL,\r\n"
					+ "    PRIMARY KEY (\r\n" + "        username\r\n"
					+ "    )\r\n" + ");",
			"CREATE TABLE IF NOT EXISTS Wallets (\r\n"
					+ "    walletID				INTEGER NOT NULL,\r\n"
					+ "    encryptedWalletName	BLOB 	 NOT NULL,\r\n"
					+ "    owner				CHAR	 NOT NULL,\r\n"
					+ "    iv					BLOB	 NOT NULL,\r\n"
					+ "    PRIMARY KEY (\r\n" + "        walletID\r\n"
					+ "    ),\r\n" + "    FOREIGN KEY (\r\n"
					+ "        owner\r\n" + "    )\r\n"
					+ "    REFERENCES Users (username) ON DELETE CASCADE \r\n" + ");",
			"CREATE TABLE IF NOT EXISTS Templates (\r\n"
					+ "    templateID				INTEGER	NOT NULL,\r\n"
					+ "    encryptedTemplateName	BLOB	NOT NULL,\r\n"
					+ "    owner					CHAR	NOT NULL,\r\n"
					+ "    iv						BLOB	NOT NULL,\r\n"
					+ "    PRIMARY KEY (\r\n" + "        templateID\r\n"
					+ "    ),\r\n" + "    FOREIGN KEY (\r\n"
					+ "        owner \r\n" + "    )\r\n"
					+ "    REFERENCES Users (username) ON DELETE CASCADE \r\n" + ");",
			"CREATE TABLE IF NOT EXISTS Customs (\r\n"
					+ "    customID				INTEGER NOT NULL,\r\n"
					+ "    templateID			INTEGER NOT NULL,\r\n"
					+ "    encryptedCustomName	BLOB	NOT NULL,\r\n"
					+ "    iv					BLOB	NOT NULL,\r\n"
					+ "    PRIMARY KEY (\r\n" + "        customID\r\n"
					+ "    ),\r\n" + "    FOREIGN KEY (\r\n"
					+ "        templateID\r\n" + "    )\r\n"
					+ "    REFERENCES Templates (templateID) ON DELETE CASCADE \r\n" + ");",
			"CREATE TABLE IF NOT EXISTS CustomColumns (\r\n"
					+ "    columnID					INTEGER     NOT NULL,\r\n"
					+ "    encryptedColumnName		BLOB		NOT NULL,\r\n"
					+ "    templateID				INTEGER     NOT NULL,\r\n"
					+ "    iv		             	BLOB		NOT NULL,\r\n"
					+ "    isPassword             	BOOL		NOT NULL,\r\n"
					+ "    PRIMARY KEY (\r\n" + "        columnID\r\n"
					+ "    ),\r\n" + "    FOREIGN KEY (\r\n"
					+ "        templateID\r\n" + "    )\r\n"
					+ "    REFERENCES Templates (templateID) ON DELETE CASCADE \r\n" + ");",
			"CREATE TABLE IF NOT EXISTS CustomInWallet (\r\n"
					+ "    customID INTEGER NOT NULL,\r\n"
					+ "    walletID INTEGER NOT NULL,\r\n"
					+ "    PRIMARY KEY (\r\n" + "        customID,\r\n"
					+ "        walletID\r\n" + "    ),\r\n"
					+ "    FOREIGN KEY (\r\n" + "        customID\r\n"
					+ "    )\r\n" + "    REFERENCES Customs (customID) ON DELETE CASCADE,\r\n"
					+ "    FOREIGN KEY (\r\n" + "        walletID\r\n"
					+ "    )\r\n" + "    REFERENCES Wallets (walletID) ON DELETE CASCADE \r\n"
					+ ");",
			"CREATE TABLE IF NOT EXISTS CustomData (\r\n"
					+ "    customID       INTEGER  NOT NULL,\r\n"
					+ "    columnID       INTEGER  NOT NULL,\r\n"
					+ "    encryptedData  BLOB 	 NOT NULL,\r\n"
					+ "    iv		        BLOB	 NOT NULL,\r\n"
					+ "    PRIMARY KEY (\r\n" + "        customID,\r\n"
					+ "		 columnID\r\n" + "    ),\r\n"
					+ "    FOREIGN KEY (\r\n" + "        customID\r\n"
					+ "    )\r\n" + "    REFERENCES Customs (customID) ON DELETE CASCADE \r\n"
					+ "    FOREIGN KEY (\r\n" + "        columnID\r\n"
					+ "    )\r\n"
					+ "    REFERENCES CustomColumns (columnID) ON DELETE CASCADE \r\n" + ");",
			"COMMIT TRANSACTION;\r\n", "PRAGMA foreign_keys = on;\r\n" };
	private String username = null;

	// connection to the database is established using the filename and database drive.
	public DatabaseConnection(String dbFile)
	{
		try             // There are many things that can go wrong in establishing a database connection...
		{         
			Class.forName("org.sqlite.JDBC");                               // ... a missing driver class ...
			connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile); // ... or an error with the file.
			System.out.println("Database connection successfully established.");
			Statement statement = connection.createStatement();
			for(int i=0; i<dbCreationStatement.length; i++){
				statement.addBatch(dbCreationStatement[i]);
			}
			statement.executeBatch();
			System.out.println("Prepared database.");
		} 
		catch (ClassNotFoundException cnfex)    // Catch any database driver error
		{
			System.out.println("Class not found exception: " + cnfex.getMessage());
			Main.fatalError(cnfex, "Database driver error, the program must exit immediately.");
		}
		catch (SQLException exception)          // Catch any database file errors.
		{                        
			System.out.println("Database connection error: " + exception.getMessage());
			Main.fatalError(exception, "Database file error, the program must exit immediately.");
		}

	}

	/* This method is used to prepare each new query. The query isn't executed until later. */
	public PreparedStatement newStatement(String query)
	{
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(query);
		}
		catch (SQLException resultsexception) 
		{
			System.out.println("Database statement error: " + resultsexception.getMessage());
		}
		return statement;
	}

	/* This method is used to actually execute a query that has previously been prepared. */
	public ResultSet runQuery(PreparedStatement statement)
	{               
		try {            
			return statement.executeQuery();           
		}
		catch (SQLException queryexception) 
		{
			System.out.println("Database query error: " + queryexception.getMessage());
			return null;
		}
	}

	/* This method is used to actually execute an update statement that has previously been prepared. */
	public void runUpdate(PreparedStatement statement)
	{               
		try {            
			statement.executeUpdate();           
		}
		catch (SQLException queryexception) 
		{
			System.out.println("Database query error: " + queryexception.getMessage());
		}
	}

	/* Finally, this method is called when the application is terminating to close the database connection. */
	public void disconnect()
	{
		System.out.println("Disconnecting from database.");
		try {
			if (connection != null) connection.close();                        
		} 
		catch (SQLException finalexception) 
		{
			System.out.println("Database disconnection error: " + finalexception.getMessage());
		}        
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void populate(){
		clearJavaData();
		try {
			populateWallets();
			populateTemplates();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void populateWallets() throws SQLException{
		PreparedStatement walletStatement = newStatement("SELECT walletID, encryptedWalletName, iv FROM Wallets WHERE owner=?");
		walletStatement.setString(1, username);
		ResultSet walletResults = runQuery(walletStatement);
		while(walletResults.next()){
			new Wallet(walletResults.getInt("walletID"), walletResults.getBytes("encryptedWalletName"), walletResults.getBytes("iv"));
		}
	}
	
	private void populateTemplates() throws SQLException {
		System.out.println(username);
		PreparedStatement templateStatement = newStatement("SELECT templateID, encryptedTemplateName, iv from Templates where owner=?");
		templateStatement.setString(1, username);
		ResultSet templateResults = runQuery(templateStatement);
		while(templateResults.next()){
			Template newTemplate = new Template(templateResults.getInt("templateID"), templateResults.getBytes("encryptedTemplateName"), templateResults.getBytes("iv"));
			
			populateColumnsForTemplate(newTemplate);
			
			populateCustomsForTemplate(newTemplate);
		}
	}

	private void populateCustomsForTemplate(Template template) throws SQLException {
		PreparedStatement customStatement = newStatement("SELECT customID, encryptedCustomName, iv from Customs where templateID=?");
		customStatement.setInt(1, template.getID());
		ResultSet customResults = runQuery(customStatement);
		while(customResults.next()){
			Custom newCustom = new Custom(customResults.getInt("customID"), template, customResults.getBytes("encryptedCustomName"), customResults.getBytes("iv"));

			for(Column column:template.getColumns()) populateDataForColumnOfCustom(column, newCustom);
				
			linkWalletsToCustom(newCustom);
		}
	}

	private void linkWalletsToCustom(Custom custom) throws SQLException {
		PreparedStatement walletLinkStatement = newStatement("SELECT Wallets.walletID from CustomInWallet INNER JOIN Wallets ON Wallets.walletID=CustomInWallet.walletID where CustomInWallet.customID=?");
		walletLinkStatement.setInt(1, custom.getID());
		ResultSet walletLinkResults = runQuery(walletLinkStatement);
		while(walletLinkResults.next()){
			for(Wallet wallet:Wallet.getAllWallets()){
				if(wallet.getID()==walletLinkResults.getInt("walletID")){
					wallet.addCustomFromDbStatement(custom);
					break;
				}
			}
		}
	}

	private void populateColumnsForTemplate(Template template) throws SQLException {
		PreparedStatement columnStatement = newStatement("SELECT columnID, encryptedColumnName, iv, isPassword from CustomColumns where templateID=?");
		columnStatement.setInt(1, template.getID());
		ResultSet columnResults = runQuery(columnStatement);
		while(columnResults.next()){
			new Column(columnResults.getInt("columnID"), template, columnResults.getBytes("encryptedColumnName"), columnResults.getBytes("iv"), columnResults.getBoolean("isPassword"));
		}
	}

	private void populateDataForColumnOfCustom(Column column, Custom custom) throws SQLException {
		PreparedStatement dataStatement = newStatement("SELECT customID, columnID, encryptedData, iv from CustomData where columnID=? and customID=?");
		dataStatement.setInt(1, column.getID());
		dataStatement.setInt(2, custom.getID());
		ResultSet dataResults = runQuery(dataStatement);
		while(dataResults.next()){
			new Data(custom, column, dataResults.getBytes("encryptedData"), dataResults.getBytes("iv"));
		}
	}

	private void clearJavaData(){
		Custom.setAllCustoms(new ArrayList<>());
		Column.setAllColumns(new ArrayList<>());
		Data.setAllValues(new ArrayList<>());
		Template.setAllTemplates(new ArrayList<>());
		Wallet.setAllWallets(new ArrayList<>());
	}
}