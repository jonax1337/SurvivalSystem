package dev.laux.survivalsystem.commands;

import dev.laux.coins.Coins;
import dev.laux.survivalsystem.SurvivalSystem;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import java.sql.SQLException;

public class HomeCommand implements CommandExecutor {

    private final SurvivalSystem plugin;

    public HomeCommand(SurvivalSystem plugin) {
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
            if (Coins.getInstance().getCoinAPI().getCoins(player.getUniqueId()) < 100) {
                player.sendMessage("§cDu besitz leider nicht genügend Coins!");
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Location home = plugin.getLocationManager().getLocation(player.getUniqueId() + ".home");
        if (home != null) {
            player.teleport(home);
            // Teleportiere Haustiere mit
            for (Entity entity : player.getWorld().getEntities()) {
                if (entity instanceof Wolf) {
                    Wolf wolf = (Wolf) entity;
                    if (wolf.isTamed() && wolf.getOwner().equals(player) && !wolf.isDead()) {
                        wolf.teleport(home);
                    }
                }
            }
            // Teleport Leashed Animal
            teleportLeashedEntities(player, home);
            // remove 100 Coins
            try {
                Coins.getInstance().getCoinAPI().removeCoins(player.getUniqueId(), 50);
                player.sendMessage("§aErfolgreich teleportiert!\n§8[§c-§8] §c50 Coins");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            player.sendMessage("§cHome wurde noch nicht gesetzt!");
        }
        return true;
    }

    private void teleportLeashedEntities(Player player, Location location) {
        for (Entity entity : player.getNearbyEntities(30, 30, 30)) { // Suche in einem 10x10x10 Block großen Bereich um den Spieler
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (livingEntity.isLeashed() && livingEntity.getLeashHolder() instanceof Player && livingEntity.getLeashHolder().equals(player)) {
                    livingEntity.teleport(location);
                }
            }
        }
    }

}
