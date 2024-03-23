package dev.laux.survivalsystem.listener;

import dev.laux.survivalsystem.SurvivalSystem;
import dev.laux.survivalsystem.util.TimberHelper;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TreeFallListener implements Listener {
    private final SurvivalSystem plugin;

    public TreeFallListener(SurvivalSystem plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (isTreeLog(block.getType()) && isAxeInHand(player)) {
            TimberHelper.fellTree(block, player);
        }
    }

    private boolean isAxeInHand(Player player) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        switch (itemInHand.getType()) {
            case WOODEN_AXE:
            case STONE_AXE:
            case IRON_AXE:
            case GOLDEN_AXE:
            case DIAMOND_AXE:
            case NETHERITE_AXE:
                return true;
            default:
                return false;
        }
    }
    private boolean isTreeLog(Material material) {
        return switch (material) {
            case OAK_LOG, SPRUCE_LOG, BIRCH_LOG, JUNGLE_LOG, ACACIA_LOG, DARK_OAK_LOG,
                    MANGROVE_LOG, CHERRY_LOG -> true;
            default -> false;
        };
    }
}