package dev.laux.survivalsystem.listener;// BackpackListener.java
import dev.laux.survivalsystem.managers.BackpackManager;
import dev.laux.survivalsystem.managers.EnderBackpackManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
import java.util.Map;
import java.util.UUID;

public class EnderBackpackListener implements Listener {

    private final EnderBackpackManager backpackManager;

    public EnderBackpackListener(EnderBackpackManager backpackManager) {
        this.backpackManager = backpackManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Überprüfe, ob der Spieler mit der rechten Maustaste in die Luft oder auf einen Block geklickt hat
        if (item != null && item.getType() == Material.PLAYER_HEAD && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasLore() && meta.getLore().get(0).contains(player.getUniqueId().toString())) {
                // Der Spieler hat mit der rechten Maustaste auf einen Rucksack geklickt, öffne das Rucksackinventar
                UUID backpackUUID = UUID.fromString(meta.getLore().get(0).substring(8));

                // Erstelle ein neues Inventar für den Rucksack
                Inventory backpackInventory = Bukkit.createInventory(null, 27, "Ender Backpack");

                // Lade den Inhalt des Rucksacks aus der Backpacks.yml-Datei in das Inventar
                backpackManager.loadBackpackContents(backpackUUID, backpackInventory);

                // In die HashMap laden
                backpackManager.getBackpacks().put(backpackUUID, backpackInventory);

                // Öffne das Rucksackinventar für den Spieler
                player.openInventory(backpackInventory);

                // Play Sound
                player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 0.5f, 1.5f);

                // cancel event
                event.setCancelled(true);
            }
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
                if (player.getOpenInventory().getTitle().equals("Ender Backpack")) {
                    // Speichere den Inhalt des Rucksacks in der Backpacks.yml-Datei
                    backpackManager.saveBackpackContents(backpackUUID, inventory);
                    // In die HashMap laden
                    backpackManager.getBackpacks().put(backpackUUID, inventory);
                    // Play Sound
                    player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 0.5f, 1.5f);
                }
            }
        }
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        Player player = (Player) event.getViewers().get(0);
        if (recipe != null) {
            // Überprüfe, ob der Spieler einen Rucksack craftet
            if (event.getRecipe().getResult().getType() == Material.PLAYER_HEAD) {
                // Setze die UUID des Rucksacks, sobald das Item im Inventar landet
                ItemStack backpack = event.getInventory().getResult();
                ItemMeta meta = backpack.getItemMeta();
                meta.setLore(Arrays.asList("§r§7UUID: " + player.getUniqueId().toString(), "§r§7Owner: " + player.getName()));
                meta.setDisplayName("§rEnder Backpack");
                backpack.setItemMeta(meta);
                event.getInventory().setResult(backpack);
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
        if (player.getOpenInventory().getTitle().equals("Ender Backpack") && clickedItem != null && clickedItem.getType() == Material.PLAYER_HEAD && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasLore() && clickedItem.getItemMeta().getLore().get(0).contains("UUID")) { // Ersetzen Sie dies durch Ihre Methode zum Überprüfen, ob ein Item ein Rucksack ist
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

}
