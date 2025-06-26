# About JustPlayer TPA

JustPlayer TPA (short JustTPA) is a simple tpa plugin where players can ask other players if
they can teleport to them. This plugin is very simple and easy to use.

I mainly made this plugin for myself, but I also wanted to share it with others.

The reason for why I even made this plugin is because no existing tpa plugin has satisfied my needs.

## Commands

- `/tpa <player>` - Sends a teleport request to the player. (`justplayer.tpa.request`)
- `/tpaccept [player]` - Accepts a teleport request, optionally accepts a player name to only accept requests from a specific player. (`justplayer.tpa.accept`)
- `/tpadeny` - Denies a teleport request. (`justplayer.tpa.deny`)
- `/tpacancel` - Cancels current teleport request. (`justplayer.tpa.cancel`)
- `/tpahere <player>` - Sends a teleport request to the player to teleport to you. (`justplayer.tpa.here`)
- `/tpareload` - Reloads the plugin configuration (`justplayer.tpa.reload`, OP by default)

## Other Permissions

- `justplayer.tpa.wait.bypass` - Allows a player to bypass the teleport wait time. OP by Default.
- `justplayer.tpa.return-timeout.bypass` - Allows a player to bypass the return timeout (won't time out anymore). OP by Default.

## Supported versions

Because of time restrictions im only able to test on the latest stable version of both spigot and paper.

I'll be looking into Folia support when I can.

In most cases, an update to JustTPA won't be necessary unless there are significant changes to the plugin system (e.g., if Paper or Spigot drops support for the 1.19 API) or if the command/teleportation mechanics are changed.
Should any issues arise, I’ll provide updates as needed.

Currently used API is Spigot 1.19

## Update Checker

This plugin checks for updates on every startup, and if there is an update available it will send a message to the console.

You can disable this in the config file if you don't want to be notified about updates.

This will send a request to the modrinth api with the server software and minecraft version the server is running on. (e.g. paper 1.20.1)

## bStats

This plugin uses bStats to collect anonymous statistics, which helps determining how many servers are using the plugin,
which minecraft versions it is used on the most, and to which degree the different features are used.

You can disable this by going to the `config.yml` file in the `bStats` folder of the plugin folder and changing `enabled` to `false`.
Alternatively, if you want to only disable bStats for this plugin you can do so in the plugin config.

You can view the statistics here: [https://bstats.org/plugin/bukkit/JustPlayer TPA/18743](https://bstats.org/plugin/bukkit/JustPlayer%20TPA/18743)