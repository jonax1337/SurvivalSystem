package dev.laux.survivalsystem.util;

import dev.laux.survivalsystem.SurvivalSystem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Bee;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TimberHelper {

    private static final long DELAY_TICKS = 2L;

    public static void fellTree(Block startBlock, Player player) {

        String path = "players." + player.getUniqueId().toString() + ".timber-enabled";
        if (!SurvivalSystem.getInstance().getConfig().getBoolean(path, true)) {
            // Wenn Timber für diesen Spieler deaktiviert ist, frühzeitig beenden
            return;
        }

        Set<Block> checkedBlocks = new HashSet<>();
        if (!hasLeavesNearby(startBlock, checkedBlocks)) {
            return; // Keine Blätter in unmittelbarer Nähe zu einem Stamm, also kein Baum
        }

        Set<Block> blocksToBreak = new HashSet<>();
        Set<Block> beeStructures = new HashSet<>(); // Sammle Bienenstöcke und Bienennester separat
        collectBlocks(startBlock, blocksToBreak, beeStructures, startBlock);

        // Sort blocks from lowest to highest Y value
        List<Block> sortedBlocks = new ArrayList<>(blocksToBreak);
        sortedBlocks.sort(Comparator.comparingInt(block -> block.getLocation().getBlockY()));

        new BukkitRunnable() {
            private final Iterator<Block> blocksIterator = sortedBlocks.iterator();
            private boolean beeStructuresHandled = false;



            @Override
            public void run() {
                if (blocksIterator.hasNext()) {
                    Block block = blocksIterator.next();
                    block.breakNaturally();
                    damageAxe(player);
                    block.getWorld().playSound(block.getLocation(), Sound.BLOCK_WOOD_BREAK, 1.0F, 1.0F);
                    // Verwende den BLOCK_CRACK Partikel mit dem BlockData des aktuellen Blocks
                    BlockData blockData = block.getBlockData();
                    block.getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation().add(0.5, 0.5, 0.5), 30, 0.5, 0.5, 0.5, blockData);
                } else if (!beeStructuresHandled) {
                    beeStructures.forEach(beeBlock -> {
                        beeBlock.breakNaturally();
                        beeBlock.getWorld().playSound(beeBlock.getLocation(), Sound.ENTITY_BEE_LOOP_AGGRESSIVE, 1.0F, 1.0F);

                        // Bestimme, wie viele Bienen spawnen sollen
                        Random random = new Random();
                        int beesToSpawn = random.nextInt(3) + 1; // Generiert eine Zahl zwischen 1 und 3

                        Location spawnLocation = beeBlock.getLocation().add(0.5, 0.5, 0.5); // Zentriere die Spawn-Position

                        for (int i = 0; i < beesToSpawn; i++) {
                            Bee bee = (Bee) beeBlock.getWorld().spawnEntity(spawnLocation, EntityType.BEE);
                            // Mache die Bienen aggressiv gegenüber dem Spieler
                            bee.setAnger(Integer.MAX_VALUE); // Die Biene ist dauerhaft sauer auf den Spieler
                            bee.setTarget(player); // Setzt den Spieler als Ziel der Aggression
                        }
                    });
                    beeStructuresHandled = true; // Stelle sicher, dass Bienenstrukturen nur einmal abgebaut werden
                } else {
                    this.cancel(); // Beende den Task, wenn alles abgebaut ist
                }
            }
        }.runTaskTimer(SurvivalSystem.getInstance(), 0L, DELAY_TICKS);
    }

    private static void collectBlocks(Block block, Set<Block> blocks, Set<Block> beeStructures, Block startBlock) {
        if (blocks.contains(block) || beeStructures.contains(block)) {
            return;
        }

        if (block.getType() == Material.BEE_NEST || block.getType() == Material.BEEHIVE) {
            beeStructures.add(block);
        } else if (isTreeLog(block.getType())) {
            blocks.add(block);
            // Sucht nach weiteren Baumstämmen in alle Richtungen
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        collectBlocks(block.getRelative(x, y, z), blocks, beeStructures, startBlock);
                    }
                }
            }
        }
    }

    private static boolean hasLeavesNearby(Block block, Set<Block> checkedBlocks) {
        // Überprüft nur direkt oben, unten und die vier Hauptseiten des Blocks
        Block[] directlyAdjacent = new Block[]{
                block.getRelative(0, 1, 0), // oben
                block.getRelative(0, -1, 0), // unten
                block.getRelative(1, 0, 0), // Osten
                block.getRelative(-1, 0, 0), // Westen
                block.getRelative(0, 0, 1), // Süden
                block.getRelative(0, 0, -1) // Norden
        };

        for (Block adjBlock : directlyAdjacent) {
            if (isLeaf(adjBlock.getType())) {
                return true; // Direkt angrenzende Blätter gefunden
            }
        }

        // Führt eine rekursive Suche durch, um direkt angrenzende Stämme und deren Blätter zu finden
        if (!checkedBlocks.contains(block)) {
            checkedBlocks.add(block);
            for (Block adjBlock : directlyAdjacent) {
                if (isTreeLog(adjBlock.getType()) && hasLeavesNearby(adjBlock, checkedBlocks)) {
                    return true;
                }
            }
        }

        return false;
    }


    private static boolean isTreeLog(Material material) {
        return switch (material) {
            case OAK_LOG, SPRUCE_LOG, BIRCH_LOG, JUNGLE_LOG, ACACIA_LOG, DARK_OAK_LOG, MANGROVE_LOG, CHERRY_LOG -> true;
            default -> false;
        };
    }

    private static boolean isLeaf(Material material) {
        return switch (material) {
            case OAK_LEAVES, SPRUCE_LEAVES, BIRCH_LEAVES, JUNGLE_LEAVES, ACACIA_LEAVES, DARK_OAK_LEAVES, MANGROVE_LEAVES, CHERRY_LEAVES -> true;
            default -> false;
        };
    }

    private static void damageAxe(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getItemMeta() instanceof Damageable) {
            Damageable damageable = (Damageable) item.getItemMeta();
            damageable.setDamage(damageable.getDamage() + 1);
            item.setItemMeta(damageable);
        }
    }
}


