package com.zlshames.minecrafttalismanplugin.database.models;

import com.zlshames.minecrafttalismanplugin.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerStat {

    public String id;
    public String name;
    public String value;

    public static List<PlayerStat> findPlayerStats(String uuid) throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return null;

        // Execute the SQL statement
        String sql = "SELECT * FROM player_stats WHERE id = ?;";
        PreparedStatement statement = db.prepareStatement(sql);
        statement.setString(1, uuid);
        ResultSet results = statement.executeQuery();

        // Read and parse the results
        List<PlayerStat> stats = new ArrayList<>();
        while (results.next()) {
            stats.add(PlayerStat.load(results));
        }

        return stats;
    }

    public static PlayerStat getPlayerStat(String uuid, String type) throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return null;

        // Execute the SQL statement
        String sql = "SELECT * FROM player_stats WHERE id = ? AND name = ?;";
        PreparedStatement statement = db.prepareStatement(sql);
        statement.setString(1, uuid);
        statement.setString(2, type);
        ResultSet results = statement.executeQuery();

        // Read and parse the results
        while (results.next()) {
            return PlayerStat.load(results);
        }

        return null;
    }

    public static boolean statExists(String uuid, String type) throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return false;

        // Execute the SQL statement
        String sql = "SELECT * FROM player_stats WHERE id = ? AND name = ?;";
        PreparedStatement statement = db.prepareStatement(sql);
        statement.setString(1, uuid);
        statement.setString(2, type);
        ResultSet results = statement.executeQuery();
        return results.next();
    }

    public static void setPlayerStat(String uuid, String type, String value) throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return;

        if (!PlayerStat.statExists(uuid, type)) {
            createNew(uuid, type, value);
        } else {
            // Execute the SQL statement
            String sql = "UPDATE player_stats SET value = ? WHERE id = ? AND name = ?;";
            PreparedStatement statement = db.prepareStatement(sql);
            statement.setString(1, value);
            statement.setString(2, uuid);
            statement.setString(3, type);
            statement.executeUpdate();
        }
    }

    public static void incrementStat(String uuid, String type) throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return;

        Integer currentValue = null;
        if (!PlayerStat.statExists(uuid, type)) {
            createNew(uuid, type, "0");
            currentValue = 0;
        } else {
            PlayerStat stat = PlayerStat.getPlayerStat(uuid, type);
            if (stat == null || stat.value == null) {
                currentValue = 0;
            } else {
                currentValue = Integer.parseInt(stat.value);
            }
        }

        PlayerStat.setPlayerStat(uuid, type, String.valueOf(currentValue + 1));
    }

    public static PlayerStat createNew(String uuid, String type, String value) throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return null;

        // Execute the SQL statement
        String sql = "INSERT INTO player_stats (id, name, value) VALUES(?, ?, ?);";
        PreparedStatement statement = db.prepareStatement(sql);
        statement.setString(1, uuid);
        statement.setString(2, type);
        statement.setString(3, value);
        statement.executeUpdate();

        // Fetch the newly inserted player
        return PlayerStat.getPlayerStat(uuid, type);
    }

    public static PlayerStat load(ResultSet dbResult) throws SQLException {
        PlayerStat stat = new PlayerStat();
        stat.id = dbResult.getString("id");
        stat.name = dbResult.getString("name");
        stat.value = dbResult.getString("value");
        return stat;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(this.id).append(" (");
        str.append(this.name).append(": ").append(this.value);
        return str.toString();
    }
}
