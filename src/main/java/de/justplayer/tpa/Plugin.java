package de.justplayer.tpa;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.justplayer.tpa.commands.tpaCommandHandler;
import de.justplayer.tpa.commands.tpacceptCommandHandler;
import de.justplayer.tpa.commands.tpahereCommandHandler;
import de.justplayer.tpa.listeners.PlayerLeaveListener;
import de.justplayer.tpa.utils.CooldownManager;
import de.justplayer.tpa.utils.TeleportRequestManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class Plugin extends JavaPlugin {
    public FileConfiguration config;
    public CooldownManager cooldownManager;
    public TeleportRequestManager teleportRequestManager;

    public Plugin() {
        config = new Config(this).getConfig();
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

        // Start the teleport request manager scheduler
        teleportRequestManager.start();

        getLogger().info("JustTPA initialized");

        if (config.getBoolean("bStats.enabled") && !getDescription().getVersion().contains("dev")) {
            new Metrics(this, 18743);
            getLogger().info("bStats enabled");
        }

        /**
         * Check for updates using modrinth
         */
        if (config.getBoolean("check-for-updates") && !getDescription().getVersion().contains("dev")) {
            String minecraftVersion = getServer().getBukkitVersion().split("-")[0];
            String serverSoftware = getServer().getVersion().split("-")[1].split(" ")[0].toLowerCase();
            String currentPluginVersion = getDescription().getVersion();

            getLogger().info("Checking for updates...");

            getServer().getScheduler().runTaskAsynchronously(this, () -> {
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(java.net.URI.create("https://api.modrinth.com/v2/project/justplayer-tpa/version?game_versions=[%22" + minecraftVersion + "%22]&loaders=[%22" + serverSoftware + "%22]"))
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
                        getLogger().info("Download it at: https://modrinth.com/plugin/justplayer-tpa");
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
}
