package com.antikeBiene.warpsManager.models;

import org.bukkit.Location;

import java.time.Instant;

public class Waypoint {
    private Location location;
    private Long created;
    private String createdBy;
    private Long killedAt;
    private Boolean notifiedDeath;

    public Waypoint(Location location, Long created, String createdBy, Long killedAt, Boolean notifiedDeath) {
        this.location = location;
        this.created = created;
        this.createdBy = createdBy;
        this.killedAt = killedAt;
        this.notifiedDeath = notifiedDeath;
    }

    public Waypoint(Location location, String createdBy, Long killedIn) {
        this.location = location;
        this.created = Instant.now().getEpochSecond();
        this.createdBy = createdBy;
        this.killedAt = killedIn + Instant.now().getEpochSecond();
        this.notifiedDeath = false;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Long getKilledAt() {
        return killedAt;
    }

    public void setKilledAt(Long killedAt) {
        this.killedAt = killedAt;
    }

    public void setKilledAt() {
        setKilledAt(Instant.now().getEpochSecond());
    }

    public Long getKilledIn() { return this.killedAt - Instant.now().getEpochSecond(); }

    public void setKilledIn(Long killedIn) {
        setKilledAt(killedIn + Instant.now().getEpochSecond());
    }

    public Boolean getNotifiedDeath() { return notifiedDeath; }

    public void setNotifiedDeath(Boolean notifiedDeath) { this.notifiedDeath = notifiedDeath; }
}
