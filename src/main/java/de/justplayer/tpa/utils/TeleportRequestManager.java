package de.justplayer.tpa.utils;

import de.justplayer.tpa.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeleportRequestManager {
    private final Plugin plugin;
    private final List<Request> requests = new ArrayList<>();
    private BukkitTask scheduler;

    public TeleportRequestManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (this.scheduler != null) {
            this.scheduler.cancel();
        }

        this.scheduler = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            String prefix = plugin.getConfig().getString("messages.prefix");

            for (Request request : requests) {
                Player sender = plugin.getServer().getPlayer(request.getSender());
                Player receiver = plugin.getServer().getPlayer(request.getReceiver());

                if (sender == null || receiver == null) {
                    cancelRequest(request);
                    return;
                }

                // Timeout check
                if (request.isTimedOut(plugin.getConfig().getInt("tpa.timeout"))) {
                    sender.sendMessage(prefix + "Your teleport request to " + request.getReceiver() + " has timed out");

                    requests.remove(request); // cleanup
                    return;
                }

                // Accept check
                if (request.isAccepted()) {
                    if (request.isHereRequest()) {
                        sender.teleport(receiver);
                        sender.sendMessage(prefix + "You have been teleported to " + receiver.getName());
                        receiver.sendMessage(prefix + sender.getName() + " has been teleported to you");
                    } else {
                        receiver.teleport(sender);
                        sender.sendMessage(prefix + receiver.getName() + " has been teleported to you");
                        receiver.sendMessage(prefix + "You have been teleported to " + sender.getName());
                    }

                    requests.remove(request);
                }
            }
        }, 0, 20);
    }

    public void stop() {
        if (scheduler != null && !scheduler.isCancelled()) {
            scheduler.cancel();
        }
    }

    /**
     * Creates a new teleport request
     */
    public void createRequest(UUID sender, UUID receiver, long timestamp, boolean isHereRequest) {
        requests.add(new Request(sender, receiver, timestamp, isHereRequest));
    }

    /**
     * Creates a new teleport request
     */
    public void createRequest(UUID sender, UUID receiver, boolean isHereRequest) {
        createRequest(sender, receiver, System.currentTimeMillis(), isHereRequest);
    }

    /**
     * Get all requests for a specific player (receiver)
     */
    public List<Request> getRequestsForPlayer(UUID playerId) {
        List<Request> foundRequests = new ArrayList<>();

        for (Request request : requests) {
            if (request.getReceiver().equals(playerId)) {
                foundRequests.add(request);
            }
        }

        return foundRequests;
    }

    /**
     * Get a request by the sender
     */
    public Request getRequestByPlayer(UUID playerId) {
        long requestTimeout = plugin.getConfig().getInt("tpa.timeout");
        for (Request request : requests) {
            if (request.getSender().equals(playerId)) {
                if (request.isTimedOut(requestTimeout)) {
                    requests.remove(request); // cleanup
                    return null;
                }
                return request;
            }
        }

        return null;
    }

    /**
     * Get all requests
     */
    public List<Request> getRequests() {
        return requests;
    }

    /**
     * Get all requests between two players
     */
    public Request getRequest(UUID sender, UUID receiver) {
        for (Request request : requests) {
            if (request.getSender().equals(sender) && request.getReceiver().equals(receiver)) {
                return request;
            }
        }

        return null;
    }

    /**
     * Remove all requests from or to a specific player
     */
    public void removeRequests(UUID playerId) {
        requests.removeIf(request ->
                request.getSender().equals(playerId)
                        || request.getReceiver().equals(playerId)
        );
    }


    public void cancelRequest(Request request) {
        Player sender = plugin.getServer().getPlayer(request.getSender());
        Player receiver = plugin.getServer().getPlayer(request.getReceiver());
        String prefix = plugin.getConfig().getString("messages.prefix");

        if(sender == null && receiver == null) {
            // both players are offline or invalid
            requests.remove(request);
            return;
        }

        if (sender != null) {
            sender.sendMessage(prefix + "Teleport cancelled");
        }

        if (receiver != null && request.isAccepted()) {
            receiver.sendMessage(prefix + "Teleport cancelled");
        }

        requests.remove(request);
    }

}


class Request {
    private final UUID sender;
    private final UUID receiver;
    private final long timestamp;

    private final boolean isHereRequest;
    private boolean isAccepted = false;

    public Request(UUID sender, UUID receiver, long timestamp, boolean isHereRequest) {
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
}