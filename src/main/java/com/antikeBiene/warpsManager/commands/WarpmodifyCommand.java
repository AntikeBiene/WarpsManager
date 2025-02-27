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
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.math.FinePosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Location;

public class WarpmodifyCommand {

    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("warpmodify")
                .then(Commands.argument("key", StringArgumentType.word())
                        .suggests((ctx, builder) -> CommandUtil.warpKeySuggestion(builder))
                        .then(Commands.literal("move")
                                .requires(sender -> sender.getSender().hasPermission(BukkitPerm.WARPMODIFY_MOVE))
                                .executes(ctx -> {
                                    String id = CommandUtil.getID(ctx);
                                    if (id.isEmpty()) return 0;
                                    Location newLocation = ctx.getSource().getExecutor().getLocation();
                                    WarpsService.getWarp(id).setLocation(newLocation);
                                    WarpsService.getWarp(id).setLastModified();
                                    WarpsService.getWarp(id).setLastModifiedBy(ctx.getSource().getSender().getName());
                                    CommandFeedback.to(ctx).WarpMoved(id, newLocation).send();
                                    return Command.SINGLE_SUCCESS;
                                })
                                .then(Commands.argument("pos", ArgumentTypes.finePosition(true))
                                        .executes(ctx -> {
                                            String id = CommandUtil.getID(ctx);
                                            if (id.isEmpty()) return 0;
                                            FinePosition finePos = ctx.getArgument("pos", FinePositionResolver.class).resolve(ctx.getSource());
                                            Location newLocation = finePos.toLocation(ctx.getSource().getExecutor().getWorld());
                                            newLocation.setYaw(ctx.getSource().getExecutor().getYaw());
                                            newLocation.setPitch(ctx.getSource().getExecutor().getPitch());
                                            WarpsService.getWarp(id).setLocation(newLocation);
                                            WarpsService.getWarp(id).setLastModified();
                                            WarpsService.getWarp(id).setLastModifiedBy(ctx.getSource().getSender().getName());
                                            CommandFeedback.to(ctx).WarpMoved(id, newLocation).send();
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(Commands.literal("rename")
                                .requires(sender -> sender.getSender().hasPermission(BukkitPerm.WARPMODIFY_RENAME))
                                .then(Commands.argument("newID", StringArgumentType.word())
                                        .executes(ctx -> {
                                            String id = CommandUtil.getID(ctx);
                                            if (id.isEmpty()) return 0;
                                            String newID = ctx.getArgument("newID", String.class).toLowerCase();
                                            if (WarpsService.hasWarp(newID)) {
                                                CommandFeedback.to(ctx).WarpAlreadyExists(newID).send();
                                                return 0;
                                            }
                                            WarpsService.addWarp(newID, WarpsService.getWarp(id));
                                            WarpsService.removeWarp(id);
                                            WarpsService.getWarp(newID).setLastModified();
                                            WarpsService.getWarp(newID).setLastModifiedBy(ctx.getSource().getSender().getName());
                                            CommandFeedback.to(ctx).WarpRenamed(id, newID).send();
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(Commands.literal("message")
                                .requires(sender -> sender.getSender().hasPermission(BukkitPerm.WARPMODIFY_MESSAGE))
                                .then(Commands.argument("message", ArgumentTypes.component())
                                        .executes(ctx -> {
                                            String id = CommandUtil.getID(ctx);
                                            if (id.isEmpty()) return 0;
                                            Component messageComponent = ctx.getArgument("message", Component.class);
                                            String message = JSONComponentSerializer.json().serialize(messageComponent);
                                            WarpsService.getWarp(id).setMessage(message);
                                            WarpsService.getWarp(id).setLastModified();
                                            WarpsService.getWarp(id).setLastModifiedBy(ctx.getSource().getSender().getName());
                                            CommandFeedback.to(ctx).WarpMessageChanged(id, message).send();
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(Commands.literal("group")
                                .requires(sender -> sender.getSender().hasPermission(BukkitPerm.WARPMODIFY_GROUP))
                                .then(Commands.argument("group", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            for (String groupName : WarpsService.getGroups().keySet())
                                                if (groupName.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                                    builder.suggest(groupName);
                                            return builder.buildFuture();
                                        })
                                        .executes(ctx -> {
                                            String id = CommandUtil.getID(ctx);
                                            if (id.isEmpty()) return 0;
                                            String groupID = ctx.getArgument("group", String.class).toLowerCase();
                                            if (WarpsService.getGroupList(groupID).contains(id)) {
                                                CommandFeedback.to(ctx).WarpAlreadyInGroup(id, groupID).send();
                                                return 0;
                                            }
                                            WarpsService.shrinkGroup(WarpsService.getWarp(id).getGroupName(), id);
                                            WarpsService.enlargeGroup(groupID, id);
                                            WarpsService.getWarp(id).setGroupName(groupID);
                                            WarpsService.getWarp(id).setLastModified();
                                            WarpsService.getWarp(id).setLastModifiedBy(ctx.getSource().getSender().getName());
                                            CommandFeedback.to(ctx).WarpGroupChanged(id, groupID).send();
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                )
                .build();
    }
}
