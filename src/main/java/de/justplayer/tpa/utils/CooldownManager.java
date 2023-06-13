package de.justplayer.tpa.utils;

import de.justplayer.tpa.Plugin;

import java.util.HashMap;
import java.util.UUID;

public class CooldownManager {

        private final HashMap<UUID, HashMap<String, Integer>> coolDowns = new HashMap<>();

        public void addCooldown(UUID uuid, String cooldownName, long cooldown) {
            if (!coolDowns.containsKey(uuid)) {
                coolDowns.put(uuid, new HashMap<>());
            }
            coolDowns.get(uuid).put(cooldownName, (int) (System.currentTimeMillis() / 1000 + cooldown));
        }

        public boolean isOnCooldown(UUID uuid, String cooldownName) {
            if (!coolDowns.containsKey(uuid)) {
                return false;
            }
            if (!coolDowns.get(uuid).containsKey(cooldownName)) {
                return false;
            }

            return coolDowns.get(uuid).get(cooldownName) > System.currentTimeMillis() / 1000;
        }

        public int getCooldown(UUID uuid, String cooldownName) {
            if (!coolDowns.containsKey(uuid)) {
                return 0;
            }
            if (!coolDowns.get(uuid).containsKey(cooldownName)) {
                return 0;
            }

            return coolDowns.get(uuid).get(cooldownName) - (int) (System.currentTimeMillis() / 1000);
        }

        public void removeCooldown(UUID uuid, String cooldownName) {
            if (!coolDowns.containsKey(uuid)) {
                return;
            }
            if (!coolDowns.get(uuid).containsKey(cooldownName)) {
                return;
            }

            coolDowns.get(uuid).remove(cooldownName);
        }
}
