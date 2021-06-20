package com.ram.bedwarsitemshop;

import com.ram.bedwarsitemshop.shop.GHDShop;
import com.ram.bedwarsitemshop.shop.NewHypixelShop;
import com.ram.bedwarsitemshop.shop.OldHypixelShop;
import com.ram.bedwarsitemshop.utils.ColorUtil;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
  public static String version = "1.0";
  
  public static int mode;
  
  public static String message_buy;
  
  public static List<String> item_frame;
  
  public static List<String> item_back;
  
  public void onEnable() {
    Bukkit.getConsoleSender().sendMessage("§f=========================================");
    Bukkit.getConsoleSender().sendMessage("§7");
    Bukkit.getConsoleSender().sendMessage("             §bBedwarsItemShop");
    Bukkit.getConsoleSender().sendMessage("§7");
    Bukkit.getConsoleSender().sendMessage(" §a版本: " + version);
    Bukkit.getConsoleSender().sendMessage(" §a依赖: BedwarsRel 1.3.6");
    Bukkit.getConsoleSender().sendMessage("§7");
    Bukkit.getConsoleSender().sendMessage("§f=========================================");
    Bukkit.getConsoleSender().sendMessage("§7");
    Bukkit.getConsoleSender().sendMessage(" §a作者: Ram");
    Bukkit.getConsoleSender().sendMessage("§7");
    Bukkit.getConsoleSender().sendMessage("§f=========================================");
    loadConfig();
    Bukkit.getPluginManager().registerEvents((Listener)new OldHypixelShop(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new NewHypixelShop(), (Plugin)this);
    Bukkit.getPluginManager().registerEvents((Listener)new GHDShop(), (Plugin)this);
  }
  
  public void loadConfig() {
    saveDefaultConfig();
    reloadConfig();
    mode = getConfig().getInt("mode");
    message_buy = ColorUtil.color(getConfig().getString("message.buy"));
    item_frame = ColorUtil.listcolor(getConfig().getStringList("item.frame"));
    item_back = ColorUtil.listcolor(getConfig().getStringList("item.back"));
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("bedwarsitemshop")) {
      if (args.length == 0) {
        sender.sendMessage("§f==========================================================");
        sender.sendMessage("");
        sender.sendMessage("§b                     BedwarsItemShop");
        sender.sendMessage("");
        sender.sendMessage("§f  版本: §a" + version);
        sender.sendMessage("");
        sender.sendMessage("§f  作者: §aRam");
        sender.sendMessage("");
        sender.sendMessage("§f==========================================================");
        return true;
      } 
      if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
        if (sender.hasPermission("bedwarsitemshop.reload")) {
          loadConfig();
          sender.sendMessage("§b§lBWIS §f>> §a配置文件重载完成！");
          return true;
        } 
        sender.sendMessage("§b§lBWIS §f>> §c你没有使用该命令的权限！");
        return true;
      } 
    } 
    return false;
  }
}
