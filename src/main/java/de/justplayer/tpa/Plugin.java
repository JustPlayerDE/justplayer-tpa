package de.justplayer.tpa;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.justplayer.tpa.commands.*;
import de.justplayer.tpa.listeners.PlayerLeaveListener;
import de.justplayer.tpa.utils.CooldownManager;
import de.justplayer.tpa.utils.TeleportRequestManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Objects;

public class Plugin extends JavaPlugin {
    public Config config;
    public CooldownManager cooldownManager;
    public TeleportRequestManager teleportRequestManager;

    public Plugin() {
        config = new Config(this);
        cooldownManager = new CooldownManager();
        teleportRequestManager = new TeleportRequestManager(this);
    }

    @Override
    public void onEnable() {

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(this), this);

        // Register commands
        Objects.requireNonNull(getCommand("tpa")).setExecutor(new tpaCommandHandler(this));
        Objects.requireNonNull(getCommand("tpahere")).setExecutor(new tpahereCommandHandler(this));
        Objects.requireNonNull(getCommand("tpaccept")).setExecutor(new tpacceptCommandHandler(this));
        Objects.requireNonNull(getCommand("tpadeny")).setExecutor(new tpadenyCommandHandler(this));
        Objects.requireNonNull(getCommand("tpacancel")).setExecutor(new tpacancelCommandHandler(this));
        Objects.requireNonNull(getCommand("tpareload")).setExecutor(new tpareloadCommandHandler(this));
        Objects.requireNonNull(getCommand("tpareturn")).setExecutor(new tpareturnCommandHandler(this));
        // Start the teleport request manager scheduler
        teleportRequestManager.start();

        getLogger().info("JustTPA initialized");

        if(getDescription().getVersion().contains("dev")) {
            getLogger().warning("Using Development version, plugin may be unstable. bStats and Update checks are disabled.");
        }

        if (config.getBoolean("bStats.enabled") && !getDescription().getVersion().contains("dev")) {
            new Metrics(this, 18743);
            getLogger().info("bStats enabled");
        }

        /**
         * Check for updates using modrinth
         */
        if (config.getBoolean("check-for-updates") && !getDescription().getVersion().contains("dev")) {
            String minecraftVersion = getServer().getVersion().split("-")[0];
            String serverSoftware = getServer().getVersion().split("-")[1].split(" ")[0].toLowerCase();
            String currentPluginVersion = getDescription().getVersion();

            getLogger().info("Checking for updates...");

            getServer().getScheduler().runTaskAsynchronously(this, () -> {
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(java.net.URI.create("https://api.modrinth.com/v2/project/justplayer-tpa/version?game_versions=" + minecraftVersion + "&loaders=" + serverSoftware))
                        .GET()
                        .header("User-Agent", "JustPlayerDE/justplayer-tpa/v" + currentPluginVersion + " (https://modrinth.com/plugin/justplayer-tpa justin.k@justplayer.de)")
                        .build();

                try {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() != 200) {
                        getLogger().warning("Failed to check for updates");
                        return;
                    }

                    JsonArray jsonResponse = JsonParser.parseString(response.body()).getAsJsonArray();

                    if (jsonResponse.isEmpty()) {
                        getLogger().warning("Failed to check for updates (no supported versions found)");
                        return;
                    }

                    JsonObject latestVersion = jsonResponse.get(0).getAsJsonObject();
                    String latestPluginVersion = latestVersion.get("version_number").getAsString();

                    if (!currentPluginVersion.equals(latestPluginVersion)) {
                        getLogger().info("A new version is available: " + latestPluginVersion);
                        getLogger().info("Download it at: https://modrinth.com/plugin/justplayer-tpa/versions?l=" + serverSoftware + "&g=" + minecraftVersion);
                    } else {
                        getLogger().info("You are using the latest version for your server.");
                    }

                } catch (Exception e) {
                    getLogger().warning("Failed to check for updates");
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("JustTPA disabled");

        teleportRequestManager.stop();
    }

    /**
     * Returns the translation for the given key
     * Will return the key if there is no translation in the config
     * @param key
     * @return
     */
    public String translate(String key) {
        return this.config.getString(key, key);
    }

    /**
     * Returns the translation for the given key and replaces all %key% with value inside the string
     */
    public String translate(String key, Map<String, String> placeholders) {
        String output = this.translate(key);

        if(output.equals(key)) {
            // We got a missing key, no need to replace placeholders.
            return output;
        }

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            output = output.replaceAll( "%" + entry.getKey() + "%", entry.getValue());
        }

        return output;
    }

    public void log(String message, String prefix) {
        if(prefix != null) {
            if(Objects.equals(prefix, "Debug") && !config.getBoolean("tpa.verbose")) {
                return;
            }

            getLogger().info("[" + prefix + "] " + message);
        } else {
            getLogger().info(message);
        }
    }

    public void log(String message) {
        this.log(message, "Debug");
    }

}
