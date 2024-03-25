package dev.laux.survivalsystem.managers;

import dev.laux.survivalsystem.SurvivalSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EnderBackpackManager implements Listener {

    private SurvivalSystem plugin;
    private File backpacksFile;
    private YamlConfiguration backpacksConfig;
    private HashMap<UUID, Inventory> backpacks = new HashMap<>();

    public EnderBackpackManager(SurvivalSystem plugin) {
        this.plugin = plugin;

        // Erstelle die Backpacks.yml-Datei
        backpacksFile = new File(plugin.getDataFolder(), "enderbackpacks.yml");
        if (!backpacksFile.exists()) {
            try {
                backpacksFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Lade die Backpacks.yml-Datei
        backpacksConfig = YamlConfiguration.loadConfiguration(backpacksFile);

        // Füge das Crafting-Rezept für den Rucksack hinzu
        addBackpackRecipe();
    }

    private void addBackpackRecipe() {
        ItemStack backpack = createBackpack("UselessKitten");
        NamespacedKey key = new NamespacedKey(plugin, "enderbackpack");
        ShapedRecipe recipe = new ShapedRecipe(key, backpack);
        recipe.shape("LLL", "LCL", "LLL");
        recipe.setIngredient('L', Material.OBSIDIAN);
        recipe.setIngredient('C', Material.ENDER_CHEST);
        Bukkit.addRecipe(recipe);
    }

    private ItemStack createBackpack(String playerName) {
        // Erstelle ein neues ItemStack, das ein Spielerkopf ist
        ItemStack backpack = new ItemStack(Material.PLAYER_HEAD);

        // Erhalte die Meta-Daten des ItemStacks
        SkullMeta backpackMeta = (SkullMeta) backpack.getItemMeta();

        // Setze den Besitzer des Kopfes auf den angegebenen Spieler
        backpackMeta.setOwner(playerName);

        // Setze den Besitzer des Kopfes auf den angegebenen Spieler
        backpackMeta.setDisplayName("§rEnder Backpack");

        // Setze die Meta-Daten des Rucksacks
        backpack.setItemMeta(backpackMeta);

        return backpack;
    }

    public void loadBackpackContents(UUID backpackUUID, Inventory inventory) {
        List<?> contentsList = backpacksConfig.getList(backpackUUID.toString());

        if (contentsList == null) {
            return; // Die Liste ist null, daher gibt es nichts zu laden
        }

        ItemStack[] contents = contentsList.toArray(new ItemStack[0]);
        inventory.setContents(contents);
    }

    public void saveBackpackContents(UUID backpackUUID, Inventory inventory) {
        // Konvertieren Sie den Inhalt des Inventars in eine Liste
        List<ItemStack> contentsList = Arrays.asList(inventory.getContents());

        backpacksConfig.set(backpackUUID.toString(), contentsList);
        try {
            backpacksConfig.save(backpacksFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getBackpacksConfig() {
        return backpacksConfig;
    }

    public File getBackpacksFile() {
        return backpacksFile;
    }

    public HashMap<UUID, Inventory> getBackpacks() {
        return backpacks;
    }
}
