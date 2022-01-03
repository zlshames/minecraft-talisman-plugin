package com.zlshames.minecrafttalismanplugin.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class JsonFile {
    private String path;

    public JsonFile(String path) {
        this.path = path;

        // Make sure the directory exists
        File file = new File(path.substring(0, path.lastIndexOf(File.separator)));
        if (!file.exists()) {
            file.mkdirs();
        }

        File configFile = new File(path);
        if (!configFile.exists()) {
            this.save("{\"\"}");

            try {
                this.setDefaults();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void setDefaults() throws IOException, ParseException {
        JSONObject defaultConfig = new JSONObject();
        defaultConfig.put("velocity_factor", 0.3);
        defaultConfig.put("headshot", true);
        defaultConfig.put("snowball_damage", 0.5);
        this.saveJsonObject(defaultConfig);
    }

    public JSONObject readJsonObject() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject data = (JSONObject) parser.parse(new FileReader(path));
        return data;
    }

    public JSONArray readJsonArray() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONArray data = (JSONArray) parser.parse(new FileReader(path));
        return data;
    }

    public boolean saveJsonObject(JSONObject jsonObject) {
        return save(jsonObject.toString());
    }

    public boolean saveJsonArray(JSONArray jsonArray) {
        return save(jsonArray.toString());
    }

    private boolean save(String string) {


        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(string);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
