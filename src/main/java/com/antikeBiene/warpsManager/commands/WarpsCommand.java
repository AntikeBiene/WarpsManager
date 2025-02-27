package com.antikeBiene.warpsManager.commands;

import com.antikeBiene.warpsManager.accessibles.BukkitPerm;
import com.antikeBiene.warpsManager.accessibles.CommandFeedback;
import com.antikeBiene.warpsManager.services.WarpsService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class WarpsCommand {

    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("warps")
                .then(Commands.argument("group", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            for (String groupName : WarpsService.getGroups().keySet())
                                if (groupName.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                    builder.suggest(groupName);
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            String group = ctx.getArgument("group", String.class).toLowerCase();
                            if (!WarpsService.hasGroup(group)) {
                                CommandFeedback.to(ctx).GroupDoesntExist(group).send();
                                return 0;
                            }
                            if (!ctx.getSource().getSender().hasPermission(BukkitPerm.EXT_LIST_WARPS + group)) {
                                CommandFeedback.to(ctx).NoPermissionToListWarps(group).send();
                                return 0;
                            }
                            CommandFeedback.to(ctx).ListWarpsInGroup(group, 1).send();
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                .executes(ctx -> {
                                    String group = ctx.getArgument("group", String.class).toLowerCase();
                                    if (!WarpsService.hasGroup(group)) {
                                        CommandFeedback.to(ctx).GroupDoesntExist(group).send();
                                        return 0;
                                    }
                                    if (!ctx.getSource().getSender().hasPermission(BukkitPerm.EXT_LIST_WARPS + group)) {
                                        CommandFeedback.to(ctx).NoPermissionToListWarps(group).send();
                                        return 0;
                                    }
                                    CommandFeedback.to(ctx).ListWarpsInGroup(group, ctx.getArgument("page", Integer.class)).send();
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .build();
    }

}
