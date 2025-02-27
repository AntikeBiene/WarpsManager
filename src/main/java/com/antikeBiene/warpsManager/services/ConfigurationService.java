package com.antikeBiene.warpsManager.services;

import com.antikeBiene.warpsManager.WarpsManager;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.time.ZoneOffset;

public class ConfigurationService {

    private static FileConfiguration config;

    public static void init() {
        config = WarpsManager.getPlugin().getConfig();
    }

    public static ZoneOffset getUTCOffset() {
        Integer offsetInt = config.getInt("utc-offset");
        return ZoneOffset.ofHours(offsetInt);
    }

    public static NamedTextColor getColor(String caseID) {
        String colorString = config.getString("message-" + caseID);
        if (colorString == null) colorString = "white";
        return NamedTextColor.NAMES.valueOr(colorString.toLowerCase(), NamedTextColor.WHITE);
    }

    public static Integer getStandardWaypointLifetime() {
        return config.getInt("waypoint-standard-lifetime");
    }

    public static Integer getWaypointReminder() {
        return config.getInt("waypoint-death-reminder");
    }

}
