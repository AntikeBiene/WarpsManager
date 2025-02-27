package com.antikeBiene.warpsManager.services;

import com.antikeBiene.warpsManager.WarpsManager;
import com.antikeBiene.warpsManager.models.Waypoint;
import com.antikeBiene.warpsManager.modules.WarpsManagerGsonBuilderModule;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;

import java.io.*;
import java.time.Instant;
import java.util.*;

public class WaypointsService {

    private static Map<String, Waypoint> waypoints = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public static void init() {
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(WarpsManager.getPlugin(), () -> {
            for (Map.Entry<String, Waypoint> wpEntry : WaypointsService.getAllWaypoints().entrySet()) {
                if (wpEntry.getValue().getKilledAt() <= Instant.now().getEpochSecond()) WaypointsService.removeWaypoint(wpEntry.getKey());
            }
        }, 0, 20);
    }

    public static void loadData() {
        Gson gson = WarpsManagerGsonBuilderModule.gson();
        TypeToken<Map<String, Waypoint>> mapping = new TypeToken<>(){};
        try {
            Reader reader = new FileReader(WarpsManager.getWaypointsFile());
            waypoints = gson.fromJson(reader, mapping);
            reader.close();
            Set<String> removals = new HashSet<>();
            for (Map.Entry<String, Waypoint> wpEntry: waypoints.entrySet()) {
                if (wpEntry.getValue().getLocation() == null) {
                    removals.add(wpEntry.getKey());
                } else if (wpEntry.getValue().getLocation().getWorld() == null) {
                    removals.add(wpEntry.getKey());
                }
            }
            for (String entry : removals) removeWaypoint(entry);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveData() {
        Gson gson = WarpsManagerGsonBuilderModule.gson();
        String jsonOutput = gson.toJson(waypoints);
        try {
            Writer writer = new FileWriter(WarpsManager.getWaypointsFile());
            writer.write(jsonOutput);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Boolean hasWaypoint(String id) {
        return waypoints.containsKey(id);
    }

    public static Waypoint getWaypoint(String id) {
        return waypoints.get(id);
    }

    public static Map<String, Waypoint> getAllWaypoints() {
        return waypoints;
    }

    public static Boolean addWaypoint(String id, Waypoint wp) {
        if (hasWaypoint(id)) return false;
        waypoints.put(id, wp);
        return true;
    }

    public static void removeWaypoint(String id) {
        waypoints.remove(id);
    }

}
