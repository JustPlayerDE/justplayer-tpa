# About JustPlayer TPA

JustPlayer TPA (short JustTPA) is a simple tpa plugin where players can ask other players if
they can teleport to them. This plugin is very simple and easy to use.

## Commands

- `/tpa <player>` - Sends a teleport request to the player. (`justplayer.tpa.request`)
- `/tpaccept` - Accepts a teleport request. (`justplayer.tpa.accept`)
- `/tpdeny` - Denies a teleport request. (`justplayer.tpa.deny`)
- `/tpacancel` - Cancels a teleport request. (`justplayer.tpa.cancel`)
- `/tpahere <player>` - Sends a teleport request to the player to teleport to you. (`justplayer.tpa.here`)
- `/tpaall` - Sends a teleport request to all online players. (`justplayer.tpa.all`)


## bStats

This plugin uses bStats to collect anonymous statistics, which helps determining how many servers are using the plugin,
which minecraft versions it is used on the most, and to which degree the different features are used.

You can disable this by going to the `config.yml` file in the `bStats` folder of the plugin folder and changing `enabled` to `false`.
