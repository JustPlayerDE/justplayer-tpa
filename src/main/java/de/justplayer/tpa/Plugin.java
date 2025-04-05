package de.justplayer.tpa;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.justplayer.tpa.commands.*;
import de.justplayer.tpa.listeners.*;
import de.justplayer.tpa.utils.CooldownManager;
import de.justplayer.tpa.utils.TeleportRequestManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;
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

    // Important for future updates and features
    public boolean isFolia = false;
    public boolean isPaper = false;

    public Plugin() {
        config = new Config(this);
        cooldownManager = new CooldownManager();
        teleportRequestManager = new TeleportRequestManager(this);
    }

    @Override
    public void onEnable() {
        checkPlatformStuff();

        initialiseCommands();

        initialiseEvents();
        initialiseOptionalEvents();

        teleportRequestManager.start();

        initialiseStatistics();
        checkForUpdates();

        getLogger().info("JustTPA initialized");
        if (getDescription().getVersion().contains("dev")) {
            getLogger().warning("Using Development version, plugin may be unstable. bStats and Update checks are disabled.");
        }
    }

    private void checkPlatformStuff() {
        // Folia
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            log("Running on Folia");
            isFolia = true;
        } catch (ClassNotFoundException e) {
            isFolia = false;
        }

        // Paper
        try {
            Class.forName("io.papermc.paper.util.Tick");
            log("Running on Paper");
            isPaper = true;
        } catch (ClassNotFoundException e) {
            isPaper = false;
        }
    }

    private void initialiseEvents() {
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new PlayerLeaveListener(this), this);
    }

    /**
     * Enable or Disable specific (optional) listeners based on config
     * I don't want these to run if they aren't used.
     */
    public void initialiseOptionalEvents() {
        if (config == null) {
            // We are still initialising
            return;
        }
        PluginManager pluginManager = getServer().getPluginManager();

        if (config.getInt("tpa.wait", 0) > 0) {
            log("PlayerMoveEvent Listener registered.", "Debug");
            pluginManager.registerEvents(new PlayerMoveListener(this), this);
        } else {
            log("PlayerMoveEvent Listener unregistered.", "Debug");
            PlayerMoveEvent.getHandlerList().unregister(this);
        }
    }

    private void initialiseCommands() {
        Objects.requireNonNull(getCommand("tpa")).setExecutor(new tpaCommand(this));
        Objects.requireNonNull(getCommand("tpahere")).setExecutor(new tpahereCommand(this));
        Objects.requireNonNull(getCommand("tpaccept")).setExecutor(new tpacceptCommand(this));
        Objects.requireNonNull(getCommand("tpadeny")).setExecutor(new tpadenyCommand(this));
        Objects.requireNonNull(getCommand("tpacancel")).setExecutor(new tpacancelCommand(this));
        Objects.requireNonNull(getCommand("tpareload")).setExecutor(new tpareloadCommand(this));
        Objects.requireNonNull(getCommand("tpareturn")).setExecutor(new tpareturnCommand(this));
    }

    private void initialiseStatistics() {
        if (!config.getBoolean("bStats.enabled") || getDescription().getVersion().contains("dev")) {
            return;
        }

        new Metrics(this, 18743);
        getLogger().info("bStats enabled");
    }


    private void checkForUpdates() {
        if (!config.getBoolean("check-for-updates") || getDescription().getVersion().contains("dev")) {
            return;
        }

        String minecraftVersion = getServer().getVersion().split("-")[0];
        String serverSoftware = getServer().getVersion().split("-")[1].split(" ")[0].toLowerCase();
        String currentPluginVersion = getDescription().getVersion();

        getLogger().info("Checking for updates...");

        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            HttpClient client = HttpClient.newHttpClient();

            // The Modrinth api is very handy for this, we just provide the current context, and it returns if there is something new for that.
            // With new I mean something different than we have currently, too lazy to implement actual version logic and just assume different = new ^^
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

    @Override
    public void onDisable() {
        getLogger().info("JustTPA disabled");

        teleportRequestManager.stop();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        this.initialiseOptionalEvents();
    }

    /**
     * Returns the translation for the given key
     * Will return the key if there is no translation in the config
     */
    public String translate(String key) {
        return this.config.getString(key, key);
    }

    /**
     * Returns the translation for the given key and replaces all %key% with value inside the string
     */
    public String translate(String key, Map<String, String> placeholders) {
        String output = this.translate(key);

        if (output.equals(key)) {
            // We got a missing key, no need to replace placeholders.
            return output;
        }

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            output = output.replaceAll("%" + entry.getKey() + "%", entry.getValue());
        }

        return output;
    }

    public void log(String message, String prefix) {
        if (prefix != null) {
            if (Objects.equals(prefix, "Debug") && !config.getBoolean("tpa.verbose")) {
                return;
            }

            if(prefix.equals("Warning")) {
                getLogger().warning(message);
            } else {
                getLogger().info("[" + prefix + "] " + message);
            }
        } else {
            getLogger().info(message);
        }
    }

    public void log(String message) {
        this.log(message, "Debug");
    }

}
