package com.antikeBiene.warpsManager.utils;

import com.antikeBiene.warpsManager.accessibles.CommandFeedback;
import com.antikeBiene.warpsManager.services.WarpsService;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.concurrent.CompletableFuture;

public class CommandUtil {

    public static CompletableFuture<Suggestions> warpKeySuggestion(SuggestionsBuilder builder) {
        String input = builder.getRemainingLowerCase();
        String[] data = input.split(":");
        if (data.length == 2) {
            for (String warpID : WarpsService.getGroupList(data[0]))
                if (warpID.toLowerCase().startsWith(data[1]))
                    builder.suggest(data[0] + ":" + warpID);
        } else if (input.endsWith(":") && data.length == 1) {
            for (String warpID : WarpsService.getGroupList(input))
                builder.suggest(input + ":" + warpID);
        } else if (data.length == 1) {
            for (String groupID : WarpsService.getAllGroupIds()) {
                if (groupID.toLowerCase().startsWith(input))
                    builder.suggest(groupID + ":");
            }
            for (String warpID : WarpsService.getAllWarpIds())
                if (warpID.toLowerCase().startsWith(input))
                    builder.suggest(warpID);
        }
        return builder.buildFuture();
    }

    public static String getID(CommandContext<CommandSourceStack> ctx) {
        String input = ctx.getArgument("key", String.class).toLowerCase();
        String[] data = input.split(":");
        String id;
        if (data.length == 1) {
            id = data[0];
        } else if (data.length == 2) {
            if (WarpsService.getGroupList(data[0]).contains(data[1])) {
                id = data[1];
            } else {
                CommandFeedback.to(ctx).WarpNotInGroup(data[1], data[0]).send();
                return "";
            }
        } else {
            CommandFeedback.to(ctx).InvalidKey(input).send();
            return "";
        }
        if (!WarpsService.hasWarp(id)) {
            CommandFeedback.to(ctx).WarpDoesntExist(id).send();
            return "";
        }
        return id;
    }
}
