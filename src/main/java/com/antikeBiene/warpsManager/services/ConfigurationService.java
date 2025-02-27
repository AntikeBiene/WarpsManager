package com.antikeBiene.warpsManager.services;

import com.antikeBiene.warpsManager.WarpsManager;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.configuration.file.FileConfiguration;

import java.time.ZoneOffset;

public class ConfigurationService {

    private static FileConfiguration config;

    public static void init() {
        config = WarpsManager.getPlugin().getConfig();
    }

    public static ZoneOffset getUTCOffset() {
        String offsetString = config.getString("utc-offset");
        Integer offsetInt;
        try {
            assert offsetString != null;
            offsetInt = Integer.parseInt(offsetString);
        } catch (NumberFormatException e) {
            offsetInt = 0;
        }
        return ZoneOffset.ofHours(offsetInt);
    }

    public static TextComponent getComponent(String componentID) {
        return null;
    }

}
