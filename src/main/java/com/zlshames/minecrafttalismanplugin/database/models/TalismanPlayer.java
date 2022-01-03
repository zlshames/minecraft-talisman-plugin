package com.zlshames.minecrafttalismanplugin.database.models;

import com.zlshames.minecrafttalismanplugin.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TalismanPlayer {

    public String id;
    public String name;
    public String character;
    public Integer lives;
    public Integer strength;
    public Integer craft;

    public static TalismanPlayer findById(String uuid) throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return null;

        // Execute the SQL statement
        String sql = "SELECT * FROM players WHERE id = ?;";
        PreparedStatement statement = db.prepareStatement(sql);
        statement.setString(1, uuid);
        ResultSet results = statement.executeQuery();

        // Read and parse the results
        while (results.next()) {
            return TalismanPlayer.load(results);
        }

        // If we didn't have a result, return null
        return null;
    }

    public static TalismanPlayer findByName(String name) throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return null;

        // Execute the SQL statement
        String sql = "SELECT * FROM players WHERE name = ?;";
        PreparedStatement statement = db.prepareStatement(sql);
        statement.setString(1, name);
        ResultSet results = statement.executeQuery();

        // Read and parse the results
        while (results.next()) {
            return TalismanPlayer.load(results);
        }

        // If we didn't have a result, return null
        return null;
    }

    public void removeLife() throws SQLException {
        this.setLives(this.lives - 1);
    }

    public void addLife() throws SQLException {
        this.setLives(this.lives + 1);
    }

    public void removeStrengthLevel() throws SQLException {
        this.setStrengthLevel(this.strength - 1);
    }

    public void addStrengthLevel() throws SQLException {
        this.setStrengthLevel(this.strength + 1);
    }

    public void removeCraftLevel() throws SQLException {
        this.setCraftLevel(this.craft - 1);
    }

    public void addCraftLevel() throws SQLException {
        this.setCraftLevel(this.craft + 1);
    }

    public void setLives(Integer lives) throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return;

        // Execute the SQL statement
        String sql = "UPDATE players SET lives = ? WHERE id = ?;";
        PreparedStatement statement = db.prepareStatement(sql);
        statement.setInt(1, lives);
        statement.setString(2, this.id);
        statement.executeUpdate();

        // Save the new lives count
        this.lives = lives;
    }

    public void setStrengthLevel(Integer strength) throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return;

        // Execute the SQL statement
        String sql = "UPDATE players SET strength = ? WHERE id = ?;";
        PreparedStatement statement = db.prepareStatement(sql);
        statement.setInt(1, strength);
        statement.setString(2, this.id);
        statement.executeUpdate();

        // Save the new strength level
        this.strength = strength;
    }

    public void setCraftLevel(Integer craft) throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return;

        // Execute the SQL statement
        String sql = "UPDATE players SET craft = ? WHERE id = ?;";
        PreparedStatement statement = db.prepareStatement(sql);
        statement.setInt(1, craft);
        statement.setString(2, this.id);
        statement.executeUpdate();

        // Save the new craft level
        this.craft = craft;
    }

    public void setCharacterType(String characterType) throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return;

        // Execute the SQL statement
        String sql = "UPDATE players SET character = ? WHERE id = ?;";
        PreparedStatement statement = db.prepareStatement(sql);
        statement.setString(1, characterType);
        statement.setString(2, this.id);
        statement.executeUpdate();

        // Save the new character type
        this.character = characterType;
    }

    public static TalismanPlayer createNew(String uuid, String name) throws SQLException {
        Connection db = DatabaseConnection.conn;
        if (db == null) return null;

        // Execute the SQL statement
        String sql = "INSERT INTO players (id, name) VALUES(?, ?);";
        PreparedStatement statement = db.prepareStatement(sql);
        statement.setString(1, uuid);
        statement.setString(2, name);
        statement.executeUpdate();

        // Fetch the newly inserted player
        return TalismanPlayer.findById(uuid);
    }

    public static TalismanPlayer load(ResultSet dbResult) throws SQLException {
        TalismanPlayer player = new TalismanPlayer();
        player.id = dbResult.getString("id");
        player.name = dbResult.getString("name");
        player.lives = dbResult.getInt("lives");
        player.strength = dbResult.getInt("strength");
        player.craft = dbResult.getInt("craft");
        return player;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(this.name).append(" (");
        str.append("Character: ").append(this.character).append(", ");
        str.append("Lives: ").append(this.lives).append(", ");
        str.append("Strength: ").append(this.strength).append(", ");
        str.append("Craft: ").append(this.craft).append(")");
        return str.toString();
    }
}
