package dev.laux.survivalsystem.listener;

import dev.laux.survivalsystem.SurvivalSystem;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.scheduler.BukkitTask;

public class SleepListener implements Listener {
    private final SurvivalSystem plugin;
    private BukkitTask fastForwardTask;
    private int sleepingPlayers = 0;

    public SleepListener(SurvivalSystem plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            sleepingPlayers++;
            if (sleepingPlayers == 1) { // Startet den Schnellvorlauf nur, wenn der erste Spieler schlafen geht
                startNightFastForward(event.getPlayer().getWorld());
            }
        }
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        sleepingPlayers--;
        if (sleepingPlayers <= 0) { // Stoppt den Schnellvorlauf, wenn kein Spieler mehr schläft
            stopNightFastForward();
            sleepingPlayers = 0; // Stellt sicher, dass der Zähler nicht negativ wird
        }
    }

    private void startNightFastForward(World world) {
        if (fastForwardTask == null || fastForwardTask.isCancelled()) {
            fastForwardTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                long time = world.getTime();
                world.setTime(time + 75); // Erhöht die Zeit schneller als normal, um den Tag herbeizuführen
                // Überprüfe, ob die Zeit den Morgen erreicht hat, und stoppe, wenn nötig
                if (time >= 23450 || time < 12300) { // Minecraft-Tage starten offiziell bei 0 und enden bei 24000
                    // Klart das Wetter
                    world.setStorm(false);
                    world.setThundering(false);
                    world.setWeatherDuration(0);
                    stopNightFastForward();
                }
            }, 0L, 1L); // Dieser Task wird jeden Tick ausgeführt
        }
    }

    private void stopNightFastForward() {
        if (fastForwardTask != null) {
            fastForwardTask.cancel();
            fastForwardTask = null;
        }
    }

}
