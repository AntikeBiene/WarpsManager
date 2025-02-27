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

import java.util.Set;

public class DelwarpCommand {

    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("delwarp")
                .requires(sender -> sender.getSender().hasPermission(BukkitPerm.DELWARP))
                .then(Commands.argument("key", StringArgumentType.word())
                        .suggests((ctx, builder) -> CommandUtil.warpKeySuggestion(builder))
                        .executes(ctx -> {
                            String id = CommandUtil.getID(ctx);
                            if (id.isEmpty()) return 0;
                            WarpsService.removeWarp(id);
                            CommandFeedback.to(ctx).WarpRemoved(id).send();
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();
    }

}
