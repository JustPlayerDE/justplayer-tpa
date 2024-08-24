package de.justplayer.tpa;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ReturnRequest {
    private final UUID playerId;
    private final Location location;
    private final long timestamp;
    private boolean requested = false;

    public ReturnRequest(UUID playerId, Location location, long timestamp) {
        this.playerId = playerId;
        this.location = location;
        this.timestamp = timestamp;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Location getLocation() {
        return location;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setRequested() {
        requested = true;
    }

    public boolean getRequested() {
        return requested;
    }

    public boolean isTimedOut(long timeOut) {
        return System.currentTimeMillis() - timestamp > timeOut * 1000L && !requested;
    }
}
