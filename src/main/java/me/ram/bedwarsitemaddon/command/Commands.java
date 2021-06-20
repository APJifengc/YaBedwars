package me.ram.bedwarsitemaddon.command;

import me.ram.bedwarsitemaddon.Main;
import me.ram.bedwarsitemaddon.config.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Commands implements CommandExecutor {
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("bedwarsitemaddon")) {
      if (args.length == 0) {
        sender.sendMessage("§f===========================================================");
        sender.sendMessage("");
        sender.sendMessage("§b                     BedwarsItemAddon");
        sender.sendMessage("");
        sender.sendMessage("§f  版本: §a" + Main.getVersion());
        sender.sendMessage("");
        sender.sendMessage("§f  作者: §aRam");
        sender.sendMessage("");
        sender.sendMessage("§f===========================================================");
        return true;
      } 
      if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
        if (sender.hasPermission("bedwarsitemaddon.reload")) {
          Config.loadConfig();
          sender.sendMessage("§b§lBWIA §f>> §e配置文件重载完成！");
          return true;
        } 
        sender.sendMessage("§b§lBWIA §f>> §c你没有使用该命令的权限！");
        return true;
      } 
    } 
    return false;
  }
}
