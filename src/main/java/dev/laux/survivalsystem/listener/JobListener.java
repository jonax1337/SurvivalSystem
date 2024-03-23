package dev.laux.survivalsystem.listener;

import dev.laux.survivalsystem.SurvivalSystem;
import dev.laux.survivalsystem.managers.JobManager;
import dev.laux.survivalsystem.enums.JobType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

public class JobListener implements Listener {
    private final JobManager jobManager;
    private final SurvivalSystem plugin;

    public JobListener(SurvivalSystem plugin) {
        this.plugin = plugin;
        this.jobManager = plugin.getJobManager();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        JobType playerJob = jobManager.getJob(player);
        Location hologramLocation = block.getLocation().add(0.5, 0.25, 0.5);

        // Bergbau: Belohnung für das Abbauen von Erzen
        if (playerJob == JobType.MINER && block.getType().name().endsWith("_ORE")) {
            jobManager.handleAction(player, JobType.MINER, hologramLocation, 4);
        }
        // Holzfäller: Belohnung für das Fällen von Bäumen
        if (playerJob == JobType.LUMBERJACK && block.getType().name().endsWith("_LOG")) {
            jobManager.handleAction(player, JobType.LUMBERJACK, hologramLocation, 2);
        }
        if (playerJob == JobType.FARMER && ((block.getType() == Material.WHEAT && block.getData() == 7) || (block.getType() == Material.CARROT && block.getData() == 7)) ) { // Reifes Weizenfeld
            jobManager.handleAction(player, JobType.FARMER, hologramLocation,3); // 3 Coins für Ernten
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player) {
            Player player = event.getEntity().getKiller();
            JobType playerJob = jobManager.getJob(player);

            // Jäger: Belohnung für das Töten von Monstern
            if (playerJob == JobType.HUNTER && event.getEntity() instanceof Monster) {
                jobManager.handleAction(player, JobType.HUNTER, event.getEntity().getEyeLocation(), 5);
            }
            // Metzger: Belohnung für das Töten von Tieren
            else if (playerJob == JobType.BUTCHER && event.getEntity() instanceof Animals) {
                jobManager.handleAction(player, JobType.BUTCHER, event.getEntity().getEyeLocation(), 3);
            }
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH && event.getCaught() instanceof Fish) {
            Player player = event.getPlayer();
            JobType playerJob = jobManager.getJob(player);

            if (playerJob == JobType.FISHER) {
                jobManager.handleAction(player, JobType.FISHER, event.getCaught().getLocation(), 3);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemInHand();
        JobType playerJob = jobManager.getJob(player);

        // Landwirtschaft: Belohnung für das Pflanzen von Saatgut
        if (playerJob == JobType.FARMER && (item.getType() == Material.WHEAT_SEEDS || item.getType() == Material.CARROT || item.getType() == Material.POTATO || item.getType() == Material.BEETROOT_SEEDS)) {
            jobManager.handleAction(player, JobType.FARMER, event.getBlock().getLocation().add(0.5, 0.5, 0.5), 1);
        }
    }
}


