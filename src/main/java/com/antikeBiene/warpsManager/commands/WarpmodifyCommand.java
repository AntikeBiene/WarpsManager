package com.antikeBiene.warpsManager.commands;

import com.antikeBiene.warpsManager.accessibles.BukkitPerm;
import com.antikeBiene.warpsManager.accessibles.CommandFeedback;
import com.antikeBiene.warpsManager.services.WarpsService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.math.FinePosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Location;

public class WarpmodifyCommand {

    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("warpmodify")
                .then(Commands.argument("warp", StringArgumentType.string())
                        .suggests((ctx, builder) -> {
                                    for (String warpName : WarpsService.getAllWarps().keySet())
                                        if (warpName.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                            builder.suggest(warpName);
                                    return builder.buildFuture();
                        })
                        .then(Commands.literal("move")
                                .requires(sender -> sender.getSender().hasPermission(BukkitPerm.WARPMODIFY_MOVE))
                                .executes(ctx -> {
                                    String warpName = ctx.getArgument("warp", String.class);
                                    if (!WarpsService.hasWarp(warpName)) {
                                        CommandFeedback.to(ctx).WarpDoesntExist(warpName).send();
                                        return 0;
                                    }
                                    Location newLocation = ctx.getSource().getExecutor().getLocation();
                                    WarpsService.getWarp(warpName).setLocation(newLocation);
                                    WarpsService.getWarp(warpName).setLastModified();
                                    WarpsService.getWarp(warpName).setLastModifiedBy(ctx.getSource().getSender().getName());
                                    CommandFeedback.to(ctx).WarpMoved(warpName, newLocation).send();
                                    return Command.SINGLE_SUCCESS;
                                })
                                .then(Commands.argument("pos", ArgumentTypes.finePosition(true))
                                        .executes(ctx -> {
                                            String warpName = ctx.getArgument("warp", String.class);
                                            if (!WarpsService.hasWarp(warpName)) {
                                                CommandFeedback.to(ctx).WarpDoesntExist(warpName).send();
                                                return 0;
                                            }
                                            FinePosition finePos = ctx.getArgument("pos", FinePositionResolver.class).resolve(ctx.getSource());
                                            Location newLocation = finePos.toLocation(ctx.getSource().getExecutor().getWorld());
                                            newLocation.setYaw(ctx.getSource().getExecutor().getYaw());
                                            newLocation.setPitch(ctx.getSource().getExecutor().getPitch());
                                            WarpsService.getWarp(warpName).setLocation(newLocation);
                                            WarpsService.getWarp(warpName).setLastModified();
                                            WarpsService.getWarp(warpName).setLastModifiedBy(ctx.getSource().getSender().getName());
                                            CommandFeedback.to(ctx).WarpMoved(warpName, newLocation).send();
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(Commands.literal("rename")
                                .requires(sender -> sender.getSender().hasPermission(BukkitPerm.WARPMODIFY_RENAME))
                                .then(Commands.argument("newName", StringArgumentType.string())
                                        .executes(ctx -> {
                                            String warpName = ctx.getArgument("warp", String.class);
                                            String newName = ctx.getArgument("newName", String.class);
                                            if (!WarpsService.hasWarp(warpName)) {
                                                CommandFeedback.to(ctx).WarpDoesntExist(warpName).send();
                                                return 0;
                                            }
                                            if (WarpsService.hasWarp(newName)) {
                                                CommandFeedback.to(ctx).WarpAlreadyExists(newName).send();
                                                return 0;
                                            }
                                            WarpsService.addWarp(newName, WarpsService.getWarp(warpName));
                                            WarpsService.removeWarp(warpName);
                                            WarpsService.getWarp(newName).setLastModified();
                                            WarpsService.getWarp(newName).setLastModifiedBy(ctx.getSource().getSender().getName());
                                            CommandFeedback.to(ctx).WarpRenamed(warpName, newName).send();
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(Commands.literal("message")
                                .requires(sender -> sender.getSender().hasPermission(BukkitPerm.WARPMODIFY_MESSAGE))
                                .then(Commands.argument("message", ArgumentTypes.component())
                                        .executes(ctx -> {
                                            String warpName = ctx.getArgument("warp", String.class);
                                            Component messageComponent = ctx.getArgument("message", Component.class);
                                            String message = JSONComponentSerializer.json().serialize(messageComponent);
                                            if (!WarpsService.hasWarp(warpName)) {
                                                CommandFeedback.to(ctx).WarpDoesntExist(warpName).send();
                                                return 0;
                                            }
                                            WarpsService.getWarp(warpName).setMessage(message);
                                            WarpsService.getWarp(warpName).setLastModified();
                                            WarpsService.getWarp(warpName).setLastModifiedBy(ctx.getSource().getSender().getName());
                                            CommandFeedback.to(ctx).WarpMessageChanged(warpName, message).send();
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(Commands.literal("group")
                                .requires(sender -> sender.getSender().hasPermission(BukkitPerm.WARPMODIFY_GROUP))
                                .then(Commands.argument("group", StringArgumentType.string())
                                        .suggests((ctx, builder) -> {
                                            for (String groupName : WarpsService.getGroups().keySet())
                                                if (groupName.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                                    builder.suggest(groupName);
                                            return builder.buildFuture();
                                        })
                                        .executes(ctx -> {
                                            String warpName = ctx.getArgument("warp", String.class);
                                            String groupName = ctx.getArgument("group", String.class);
                                            if (!WarpsService.hasWarp(warpName)) {
                                                CommandFeedback.to(ctx).WarpDoesntExist(warpName).send();
                                                return 0;
                                            }
                                            if (groupName.equals(WarpsService.getWarp(warpName).getGroupName())) {
                                                CommandFeedback.to(ctx).WarpAlreadyInGroup(warpName, groupName).send();
                                                return 0;
                                            }
                                            WarpsService.shrinkGroup(WarpsService.getWarp(warpName).getGroupName());
                                            WarpsService.enlargeGroup(groupName);
                                            WarpsService.getWarp(warpName).setGroupName(groupName);
                                            WarpsService.getWarp(warpName).setLastModified();
                                            WarpsService.getWarp(warpName).setLastModifiedBy(ctx.getSource().getSender().getName());
                                            CommandFeedback.to(ctx).WarpGroupChanged(warpName, groupName).send();
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                )
                .build();
    }
}
