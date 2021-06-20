package me.ram.bedwarsscoreboardaddon;

import io.github.bedwarsrel.BedwarsRel;
import java.util.concurrent.Callable;
import me.ram.bedwarsscoreboardaddon.addon.ChatFormat;
import me.ram.bedwarsscoreboardaddon.addon.Compass;
import me.ram.bedwarsscoreboardaddon.addon.DeathItem;
import me.ram.bedwarsscoreboardaddon.addon.GiveItem;
import me.ram.bedwarsscoreboardaddon.addon.HidePlayer;
import me.ram.bedwarsscoreboardaddon.addon.LobbyScoreBoard;
import me.ram.bedwarsscoreboardaddon.addon.Shop;
import me.ram.bedwarsscoreboardaddon.addon.SpawnNoBuild;
import me.ram.bedwarsscoreboardaddon.addon.Spectator;
import me.ram.bedwarsscoreboardaddon.addon.TimeTask;
import me.ram.bedwarsscoreboardaddon.addon.Title;
import me.ram.bedwarsscoreboardaddon.addon.WitherBow;
import me.ram.bedwarsscoreboardaddon.command.BedwarsRelCommandTabCompleter;
import me.ram.bedwarsscoreboardaddon.command.CommandTabCompleter;
import me.ram.bedwarsscoreboardaddon.command.Commands;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.edit.EditGame;
import me.ram.bedwarsscoreboardaddon.listener.EventListener;
import me.ram.bedwarsscoreboardaddon.manager.ArenaManager;
import me.ram.bedwarsscoreboardaddon.manager.HolographicManager;
import me.ram.bedwarsscoreboardaddon.metrics.Metrics;
import me.ram.bedwarsscoreboardaddon.networld.UpdateCheck;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin {
  private static Main instance;
  
  private static ArenaManager arenamanager;
  
  private static HolographicManager holographicmanager;
  
  public static Main getInstance() {
    return instance;
  }
  
  public static String getVersion() {
    return "2.10.2";
  }
  
  public ArenaManager getArenaManager() {
    return arenamanager;
  }
  
  public HolographicManager getHolographicManager() {
    return holographicmanager;
  }
  
  public void onEnable() {
    if (!getDescription().getVersion().equals(getVersion())) {
      Bukkit.getPluginManager().disablePlugin((Plugin)this);
      return;
    } 
    instance = this;
    arenamanager = new ArenaManager();
    holographicmanager = new HolographicManager();
    (new BukkitRunnable() {
        public void run() {
          if (Bukkit.getPluginManager().getPlugin("BedwarsRel") == null || 
            Bukkit.getPluginManager().getPlugin("Citizens") == null || 
            Bukkit.getPluginManager().getPlugin("ProtocolLib") == null || (
            Bukkit.getPluginManager().isPluginEnabled("BedwarsRel") && 
            Bukkit.getPluginManager().isPluginEnabled("Citizens") && 
            Bukkit.getPluginManager().isPluginEnabled("ProtocolLib"))) {
            cancel();
            Bukkit.getConsoleSender().sendMessage("§f========================================");
            Bukkit.getConsoleSender().sendMessage("§7");
            Bukkit.getConsoleSender().sendMessage("         §bBedwarsScoreBoardAddon");
            Bukkit.getConsoleSender().sendMessage("§7");
            Bukkit.getConsoleSender().sendMessage(" §a版本: " + Main.getVersion());
            Bukkit.getConsoleSender().sendMessage("§7");
            Bukkit.getConsoleSender().sendMessage(" §a作者: Ram");
            Bukkit.getConsoleSender().sendMessage("§7");
            Bukkit.getConsoleSender().sendMessage("§f========================================");
            Main.this.init();
          } 
        }
      }).runTaskTimer((Plugin)this, 0L, 0L);
  }
  
  private void init() {
    Boolean debug = Boolean.valueOf(false);
    try {
      debug = Boolean.valueOf(getConfig().getBoolean("init_debug"));
    } catch (Exception exception) {}
    String prefix = "[" + getDescription().getName() + "] ";
    Bukkit.getConsoleSender().sendMessage(String.valueOf(prefix) + "§f开始加载插件...");
    if (Bukkit.getPluginManager().getPlugin("BedwarsRel") != null) {
      if (!Bukkit.getPluginManager().getPlugin("BedwarsRel").getDescription().getVersion().equals("1.3.6")) {
        Bukkit.getConsoleSender().sendMessage(String.valueOf(prefix) + "§c错误: §fBedwarsRel版本过低！");
        Bukkit.getConsoleSender().sendMessage(String.valueOf(prefix) + "§c插件加载失败！");
        Bukkit.getPluginManager().disablePlugin((Plugin)instance);
        return;
      } 
    } else {
      Bukkit.getConsoleSender().sendMessage(String.valueOf(prefix) + "§c错误: §f缺少必要前置 §aBedwarsRel");
      Bukkit.getConsoleSender().sendMessage(String.valueOf(prefix) + "§c插件加载失败！");
      Bukkit.getPluginManager().disablePlugin((Plugin)instance);
      return;
    } 
    if (Bukkit.getPluginManager().getPlugin("Citizens") == null) {
      Bukkit.getConsoleSender().sendMessage(String.valueOf(prefix) + "§c错误: §f缺少必要前置 §aCitizens");
      Bukkit.getConsoleSender().sendMessage(String.valueOf(prefix) + "§c插件加载失败！");
      Bukkit.getPluginManager().disablePlugin((Plugin)instance);
      return;
    } 
    if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
      Bukkit.getConsoleSender().sendMessage(String.valueOf(prefix) + "§c错误: §f缺少必要前置 §aProtocolLib");
      Bukkit.getConsoleSender().sendMessage(String.valueOf(prefix) + "§c插件加载失败！");
      Bukkit.getPluginManager().disablePlugin((Plugin)instance);
      return;
    } 
    try {
      Config.loadConfig();
    } catch (Exception e) {
      Bukkit.getConsoleSender().sendMessage(String.valueOf(prefix) + "§c错误: §f配置文件加载失败！");
      Bukkit.getConsoleSender().sendMessage(String.valueOf(prefix) + "§c插件加载失败！");
      Bukkit.getPluginManager().disablePlugin((Plugin)instance);
      if (debug.booleanValue())
        e.printStackTrace(); 
      return;
    } 
    try {
      Bukkit.getConsoleSender().sendMessage(String.valueOf(prefix) + "§f正在注册监听器...");
      registerEvents();
      Bukkit.getConsoleSender().sendMessage(String.valueOf(prefix) + "§a监听器注册成功！");
    } catch (Exception e) {
      Bukkit.getConsoleSender().sendMessage(String.valueOf(prefix) + "§c错误: §f监听器注册失败！");
      Bukkit.getConsoleSender().sendMessage(String.valueOf(prefix) + "§c插件加载失败！");
      Bukkit.getPluginManager().disablePlugin((Plugin)instance);
      if (debug.booleanValue())
        e.printStackTrace(); 
      return;
    } 
    try {
      Bukkit.getConsoleSender().sendMessage(String.valueOf(prefix) + "§f正在注册指令...");
      Bukkit.getPluginCommand("bedwarsscoreboardaddon").setExecutor((CommandExecutor)new Commands());
      Bukkit.getPluginCommand("bedwarsscoreboardaddon").setTabCompleter((TabCompleter)new CommandTabCompleter());
      Bukkit.getPluginCommand("bw").setTabCompleter((TabCompleter)new BedwarsRelCommandTabCompleter());
      Bukkit.getConsoleSender().sendMessage(String.valueOf(prefix) + "§a指令注册成功！");
    } catch (Exception e) {
      Bukkit.getConsoleSender().sendMessage(String.valueOf(prefix) + "§c错误: §f指令注册失败！");
      Bukkit.getConsoleSender().sendMessage(String.valueOf(prefix) + "§c插件加载失败！");
      Bukkit.getPluginManager().disablePlugin((Plugin)instance);
      if (debug.booleanValue())
        e.printStackTrace(); 
      return;
    } 
    Bukkit.getConsoleSender().sendMessage(String.valueOf(prefix) + "§a插件加载成功！");
    try {
      Metrics metrics = new Metrics((Plugin)this);
      metrics.addCustomChart((Metrics.CustomChart)new Metrics.SimplePie("pluginPrefix", new Callable<String>() {
              public String call() throws Exception {
                return BedwarsRel.getInstance().getConfig().getString("chat-prefix", 
                    ChatColor.GRAY + "[" + ChatColor.AQUA + "BedWars" + ChatColor.GRAY + "]");
              }
            }));
      metrics.addCustomChart((Metrics.CustomChart)new Metrics.SimplePie("language", new Callable<String>() {
              public String call() throws Exception {
                return "Chinese";
              }
            }));
    } catch (Exception exception) {}
  }
  
  private void registerEvents() {
    Bukkit.getPluginManager().registerEvents((Listener)new EventListener(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new LobbyScoreBoard(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new SpawnNoBuild(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new UpdateCheck(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new ChatFormat(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new HidePlayer(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new WitherBow(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new DeathItem(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new Spectator(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new GiveItem(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new TimeTask(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new EditGame(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new Compass(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new Title(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new Shop(), (Plugin)this);
  }
}
