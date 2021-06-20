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
    instance = (Plugin)this;
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
    Bukkit.getPluginCommand("bedwarsitemaddon").setExecutor((CommandExecutor)new Commands());
    if (Bukkit.getPluginManager().getPlugin("BedwarsRel") != null) {
      (new BukkitRunnable() {
          public void run() {
            if (Bukkit.getPluginManager().isPluginEnabled("BedwarsRel")) {
              cancel();
              Main.this.registerEvents();
            } 
          }
        }).runTaskTimer((Plugin)this, 0L, 0L);
    } else {
      Bukkit.getPluginManager().disablePlugin((Plugin)this);
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
    Bukkit.getPluginManager().registerEvents((Listener)new FireBall(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new LightTNT(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new BridgeEgg(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new Parachute(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new TNTLaunch(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new Trampoline(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new WalkPlatform(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new EventListener(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new TeamIronGolem(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new TeamSilverFish(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new ExplosionProof(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new EnderPearlChair(), (Plugin)this);
  }
}
