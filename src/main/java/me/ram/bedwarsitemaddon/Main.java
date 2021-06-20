package me.ram.bedwarsitemaddon;

import me.ram.bedwarsitemaddon.command.Commands;
import me.ram.bedwarsitemaddon.config.Config;
import me.ram.bedwarsitemaddon.items.BridgeEgg;
import me.ram.bedwarsitemaddon.items.EnderPearlChair;
import me.ram.bedwarsitemaddon.items.ExplosionProof;
import me.ram.bedwarsitemaddon.items.FireBall;
import me.ram.bedwarsitemaddon.items.LightTNT;
import me.ram.bedwarsitemaddon.items.Parachute;
import me.ram.bedwarsitemaddon.items.TNTLaunch;
import me.ram.bedwarsitemaddon.items.TeamIronGolem;
import me.ram.bedwarsitemaddon.items.TeamSilverFish;
import me.ram.bedwarsitemaddon.items.Trampoline;
import me.ram.bedwarsitemaddon.items.WalkPlatform;
import me.ram.bedwarsitemaddon.listener.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin {
  private static Plugin instance;
  
  public void onEnable() {
    instance = this;
    Bukkit.getConsoleSender().sendMessage("§f========================================");
    Bukkit.getConsoleSender().sendMessage("§7");
    Bukkit.getConsoleSender().sendMessage("            §bBedwarsItemAddon");
    Bukkit.getConsoleSender().sendMessage("§7");
    Bukkit.getConsoleSender().sendMessage(" §a版本: " + getVersion());
    Bukkit.getConsoleSender().sendMessage(" §a依赖: BedwarsRel 1.3.6");
    Bukkit.getConsoleSender().sendMessage("§7");
    Bukkit.getConsoleSender().sendMessage("§f========================================");
    Bukkit.getConsoleSender().sendMessage("§7");
    Bukkit.getConsoleSender().sendMessage(" §a作者: Ram");
    Bukkit.getConsoleSender().sendMessage("§7");
    Bukkit.getConsoleSender().sendMessage("§f========================================");
    Config.loadConfig();
    Bukkit.getPluginCommand("bedwarsitemaddon").setExecutor(new Commands());
    if (Bukkit.getPluginManager().getPlugin("BedwarsRel") != null) {
      (new BukkitRunnable() {
          public void run() {
            if (Bukkit.getPluginManager().isPluginEnabled("BedwarsRel")) {
              cancel();
              Main.this.registerEvents();
            } 
          }
        }).runTaskTimer(this, 0L, 0L);
    } else {
      Bukkit.getPluginManager().disablePlugin(this);
    } 
    try {
    
    } catch (Exception exception) {}
  }
  
  public static Plugin getInstance() {
    return instance;
  }
  
  public static String getVersion() {
    return "1.6.1";
  }
  
  private void registerEvents() {
    Bukkit.getPluginManager().registerEvents(new FireBall(), this);
    Bukkit.getPluginManager().registerEvents(new LightTNT(), this);
    Bukkit.getPluginManager().registerEvents(new BridgeEgg(), this);
    Bukkit.getPluginManager().registerEvents(new Parachute(), this);
    Bukkit.getPluginManager().registerEvents(new TNTLaunch(), this);
    Bukkit.getPluginManager().registerEvents(new Trampoline(), this);
    Bukkit.getPluginManager().registerEvents(new WalkPlatform(), this);
    Bukkit.getPluginManager().registerEvents(new EventListener(), this);
    Bukkit.getPluginManager().registerEvents(new TeamIronGolem(), this);
    Bukkit.getPluginManager().registerEvents(new TeamSilverFish(), this);
    Bukkit.getPluginManager().registerEvents(new ExplosionProof(), this);
    Bukkit.getPluginManager().registerEvents(new EnderPearlChair(), this);
  }
}
