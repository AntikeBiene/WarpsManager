package com.antikeBiene.warpsManager.commands;

import com.antikeBiene.warpsManager.accessibles.BukkitPerm;
import com.antikeBiene.warpsManager.accessibles.CommandFeedback;
import com.antikeBiene.warpsManager.services.WarpsService;
import com.antikeBiene.warpsManager.utils.*;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class WarpinfoCommand {

    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("warpinfo")
                .requires(sender -> sender.getSender().hasPermission(BukkitPerm.WARPINFO))
                .then(Commands.argument("warp", StringArgumentType.word())
                        .suggests((ctx, builder) -> CommandUtil.warpKeySuggestion(builder))
                        .executes(ctx -> {
                            String id = CommandUtil.getID(ctx);
                            if (id.isEmpty()) return 0;
                            if (!WarpsService.hasWarp(id)) {
                                CommandFeedback.to(ctx).WarpDoesntExist(id).send();
                                return 0;
                            }
                            CommandFeedback.to(ctx).WarpInfo(id).send();
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();
    }

}
