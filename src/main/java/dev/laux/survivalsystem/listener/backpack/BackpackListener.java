package dev.laux.survivalsystem.listener.backpack;// BackpackListener.java
import dev.laux.survivalsystem.managers.backpack.BackpackManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BackpackListener implements Listener {

    private final BackpackManager backpackManager;

    public BackpackListener(BackpackManager backpackManager) {
        this.backpackManager = backpackManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        // Überprüfe, ob der Spieler mit der rechten Maustaste in die Luft oder auf einen Block geklickt hat
        if (item != null && item.getType() == Material.PLAYER_HEAD && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            Block block = event.getClickedBlock();
            if (block != null && interactiveBlocks.contains(block.getType())) {
                return;
            }
            // Erhalte die Meta-Daten
            ItemMeta meta = item.getItemMeta();
            if (meta.hasLore() && meta.getLore().get(1).contains("Public")) {
                if (meta.hasLore() && meta.getLore().get(0).contains("UUID")) {
                    // Der Spieler hat mit der rechten Maustaste auf einen Rucksack geklickt, öffne das Rucksackinventar
                    UUID backpackUUID = UUID.fromString(meta.getLore().get(0).substring(8));
                    // Erstelle ein neues Inventar für den Rucksack
                    Inventory backpackInventory = Bukkit.createInventory(null, 27, "Backpack");
                    // Lade den Inhalt des Rucksacks aus der Backpacks.yml-Datei in das Inventar
                    backpackManager.loadBackpackContents(backpackUUID, backpackInventory);
                    // In die HashMap laden
                    backpackManager.getBackpacks().put(backpackUUID, backpackInventory);
                    // Öffne das Rucksackinventar für den Spieler
                    player.openInventory(backpackInventory);
                    // Play Sound
                    player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.5f, 1.5f);
                    // cancel event
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().getType() == Material.PLAYER_HEAD && event.getItemInHand().getItemMeta().getLore().get(1).contains("Public")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getPlayer();
        // Überprüfe, ob das geschlossene Inventar ein Rucksackinventar ist
        ItemStack backpack = player.getInventory().getItemInMainHand();
        if (backpack.getType() == Material.PLAYER_HEAD) {
            ItemMeta meta = backpack.getItemMeta();
            if (meta.hasLore() && meta.getLore().get(0).contains("UUID")) {
                UUID backpackUUID = UUID.fromString(meta.getLore().get(0).substring(8));
                if (player.getOpenInventory().getTitle().equals("Backpack")) {
                    // Speichere den Inhalt des Rucksacks in der Backpacks.yml-Datei
                    backpackManager.saveBackpackContents(backpackUUID, inventory);
                    // In die HashMap laden
                    backpackManager.getBackpacks().put(backpackUUID, inventory);
                    // Play Sound
                    player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 0.5f, 1.5f);
                }
            }
        }
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe != null) {
            // Überprüfe, ob der Spieler einen Rucksack craftet
            if (event.getRecipe().getResult().getType() == Material.PLAYER_HEAD) {
                // Setze die UUID des Rucksacks, sobald das Item im Inventar landet
                ItemStack backpack = event.getInventory().getResult();
                if (backpack.getItemMeta().getDisplayName().startsWith("Backpack")) {
                    ItemMeta meta = backpack.getItemMeta();
                    UUID backpackUUID = UUID.randomUUID();
                    meta.setLore(Arrays.asList("§r§7UUID: " + backpackUUID.toString(), "§r§7Public Backpack"));
                    backpack.setItemMeta(meta);
                    event.getInventory().setResult(backpack);
                }
            }
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe != null) {
            ItemStack result = recipe.getResult();
            // Überprüfe, ob der Spieler einen Rucksack craftet
            if (result.getType() == Material.PLAYER_HEAD) {
                // Verhindere, dass mehr als 1 Item auf einmal gecraftet wird
                if (event.getInventory().getResult().getAmount() > 1) {
                    event.setCancelled(true);
                    ((Player) event.getWhoClicked()).sendMessage("Du kannst nicht mehr als 1 Rucksack auf einmal craften!");
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        // Überprüfen, ob das geklickte Item ein Rucksack ist
        if (player.getOpenInventory().getTitle().equals("Backpack") && clickedItem != null && clickedItem.getType() == Material.PLAYER_HEAD && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasLore() && clickedItem.getItemMeta().getLore().get(0).contains("UUID")) { // Ersetzen Sie dies durch Ihre Methode zum Überprüfen, ob ein Item ein Rucksack ist
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().getName().equals("SurvivalSystem")) {
            for (Map.Entry<UUID, Inventory> entry : backpackManager.getBackpacks().entrySet()) {
                UUID backpackUUID = entry.getKey();
                Inventory inventory = entry.getValue();
                backpackManager.saveBackpackContents(backpackUUID, inventory);
            }
        }
    }

    List<Material> interactiveBlocks = Arrays.asList(
            Material.ANVIL, Material.DAMAGED_ANVIL, Material.CHIPPED_ANVIL,
            Material.BLACK_BED, Material.BLUE_BED, Material.BROWN_BED, Material.CYAN_BED, Material.GRAY_BED, Material.GREEN_BED, Material.LIGHT_BLUE_BED, Material.LIGHT_GRAY_BED, Material.LIME_BED, Material.MAGENTA_BED, Material.ORANGE_BED, Material.PINK_BED, Material.PURPLE_BED, Material.RED_BED, Material.WHITE_BED, Material.YELLOW_BED,
            Material.BELL,
            Material.BLAST_FURNACE,
            Material.BREWING_STAND,
            Material.ACACIA_BUTTON, Material.BIRCH_BUTTON, Material.DARK_OAK_BUTTON, Material.JUNGLE_BUTTON, Material.OAK_BUTTON, Material.SPRUCE_BUTTON, Material.STONE_BUTTON, Material.POLISHED_BLACKSTONE_BUTTON,
            Material.CARTOGRAPHY_TABLE,
            Material.CAULDRON,
            Material.CHEST,
            Material.COMMAND_BLOCK,
            Material.COMPOSTER,
            Material.CRAFTING_TABLE,
            Material.ACACIA_DOOR, Material.BIRCH_DOOR, Material.DARK_OAK_DOOR, Material.IRON_DOOR, Material.JUNGLE_DOOR, Material.OAK_DOOR, Material.SPRUCE_DOOR,
            Material.ENCHANTING_TABLE,
            Material.END_PORTAL_FRAME,
            Material.ENDER_CHEST,
            Material.ACACIA_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.OAK_FENCE_GATE, Material.SPRUCE_FENCE_GATE,
            Material.FURNACE,
            Material.GRINDSTONE,
            Material.ITEM_FRAME,
            Material.JUKEBOX,
            Material.LECTERN,
            Material.LEVER,
            Material.LODESTONE,
            Material.LOOM,
            Material.NOTE_BLOCK,
            Material.PISTON,
            Material.PUMPKIN,
            Material.REDSTONE_ORE,
            Material.RESPAWN_ANCHOR,
            Material.SMITHING_TABLE,
            Material.SMOKER,
            Material.STONECUTTER,
            Material.ACACIA_TRAPDOOR, Material.BIRCH_TRAPDOOR, Material.DARK_OAK_TRAPDOOR, Material.IRON_TRAPDOOR, Material.JUNGLE_TRAPDOOR, Material.OAK_TRAPDOOR, Material.SPRUCE_TRAPDOOR,
            Material.TRAPPED_CHEST,
            Material.SHULKER_BOX
    );


}
