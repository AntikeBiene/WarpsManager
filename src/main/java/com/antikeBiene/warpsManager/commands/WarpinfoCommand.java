package com.antikeBiene.warpsManager.commands;

import com.antikeBiene.warpsManager.accessibles.BukkitPerm;
import com.antikeBiene.warpsManager.accessibles.CommandFeedback;
import com.antikeBiene.warpsManager.services.WarpsService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class WarpinfoCommand {

    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("warpinfo")
                .requires(sender -> sender.getSender().hasPermission(BukkitPerm.WARPINFO))
                .then(Commands.argument("warp", StringArgumentType.string())
                        .suggests((ctx, builder) -> {
                            for (String warpName : WarpsService.getAllWarps().keySet())
                                if (warpName.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                    builder.suggest(warpName);
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            String warpName = ctx.getArgument("warp", String.class);
                            if (!WarpsService.hasWarp(warpName)) {
                                CommandFeedback.to(ctx).WarpDoesntExist(warpName).send();
                                return 0;
                            }
                            CommandFeedback.to(ctx).WarpInfo(warpName).send();
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();
    }

}
