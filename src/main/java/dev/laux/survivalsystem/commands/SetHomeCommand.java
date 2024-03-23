package dev.laux.survivalsystem.commands;

import dev.laux.survivalsystem.SurvivalSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand implements CommandExecutor {

    private final SurvivalSystem plugin;
    public SetHomeCommand(SurvivalSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl verwenden.");
            return true;
        }

        Player player = (Player) sender;

        // Setzt den Spawn auf die aktuelle Position des Spielers
        plugin.getLocationManager().saveLocation(player.getUniqueId() + ".home", player.getLocation());
        player.sendMessage("§7Dein §aHome §7wurde gesetzt!");

        return true;
    }
}
