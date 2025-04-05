package de.justplayer.tpa;

import java.util.UUID;

public class TeleportRequest {
    private final UUID sender;
    private final UUID receiver;
    private final long timestamp;

    private boolean teleporting;
    private long warmUpSinceTimestamp;

    private final boolean isHereRequest;
    private boolean isAccepted = false;

    public TeleportRequest(UUID sender, UUID receiver, long timestamp, boolean isHereRequest) {
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.isHereRequest = isHereRequest;
    }

    public UUID getSender() {
        return sender;
    }

    public UUID getReceiver() {
        return receiver;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isHereRequest() {
        return isHereRequest;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }

    public boolean isTimedOut(long timeOut) {
        return System.currentTimeMillis() - timestamp > timeOut * 1000L;
    }

    public void setTeleporting(boolean teleporting) {
        this.teleporting = teleporting;
    }

    public boolean isTeleporting()
    {
        return this.teleporting;
    }

    public void setWarmUpSinceTimestamp(long timestamp) {
        this.warmUpSinceTimestamp = timestamp;
    }

    public long getWarmUpSinceTimestamp()
    {
        return this.warmUpSinceTimestamp;
    }
}
