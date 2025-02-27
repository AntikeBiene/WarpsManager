package com.antikeBiene.warpsManager.commands;

import com.antikeBiene.warpsManager.accessibles.BukkitPerm;
import com.antikeBiene.warpsManager.accessibles.CommandFeedback;
import com.antikeBiene.warpsManager.services.WaypointsService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class DelwpCommand {

    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("delwp")
                .requires(sender -> sender.getSender().hasPermission(BukkitPerm.DELWAYPOINT))
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
                            WaypointsService.removeWaypoint(wpName);
                            CommandFeedback.to(ctx).WaypointRemoved(wpName).send();
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();
    }
}

