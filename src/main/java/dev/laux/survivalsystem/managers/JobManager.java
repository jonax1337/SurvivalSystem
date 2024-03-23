package dev.laux.survivalsystem.managers;

import dev.laux.coins.Coins;
import dev.laux.survivalsystem.SurvivalSystem;
import dev.laux.survivalsystem.util.HologramUtil;
import dev.laux.survivalsystem.enums.JobType;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class JobManager {
    private final SurvivalSystem plugin;

    public JobManager(SurvivalSystem plugin) {
        this.plugin = plugin;
    }

    public boolean assignJob(Player player, JobType jobType) {
        String path = "players." + player.getUniqueId().toString() + ".jobs";
        FileConfiguration config = plugin.getConfig();

        long lastChanged = config.getLong(path + ".lastChanged", 0);
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastChanged < 24 * 60 * 60 * 1000) { // 24 Stunden
            player.sendMessage("§cDu kannst deinen Job nur alle 24 Stunden wechseln.");
            return false;
        }

        config.set(path + ".job", jobType.name());
        config.set(path + ".lastChanged", currentTime);
        plugin.saveConfig();

        player.sendMessage("§7Dein Job wurde zu §e" + jobType.name() + " §7geändert.");
        return true;
    }

    public JobType getJob(Player player) {
        String path = "players." + player.getUniqueId().toString() + ".jobs.job";
        String jobName = plugin.getConfig().getString(path);
        if (jobName == null) return null;
        return JobType.valueOf(jobName.toUpperCase());
    }

    public void handleAction(Player player, JobType jobType, Location hologramLocation, Integer coins) {
        HologramUtil.createHologram(hologramLocation, "§a+" + coins + " Coins", plugin);
        addCoinsToPlayer(player, coins);
    }

    private int calculateCoins(JobType jobType) {
        // Hier könntest du eine unterschiedliche Anzahl von Coins basierend auf dem Jobtyp zurückgeben
        return switch (jobType) {
            case MINER -> 5;
            case LUMBERJACK -> 4;
            case FARMER -> 3;
            case HUNTER -> 6;
            default -> 0;
        };
    }

    private void addCoinsToPlayer(Player player, int coins) {
        // Hier würdest du die externen API aufrufen, um die Coins dem Spielerkonto hinzuzufügen
        try {
            Coins.getInstance().getCoinAPI().addCoins(player.getUniqueId(), coins);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
