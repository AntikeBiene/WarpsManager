package com.antikeBiene.warpsManager.commands;

import com.antikeBiene.warpsManager.WarpsManager;
import com.antikeBiene.warpsManager.accessibles.BukkitPerm;
import com.antikeBiene.warpsManager.accessibles.CommandFeedback;
import com.antikeBiene.warpsManager.services.WarpsService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class GroupmodifyCommand {

    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("groupmodify")
                .then(Commands.argument("group", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            for (String groupName : WarpsService.getGroups().keySet())
                                if (groupName.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                    builder.suggest(groupName);
                            return builder.buildFuture();
                        })
                        .then(Commands.literal("rename")
                                .requires(sender -> sender.getSender().hasPermission(BukkitPerm.GROUPMODIFY_RENAME))
                                .then(Commands.argument("newName", StringArgumentType.word())
                                        .executes(ctx -> {
                                            String group = ctx.getArgument("group", String.class).toLowerCase().replace(".", "_");
                                            String newName = ctx.getArgument("newName", String.class).toLowerCase().replace(".", "_");
                                            if (!WarpsService.hasGroup(group)) {
                                                CommandFeedback.to(ctx).GroupDoesntExist(group).send();
                                                return 0;
                                            }
                                            if (WarpsService.hasGroup(newName)) {
                                                CommandFeedback.to(ctx).GroupAlreadyExists(newName).send();
                                                return 0;
                                            }
                                            for (String warpID : WarpsService.getGroupList(group)) {
                                                WarpsService.getWarp(warpID).setGroupName(newName);
                                                WarpsService.getWarp(warpID).setLastModified();
                                                WarpsService.getWarp(warpID).setLastModifiedBy(ctx.getSource().getSender().getName());
                                                WarpsService.enlargeGroup(newName, warpID);
                                            }
                                            WarpsService.removeGroup(group);
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(Commands.literal("merge")
                                .requires(sender -> sender.getSender().hasPermission(BukkitPerm.GROUPMODIFY_MERGE))
                                .then(Commands.argument("newGroup", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            for (String groupName : WarpsService.getGroups().keySet())
                                                if (groupName.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                                    builder.suggest(groupName);
                                            return builder.buildFuture();
                                        })
                                        .executes(ctx -> {
                                            String group = ctx.getArgument("group", String.class).toLowerCase().replace(".", "_");
                                            String newGroup = ctx.getArgument("newGroup", String.class).toLowerCase().replace(".", "_");
                                            if (!WarpsService.hasGroup(group)) {
                                                CommandFeedback.to(ctx).GroupDoesntExist(group).send();
                                                return 0;
                                            }
                                            if (!WarpsService.hasWarp(newGroup)) {
                                                CommandFeedback.to(ctx).GroupDoesntExist(newGroup).send();
                                                return 0;
                                            }
                                            for (String warpID : WarpsService.getGroupList(group)) {
                                                WarpsService.getWarp(warpID).setGroupName(newGroup);
                                                WarpsService.getWarp(warpID).setLastModified();
                                                WarpsService.getWarp(warpID).setLastModifiedBy(ctx.getSource().getSender().getName());
                                                WarpsService.enlargeGroup(newGroup, warpID);
                                            }
                                            WarpsService.removeGroup(group);
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                )
                .build();
    }

}
