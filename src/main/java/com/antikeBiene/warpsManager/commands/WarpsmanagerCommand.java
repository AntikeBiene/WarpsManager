package com.antikeBiene.warpsManager.commands;

import com.antikeBiene.warpsManager.WarpsManager;
import com.antikeBiene.warpsManager.accessibles.BukkitPerm;
import com.antikeBiene.warpsManager.accessibles.CommandFeedback;
import com.antikeBiene.warpsManager.services.ConfigurationService;
import com.antikeBiene.warpsManager.services.WarpsService;
import com.antikeBiene.warpsManager.services.WaypointsService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class WarpsmanagerCommand {

    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("warpsmanager")
                .requires(sender -> sender.getSender().hasPermission(BukkitPerm.ADMIN))
                .then(Commands.literal("reload")
                        .executes(ctx -> {
                            WarpsManager.getPlugin().reloadConfig();
                            ConfigurationService.init();
                            WarpsService.saveData();
                            WaypointsService.saveData();
                            WarpsService.loadData();
                            WaypointsService.loadData();
                            CommandFeedback.to(ctx).PluginReloaded().send();
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("loaddata")
                        .executes(ctx -> {
                            WarpsService.loadData();
                            WaypointsService.loadData();
                            CommandFeedback.to(ctx).DataLoaded().send();
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("save")
                        .executes(ctx -> {
                            WarpsService.saveData();
                            WaypointsService.saveData();
                            CommandFeedback.to(ctx).DataSaved().send();
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();

    }
}