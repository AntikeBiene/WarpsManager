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
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.entity.Entity;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public class SetwarpCommand {

    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("setwarp")
                .requires(sender -> sender.getSender().hasPermission(BukkitPerm.SETWARP))
                .then(Commands.argument("name", StringArgumentType.string())
                    .then(Commands.argument("group", StringArgumentType.string())
                        .suggests((ctx, builder) -> {
                            for (String groupName : WarpsService.getGroups().keySet())
                                if (groupName.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                    builder.suggest(groupName);
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            Entity executor = ctx.getSource().getExecutor();
                            String name = ctx.getArgument("name", String.class);
                            TextComponent.Builder standardComponent = text().append(text("You've been warped to ", GREEN)).append(text(name, YELLOW, TextDecoration.ITALIC)).append(text(".", GREEN));
                            Warp newWarp = new Warp(
                                    executor.getLocation(),
                                    ctx.getSource().getSender().getName(),
                                    ctx.getArgument("group", String.class),
                                    JSONComponentSerializer.json().serialize(standardComponent.build())
                            );
                            if (WarpsService.addWarp(name, newWarp)) {
                                CommandFeedback.to(ctx).WarpCreated(name).send();
                                return Command.SINGLE_SUCCESS;
                            }
                            CommandFeedback.to(ctx).WarpAlreadyExists(name).send();
                            return 0;
                        })
                    )
                )
                .build();
    }

}
