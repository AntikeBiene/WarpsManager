
# WarpsManager

WarpsManager is a Minecraft-Plugin for PaperMC (incompatible with standalone Spigot/CraftBukkit) that 
provides a more systematical approach to Warps compared to EssentialsX. WarpsManager offers not only Warps as a means of 
teleportation but also enables users to group and modify them in addition to some insights. WarpsManager also 
adds Waypoints which are essentially primitive and short-lived Warps.

**Versions:** v1.0 - v1.2 are compatible with 1.21.4, 1.21.5 and 1.21.7; v1.3 is compatible with 26.3

## Warps and Groups

WarpsManager requires every Warp to be part of a group. Warps may be written as "_[GROUP NAME].[WARP NAME]_" (with a dot in between). These groups exist only for organizational purposes and should not 
be seen as namespaces. Hence, it is not possible to create multiple Warps with the same name but different groups. For this reason, writing "_[GROUP NAME].[WARP NAME]_" means the same 
as just writing "_[WARP NAME]_". Both are considered the identification key of a Warp (or just "key"). This notation allows both a shorthand way of referring to Warps by name and a more specific referral by adding its group name.

Warps have some additional properties besides their name, location and group: this information includes their creation, access (the last time someone teleported to this location using the Warp), modification and message. 
The creation, access and modification provides both a timestamp and the name of the corresponding player. The message is a chat message that is shown to a player when they teleport to the location using the Warp. It is 
initially set to "You've been warped to [Warp]."

Groups can not be created individually. They are always just a trait of Warps. A group with no Warps is being deleted immediately. 
A group will always be created if a corresponding Warp is created where the group name will be provided during creation.

Dots inside a Warp or group name are always replaced by an underscore.

You can learn more about Warp and group modification by checking out the commands down below.

## Waypoints

WarpsManager's Waypoints are a way to save locations which are only required temporarily. A new Waypoint's lifetime is 
initially set to 30 days. If a Waypoint will be deleted in less than 3 days (initially) then the creator of the Waypoint 
will be notified in chat the next time they are online. Waypoints can not be grouped. To avoid confusion with the vanilla 
command "/waypoint" every WarpsManager-related Waypoint command abbreviates Waypoint as "wp".

## Commands

* /warpsmanager [loaddata|reload|save] // debugging commands
  * loaddata - clears all data from memory and loads them directly from the saved files (may cause data loss)
  * reload - reloads the config; saves all data to the files and loads them from there afterward
  * save - saves all data in memory to the files
* /setwarp <id\> <group\> - creates a Warp at the player's current location
  * <id\> - name of the Warp
  * <group\> - name of an existing or new group
* /warp <key\> - uses a Warp to teleport to a location
  * <key\> - identification key of the Warp (with or without group name)
* /delwarp <key\> - deletes a Warp
  * <key\> - identification key of the Warp (with or without group name)
* /warpmodify <key\> [group|message|move|rename] // warp modification commands
  * <key\> - identification key of the Warp (with or without group name)
  * group <group\> - changes the group name
    * <group\> - new group name
  * message <message\> - changes the Warp message
    * <message\> - message as a text component (JSON)
  * move (<pos\>) - moves the Warp to another location
    * <pos\> - coordinates of the new location (leave empty for current player location)
  * rename <newID\> - renames a Warp
    * <newID\> - new name of the Warp
* /warpinfo <key\> (<page\>) - returns all available information about a given Warp
  * <key\> - identification key of the Warp (with or without group name)
  * <page\> - page number (for long lists)
* /allwarps - returns a list of all Warps
* /warps <group\> (<page\>) - returns a list of all Warps in a certain group
  * <group\> - name of an existing group
  * <page\> - page number (for long lists)
* /groups (<page\>) - return a list of all groups
  * <page\> - page number (for long lists)
* /groupmodify <group\> [merge|rename] // group modification commands
  * <group\> - name of an existing group
  * merge <newGroup\> - merges <group\> to <newGroup\> (all Warps in <group\> become Warps of <newGroup\>)
    * <newGroup\> - name of an existing group
  * rename <newName\> - renames the group
    * <newName\> - new name for the group (must not already belong to an existing group)
* /setwp <id\> (<death_in_days\>) - creates a Waypoint at the player's current location
  * <id\> - name of the Waypoint
  * <death_in_days\> - amount of days until the Waypoint gets deleted (max. 180)
* /wp <id\> - uses a Waypoint to teleport to a location
  * <id\> - name of the Waypoint
* /delwp <id\> - deletes a Waypoint
  * <id\> - name of the Waypoint
* /wplist (<page\>) - returns a list of all Waypoints
  * <page\> - page number (for long lists)
* /wpdeath <id\> - returns the time when a Waypoint is being killed
  * <id\> - name of the Waypoint
* /wpdeath <id\> <death_in_days\> - sets the amount of days until a Waypoint is being killed
  * <id\> - name of the Waypoint
  * <death_in_days\> - amount of days until the Waypoint is being killed

## Permissions

* warpsmanager.warp.set
  * /setwarp
* warpsmanager.warp.delete
  * /delwarp
* warpsmanager.warp.use.[*]
  * /warp
  * you may specify which Warp can be used
* warpsmanager.warp.group.use.[*]
  * /warp
  * you may specify of which group Warps can be used
* warpsmanager.warp.group.listall
  * /groups
* warpsmanager.warp.group.list.[*]
  * /warps
  * you may specify for which groups a list can be returned
* warpsmanager.warp.info
  * /warpinfo
* warpsmanager.warp.modify.move
  * /warpmodify ... move
* warpsmanager.warp.modify.rename
  * /warpmodify ... rename
* warpsmanager.warp.modify.message
  * /warpmodify ... message
* warpsmanager.warp.modify.group
  * /warpmodify ... group
* warpsmanager.waypoint.use
  * /wp
* warpsmanager.waypoint.set
  * /setwp
* warpsmanager.waypoint.delete
  * /delwp
* warpsmanager.waypoint.death.set
  * /wpdeath <id\> <death_in_days\>
* warpsmanager.waypoint.death.get
  * /wpdeath <id\>
* warpsmanager.waypoint.list
  * /wplist
* warpsmanager.admin
  * /warpsmanager
* warpsmanager.warp.group.modify.rename
  * /groupmodify ... rename
* warpsmanager.warp.group.modify.merge
  * /groupmodify ... merge

## Configuration

Initial configuration (config.yml)

```yaml title="config.yml"
# ANTIKE BIENE'S WARPSMANAGER CONFIGURATION

# Offset from UTC time
utc-offset: 0

# Standard Lifetime for Waypoints (emitted on Creation)
waypoint-standard-lifetime: 30

# Days before Waypoint Deletion to remind creator of Waypoints death
waypoint-death-reminder: 3

# ++ Message Formatting Configuration ++
# Available Colors:
# BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_GRAY, BLUE, GREEN, AQUA, DARK_RED, DARK_PURPLE,
# GRAY, RED, LIGHT_PURPLE, GOLD, YELLOW, WHITE

message-positive: 'GREEN'
message-negative: 'RED'
message-descriptive: 'GRAY'
message-neutral: 'WHITE'
message-variable: 'DARK_AQUA'
message-warp: 'YELLOW'
message-group: 'LIGHT_PURPLE'
message-waypoint: 'GOLD'
```

## Disclaimer
This plugin has not made and will never make significant use of LLM-output and every attempt to 
rewrite this plugin by "vibecoding" it is heavily discouraged by the original author (me).