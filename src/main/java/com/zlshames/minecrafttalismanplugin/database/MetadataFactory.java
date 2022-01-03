package com.zlshames.minecrafttalismanplugin.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MetadataFactory {

    private Connection db;

    public MetadataFactory(Connection db) {
        this.db = db;
    }

    public void createTables() {
        try {
            this.createPlayerTable();
            this.createPlayerStatsTable();
            this.createLocationsTable();
            this.createDeathLogsTable();
        } catch (Exception ex) {
            System.err.println("Failed to create tables!");
            System.err.println(ex.getMessage());
        }
    }

    public void createPlayerTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS players (" +
                "id VARCHAR(36) UNIQUE PRIMARY KEY, " +
                "name VARCHAR(50) UNIQUE NOT NULL, " +
                "character VARCHAR(100) DEFAULT NULL, " +
                "lives INTEGER DEFAULT 3, " +
                "strength INTEGER DEFAULT 1, " +
                "craft INTEGER DEFAULT 1);";
        PreparedStatement statement = this.db.prepareStatement(sql);
        statement.executeUpdate();
    }

    public void createPlayerStatsTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS player_stats (" +
                "id VARCHAR(36) NOT NULL, " +
                "name VARCHAR(50) NOT NULL, " +
                "value VARCHAR(50) DEFAULT NULL, " +
                "UNIQUE(id, name));";
        PreparedStatement statement = this.db.prepareStatement(sql);
        statement.executeUpdate();
    }

    public void createLocationsTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS locations (" +
                "id VARCHAR(36) NOT NULL, " +
                "name VARCHAR(50) NOT NULL, " +
                "location_x VARCHAR(10) NOT NULL, " +
                "location_y VARCHAR(10) NOT NULL, " +
                "location_z VARCHAR(10) NOT NULL, " +
                "UNIQUE(id, name));";
        PreparedStatement statement = this.db.prepareStatement(sql);
        statement.executeUpdate();
    }

    public void createDeathLogsTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS death_logs (" +
                "id VARCHAR(36) NOT NULL, " +
                "location_x VARCHAR(10) NOT NULL, " +
                "location_y VARCHAR(10) NOT NULL, " +
                "location_z VARCHAR(10) NOT NULL, " +
                "date INTEGER NOT NULL, " +
                "UNIQUE(id));";
        PreparedStatement statement = this.db.prepareStatement(sql);
        statement.executeUpdate();
    }
}
