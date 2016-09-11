package erj4.as;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class DatabaseConnection {

    private Connection conection = null;
    private String[] dbCreationStatement = {"PRAGMA foreign_keys = off;\r\n", 
    		"BEGIN TRANSACTION;\r\n",
    		"CREATE TABLE IF NOT EXISTS Data (ID INTEGER PRIMARY KEY, User STRING REFERENCES Users (User) ON DELETE CASCADE ON UPDATE CASCADE, EncryptedUsername STRING, EncryptedPassword STRING, EncryptedNotes STRING, IV STRING NOT NULL);\r\n",
    		"CREATE TABLE IF NOT EXISTS Users (User STRING PRIMARY KEY UNIQUE, SHPass STRING, Salt STRING NOT NULL);\r\n",
    		"COMMIT TRANSACTION;\r\n",
    		"PRAGMA foreign_keys = on;"};
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

}