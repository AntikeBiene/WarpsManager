package com.antikeBiene.warpsManager.models;

import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Location;
import org.bukkit.World;

import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

public class Warp {
    private Location location;
    private Long created;
    private Long lastModified;
    private Long lastAccess;
    private String createdBy;
    private String lastModifiedBy;
    private String lastAccessedBy;
    private String groupName;
    private String message;

    public Warp(Location location, Long created, Long lastModified, Long lastAccess, String createdBy, String lastModifiedBy, String lastAccessedBy, String groupName, String message) {
        this.location = location;
        this.created = created;
        this.lastModified = lastModified;
        this.lastAccess = lastAccess;
        this.createdBy = createdBy;
        this.lastModifiedBy = lastModifiedBy;
        this.lastAccessedBy = lastAccessedBy;
        this.groupName = groupName;
        this.message = message;
    }

    public Warp(Location location, String createdBy, String groupName, String message) {
        this.location = location;
        this.created = Instant.now().getEpochSecond();
        this.lastModified = this.created;
        this.lastAccess = this.created;
        this.createdBy = createdBy;
        this.lastModifiedBy = createdBy;
        this.lastAccessedBy = createdBy;
        this.groupName = groupName;
        this.message = message;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public void setCreated() {
        setCreated(Instant.now().getEpochSecond());
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public void setLastModified() {
        setLastModified(Instant.now().getEpochSecond());
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Long lastAccess) {
        this.lastAccess = lastAccess;
    }

    public void setLastAccess() {
        setLastAccess(Instant.now().getEpochSecond());
    }

    public String getLastAccessedBy() {
        return lastAccessedBy;
    }

    public void setLastAccessedBy(String lastAccessedBy) {
        this.lastAccessedBy = lastAccessedBy;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Component getMessageAsComponent() {
        return JSONComponentSerializer.json().deserialize(this.message);
    }

    public void setMessageAsComponent(Component message) {
        this.message = JSONComponentSerializer.json().serialize(message);
    }

}
