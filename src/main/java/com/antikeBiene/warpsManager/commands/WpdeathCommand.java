package com.antikeBiene.warpsManager.commands;

import com.antikeBiene.warpsManager.accessibles.BukkitPerm;
import com.antikeBiene.warpsManager.accessibles.CommandFeedback;
import com.antikeBiene.warpsManager.services.WaypointsService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class WpdeathCommand {

    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("wpdeath")
                .requires(sender -> sender.getSender().hasPermission(BukkitPerm.WAYPOINT_GETDEATH))
                .then(Commands.argument("name", StringArgumentType.string())
                        .suggests((ctx, builder) -> {
                            for (String wpName : WaypointsService.getAllWaypoints().keySet())
                                if (wpName.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                    builder.suggest(wpName);
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            String wpName = ctx.getArgument("name", String.class);
                            if (!WaypointsService.hasWaypoint(wpName)) {
                                CommandFeedback.to(ctx).WaypointDoesntExist(wpName).send();
                                return 0;
                            }
                            CommandFeedback.to(ctx).WaypointGetDeath(wpName).send();
                            return Command.SINGLE_SUCCESS;

                        })
                        .then(Commands.argument("death_in_days", IntegerArgumentType.integer(1, 180))
                                .requires(sender -> sender.getSender().hasPermission(BukkitPerm.WAYPOINT_SETDEATH))
                                .executes(ctx -> {
                                    String wpName = ctx.getArgument("name", String.class);
                                    if (!WaypointsService.hasWaypoint(wpName)) {
                                        CommandFeedback.to(ctx).WaypointDoesntExist(wpName).send();
                                        return 0;
                                    }
                                    Long death_in_days = (long) ctx.getArgument("death_in_days", Integer.class);
                                    WaypointsService.getWaypoint(wpName).setKilledIn(death_in_days * 3600 * 24);
                                    CommandFeedback.to(ctx).WaypointDeathSet(wpName, death_in_days).send();
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .build();
    }
}
