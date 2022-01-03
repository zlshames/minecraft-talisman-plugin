package com.zlshames.minecrafttalismanplugin.database.models;

import com.zlshames.minecrafttalismanplugin.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Location {

    public String id;
    public String name;
    public Double locationX;
    public Double locationY;
    public Double locationZ;

    public Location() {
        // Empty object
    }

    public Location(String name, Double x, Double y, Double z) {
        this.name = name;
        this.locationX = x;
        this.locationY = y;
        this.locationZ = z;
    }

    public static List<Location> findAllForUser(String uuid) throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return null;

        // Execute the SQL statement
        String sql = "SELECT * FROM locations WHERE id = ?;";
        PreparedStatement statement = db.prepareStatement(sql);
        statement.setString(1, uuid);
        ResultSet results = statement.executeQuery();

        // Read and parse the results
        List<Location> locations = new ArrayList<>();
        while (results.next()) {
            locations.add(Location.load(results));
        }

        return locations;
    }

    public static List<Location> findAll() throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return null;

        // Execute the SQL statement
        String sql = "SELECT * FROM locations;";
        PreparedStatement statement = db.prepareStatement(sql);
        ResultSet results = statement.executeQuery();

        // Read and parse the results
        List<Location> locations = new ArrayList<>();
        while (results.next()) {
            locations.add(Location.load(results));
        }

        return locations;
    }

    public static List<Location> findAllGlobal() throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return null;

        // Execute the SQL statement
        String sql = "SELECT * FROM locations WHERE id = ?;";
        PreparedStatement statement = db.prepareStatement(sql);
        statement.setString(1, "1");
        ResultSet results = statement.executeQuery();

        // Read and parse the results
        List<Location> locations = new ArrayList<>();
        while (results.next()) {
            locations.add(Location.load(results));
        }

        return locations;
    }

    public static Location findByName(String uuid, String name) throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return null;

        // Execute the SQL statement
        String sql = "SELECT * FROM locations WHERE id = ? AND name = ?;";
        PreparedStatement statement = db.prepareStatement(sql);
        statement.setString(1, uuid);
        statement.setString(2, name);
        ResultSet results = statement.executeQuery();

        // Read and parse the results
        while (results.next()) {
            return Location.load(results);
        }

        return null;
    }

    public static Location findByNameGlobal(String name) throws SQLException {
        return findByName("1", name);
    }

    public static void createNew(String uuid, String name, Double x, Double y, Double z) throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return;

        // Execute the SQL statement
        String sql = "INSERT INTO locations (id, name, location_x, location_y, location_z) VALUES(?, ?, ?, ?, ?);";
        PreparedStatement statement = db.prepareStatement(sql);
        statement.setString(1, uuid);
        statement.setString(2, name);
        statement.setString(3, String.valueOf(x));
        statement.setString(4, String.valueOf(y));
        statement.setString(5, String.valueOf(z));
        statement.executeUpdate();
    }

    public static void createNewGlobal(String name, Double x, Double y, Double z) throws SQLException {
        createNew("1", name, x, y ,z);
    }

    public static void remove(String uuid, String name) throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return;

        // Execute the SQL statement
        String sql = "DELETE FROM locations WHERE id = ? AND name = ?;";
        PreparedStatement statement = db.prepareStatement(sql);
        statement.setString(1, uuid);
        statement.setString(2, name);
        statement.executeUpdate();
    }

    public static void removeGlobal(String name) throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return;

        // Execute the SQL statement
        String sql = "DELETE FROM locations WHERE id = ? AND name = ?;";
        PreparedStatement statement = db.prepareStatement(sql);
        statement.setString(1, "1");
        statement.setString(2, name);
        statement.executeUpdate();
    }

    public static Location load(ResultSet dbResult) throws SQLException {
        Location location = new Location();
        location.id = dbResult.getString("id");
        location.name = dbResult.getString("name");
        location.locationX = Double.parseDouble(dbResult.getString("location_x"));
        location.locationY = Double.parseDouble(dbResult.getString("location_y"));
        location.locationZ = Double.parseDouble(dbResult.getString("location_z"));
        return location;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(this.name).append(" (");
        str.append("X: ").append(Math.round(this.locationX)).append(", ");
        str.append("Y: ").append(Math.round(this.locationY)).append(", ");
        str.append("Z: ").append(Math.round(this.locationZ)).append(")");
        return str.toString();
    }
}
