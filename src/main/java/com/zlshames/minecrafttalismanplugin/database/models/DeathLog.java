package com.zlshames.minecrafttalismanplugin.database.models;

import com.zlshames.minecrafttalismanplugin.database.DatabaseConnection;
import com.zlshames.minecrafttalismanplugin.utils.TimeAgo;
import com.zlshames.minecrafttalismanplugin.utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;

public class DeathLog {

    public String id;
    public Double locationX;
    public Double locationY;
    public Double locationZ;
    public Date date;

    public static DeathLog find(String uuid) throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return null;

        // Execute the SQL statement
        String sql = "SELECT * FROM death_logs WHERE id = ?;";
        PreparedStatement statement = db.prepareStatement(sql);
        statement.setString(1, uuid);
        ResultSet results = statement.executeQuery();

        // Read and parse the results
        while (results.next()) {
            return DeathLog.load(results);
        }

        return null;
    }

    public static void add(String uuid, Double x, Double y, Double z, Date date) throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return;

        Boolean exists = DeathLog.deathExists(uuid);
        if (exists) {
            DeathLog.update(uuid, x, y, z, date);
        } else {
            // Execute the SQL statement
            String sql = "INSERT INTO death_logs (id, location_x, location_y, location_z, date) VALUES(?, ?, ?, ?, ?);";
            PreparedStatement statement = db.prepareStatement(sql);
            statement.setString(1, uuid);
            statement.setString(2, String.valueOf(x));
            statement.setString(3, String.valueOf(y));
            statement.setString(4, String.valueOf(z));
            statement.setLong(5, date.getTime());
            statement.executeUpdate();
        }
    }

    public static void update(String uuid, Double x, Double y, Double z, Date date) throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return;

        // Execute the SQL statement
        String sql = "UPDATE death_logs SET location_x = ?, location_y = ?, location_z = ?, date = ? WHERE id = ?;";
        PreparedStatement statement = db.prepareStatement(sql);
        statement.setString(1, String.valueOf(x));
        statement.setString(2, String.valueOf(y));
        statement.setString(3, String.valueOf(z));
        statement.setLong(4, date.getTime());
        statement.setString(5, uuid);
        statement.executeUpdate();
    }

    public static DeathLog load(ResultSet dbResult) throws SQLException {
        DeathLog log = new DeathLog();
        log.id = dbResult.getString("id");
        log.locationX = Double.parseDouble(dbResult.getString("location_x"));
        log.locationY = Double.parseDouble(dbResult.getString("location_y"));
        log.locationZ = Double.parseDouble(dbResult.getString("location_z"));
        log.date = Date.from(Instant.ofEpochMilli(dbResult.getLong("date")));
        return log;
    }

    public static boolean deathExists(String uuid) throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return false;

        // Execute the SQL statement
        String sql = "SELECT * FROM death_logs WHERE id = ?;";
        PreparedStatement statement = db.prepareStatement(sql);
        statement.setString(1, uuid);
        ResultSet results = statement.executeQuery();
        return results.next();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Last Death -> ");
        str.append("X: ").append(Math.round(this.locationX)).append(", ");
        str.append("Y: ").append(Math.round(this.locationY)).append(", ");
        str.append("Z: ").append(Math.round(this.locationZ));

        // Relative date
        str.append(" (");
        str.append(TimeAgo.toRelative(this.date, new Date(new Date().getTime())));
        str.append(")");
        return str.toString();
    }
}
