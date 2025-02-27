package com.antikeBiene.warpsManager.accessibles;

import com.antikeBiene.warpsManager.models.Warp;
import com.antikeBiene.warpsManager.models.Waypoint;
import com.antikeBiene.warpsManager.services.*;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

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
        this.message.append(text("The warp ", negC()))
                    .append(fWarp(warp))
                    .append(text(" doesn't exist.", negC()));
        return this;
    }

    public CommandFeedback WarpingTo(String warp) {
        this.message.append(text("Warping to ", posC()))
                .append(fWarp(warp))
                .append(text(".", posC()));
        return this;
    }

    public CommandFeedback NoPermissionToWarp(String warp) {
        this.message.append(text("You're missing the required group/warp permission to warp to ", negC()))
                .append(fWarp(warp))
                .append(text(".", negC()));
        return this;
    }

    public CommandFeedback WarpCreated(String warp) {
        this.message.append(text("Warp ", posC()))
                .append(fWarp(warp))
                .append(text(" successfully created!", posC()));
        return this;
    }

    public CommandFeedback WarpAlreadyExists(String warp) {
        this.message.append(text("The warp " , negC()))
                .append(fWarp(warp))
                .append(text(" already exists!", negC()));
        return this;
    }

    public CommandFeedback WarpRemoved(String warp) {
        this.message.append(text("Warp ", posC()))
                .append(fWarp(warp))
                .append(text(" successfully deleted!", posC()));
        return this;
    }

    public CommandFeedback NoPermissionToListWarps(String group) {
        this.message.append(text("You are not permitted to list warps of ", negC()))
                .append(fGroup(group))
                .append(text("!", negC()));
        return this;
    }

    public CommandFeedback NoPermissionToListGroups() {
        this.message.append(text("You are not permitted to list warp groups!", negC()));
        return this;
    }

    public CommandFeedback GroupDoesntExist(String group) {
        this.message.append(text("The group ", negC()))
                .append(fGroup(group))
                .append(text(" doesn't exist.", negC()));
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
                .append(text(" - Warps (" + page + "/" + groupPages + ")", ntlC()));
        for (Map.Entry<String, Warp> warpEntry : WarpsService.getAllWarps().entrySet()) {
            Warp warp = warpEntry.getValue();
            String name = warpEntry.getKey();
            if (group.equals(warp.getGroupName())) {
                currentEntry++;
                if (currentEntry - 1 > pgEnd) break;
                if (currentEntry - 1 < pgStart) continue;
                this.message.append(text("\n"))
                        .append(fWarp(name)
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/warp " + warp.getGroupName() + ":" + name))
                                .hoverEvent(HoverEvent.showText(text("Click to warp"))))
                        .append(text(" | in ", descC()))
                        .append(text(warp.getLocation().getWorld().getName(), varC()))
                        .append(text(" | by ", descC()))
                        .append(text(warp.getCreatedBy(), varC()));
            }
        }
        return this;
    }

    public CommandFeedback ListAllWarps(Integer page) {
        Integer totalSize = WarpsService.getAllWarpIds().size();
        if (totalSize == 0) return this.NoWarps();
        Integer pgSize = 8;
        Integer pgStart = (page - 1) * pgSize;
        Integer pgEnd = (page * pgSize) - 1;
        Integer currentEntry = 0;
        Integer totalPages = (int) Math.ceil((double) totalSize / pgSize);
        if (totalPages < page || page < 1) return this.AccessingInvalidPage(page, totalPages);
        this.message.append(text("All Warps (" + page + "/" + totalPages + ")", ntlC()));
        for (Map.Entry<String, Warp> warpEntry : WarpsService.getAllWarps().entrySet()) {
            Warp warp = warpEntry.getValue();
            String name = warpEntry.getKey();
            currentEntry++;
            if (currentEntry - 1 > pgEnd) break;
            if (currentEntry - 1 < pgStart) continue;
            this.message.append(text("\n"))
                    .append(fWarp(name)
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/warp " + warp.getGroupName() + ":" + name))
                            .hoverEvent(HoverEvent.showText(text("Click to warp"))))
                    .append(text(" (", descC()))
                    .append(fGroup(warp.getGroupName())
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/warps " + warp.getGroupName()))
                            .hoverEvent(HoverEvent.showText(text("Click to show all warps in group")))
                    )
                    .append(text(")", descC()))
                    .append(text(" | in ", descC()))
                    .append(text(warp.getLocation().getWorld().getName(), varC()))
                    .append(text(" | by ", descC()))
                    .append(text(warp.getCreatedBy(), varC()));
        }
        return this;
    }

    public CommandFeedback NoWarps() {
        this.message.append(text("There are no Warps!", negC()));
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
        this.message.append(text("Warp Groups (" + page + "/" + listPages + ")\n", ntlC()));
        for (Map.Entry<String, Set<String>> groupEntry : WarpsService.getGroups().entrySet()) {
            currentEntry++;
            if (currentEntry - 1 > pgEnd) break;
            if (currentEntry - 1 < pgStart) continue;
            this.message.append(fGroup(groupEntry.getKey())
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/warps " + groupEntry.getKey()))
                        .hoverEvent(HoverEvent.showText(text("Click to list warps in group")))
                    )
                    .append(text(" (" + groupEntry.getValue().size() + " Warps)", varC()));
            if (currentEntry - 1 < pgEnd && currentEntry < listSize) this.message.append(text(", ", descC()));
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
                .append(text("Group", descC(), TextDecoration.BOLD))
                .append(text("\n" + warp.getGroupName(), varC()))
                .append(text("\nLocation", descC(), TextDecoration.BOLD))
                .append(text("\n" + warp.getLocation().getBlockX() + " " + warp.getLocation().getBlockY() + " " + warp.getLocation().getBlockZ() + " (" + warp.getLocation().getWorld().getName() + ")", varC()))
                .append(text("\nCreated", descC(), TextDecoration.BOLD))
                .append(text("\n" + formatEpochSecs(warp.getCreated()) + " by " + warp.getCreatedBy(), varC()))
                .append(text("\nLast Access", descC(), TextDecoration.BOLD))
                .append(text("\n" + formatEpochSecs(warp.getLastAccess()) + " by " + warp.getLastAccessedBy(), varC()))
                .append(text("\nLast Modification", descC(), TextDecoration.BOLD))
                .append(text("\n" + formatEpochSecs(warp.getLastModified()) + " by " + warp.getLastModifiedBy(), varC()))
                .append(text("\nMessage", descC(), TextDecoration.BOLD))
                .append(text("\n > ", varC()))
                .append(warp.getMessageAsComponent())
                .append(text(" <", varC()));
        return this;
    }

    public CommandFeedback WarpMoved(String name, Location location) {
        this.message.append(text("Warp ", posC()))
                .append(fWarp(name))
                .append(text(" has been moved to ", posC()))
                .append(text(location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ() + " (" + location.getWorld().getName() + ")", varC()))
                .append(text(".", posC()));
        return this;
    }

    public CommandFeedback WarpRenamed(String oldname, String newname) {
        this.message.append(text("Warp ", posC()))
                .append(fWarp(oldname))
                .append(text(" has been renamed to ", posC()))
                .append(fWarp(newname))
                .append(text(".", posC()));
        return this;
    }

    public CommandFeedback WarpMessageChanged(String warp, String message) {
        this.message.append(text("Warp ", posC()))
                .append(fWarp(warp))
                .append(text(" has a new message:\n", posC()))
                .append(text("> ", varC()))
                .append(JSONComponentSerializer.json().deserialize(message))
                .append(text(" <", varC()));
        return this;
    }

    public CommandFeedback WarpGroupChanged(String warp, String newgroup) {
        this.message.append(text("Warp ", posC()))
                .append(fWarp(warp))
                .append(text(" is now in group ", posC()))
                .append(fGroup(newgroup))
                .append(text(".", posC()));
        return this;
    }

    public CommandFeedback WarpAlreadyInGroup(String warp, String group) {
        this.message.append(text("Warp ", negC()))
                .append(fWarp(warp))
                .append(text(" is already in group ", negC()))
                .append(fGroup(group))
                .append(text(".", negC()));
        return this;
    }

    public CommandFeedback NoGroups() {
        this.message.append(text("There are no Warp groups!", negC()));
        return this;
    }

    public CommandFeedback WaypointDoesntExist(String wp) {
        this.message.append(text("Waypoint ", negC()))
                .append(fWaypoint(wp))
                .append(text(" doesn't exist!", negC()));
        return this;
    }

    public CommandFeedback WaypointRemoved(String wp) {
        this.message.append(text("Waypoint ", posC()))
                .append(fWaypoint(wp))
                .append(text(" successfully deleted!", posC()));
        return this;
    }

    public CommandFeedback WaypointCreated(String wp) {
        this.message.append(text("Waypoint ", posC()))
                .append(fWaypoint(wp))
                .append(text(" created!", posC()));
        return this;
    }

    public CommandFeedback WaypointAlreadyExists(String wp) {
        this.message.append(text("Waypoint ", negC()))
                .append(fWaypoint(wp))
                .append(text(" already exists!", negC()));
        return this;
    }

    public CommandFeedback GoingToWaypoint(String wp) {
        this.message.append(text("Going to ", posC()))
                .append(fWaypoint(wp))
                .append(text(".", posC()));
        return this;
    }

    public CommandFeedback NoPermissionToGoToWaypoint(String wp) {
        this.message.append(text("You're missing the required permission to go to ", negC()))
                .append(fWaypoint(wp))
                .append(text("!", negC()));
        return this;
    }

    public CommandFeedback WaypointDeathSet(String wp, Long days) {
        this.message.append(text("Waypoint ", posC()))
                .append(fWaypoint(wp))
                .append(text(" is now being killed in ", posC()))
                .append(text(days, varC()))
                .append(text(" days.", posC()));
        return this;
    }

    public CommandFeedback WaypointGetDeath(String wp) {
        if (!WaypointsService.hasWaypoint(wp)) return this.WaypointDoesntExist(wp);
        Long killedAt = WaypointsService.getWaypoint(wp).getKilledAt();
        double killedIn = Math.ceil((double) (killedAt - Instant.now().getEpochSecond()) / (3600 * 24));
        this.message.append(text("Waypoint ", descC()))
                .append(fWaypoint(wp))
                .append(text(" is being killed in ", descC()))
                .append(text((int) killedIn, varC()))
                .append(text(" days at ", descC()))
                .append(text(formatEpochSecs(killedAt), varC()))
                .append(text(".", descC()));
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
        this.message.append(text("Waypoints (" + page + "/" + listPages + ")", ntlC()));
        for (Map.Entry<String, Waypoint> wpEntry : WaypointsService.getAllWaypoints().entrySet()) {
            currentEntry++;
            if (currentEntry - 1 > pgEnd) break;
            if (currentEntry - 1 < pgStart) continue;
            this.message.appendNewline()
                    .append(fWaypoint(wpEntry.getKey()))
                    .append(text(" (in ", descC()))
                    .append(text(wpEntry.getValue().getLocation().getWorld().getName(), varC()))
                    .append(text(" by ", descC()))
                    .append(text(wpEntry.getValue().getCreatedBy(), varC()))
                    .append(text(")", descC()));
        }
        return this;

    }

    public CommandFeedback NoWaypoints() {
        this.message.append(text("There are no Waypoints!", negC()));
        return this;
    }

    public CommandFeedback PluginReloaded() {
        this.message.append(text("WarpsManager has been reloaded!", posC(), TextDecoration.BOLD));
        return this;
    }

    public CommandFeedback WarpNotInGroup(String id, String group) {
        this.message.append(text("Warp ", negC()))
                .append(fWarp(id))
                .append(text(" is not part of group ", negC()))
                .append(fGroup(group))
                .append(text("!", negC()));
        return this;
    }

    public CommandFeedback InvalidKey(String key) {
        this.message.append(text("The key ", negC()))
                .append(text(key, varC()))
                .append(text(" does not redirect to any valid warp.", negC()));
        return this;
    }

    public CommandFeedback WaypointExpires(String id) {
        this.message.append(text("Your waypoint ", descC()))
                .append(fWaypoint(id))
                .append(text(" expires in less than ", descC()))
                .append(text(ConfigurationService.getWaypointReminder(), varC()))
                .append(text(" days!", descC()));
        return this;
    }

    public CommandFeedback DataLoaded() {
        this.message.append(text("Data has been loaded from files.", posC()));
        return this;
    }

    public CommandFeedback DataSaved() {
        this.message.append(text("Data has been saved to files.", posC()));
        return this;
    }

    public CommandFeedback GroupAlreadyExists(String group) {
        this.message.append(text("The group " , negC()))
                .append(fGroup(group))
                .append(text(" already exists!", negC()));
        return this;
    }

    public CommandFeedback GroupMerged(String oldGroup, String newGroup) {
        this.message.append(text("Group ", posC()))
                .append(fGroup(oldGroup))
                .append(text(" has been merged to group ", posC()))
                .append(fGroup(newGroup))
                .append(text(".", posC()));
        return this;
    }

    public CommandFeedback GroupRenamed(String group, String newName) {
        this.message.append(text("Group ", posC()))
                .append(fGroup(group))
                .append(text(" has been renamed to ", posC()))
                .append(fGroup(newName))
                .append(text(".", posC()));
        return this;
    }

    private CommandFeedback AccessingInvalidPage(Integer page, Integer maxPages) {
        this.message.append(text("Invalid page! Trying to access page " + page + " out of " + maxPages + ".", negC()));
        return this;
    }

    private CommandFeedback ListWarpsInGroups_AccessingInvalidPage(String group, Integer page, Integer maxPages) {
        this.message.append(text("Invalid page! Trying to access page " + page + " out of " + maxPages + " in ", negC()))
                .append(fGroup(group))
                .append(text(".", negC()));
        return this;
    }

    private TextComponent fWarp(String warp) {
        return text(warp, ConfigurationService.getColor("warp"), TextDecoration.ITALIC);
    }

    private TextComponent fGroup(String group) {
        return text(group, ConfigurationService.getColor("group"), TextDecoration.ITALIC);
    }

    private TextComponent fWaypoint(String waypoint) {
        return text(waypoint, ConfigurationService.getColor("waypoint"), TextDecoration.ITALIC);
    }

    private NamedTextColor posC() {
        return ConfigurationService.getColor("positive");
    }

    private NamedTextColor negC() { return ConfigurationService.getColor("negative"); }

    private NamedTextColor descC() { return ConfigurationService.getColor("descriptive"); }

    private NamedTextColor ntlC() { return ConfigurationService.getColor("neutral"); }

    private NamedTextColor varC() { return ConfigurationService.getColor("variable"); }

    private String formatEpochSecs(Long epochSecs) {
        ZoneOffset offset = ConfigurationService.getUTCOffset();
        Instant instant = Instant.ofEpochSecond(epochSecs);
        ZonedDateTime dateTime = instant.atZone(offset);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy h:mm:ss a");
        return dateTime.format(formatter);
    }

}
