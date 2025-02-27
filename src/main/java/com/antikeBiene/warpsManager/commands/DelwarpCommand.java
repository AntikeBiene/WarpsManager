package com.antikeBiene.warpsManager.commands;

import com.antikeBiene.warpsManager.accessibles.BukkitPerm;
import com.antikeBiene.warpsManager.accessibles.CommandFeedback;
import com.antikeBiene.warpsManager.services.WarpsService;
import com.antikeBiene.warpsManager.utils.CommandUtil;
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
                            String input = ctx.getArgument("key", String.class);
                            String[] data = input.split(":");
                            String id;
                            if (data.length == 1) {
                                id = data[0];
                            } else if (data.length == 2) {
                                if (WarpsService.getGroupList(data[0]).contains(data[1])) {
                                    id = data[1];
                                }
                            } else {
                                CommandFeedback.to(ctx).InvalidKey(input).send();
                                return 0;
                            }
                            if (!WarpsService.hasWarp(id)) {
                                CommandFeedback.to(ctx).WarpDoesntExist(id).send();
                                return 0;
                            }
                            WarpsService.removeWarp(id);
                            CommandFeedback.to(ctx).WarpRemoved(id).send();
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();
    }

}
