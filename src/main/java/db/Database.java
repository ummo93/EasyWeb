package db;
import java.sql.*;
import java.util.HashMap;

/**
 * Database class
 */

public class Database {

    private String host;
    private String port;
    private String schema;
    private String username;
    private String pass;

    public Database (HashMap<String, String> credentials) throws Exception{
        if (credentials.size() >= 5) {
            this.host = credentials.get("host");
            this.port = credentials.get("port");
            this.schema = credentials.get("schema");
            this.username = credentials.get("username");
            this.pass = credentials.get("pass");
            System.out.println("Creating a MySQL environment successfull");
        } else {
            throw new Exception("You forgot to specify one of the parameters. You need define: host, port, schema, username, pass");
        }
    }

    private Connection getConnect() {

        Connection dbConnection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            dbConnection = DriverManager.getConnection("jdbc:" + this.host + ":" + this.port + "/" + this.schema, this.username, this.pass);
            System.out.println("Established connection to MySQL server");
            return dbConnection;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dbConnection;
    }

    public void addRecord(String title, String description, String imageURL,
                                 String shortDescription, String autor) throws SQLException {

        Connection dbConnection = null;
        PreparedStatement stmt = null;

        try {

            dbConnection = this.getConnect();

            String selectTableSQL = "INSERT INTO `records` (" +
                    "`TITLE`,`DESCRIPTION`,`IMAGE`,`SHORTDESCR`,`AUTOR`) VALUES (?,?,?,?,?)";

            stmt = dbConnection.prepareStatement(selectTableSQL);

            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, imageURL);
            stmt.setString(4, shortDescription);
            stmt.setString(5, autor);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (dbConnection != null) {
                dbConnection.close();
            }
        }
    }
}