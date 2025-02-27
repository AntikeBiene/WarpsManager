package com.antikeBiene.warpsManager.commands;

import com.antikeBiene.warpsManager.accessibles.BukkitPerm;
import com.antikeBiene.warpsManager.accessibles.CommandFeedback;
import com.antikeBiene.warpsManager.models.Waypoint;
import com.antikeBiene.warpsManager.services.WaypointsService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Entity;

import static net.kyori.adventure.text.Component.text;

public class SetwpCommand {

    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("setwp")
                .requires(sender -> sender.getSender().hasPermission(BukkitPerm.SETWAYPOINT))
                .then(Commands.argument("name", StringArgumentType.string())
                        .executes(ctx -> {
                            Entity executor = ctx.getSource().getExecutor();
                            String name = ctx.getArgument("name", String.class);
                            Waypoint newWaypoint = new Waypoint(
                                    executor.getLocation(),
                                    ctx.getSource().getSender().getName(),
                                    (long) 30 * 3600 * 24
                            );
                            if (WaypointsService.addWaypoint(name, newWaypoint)) {
                                CommandFeedback.to(ctx).WaypointCreated(name).send();
                                return Command.SINGLE_SUCCESS;
                            }
                            CommandFeedback.to(ctx).WaypointAlreadyExists(name).send();
                            return 0;
                        })
                        .then(Commands.argument("death_in_days", IntegerArgumentType.integer(1, 180))
                                .executes(ctx -> {
                                    Entity executor = ctx.getSource().getExecutor();
                                    String name = ctx.getArgument("name", String.class);
                                    Long death = (long) ctx.getArgument("death_in_days", Integer.class);
                                    Waypoint newWaypoint = new Waypoint(
                                            executor.getLocation(),
                                            ctx.getSource().getSender().getName(),
                                            death * 3600 * 24
                                    );
                                    if (WaypointsService.addWaypoint(name, newWaypoint)) {
                                        CommandFeedback.to(ctx).WaypointCreated(name).send();
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    CommandFeedback.to(ctx).WaypointAlreadyExists(name).send();
                                    return 0;
                                })
                        )
                )
                .build();
    }

}
