package dev.laux.survivalsystem.commands;

import dev.laux.survivalsystem.SurvivalSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleTimberCommand implements CommandExecutor {

    private final SurvivalSystem plugin;

    public ToggleTimberCommand(SurvivalSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("toggletimber")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                String path = "players." + player.getUniqueId().toString() + "timber-enabled";
                boolean isEnabled = plugin.getConfig().getBoolean(path, true); // Standardmäßig eingeschaltet
                plugin.getConfig().set(path, !isEnabled);
                plugin.saveConfig();
                player.sendMessage("§7Timber wurde " + (plugin.getConfig().getBoolean(path) ? "§aaktiviert" : "§cdeaktiviert") + "§7.");
                return true;
            } else {
                sender.sendMessage("§cDieser Befehl kann nur von einem Spieler ausgeführt werden.");
                return true;
            }
        }
        return false;
    }
}
