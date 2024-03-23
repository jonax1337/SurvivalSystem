package dev.laux.survivalsystem.listener;

import dev.laux.survivalsystem.SurvivalSystem;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;

import static dev.laux.survivalsystem.managers.MobLevelManager.HOSTILE_MOBS;

public class MobDamageListener implements Listener {

    private final SurvivalSystem plugin;

    public MobDamageListener(SurvivalSystem plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof LivingEntity && event.getEntityType() != EntityType.PLAYER) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            if (HOSTILE_MOBS.contains(entity.getType())) {
                plugin.getMobLevel().levelEntity(entity);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof LivingEntity) {
            LivingEntity damager = (LivingEntity) event.getDamager();
            if (damager.hasMetadata("mobLevel")) {
                int level = damager.getMetadata("mobLevel").get(0).asInt();
                double damageMultiplier = 1.0 + (level * 0.2); // Stärkerer Einfluss des Levels auf den Schaden
                event.setDamage(event.getDamage() * damageMultiplier);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (HOSTILE_MOBS.contains(entity.getType()) && entity.hasMetadata("mobLevel")) {
            int level = entity.getMetadata("mobLevel").get(0).asInt();

            switch (level) {
                case 5:
                    event.getDrops().add(new ItemStack(Material.DIAMOND, 1));
                    break;
                case 4:
                    event.getDrops().add(new ItemStack(Material.GOLD_INGOT, 2));
                    break;
                // Füge hier weitere Fälle für Level 1 bis 3 ein, wenn gewünscht
            }
        }

        if (event.getEntity() instanceof Wolf) {
            Wolf wolf = (Wolf) event.getEntity();
            if (wolf.isTamed()) {
                // Entferne den Wolf aus der Konfiguration
                String path = "wolves." + wolf.getUniqueId().toString();
                if (plugin.getConfig().contains(path)) {
                    plugin.getConfig().set(path, null); // Entfernt den Eintrag aus der Konfiguration
                    plugin.saveConfig(); // Speichert die Änderungen
                }
            }
        }

    }

}
