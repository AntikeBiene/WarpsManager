package com.antikeBiene.warpsManager.accessibles;

import com.antikeBiene.warpsManager.models.Warp;
import com.antikeBiene.warpsManager.models.Waypoint;
import com.antikeBiene.warpsManager.services.ConfigurationService;
import com.antikeBiene.warpsManager.services.WarpsService;
import com.antikeBiene.warpsManager.services.WaypointsService;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class CommandFeedback {

    private final CommandSender sender;
    private TextComponent.Builder message = text();

    public CommandFeedback(CommandSender sender) {
        this.sender = sender;
    }

    public static CommandFeedback to(CommandSender sender) {
        return new CommandFeedback(sender);
    }

    public static CommandFeedback to(CommandContext<CommandSourceStack> ctx) {
        return new CommandFeedback(ctx.getSource().getSender());
    }

    public void send() {
        this.sender.sendMessage(this.message.build());
    }

    public CommandFeedback WarpDoesntExist(String warp) {
        this.message.append(text("The warp ", RED))
                    .append(fWarp(warp))
                    .append(text(" doesn't exist.", RED));
        return this;
    }

    public CommandFeedback WarpingTo(String warp) {
        this.message.append(text("Warping to ", GREEN))
                .append(fWarp(warp))
                .append(text(".", GREEN));
        return this;
    }

    public CommandFeedback NoPermissionToWarp(String warp) {
        this.message.append(text("You're missing the required group/warp permission to warp to ", RED))
                .append(fWarp(warp))
                .append(text(".", RED));
        return this;
    }

    public CommandFeedback WarpCreated(String warp) {
        this.message.append(text("Warp ", GREEN))
                .append(fWarp(warp))
                .append(text(" successfully created!", GREEN));
        return this;
    }

    public CommandFeedback WarpAlreadyExists(String warp) {
        this.message.append(text("The warp " , RED))
                .append(fWarp(warp))
                .append(text(" already exists!", RED));
        return this;
    }

    public CommandFeedback WarpRemoved(String warp) {
        this.message.append(text("Warp ", GREEN))
                .append(fWarp(warp))
                .append(text(" successfully deleted!", GREEN));
        return this;
    }

    public CommandFeedback NoPermissionToListWarps(String group) {
        this.message.append(text("You are not permitted to list warps of ", RED))
                .append(fGroup(group))
                .append(text("!", RED));
        return this;
    }

    public CommandFeedback NoPermissionToListGroups() {
        this.message.append(text("You are not permitted to list warp groups!", RED));
        return this;
    }

    public CommandFeedback GroupDoesntExist(String group) {
        this.message.append(text("The group ", RED))
                .append(fGroup(group))
                .append(text(" doesn't exist.", RED));
        return this;
    }

    public CommandFeedback ListWarpsInGroup(String group, Integer page) {
        if (!WarpsService.hasGroup(group)) return this.GroupDoesntExist(group);
        Integer pgSize = 8;
        Integer pgStart = (page - 1) * pgSize;
        Integer pgEnd = (page * pgSize) - 1;
        Integer currentEntry = 0;
        Integer groupSize = WarpsService.getGroupSize(group);
        Integer groupPages = (int) Math.ceil((double) groupSize / pgSize);
        if (groupPages < page || page < 1) return this.ListWarpsInGroups_AccessingInvalidPage(group, page, groupPages);
        this.message.append(fGroup(group))
                .append(text(" - Warps (" + page + "/" + groupPages + ")", WHITE));
        for (Map.Entry<String, Warp> warpEntry : WarpsService.getAllWarps().entrySet()) {
            Warp warp = warpEntry.getValue();
            String name = warpEntry.getKey();
            if (group.equals(warp.getGroupName())) {
                currentEntry++;
                if (currentEntry - 1 > pgEnd) break;
                if (currentEntry - 1 < pgStart) continue;
                this.message.append(text("\n"))
                        .append(fWarp(name)
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/warp " + name))
                                .hoverEvent(HoverEvent.showText(text("Click to warp"))))
                        .append(text(" | in ", GRAY))
                        .append(text(warp.getLocation().getWorld().getName(), DARK_AQUA))
                        .append(text(" | by ", GRAY))
                        .append(text(warp.getCreatedBy(), DARK_AQUA));
            }
        }
        return this;
    }

    public CommandFeedback ListGroups(Integer page) {
        Integer pgSize = 12;
        Integer pgStart = (page - 1) * pgSize;
        Integer pgEnd = (page * pgSize) - 1;
        Integer currentEntry = 0;
        Integer listSize = WarpsService.getGroups().size();
        if (listSize == 0) return this.NoGroups();
        Integer listPages = (int) Math.ceil((double) listSize / pgSize);
        if (listPages < page || listPages < 0) return this.AccessingInvalidPage(page, listPages);
        this.message.append(text("Warp Groups (" + page + "/" + listPages + ")\n"));
        for (Map.Entry<String, Integer> groupEntry : WarpsService.getGroups().entrySet()) {
            currentEntry++;
            if (currentEntry - 1 > pgEnd) break;
            if (currentEntry - 1 < pgStart) continue;
            this.message.append(fGroup(groupEntry.getKey()))
                    .append(text(" (" + groupEntry.getValue() + " Warps)", GRAY));
            if (currentEntry - 1 < pgEnd && currentEntry < listSize) this.message.append(text(", ", GRAY));
        }
        return this;
    }

    public CommandFeedback WarpInfo(String name) {
        Warp warp = WarpsService.getWarp(name);
        this.message.append(text(new String(new char[53]).replace("\0", "-"), DARK_GRAY))
                .appendNewline()
                .append(fWarp(name).decorate(TextDecoration.BOLD))
                .appendNewline()
                .append(text(new String(new char[53]).replace("\0", "-"), DARK_GRAY))
                .appendNewline()
                .append(text("Group", GRAY, TextDecoration.BOLD))
                .append(text("\n" + warp.getGroupName(), DARK_AQUA))
                .append(text("\nLocation", GRAY, TextDecoration.BOLD))
                .append(text("\n" + warp.getLocation().getBlockX() + " " + warp.getLocation().getBlockY() + " " + warp.getLocation().getBlockZ() + " (" + warp.getLocation().getWorld().getName() + ")", DARK_AQUA))
                .append(text("\nCreated", GRAY, TextDecoration.BOLD))
                .append(text("\n" + formatEpochSecs(warp.getCreated()) + " by " + warp.getCreatedBy(), DARK_AQUA))
                .append(text("\nLast Access", GRAY, TextDecoration.BOLD))
                .append(text("\n" + formatEpochSecs(warp.getLastAccess()) + " by " + warp.getLastAccessedBy(), DARK_AQUA))
                .append(text("\nLast Modification", GRAY, TextDecoration.BOLD))
                .append(text("\n" + formatEpochSecs(warp.getLastModified()) + " by " + warp.getLastModifiedBy(), DARK_AQUA))
                .append(text("\nMessage", GRAY, TextDecoration.BOLD))
                .append(text("\n > ", DARK_AQUA))
                .append(warp.getMessageAsComponent())
                .append(text(" <", DARK_AQUA));
        return this;
    }

    public CommandFeedback WarpMoved(String name, Location location) {
        this.message.append(text("Warp ", GREEN))
                .append(fWarp(name))
                .append(text(" has been moved to ", GREEN))
                .append(text(location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ() + " (" + location.getWorld().getName() + ")", DARK_AQUA))
                .append(text(".", GREEN));
        return this;
    }

    public CommandFeedback WarpRenamed(String oldname, String newname) {
        this.message.append(text("Warp ", GREEN))
                .append(fWarp(oldname))
                .append(text(" has been renamed to ", GREEN))
                .append(fWarp(newname))
                .append(text(".", GREEN));
        return this;
    }

    public CommandFeedback WarpMessageChanged(String warp, String message) {
        this.message.append(text("Warp ", GREEN))
                .append(fWarp(warp))
                .append(text(" has a new message:\n", GREEN))
                .append(text("> ", DARK_AQUA))
                .append(JSONComponentSerializer.json().deserialize(message))
                .append(text(" <", DARK_AQUA));
        return this;
    }

    public CommandFeedback WarpGroupChanged(String warp, String newgroup) {
        this.message.append(text("Warp ", GREEN))
                .append(fWarp(warp))
                .append(text(" is now in group ", GREEN))
                .append(fGroup(newgroup))
                .append(text(".", GREEN));
        return this;
    }

    public CommandFeedback WarpAlreadyInGroup(String warp, String group) {
        this.message.append(text("Warp ", RED))
                .append(fWarp(warp))
                .append(text(" is already in group ", RED))
                .append(fGroup(group))
                .append(text(".", RED));
        return this;
    }

    public CommandFeedback NoGroups() {
        this.message.append(text("There are no Warp groups!", RED));
        return this;
    }

    public CommandFeedback WaypointDoesntExist(String wp) {
        this.message.append(text("Waypoint ", RED))
                .append(fWaypoint(wp))
                .append(text(" doesn't exist!", RED));
        return this;
    }

    public CommandFeedback WaypointRemoved(String wp) {
        this.message.append(text("Waypoint ", GREEN))
                .append(fWaypoint(wp))
                .append(text(" successfully deleted!", GREEN));
        return this;
    }

    public CommandFeedback WaypointCreated(String wp) {
        this.message.append(text("Waypoint ", GREEN))
                .append(fWaypoint(wp))
                .append(text(" created!", GREEN));
        return this;
    }

    public CommandFeedback WaypointAlreadyExists(String wp) {
        this.message.append(text("Waypoint ", RED))
                .append(fWaypoint(wp))
                .append(text(" already exists!", RED));
        return this;
    }

    public CommandFeedback GoingToWaypoint(String wp) {
        this.message.append(text("Going to ", GREEN))
                .append(fWaypoint(wp))
                .append(text(".", GREEN));
        return this;
    }

    public CommandFeedback NoPermissionToGoToWaypoint(String wp) {
        this.message.append(text("You're missing the required permission to go to ", RED))
                .append(fWaypoint(wp))
                .append(text("!", RED));
        return this;
    }

    public CommandFeedback WaypointDeathSet(String wp, Long days) {
        this.message.append(text("Waypoint ", GREEN))
                .append(fWaypoint(wp))
                .append(text(" is now being killed in ", GREEN))
                .append(text(days, DARK_AQUA))
                .append(text(" days.", GREEN));
        return this;
    }

    public CommandFeedback WaypointGetDeath(String wp) {
        if (!WaypointsService.hasWaypoint(wp)) return this.WaypointDoesntExist(wp);
        Long killedAt = WaypointsService.getWaypoint(wp).getKilledAt();
        double killedIn = Math.ceil((double) (killedAt - Instant.now().getEpochSecond()) / (3600 * 24));
        this.message.append(text("Waypoint ", GRAY))
                .append(fWaypoint(wp))
                .append(text(" is being killed in ", GRAY))
                .append(text(killedIn, DARK_AQUA))
                .append(text(" days at ", GRAY))
                .append(text(formatEpochSecs(killedAt), DARK_AQUA))
                .append(text(".", GRAY));
        return this;
    }

    public CommandFeedback ListWaypoints(Integer page) {
        Integer pgSize = 9;
        Integer pgStart = (page - 1) * pgSize;
        Integer pgEnd = (page * pgSize) - 1;
        Integer currentEntry = 0;
        Integer listSize = WaypointsService.getAllWaypoints().size();
        if (listSize == 0) return this.NoWaypoints();
        Integer listPages = (int) Math.ceil((double) listSize / pgSize);
        if (listPages < page || listPages < 0) return this.AccessingInvalidPage(page, listPages);
        this.message.append(text("Waypoints (" + page + "/" + listPages + ")\n"));
        for (Map.Entry<String, Waypoint> wpEntry : WaypointsService.getAllWaypoints().entrySet()) {
            currentEntry++;
            if (currentEntry - 1 > pgEnd) break;
            if (currentEntry - 1 < pgStart) continue;
            this.message.append(fWaypoint(wpEntry.getKey()))
                    .append(text(" (in ", GRAY))
                    .append(text(wpEntry.getValue().getLocation().getWorld().getName(), DARK_AQUA))
                    .append(text(" by ", GRAY))
                    .append(text(wpEntry.getValue().getCreatedBy(), DARK_AQUA))
                    .append(text(")", GRAY));
            if (currentEntry - 1 < pgEnd && currentEntry < listSize) this.message.append(text(" - ", GRAY));
        }
        return this;

    }

    public CommandFeedback NoWaypoints() {
        this.message.append(text("There are no Waypoints!", RED));
        return this;
    }

    public CommandFeedback PluginReloaded() {
        this.message.append(text("WarpsManager has been reloaded!", GREEN, TextDecoration.BOLD));
        return this;
    }

    private CommandFeedback AccessingInvalidPage(Integer page, Integer maxPages) {
        this.message.append(text("Invalid page! Trying to access page " + page + " out of " + maxPages + ".", RED));
        return this;
    }

    private CommandFeedback ListWarpsInGroups_AccessingInvalidPage(String group, Integer page, Integer maxPages) {
        this.message.append(text("Invalid page! Trying to access page " + page + " out of " + maxPages + " in ", RED))
                .append(fGroup(group))
                .append(text(".", RED));
        return this;
    }

    private TextComponent fWarp(String warp) {
        return text(warp, YELLOW, TextDecoration.ITALIC);
    }

    private TextComponent fGroup(String group) {
        return text(group, LIGHT_PURPLE, TextDecoration.ITALIC);
    }

    private TextComponent fWaypoint(String waypoint) {
        return text(waypoint, GOLD, TextDecoration.ITALIC);
    }

    private String formatEpochSecs(Long epochSecs) {
        ZoneOffset offset = ConfigurationService.getUTCOffset();
        Instant instant = Instant.ofEpochSecond(epochSecs);
        ZonedDateTime dateTime = instant.atZone(offset);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy h:mm:ss a");
        return dateTime.format(formatter);
    }

}
