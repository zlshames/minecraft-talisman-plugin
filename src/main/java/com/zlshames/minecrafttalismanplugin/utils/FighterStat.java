package com.zlshames.minecrafttalismanplugin.utils;

import java.util.Arrays;
import java.util.List;

public class FighterStat {
    public static final String SNOWBALL_HITS = "Snowball Hits";
    public static final String SNOWBALL_HEADSHOTS = "Snowball Headshots";
    public static final String SNOWBALL_KILLS = "Snowball Kills";
    public static final String SNOWBALL_WINS = "Snowball Wins";
    public static final String SNOWBALL_LOSSES = "Snowball Loses";

    public static List<String> all() {
        return Arrays.asList(
                FighterStat.SNOWBALL_HITS,
                FighterStat.SNOWBALL_HEADSHOTS,
                FighterStat.SNOWBALL_KILLS,
                FighterStat.SNOWBALL_WINS,
                FighterStat.SNOWBALL_LOSSES
        );
    }
}
