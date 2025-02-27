package com.antikeBiene.warpsManager.commands;

import com.antikeBiene.warpsManager.accessibles.BukkitPerm;
import com.antikeBiene.warpsManager.accessibles.CommandFeedback;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class WaypointsCommand {

    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("waypoints")
                .requires(sender -> sender.getSender().hasPermission(BukkitPerm.LIST_WAYPOINTS))
                .executes(ctx -> {
                    CommandFeedback.to(ctx).ListWaypoints(1).send();
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                        .executes(ctx -> {
                            CommandFeedback.to(ctx).ListWaypoints(ctx.getArgument("page", Integer.class)).send();
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();
    }

}
