package com.antikeBiene.warpsManager.commands;

import com.antikeBiene.warpsManager.accessibles.BukkitPerm;
import com.antikeBiene.warpsManager.accessibles.CommandFeedback;
import com.antikeBiene.warpsManager.models.Warp;
import com.antikeBiene.warpsManager.services.WarpsService;
import com.antikeBiene.warpsManager.utils.*;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class WarpCommand {

    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("warp")
                .then(Commands.argument("key", StringArgumentType.word())
                        .suggests((ctx, builder) -> CommandUtil.warpKeySuggestion(builder))
                        .executes(ctx -> {
                            String id = CommandUtil.getID(ctx);
                            if (id.isEmpty()) return 0;
                            Warp warp = WarpsService.getWarp(id);
                            if (ctx.getSource().getSender().hasPermission(BukkitPerm.EXT_USEWARP + id)
                            || ctx.getSource().getSender().hasPermission(BukkitPerm.EXT_USEGROUP + warp.getGroupName())) {
                                warp.setLastAccess();
                                warp.setLastAccessedBy(ctx.getSource().getExecutor().getName());
                                CommandFeedback.to(ctx).WarpingTo(id).send();
                                ctx.getSource().getExecutor().teleport(warp.getLocation());
                                ctx.getSource().getExecutor().sendMessage(warp.getMessageAsComponent());
                                return Command.SINGLE_SUCCESS;
                            }
                            CommandFeedback.to(ctx).NoPermissionToWarp(id).send();
                            return 0;
                        })
                )
                .build();
    }

}
