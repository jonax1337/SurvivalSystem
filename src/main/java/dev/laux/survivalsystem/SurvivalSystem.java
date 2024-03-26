package dev.laux.survivalsystem;

import dev.laux.survivalsystem.commands.*;
import dev.laux.survivalsystem.listener.*;
import dev.laux.survivalsystem.listener.backpack.BackpackListener;
import dev.laux.survivalsystem.listener.backpack.EnderBackpackListener;
import dev.laux.survivalsystem.managers.*;
import dev.laux.survivalsystem.managers.backpack.BackpackManager;
import dev.laux.survivalsystem.managers.backpack.EnderBackpackManager;
import dev.laux.survivalsystem.util.ActionBarClock;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SurvivalSystem extends JavaPlugin {

    private LocationManager locationManager;
    private static SurvivalSystem instance;
    private MobLevelManager mobLevel;

    private JobManager jobManager;
    private BackpackManager backpackManager;
    private EnderBackpackManager enderBackpackManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        // Location Manager
        locationManager = new LocationManager(this);
        // Job Manager
        jobManager = new JobManager(this);
        // Backpack Manager
        backpackManager = new BackpackManager(this);
        // Ender Backpack Manager
        enderBackpackManager = new EnderBackpackManager(this);
        // register Commands
        registerCommands();
        // register Events
        registerEvents(getServer().getPluginManager());
        // Real Time Clock
        ActionBarClock actionBarClock = new ActionBarClock();
        Bukkit.getScheduler().runTaskTimer(this, actionBarClock::showRealTimeClock, 0L, 20L);
        // Mob Level
        mobLevel = new MobLevelManager(this);
        mobLevel.loadMobLevels();
        Bukkit.getScheduler().runTaskTimer(this, mobLevel::showMobLevel, 0L, 1L);
        // Stellt sicher, dass die config.yml existiert
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Save Mob Levels
        mobLevel.saveMobLevels();
    }

    private void registerEvents(PluginManager pluginManager) {
        pluginManager.registerEvents(new DeathListener(this), this);
        pluginManager.registerEvents(new JoinListener(), this);
        pluginManager.registerEvents(new QuitListener(), this);
        pluginManager.registerEvents(new TreeFallListener(this), this);
        pluginManager.registerEvents(new SleepListener(this), this);
        pluginManager.registerEvents(new MobDamageListener(this), this);
        pluginManager.registerEvents(new JobListener(this), this);
        pluginManager.registerEvents(new TeleportListener(), this);
        pluginManager.registerEvents(new BackpackListener(backpackManager), this);
        pluginManager.registerEvents(new EnderBackpackListener(enderBackpackManager), this);
    }

    private void registerCommands() {
        this.getCommand("sethome").setExecutor(new SetHomeCommand(this));
        this.getCommand("home").setExecutor(new HomeCommand(this));
        this.getCommand("lastdeath").setExecutor(new LastDeathCommand(this));
        this.getCommand("toggletimber").setExecutor(new ToggleTimberCommand(this));
        this.getCommand("job").setExecutor(new JobCommand(this));
    }

    public static SurvivalSystem getInstance() {
        return instance;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public MobLevelManager getMobLevel() {
        return mobLevel;
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    public BackpackManager getBackpackManager() {
        return backpackManager;
    }
}
