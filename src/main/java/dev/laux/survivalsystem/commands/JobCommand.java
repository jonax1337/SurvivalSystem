package dev.laux.survivalsystem.commands;

import dev.laux.survivalsystem.SurvivalSystem;
import dev.laux.survivalsystem.managers.JobManager;
import dev.laux.survivalsystem.enums.JobType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JobCommand implements CommandExecutor {

    private final SurvivalSystem plugin;
    private final JobManager jobManager;

    public JobCommand(SurvivalSystem plugin) {
        this.plugin = plugin;
        jobManager = plugin.getJobManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl nutzen.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length != 1) {
            player.sendMessage("§cBitte gib einen Job an.§7 \nVerfügbare Jobs: §eMINER, LUMBERJACK, FARMER, HUNTER, BUTCHER, FISHER");
            return true;
        }

        try {
            JobType jobType = JobType.valueOf(args[0].toUpperCase());
            jobManager.assignJob(player, jobType);
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cUngültiger Job.\n§7Verfügbare Jobs: §eMINER, LUMBERJACK, FARMER, HUNTER, BUTCHER, FISHER");
        }

        return true;
    }
}
