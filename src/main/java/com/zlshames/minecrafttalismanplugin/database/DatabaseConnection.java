package com.zlshames.minecrafttalismanplugin.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author sqlitetutorial.net
 */
public class DatabaseConnection {

    public static Connection conn = null;

    /**
     * Connect to a sample database
     */
    public void connect() {
        try {
            // Connect to the database.
            // This will create it if it doesn't exist
            String url = "jdbc:sqlite:talisman.db";
            DatabaseConnection.conn = DriverManager.getConnection(url);
            this.sync();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void sync() {
        if (this.conn == null) return;

        MetadataFactory factory = new MetadataFactory(this.conn);
        factory.createTables();
    }

    public void disconnect() throws SQLException {
        if (this.conn != null) {
            this.conn.close();
        }
    }
}
