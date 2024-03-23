package dev.laux.survivalsystem.util;

import dev.laux.survivalsystem.SurvivalSystem;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

public class HologramUtil {

    public static void createHologram(Location location, String text, SurvivalSystem plugin) {
        ArmorStand hologram = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        hologram.setVisible(false); // Unsichtbar machen
        hologram.setGravity(false); // Keine Schwerkraft
        hologram.setCustomName(text); // Text setzen
        hologram.setCustomNameVisible(true); // Name immer sichtbar
        hologram.setMarker(true); // Keine Kollision

        // Entferne den Rüstungsständer nach 1 Sekunde
        new BukkitRunnable() {
            @Override
            public void run() {
                hologram.remove();
            }
        }.runTaskLater(plugin, 20); // 20 Ticks = 1 Sekunde
    }

}