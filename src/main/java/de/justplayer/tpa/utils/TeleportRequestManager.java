package de.justplayer.tpa.utils;

import de.justplayer.tpa.Plugin;
import de.justplayer.tpa.ReturnRequest;
import de.justplayer.tpa.TeleportRequest;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TeleportRequestManager {
    private final Plugin plugin;
    private final List<TeleportRequest> requests = new ArrayList<>();
    private final Map<UUID, ReturnRequest> returnRequests = new ConcurrentHashMap<>();
    private BukkitTask scheduler;

    public TeleportRequestManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (this.scheduler != null) {
            plugin.log("Teleport Scheduler has been started while one is already running.", "Warning");
            this.scheduler.cancel();
        }

        this.scheduler = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            var prefix = plugin.translate("messages.prefix");
            var warmUpTime = plugin.config.getInt("tpa.wait", 0);
            var ignoredPlayersForThisRun = processReturnRequests(warmUpTime, prefix);
            processTeleportRequests(ignoredPlayersForThisRun, warmUpTime, prefix);
        }, 0, 20);
    }

    private void processTeleportRequests(List<UUID> ignoredPlayersForThisRun, long warmUpTime, String prefix) {
        var tempRequests = new ArrayList<>(requests);

        for (var request : tempRequests) {
            var sender = plugin.getServer().getPlayer(request.getSender());
            var receiver = plugin.getServer().getPlayer(request.getReceiver());

            var teleportPlayer = request.isHereRequest() ? receiver : sender;
            var teleportPlayerTo = request.isHereRequest() ? sender : receiver;

            if (sender == null || receiver == null) {
                plugin.log("Request for " + request.getSender() + " to " + request.getReceiver() + " has been removed because either sender or receiver is gone.");
                cancelRequest(request);
                continue;
            }

            if (ignoredPlayersForThisRun.contains(request.getSender())) {
                plugin.log("Request for " + request.getSender() + " to " + request.getReceiver() + " has been ignored because sender has requested a return before.");
                continue;
            }

            var timeoutValue = plugin.getConfig().getInt("tpa.timeout");
            if (request.isTimedOut(timeoutValue) && !request.isAccepted() && !request.isTeleporting()) {
                plugin.log("Request for " + request.getSender() + " to " + request.getReceiver() + " has timed out.");

                cancelRequest(request,
                        "messages.request.timeout-to", Map.of("playername", receiver.getName()),
                        "messages.request.timeout-from", Map.of("playername", sender.getName())
                );

                continue;
            }

            if (request.isAccepted() && !request.isTeleporting()) {
                plugin.log("Request for " + request.getSender() + " to " + request.getReceiver() + " has been accepted.");
                if (warmUpTime > 0 && !sender.hasPermission("justplayer.tpa.wait.bypass")) {
                    sender.sendMessage(prefix + plugin.translate(
                            request.isHereRequest() ? "messages.request.wait-to-here" : "messages.request.wait-to",
                            Map.of("playername", receiver.getName(), "time", String.valueOf(warmUpTime)))
                    );

                    receiver.sendMessage(prefix + plugin.translate(
                            request.isHereRequest() ? "messages.request.wait-from-here" : "messages.request.wait-from",
                            Map.of("playername", sender.getName(), "time", String.valueOf(warmUpTime)))
                    );
                }
                request.setTeleporting(true);
                request.setWarmUpSinceTimestamp(System.currentTimeMillis());
            }

            if (request.isTeleporting() && (request.getWarmUpSinceTimestamp() + (warmUpTime * 1000) <= System.currentTimeMillis() || sender.hasPermission("justplayer.tpa.wait.bypass"))) {

                teleportPlayer.sendMessage(prefix + plugin.translate("messages.request.teleported-to", Map.of("playername", teleportPlayerTo.getName())));
                teleportPlayerTo.sendMessage(prefix + plugin.translate("messages.request.teleported-from", Map.of("playername", teleportPlayer.getName())));

                plugin.log("Request for " + request.getSender() + " to " + request.getReceiver() + " has been fulfilled.");

                new BukkitRunnable() {
                    public void run() {
                        var returnRequest = new ReturnRequest(
                                teleportPlayer.getUniqueId(),
                                teleportPlayer.getLocation(),
                                System.currentTimeMillis()
                        );
                        returnRequests.put(teleportPlayer.getUniqueId(), returnRequest);
                        teleportPlayer.teleport(teleportPlayerTo);
                    }
                }.runTask(plugin);

                requests.remove(request);
            }
        }
    }

    private List<UUID> processReturnRequests(long warmUpTime, String prefix) {
        var returnedPlayers = new ArrayList<UUID>();

        for (var playerId : new ArrayList<>(returnRequests.keySet())) {
            var request = returnRequests.get(playerId);
            var teleportPlayer = Bukkit.getPlayer(request.getPlayerId());
            if (teleportPlayer == null) {
                continue;
            }

            var hasBypassReturnTimeout = teleportPlayer.hasPermission("justplayer.tpa.return-timeout.bypass");
            var hasBypassWait = teleportPlayer.hasPermission("justplayer.tpa.wait.bypass");
            var isTeleporting = request.isTeleporting();

            if (!request.getRequested()) {
                var timeout = plugin.getConfig().getInt("tpa.return-timeout");

                if (!hasBypassReturnTimeout && timeout > 0 && request.isTimedOut(timeout)) {
                    plugin.log("Return request for " + request.getPlayerId() + " has timed out");
                    returnRequests.remove(playerId);
                }

                continue;
            }

            if (!isTeleporting) {
                request.setTeleporting(true);
                request.setWarmUpSinceTimestamp(System.currentTimeMillis());
                if (warmUpTime > 0 && !hasBypassWait) {
                    teleportPlayer.sendMessage(prefix + plugin.translate("messages.request.wait-return", Map.of("time", String.valueOf(warmUpTime))));
                }
            }

            long fulfillRequestAt = request.getWarmUpSinceTimestamp() + (warmUpTime * 1000);
            if (isTeleporting && (fulfillRequestAt <= System.currentTimeMillis() || hasBypassWait)) {
                returnRequests.remove(playerId);
                returnedPlayers.add(request.getPlayerId());

                new BukkitRunnable() {
                    public void run() {
                        teleportPlayer.teleport(request.getLocation());
                    }
                }.runTask(plugin);
            }
        }

        return returnedPlayers;
    }

    public void stop() {
        plugin.log("Stopping teleport scheduler task.");
        if (scheduler != null && !scheduler.isCancelled()) {
            scheduler.cancel();
        }
    }

    /**
     * Creates a new teleport request.
     */
    public void createRequest(UUID sender, UUID receiver, long timestamp, boolean isHereRequest) {
        requests.add(new TeleportRequest(sender, receiver, timestamp, isHereRequest));
    }

    /**
     * Creates a new teleport request with current timestamp.
     */
    public void createRequest(UUID sender, UUID receiver, boolean isHereRequest) {
        createRequest(sender, receiver, System.currentTimeMillis(), isHereRequest);
    }

    /**
     * Get all requests for a specific player (receiver).
     */
    public List<TeleportRequest> getRequestsForPlayer(UUID playerId) {
        return requests.stream()
                .filter(request -> request.getReceiver().equals(playerId))
                .collect(Collectors.toList());
    }

    /**
     * Get a request by the sender.
     */
    public TeleportRequest getRequestBySender(UUID playerId) {
        var requestTimeout = plugin.getConfig().getInt("tpa.timeout");
        
        for (Iterator<TeleportRequest> teleportRequestIterator = requests.iterator(); teleportRequestIterator.hasNext(); ) {
            var request = teleportRequestIterator.next();

            if (request.getSender().equals(playerId)) {
                if (request.isTimedOut(requestTimeout)) {
                    teleportRequestIterator.remove();

                    return null;
                }

                return request;
            }
        }

        return null;
    }

    /**
     * Get all requests.
     */
    public List<TeleportRequest> getRequests() {
        return requests;
    }

    /**
     * Get a request between two players.
     */
    public TeleportRequest getRequest(UUID sender, UUID receiver) {
        for (var request : requests) {
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
     * Remove all requests from or to a specific player.
     */
    public void removeRequests(UUID playerId) {
        var toRemove = requests.stream()
                .filter(request -> request.getSender().equals(playerId) || request.getReceiver().equals(playerId))
                .toList();

        toRemove.forEach(this::cancelRequest);
        requests.removeAll(toRemove);
        returnRequests.remove(playerId);
    }

    public void acceptRequest(TeleportRequest request) {
        request.setAccepted(true);
    }

    public void cancelRequest(TeleportRequest request, String senderMessage, String receiverMessage) {
        var sender = plugin.getServer().getPlayer(request.getSender());
        var receiver = plugin.getServer().getPlayer(request.getReceiver());
        var prefix = plugin.translate("messages.prefix");

        if (sender == null && receiver == null) {
            requests.remove(request);
            return;
        }

        if (sender != null) {
            sender.sendMessage(prefix + senderMessage);
        }

        if (receiver != null && receiverMessage != null && !receiverMessage.isEmpty()) {
            receiver.sendMessage(prefix + receiverMessage);
        }

        requests.remove(request);
    }

    public void cancelRequest(TeleportRequest request, String key, Map<String, String> placeholders) {
        cancelRequest(request, plugin.translate(key, placeholders));
    }


    public void cancelRequest(TeleportRequest request, String senderKey, Map<String, String> senderPlaceholders, String receiverKey, Map<String, String> receiverPlaceholders) {
        cancelRequest(request, plugin.translate(senderKey, senderPlaceholders), plugin.translate(receiverKey, receiverPlaceholders));
    }


    public void cancelRequest(TeleportRequest request, String reason) {
        cancelRequest(request, reason, reason);
    }

    public void cancelRequest(ReturnRequest request, String reason) {
        var sender = plugin.getServer().getPlayer(request.getPlayerId());
        var prefix = plugin.translate("messages.prefix");

        if (sender != null) {
            sender.sendMessage(prefix + reason);
        }

        returnRequests.remove(request.getPlayerId());
    }

    public void cancelRequest(TeleportRequest request) {
        cancelRequest(request, "messages.request.canceled");
    }

    public void cancelRequest(ReturnRequest request) {
        cancelRequest(request, "messages.request.canceled");
    }
}
