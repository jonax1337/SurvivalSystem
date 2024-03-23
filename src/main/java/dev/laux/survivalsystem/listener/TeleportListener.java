package dev.laux.survivalsystem.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportListener implements Listener {

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        // Durchlaufe alle nahegelegenen Entitäten
        for (Entity entity : player.getNearbyEntities(30, 30, 30)) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;

                // Prüfe, ob die Entität angeleint ist und der Spieler der Halter der Leine ist
                if (livingEntity.isLeashed() && livingEntity.getLeashHolder() instanceof Player && livingEntity.getLeashHolder().equals(player)) {
                    // Teleportiere die angeleinte Entität zum Spieler
                    livingEntity.teleport(event.getTo());
                }
            }
        }
    }
}

