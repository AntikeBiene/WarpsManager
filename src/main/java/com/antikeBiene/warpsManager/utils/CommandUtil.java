package com.antikeBiene.warpsManager.utils;

import com.antikeBiene.warpsManager.services.WarpsService;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

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
}
