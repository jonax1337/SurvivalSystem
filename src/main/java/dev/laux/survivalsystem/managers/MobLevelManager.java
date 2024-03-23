package dev.laux.survivalsystem.managers;

import dev.laux.survivalsystem.SurvivalSystem;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MobLevelManager {

    private final SurvivalSystem plugin;

    private static final Random random = new Random();

    public static final List<EntityType> HOSTILE_MOBS = Arrays.asList(
            EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER,
            EntityType.SPIDER, EntityType.ENDERMAN, EntityType.WITCH,
            EntityType.BLAZE, EntityType.GHAST, EntityType.PIGLIN,
            EntityType.ZOMBIFIED_PIGLIN, EntityType.DROWNED, EntityType.HUSK,
            EntityType.STRAY, EntityType.PHANTOM, EntityType.CAVE_SPIDER,
            EntityType.SILVERFISH, EntityType.MAGMA_CUBE, EntityType.SLIME,
            EntityType.VEX, EntityType.VINDICATOR, EntityType.EVOKER,
            EntityType.PILLAGER, EntityType.RAVAGER, EntityType.GUARDIAN,
            EntityType.ELDER_GUARDIAN, EntityType.SHULKER, EntityType.ENDERMITE,
            EntityType.WITHER_SKELETON, EntityType.WITHER, EntityType.ENDER_DRAGON,
            EntityType.HOGLIN, EntityType.ZOGLIN, EntityType.WARDEN, EntityType.WOLF
    );

    public MobLevelManager(SurvivalSystem plugin) {
        this.plugin = plugin;
    }

    public void showMobLevel() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Überprüfe Mobs in einem Radius um den Spieler
            player.getNearbyEntities(20, 20, 20).forEach(entity -> {

                if (entity instanceof LivingEntity) {

                    LivingEntity mob = (LivingEntity) entity;

                    if (HOSTILE_MOBS.contains(mob.getType()) && !mob.hasMetadata("mobLevel")) {
                        levelEntity(mob);
                    }

                    if (mob.hasMetadata("mobLevel") && !HOSTILE_MOBS.contains(mob.getType())) {
                        mob.removeMetadata("mobLevel", plugin);
                        mob.setCustomNameVisible(false);
                    }

                    double distance = player.getLocation().distance(mob.getLocation());

                    if (HOSTILE_MOBS.contains(mob.getType()) && mob.hasMetadata("mobLevel")) {
                        if (distance < 10) {
                            // Spieler ist nah genug, zeige das Level an
                            MetadataValue value = mob.getMetadata("mobLevel").get(0);
                            mob.setCustomName("Level " + value.asInt());
                            mob.setCustomNameVisible(true);
                        } else {
                            // Spieler ist zu weit weg, verstecke das Level
                            mob.setCustomNameVisible(false);
                        }
                    }
                }
            });
        }
    }

    public void levelEntity(LivingEntity livingEntity) {
        if (HOSTILE_MOBS.contains(livingEntity.getType())) {
            int level = weightedRandomLevel();
            livingEntity.setMetadata("mobLevel", new FixedMetadataValue(plugin, level));
            double health = 20.0 + (level * 1.0); // Basisgesundheit + Zusatz je nach Level
            livingEntity.setMaxHealth(health);
            livingEntity.setHealth(health);
        }
    }

    private static int weightedRandomLevel() {
        int randomNumber = random.nextInt(100) + 1; // Zufallszahl zwischen 1 und 100
        if (randomNumber <= 75) return 1; // 75% Chance für Level 1
        if (randomNumber <= 90) return 2; // 15% Chance für Level 2
        if (randomNumber <= 97) return 3; // 7% Chance für Level 3
        if (randomNumber <= 99) return 4; // 2% Chance für Level 4
        return 5; // 1% Chance für Level 5
    }

    public void saveMobLevels() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Wolf) {
                    Wolf wolf = (Wolf) entity;
                    if (wolf.isTamed() && wolf.hasMetadata("mobLevel")) {
                        List<MetadataValue> values = wolf.getMetadata("mobLevel");
                        for (MetadataValue value : values) {
                            if (value.getOwningPlugin().getName().equals(plugin.getName())) {
                                int mobLevel = value.asInt();
                                String path = "wolves." + wolf.getUniqueId().toString();
                                plugin.getConfig().set(path + ".mobLevel", mobLevel);
                                plugin.getConfig().set(path + ".owner", wolf.getOwner().getUniqueId().toString());
                                plugin.getLogger().info("Plugin wird deaktiviert. Speichere Wolf: " + wolf.getUniqueId() +";" + mobLevel + ";" + wolf.getOwner().getUniqueId().toString());
                            }
                        }
                    }
                }
            }
        }
        plugin.saveConfig();
    }

    public void loadMobLevels() {
        if (plugin.getConfig().contains("wolves")) {
            ConfigurationSection wolvesSection = plugin.getConfig().getConfigurationSection("wolves");
            for (String key : wolvesSection.getKeys(false)) {
                try {
                    UUID wolfUUID = UUID.fromString(key);
                    World world = Bukkit.getWorlds().get(0); // Beispiel: Erste Welt
                    Entity entity = world.getEntity(wolfUUID);
                    if (entity instanceof Wolf) {
                        Wolf wolf = (Wolf) entity;
                        int mobLevel = wolvesSection.getInt(key + ".mobLevel");
                        wolf.setMetadata("mobLevel", new FixedMetadataValue(plugin, mobLevel));
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Ungültige UUID in wolves Konfiguration: " + key);
                }
            }
        }
    }

}

