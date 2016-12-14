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

	private Connection conection = null;
	private String[] dbCreationStatement = {
			"PRAGMA foreign_keys = off;\r\n",
			"BEGIN TRANSACTION;\r\n",
			"CREATE TABLE IF NOT EXISTS UserSettings (\r\n"
					+ "    username CHAR NOT NULL,\r\n"
					+ "    PRIMARY KEY (\r\n" + "        username\r\n"
					+ "    ),\r\n" + "    FOREIGN KEY (\r\n"
					+ "        username\r\n" + "    )\r\n"
					+ "    REFERENCES Users (username) \r\n" + ");",
			"CREATE TABLE IF NOT EXISTS Users (\r\n"
					+ "    username CHAR      NOT NULL,\r\n"
					+ "    hsKey    BLOB		NOT NULL,\r\n"
					+ "    salt     BLOB		NOT NULL,\r\n"
					+ "    PRIMARY KEY (\r\n" + "        username\r\n"
					+ "    )\r\n" + ");",
			"CREATE TABLE IF NOT EXISTS Wallets (\r\n"
					+ "    walletID            INTEGER NOT NULL,\r\n"
					+ "    encryptedWalletName BLOB 	 NOT NULL,\r\n"
					+ "    username            CHAR	 NOT NULL,\r\n"
					+ "    iv		             BLOB	 NOT NULL,\r\n"
					+ "    PRIMARY KEY (\r\n" + "        walletID\r\n"
					+ "    ),\r\n" + "    FOREIGN KEY (\r\n"
					+ "        username\r\n" + "    )\r\n"
					+ "    REFERENCES Users (username) \r\n" + ");",
			"CREATE TABLE IF NOT EXISTS Templates (\r\n"
					+ "    templateID            INTEGER NOT NULL,\r\n"
					+ "    encryptedTemplateName BLOB	   NOT NULL,\r\n"
					+ "    owner				   CHAR    NOT NULL,\r\n"
					+ "    iv		           	   BLOB	   NOT NULL,\r\n"
					+ "    PRIMARY KEY (\r\n" + "        templateID\r\n"
					+ "    ),\r\n" + "    FOREIGN KEY (\r\n"
					+ "        owner \r\n" + "    )\r\n"
					+ "    REFERENCES Users (username) \r\n" + ");",
			"CREATE TABLE IF NOT EXISTS Customs (\r\n"
					+ "    customID      			INTEGER NOT NULL,\r\n"
					+ "    templateID	   			INTEGER NOT NULL,\r\n"
					+ "    encryptedCustomName 	BLOB	NOT NULL,\r\n"
					+ "    iv		       			BLOB	NOT NULL,\r\n"
					+ "    PRIMARY KEY (\r\n" + "        customID\r\n"
					+ "    ),\r\n" + "    FOREIGN KEY (\r\n"
					+ "        templateID\r\n" + "    )\r\n"
					+ "    REFERENCES Templates (templateID) \r\n" + ");",
			"CREATE TABLE IF NOT EXISTS CustomColumns (\r\n"
					+ "    columnID			    INTEGER     NOT NULL,\r\n"
					+ "    encryptedColumnName    BLOB		NOT NULL,\r\n"
					+ "    templateID             INTEGER     NOT NULL,\r\n"
					+ "    iv		             	BLOB		NOT NULL,\r\n"
					+ "    PRIMARY KEY (\r\n" + "        templateID\r\n"
					+ "    ),\r\n" + "    FOREIGN KEY (\r\n"
					+ "        templateID\r\n" + "    )\r\n"
					+ "    REFERENCES Templates (templateID) \r\n" + ");",
			"CREATE TABLE IF NOT EXISTS CustomInWallet (\r\n"
					+ "    customID INTEGER NOT NULL,\r\n"
					+ "    walletID INTEGER NOT NULL,\r\n"
					+ "    PRIMARY KEY (\r\n" + "        customID,\r\n"
					+ "        walletID\r\n" + "    ),\r\n"
					+ "    FOREIGN KEY (\r\n" + "        customID\r\n"
					+ "    )\r\n" + "    REFERENCES Customs (customID),\r\n"
					+ "    FOREIGN KEY (\r\n" + "        walletID\r\n"
					+ "    )\r\n" + "    REFERENCES Wallets (walletID) \r\n"
					+ ");",
			"CREATE TABLE IF NOT EXISTS CustomData (\r\n"
					+ "    customID       INTEGER  NOT NULL,\r\n"
					+ "    columnID       INTEGER  NOT NULL,\r\n"
					+ "    encryptedData  BLOB 	 NOT NULL,\r\n"
					+ "    iv		        BLOB	 NOT NULL,\r\n"
					+ "    PRIMARY KEY (\r\n" + "        customID,\r\n"
					+ "		 columnID\r\n" + "    ),\r\n"
					+ "    FOREIGN KEY (\r\n" + "        customID\r\n"
					+ "    )\r\n" + "    REFERENCES Customs (customID) \r\n"
					+ "    FOREIGN KEY (\r\n" + "        columnID\r\n"
					+ "    )\r\n"
					+ "    REFERENCES CustomColumns (columnID) \r\n" + ");",
			"COMMIT TRANSACTION;\r\n", "PRAGMA foreign_keys = on;\r\n" };
	private String username = null;

	/* This method is the constructor. When a new DatabaseConnection object is created a connection
	 * to the database is established using the filename and database drive. */
	public DatabaseConnection(String dbFile)
	{
		try             // There are many things that can go wrong in establishing a database connection...
		{         
			Class.forName("org.sqlite.JDBC");                               // ... a missing driver class ...
			conection = DriverManager.getConnection("jdbc:sqlite:" + dbFile); // ... or an error with the file.
			System.out.println("Database connection successfully established.");
			Statement statement = conection.createStatement();
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
			statement = conection.prepareStatement(query);
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
			if (conection != null) conection.close();                        
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
		try {
			PreparedStatement walletStatement = newStatement("SELECT walletID, encryptedWalletName, iv FROM wallet WHERE user=?");
			walletStatement.setString(1, username);
			ResultSet walletResults = runQuery(walletStatement);
			ArrayList<Wallet> wallets = new ArrayList<Wallet>();
			Wallet.setAllWallets(wallets);
			while(walletResults.next()){
				Wallet newWallet = new Wallet(walletResults.getInt("walletID"), walletResults.getBytes("encryptedWalletName"), walletResults.getBytes("iv"));
				wallets.add(newWallet);
			}
			
			PreparedStatement templateStatement = newStatement("SELECT templateID, encryptedTemplateName, iv from Templates where user=?");
			templateStatement.setString(1, username);
			ResultSet templateResults = runQuery(templateStatement);
			ArrayList<Template> templates = new ArrayList<Template>();
			Template.setAllTemplates(templates);
			while(templateResults.next()){
				Template newTemplate = new Template(templateResults.getInt("templateID"), templateResults.getBytes("encryptedTemplateName"), templateResults.getBytes("iv"));
				templates.add(newTemplate);

				PreparedStatement customStatement = newStatement("SELECT customID, encryptedCustomName, iv from Customs where templateID=?");
				customStatement.setInt(1, newTemplate.getID());
				ResultSet customResults = runQuery(customStatement);
				ArrayList<Custom> customs = new ArrayList<Custom>();
				Custom.setAllCustoms(customs);
				while(customResults.next()){
					Custom newCustom = new Custom(customResults.getInt("customID"), newTemplate, customResults.getBytes("encryptedCustomName"), customResults.getBytes("iv"));
					customs.add(newCustom);

					PreparedStatement columnStatement = newStatement("SELECT columnID, encryptedColumnName, iv from Columns where templateID=?");
					columnStatement.setInt(1, newTemplate.getID());
					ResultSet columnResults = runQuery(columnStatement);
					ArrayList<CustomColumn> columns = new ArrayList<CustomColumn>();
					CustomColumn.setAllColumns(columns);
					while(columnResults.next()){
						CustomColumn newColumn = new CustomColumn(columnResults.getInt("columnID"), newTemplate, columnResults.getBytes("encryptedColumnName"), columnResults.getBytes("iv"));
						columns.add(newColumn);
						
						PreparedStatement dataStatement = newStatement("SELECT customID, columnID, encryptedData, iv from CustomData where columnID=? and customID=?");
						dataStatement.setInt(1, newColumn.getID());
						dataStatement.setInt(2, newCustom.getID());
						ResultSet dataResults = runQuery(dataStatement);
						ArrayList<CustomData> data = new ArrayList<CustomData>();
						CustomData.setAllValues(data);
						while(dataResults.next()){
							CustomData newData = new CustomData(newCustom, newColumn, dataResults.getBytes("encryptedData"), dataResults.getBytes("iv"));
							data.add(newData);
						}
					}
						
					PreparedStatement walletLinkStatement = newStatement("SELECT Wallet.walletID from CustomInWallet INNER JOIN Wallet ON Wallet.walletID=CustomInWallet.walletID where CustomInWallet.customID=?");
					walletLinkStatement.setInt(1, newCustom.getID());
					ResultSet walletLinkResults = runQuery(walletLinkStatement);
					while(walletLinkResults.next()){
						for(Wallet wallet:wallets){
							if(wallet.getID()==walletLinkResults.getInt("Wallet.walletID")){
								newCustom.addToWallet(wallet);
								break;
							}
						}
					}
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
}