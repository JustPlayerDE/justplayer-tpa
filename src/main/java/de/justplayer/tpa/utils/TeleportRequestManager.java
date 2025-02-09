package de.justplayer.tpa.utils;

import de.justplayer.tpa.Plugin;
import de.justplayer.tpa.Request;
import de.justplayer.tpa.ReturnRequest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class TeleportRequestManager {
    private final Plugin plugin;
    private final List<Request> requests = new ArrayList<>();
    private final HashMap<UUID, ReturnRequest> returnRequests = new HashMap<>();
    private BukkitTask scheduler;

    public TeleportRequestManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (this.scheduler != null) {
            plugin.log("Teleport Scheduler has been started while one is already started.", "Debug");
            this.scheduler.cancel();
        }

        this.scheduler = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            String prefix = plugin.translate("messages.prefix");
            List<Request> requestList = new ArrayList<>(requests);
            HashMap<UUID, ReturnRequest> returnRequestMap = new HashMap<>(returnRequests);
            List<UUID> ignoredPlayersForThisRun = new ArrayList<>();

            returnRequestMap.forEach((UUID playerId, ReturnRequest request) -> {

                if(!request.getRequested()) {
                    int timeout = plugin.getConfig().getInt("tpa.return-timeout");
                    if (timeout > 0 && request.isTimedOut(timeout)) {
                        // Silently remove it, the player doesn't care about returns as much and if so they already used it.
                        plugin.log("Return request for " + playerId + " has been timed out", "Debug");
                        returnRequests.remove(playerId);
                    }

                    return;
                }

                returnRequests.remove(playerId);
                ignoredPlayersForThisRun.add(playerId);
                Player teleportPlayer = Bukkit.getPlayer(playerId);

                if(teleportPlayer == null) {
                    return;
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        teleportPlayer.teleport(request.getLocation());
                    }
                }.runTask(plugin);
            });

            // Then normal requests
            for (Request request : requestList) {
                Player sender = plugin.getServer().getPlayer(request.getSender());
                Player receiver = plugin.getServer().getPlayer(request.getReceiver());

                if (sender == null || receiver == null) {

                    plugin.log("Request for " + request.getSender() + " to " + request.getReceiver() + " has been removed because either sender or receiver is gone.", "Debug");
                    cancelRequest(request);
                    continue;
                }

                if(ignoredPlayersForThisRun.contains(request.getSender())) {
                    plugin.log("Request for " + request.getSender() + " to " + request.getReceiver() + " has been ignored for now because sender has requested a return before.", "Debug");
                    continue;
                }

                // Timeout check
                if (request.isTimedOut(plugin.getConfig().getInt("tpa.timeout")) && !request.isAccepted()) {
                    plugin.log("Request for " + request.getSender() + " to " + request.getReceiver() + " has been timed out.", "Debug");
                    cancelRequest(
                            request,
                            "messages.request.timeout-to",
                            Map.of("playername", receiver.getName()),
                            "messages.request.timeout-from",
                            Map.of("playername", sender.getName())
                    );

                    continue;
                }

                // Accept check
                if (request.isAccepted()) {
                    plugin.log("Request for " + request.getSender() + " to " + request.getReceiver() + " has been accepted.", "Debug");
                    Player teleportPlayer = request.isHereRequest() ? receiver : sender;
                    if (!request.isHereRequest()) {
                        sender.sendMessage(prefix + plugin.translate("messages.request.teleported-to", Map.of("playername", receiver.getName())));
                        receiver.sendMessage(prefix + plugin.translate("messages.request.teleported-from", Map.of("playername", sender.getName())));
                    } else {
                        sender.sendMessage(prefix + plugin.translate("messages.request.teleported-from", Map.of("playername", receiver.getName())));
                        receiver.sendMessage(prefix + plugin.translate("messages.request.teleported-to", Map.of("playername", sender.getName())));
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            ReturnRequest returnRequest = new ReturnRequest(
                                    teleportPlayer.getUniqueId(),
                                    teleportPlayer.getLocation(),
                                    System.currentTimeMillis()
                            );

                            returnRequests.put(teleportPlayer.getUniqueId(), returnRequest);
                            teleportPlayer.teleport(request.isHereRequest() ? sender : receiver);
                        }
                    }.runTask(plugin);

                    requests.remove(request);
                }
            }
        }, 0, 20);
    }

    public void stop() {
        plugin.log("Stopping teleport scheduler task.", "Debug");
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

    public ReturnRequest getPlayerReturnRequest(UUID playerId) {
        return returnRequests.get(playerId);
    }

    /**
     * Remove all requests from or to a specific player
     */
    public void removeRequests(UUID playerId) {
        List<Request> foundRequests = new ArrayList<>();

        for (Request request : requests) {
            if (request.getSender().equals(playerId) || request.getReceiver().equals(playerId)) {
                foundRequests.add(request);
            }
        }

        for (Request request : foundRequests) {
            cancelRequest(request);
            requests.remove(request);
        }

        returnRequests.remove(playerId);
    }

    public void acceptRequest(Request request) {
        request.setAccepted(true);
    }

    // We don't talk about the code below

    public void cancelRequest(Request request, String senderReason, String receiverReason) {
        Player sender = plugin.getServer().getPlayer(request.getSender());
        Player receiver = plugin.getServer().getPlayer(request.getReceiver());
        String prefix = plugin.translate("messages.prefix");

        if (sender == null && receiver == null) {
            // both players are offline or invalid
            requests.remove(request);
            return;
        }

        if (sender != null) {
            sender.sendMessage(prefix + plugin.translate(senderReason));
        }

        if (receiver != null && !receiverReason.isEmpty()) {
            receiver.sendMessage(prefix + plugin.translate(receiverReason));
        }

        requests.remove(request);
    }

    public void cancelRequest(ReturnRequest request, String senderReason, String receiverReason) {
        Player sender = plugin.getServer().getPlayer(request.getPlayerId());
        String prefix = plugin.translate("messages.prefix");

        if (sender == null) {
            returnRequests.remove(request.getPlayerId());
            return;
        }

        sender.sendMessage(prefix + plugin.translate(senderReason));
        returnRequests.remove(request.getPlayerId());
    }

    public void cancelRequest(Request request, String key, Map<String, String> placeholders)
    {
        this.cancelRequest(request, plugin.translate(key,placeholders));
    }

    public void cancelRequest(ReturnRequest returnRequest, String key, Map<String, String> placeholders)
    {
        this.cancelRequest(returnRequest, plugin.translate(key,placeholders));
    }

    public void cancelRequest(Request request, String senderKey, Map<String, String> senderPlaceholders, String receiverKey, Map<String, String> receiverPlaceholders)
    {
        cancelRequest(request,
                plugin.translate(senderKey,senderPlaceholders),
                plugin.translate(receiverKey,receiverPlaceholders)
        );
    }
    public void cancelRequest(ReturnRequest request, String senderKey, Map<String, String> senderPlaceholders, String receiverKey, Map<String, String> receiverPlaceholders)
    {
        cancelRequest(request,
                plugin.translate(senderKey,senderPlaceholders),
                plugin.translate(receiverKey,receiverPlaceholders)
        );
    }

    public void cancelRequest(Request request, String reason) {
        cancelRequest(request, reason, reason);
    }
    public void cancelRequest(ReturnRequest request, String reason) {
        cancelRequest(request, reason, reason);
    }

    public void cancelRequest(Request request) {
        cancelRequest(request, "messages.request.canceled");
    }
    public void cancelRequest(ReturnRequest request) {
        cancelRequest(request, "messages.request.canceled");
    }
}