package com.zlshames.minecrafttalismanplugin.utils;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

public class Utils {
    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static boolean isStringInt(String s) {
        if (s == null) return false;

        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isStringDouble(String s) {
        if (s == null) return false;

        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static String stringListToSingleString(String[] args) {
        return Arrays.asList(args).stream().collect(Collectors.joining(" "));
    }

    public static Long getEpochMillis() {
        return Instant.now().toEpochMilli();
    }

    public static String epochToTimestamp(Long epochMillis) {
        Instant ins = Instant.ofEpochMilli(epochMillis);
        Date myDate = Date.from(ins);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
        return formatter.format(myDate);
    }
}
