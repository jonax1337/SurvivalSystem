package dev.laux.survivalsystem.commands;

import dev.laux.coins.Coins;
import dev.laux.survivalsystem.SurvivalSystem;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class LastDeathCommand implements CommandExecutor {

    private final SurvivalSystem plugin;

    public LastDeathCommand(SurvivalSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl verwenden.");
            return true;
        }

        Player player = (Player) sender;

        // Check if enough Coins
        try {
            if (Coins.getInstance().getCoinAPI().getCoins(player.getUniqueId()) < 500) {
                player.sendMessage("§cDu besitz leider nicht genügend Coins!");
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Location location = plugin.getLocationManager().getLocation(player.getUniqueId() + ".lastDeath");
        if (location != null) {
            player.teleport(location);
            // delete Location
            plugin.getLocationManager().deleteLocation(player.getUniqueId() + ".lastDeath");
            // remove 500 Coins
            try {
                Coins.getInstance().getCoinAPI().removeCoins(player.getUniqueId(), 500);
                player.sendMessage("§aErfolgreich teleportiert!\n§8[§c-§8] §c500 Coins\n§eDie Postion deines letzten Todes wurde nun gelöscht!");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            player.sendMessage("§cDie Position deines letzten Todes ist nicht bekannt!");
        }
        return true;
    }
}
