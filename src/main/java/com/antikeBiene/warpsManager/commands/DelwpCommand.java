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
                .then(Commands.argument("id", StringArgumentType.word())
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
                            WaypointsService.removeWaypoint(wpid);
                            CommandFeedback.to(ctx).WaypointRemoved(wpid).send();
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();
    }
}

