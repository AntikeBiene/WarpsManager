package com.antikeBiene.warpsManager.commands;

import com.antikeBiene.warpsManager.accessibles.BukkitPerm;
import com.antikeBiene.warpsManager.accessibles.CommandFeedback;
import com.antikeBiene.warpsManager.models.Warp;
import com.antikeBiene.warpsManager.services.WarpsService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class WarpCommand {

    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("warp")
                .then(Commands.argument("name", StringArgumentType.string())
                        .suggests((ctx, builder) -> {
                            for (String warpName : WarpsService.getAllWarps().keySet())
                                if (warpName.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                    builder.suggest(warpName);
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            String warpName = ctx.getArgument("name", String.class);
                            if (!WarpsService.hasWarp(warpName)) {
                                CommandFeedback.to(ctx).WarpDoesntExist(warpName).send();
                                return 0;
                            }
                            Warp warp = WarpsService.getWarp(warpName);
                            if (ctx.getSource().getSender().hasPermission(BukkitPerm.EXT_USEWARP + warpName)
                            || ctx.getSource().getSender().hasPermission(BukkitPerm.EXT_USEGROUP + warp.getGroupName())) {
                                warp.setLastAccess();
                                warp.setLastAccessedBy(ctx.getSource().getExecutor().getName());
                                CommandFeedback.to(ctx).WarpingTo(warpName).send();
                                ctx.getSource().getExecutor().teleport(warp.getLocation());
                                ctx.getSource().getExecutor().sendMessage(warp.getMessageAsComponent());
                                return Command.SINGLE_SUCCESS;
                            }
                            CommandFeedback.to(ctx).NoPermissionToWarp(warpName).send();
                            return 0;
                        })
                )
                .build();
    }

}
