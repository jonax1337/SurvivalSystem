package dev.laux.survivalsystem.listener;

import dev.laux.survivalsystem.SurvivalSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    private final SurvivalSystem plugin;

    public DeathListener(SurvivalSystem plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        // Get Player
        Player player = event.getPlayer();
        // Setzt den Last Death auf die aktuelle Position des Spielers
        plugin.getLocationManager().saveLocation(player.getUniqueId() + ".lastDeath", player.getLocation());
        player.sendMessage("§aDie Position deines letzten Todes wurde gespeichert!\n§7Verwende §e/lastdeath §7um dich zu teleportieren.");
    }
}
