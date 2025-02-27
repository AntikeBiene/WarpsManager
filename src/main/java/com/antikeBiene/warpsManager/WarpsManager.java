package com.antikeBiene.warpsManager;

import com.antikeBiene.warpsManager.commands.*;
import com.antikeBiene.warpsManager.services.ConfigurationService;
import com.antikeBiene.warpsManager.services.WarpsService;
import com.antikeBiene.warpsManager.services.WaypointsService;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class WarpsManager extends JavaPlugin {

    private static WarpsManager plugin;
    private static File dir;
    private static File warpsFile;
    private static File waypointsFile;

    @Override
    public void onEnable() {
        plugin = this;
        dir = this.getDataFolder();

        warpsFile = new File(dir.getAbsolutePath() + "/warps.json");
        waypointsFile = new File(dir.getAbsolutePath() + "/waypoints.json");

        registerCommands();

        if (!dir.exists()) dir.mkdir();
        if (warpsFile.exists()) WarpsService.loadData();
        if (waypointsFile.exists()) WaypointsService.loadData();
        WaypointsService.init();
        File configFile = new File(dir.getAbsolutePath() + "/config.yml");
        if (!configFile.exists()) saveDefaultConfig();

        ConfigurationService.init();

        getLogger().info("antike Biene's WarpsManager - initialized");
    }

    @Override
    public void onDisable() {
        WarpsService.saveData();
        WaypointsService.saveData();

        getLogger().info("antike Biene's WarpsManager - deactivated");
    }

    public static WarpsManager getPlugin() {
        return plugin;
    }

    public static File getDir() {
        return dir;
    }

    public static File getWarpsFile() {
        return warpsFile;
    }

    public static  File getWaypointsFile() { return waypointsFile; }

    private static void registerCommands() {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(SetwarpCommand.build());
            commands.registrar().register(WarpCommand.build());
            commands.registrar().register(DelwarpCommand.build());
            commands.registrar().register(WarpsCommand.build());
            commands.registrar().register(GroupsCommand.build());
            commands.registrar().register(WarpinfoCommand.build());
            commands.registrar().register(WarpmodifyCommand.build());
            commands.registrar().register(SetwpCommand.build());
            commands.registrar().register(WpCommand.build());
            commands.registrar().register(DelwpCommand.build());
            commands.registrar().register(WaypointsCommand.build());
            commands.registrar().register(WpdeathCommand.build());
            commands.registrar().register(WarpsmanagerCommand.build());
            commands.registrar().register(WarplistCommand.build());
            commands.registrar().register(GroupmodifyCommand.build());
        });
    }
}
