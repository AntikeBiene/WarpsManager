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
                .then(Commands.argument("id", StringArgumentType.string())
                        .suggests((ctx, builder) -> {
                            for (String wpid : WaypointsService.getAllWaypoints().keySet())
                                if (wpid.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                    builder.suggest(wpid);
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            String wpid = ctx.getArgument("id", String.class).toLowerCase();
                            if (!WaypointsService.hasWaypoint(wpid)) {
                                CommandFeedback.to(ctx).WaypointDoesntExist(wpid).send();
                                return 0;
                            }
                            CommandFeedback.to(ctx).WaypointGetDeath(wpid).send();
                            return Command.SINGLE_SUCCESS;

                        })
                        .then(Commands.argument("death_in_days", IntegerArgumentType.integer(1, 180))
                                .requires(sender -> sender.getSender().hasPermission(BukkitPerm.WAYPOINT_SETDEATH))
                                .executes(ctx -> {
                                    String wpid = ctx.getArgument("id", String.class).toLowerCase();
                                    if (!WaypointsService.hasWaypoint(wpid)) {
                                        CommandFeedback.to(ctx).WaypointDoesntExist(wpid).send();
                                        return 0;
                                    }
                                    Long death_in_days = (long) ctx.getArgument("death_in_days", Integer.class);
                                    WaypointsService.getWaypoint(wpid).setKilledIn(death_in_days * 3600 * 24);
                                    CommandFeedback.to(ctx).WaypointDeathSet(wpid, death_in_days).send();
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .build();
    }
}
