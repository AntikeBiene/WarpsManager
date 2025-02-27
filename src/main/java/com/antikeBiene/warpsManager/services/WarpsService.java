package com.antikeBiene.warpsManager.services;

import com.antikeBiene.warpsManager.WarpsManager;
import com.antikeBiene.warpsManager.models.Warp;
import com.antikeBiene.warpsManager.modules.WarpsManagerGsonBuilderModule;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.*;


public class WarpsService {

    private static Map<String, Set<String>> groups = new HashMap<>();
    private static Map<String, Warp> warps = new HashMap<>();

    public static void loadData() {
        groups = new HashMap<>();
        Gson gson = WarpsManagerGsonBuilderModule.gson();
        TypeToken<Map<String, Warp>> mapping = new TypeToken<>(){};
        try {
            Reader reader = new FileReader(WarpsManager.getWarpsFile());
            warps = gson.fromJson(reader, mapping);
            reader.close();
            Set<String> removals = new HashSet<>();
            for (Map.Entry<String, Warp> entry : warps.entrySet()) {
                Warp warp = entry.getValue();
                String group = warp.getGroupName();
                enlargeGroup(group, entry.getKey());
                if (warp.getLocation() == null) {
                    removals.add(entry.getKey());
                } else if (warp.getLocation().getWorld() == null) {
                    removals.add(entry.getKey());
                }
            }
            for (String entry : removals) WarpsService.removeWarp(entry);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveData() {
        Gson gson = WarpsManagerGsonBuilderModule.gson();
        String jsonOutput = gson.toJson(warps);
        try {
            Writer writer = new FileWriter(WarpsManager.getWarpsFile());
            writer.write(jsonOutput);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Boolean addWarp(String id, Warp warp) {
        if (hasWarp(id)) return false;
        warps.put(id, warp);
        enlargeGroup(warp.getGroupName(), id);
        return true;
    }

    public static Boolean removeWarp(String id) {
        if (!hasWarp(id)) return false;
        shrinkGroup(warps.get(id).getGroupName(), id);
        warps.remove(id);
        return true;
    }

    public static Map<String, Set<String>> getGroups() {
        return groups;
    }

    public static void enlargeGroup(String group, String id) {
        Set<String> idSet = new HashSet<>();
        if (hasGroup(group)) idSet = groups.get(group);
        idSet.add(id);
        groups.put(group, idSet);
    }

    public static void shrinkGroup(String group, String id) {
        Set<String> idSet = new HashSet<>();
        if (hasGroup(group)) idSet = groups.get(group);
        idSet.remove(id);
        if (idSet.isEmpty()) {
            groups.remove(group);
        } else groups.put(group, idSet);
    }

    public static Boolean hasGroup(String group) {
        return groups.containsKey(group);
    }

    public static Integer getGroupSize(String group) {
        if (!hasGroup(group)) return 0;
        return groups.get(group).size();
    }

    public static Set<String> getGroupList(String group) {
        if (!hasGroup(group)) return new HashSet<>();
        return groups.get(group);
    }

    public static Set<String> getAllGroupIds() {
        return groups.keySet();
    }

    public static Map<String, Warp> getAllWarps() {
        return warps;
    }

    public static Set<String> getAllWarpIds() {
        return warps.keySet();
    }

    public static Warp getWarp(String id) {
        return warps.get(id);
    }

    public static Boolean hasWarp(String id) {
        return warps.containsKey(id);
    }

}
