# About JustPlayer TPA

JustPlayer TPA (short JustTPA) is a simple tpa plugin where players can ask other players if
they can teleport to them. This plugin is very simple and easy to use.

I mainly made this plugin for myself, but i also wanted to share it with others.

The reason for why i even made this plugin is because no existing tpa plugin has satisfied my needs.

## Supported versions

As i don't have much time to work on this plugin, i will only officially support the latest version of Minecraft.

Thanks to how spigot/paper works it may work on older versions, but i will not guarantee that it will work.

## Commands

- `/tpa <player>` - Sends a teleport request to the player. (`justplayer.tpa.request`)
- `/tpaccept [player]` - Accepts a teleport request, optionally accepts a player name to only accept requests from a specific player. (`justplayer.tpa.accept`)
- ~~`/tpdeny` - Denies a teleport request. (`justplayer.tpa.deny`)~~
- ~~`/tpacancel` - Cancels a teleport request. (`justplayer.tpa.cancel`)~~
- `/tpahere <player>` - Sends a teleport request to the player to teleport to you. (`justplayer.tpa.here`)

> This plugin is still in development, so some commands may not work but will be added in the future.

## bStats

This plugin uses bStats to collect anonymous statistics, which helps determining how many servers are using the plugin,
which minecraft versions it is used on the most, and to which degree the different features are used.

You can disable this by going to the `config.yml` file in the `bStats` folder of the plugin folder and changing `enabled` to `false`.

You can view the statistics here: [https://bstats.org/plugin/bukkit/JustPlayer TPA/18743](https://bstats.org/plugin/bukkit/JustPlayer%20TPA/18743)