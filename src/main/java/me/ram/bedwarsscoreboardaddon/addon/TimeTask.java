package me.ram.bedwarsscoreboardaddon.addon;

import io.github.bedwarsrel.events.BedwarsGameStartedEvent;
import io.github.bedwarsrel.game.GameState;
import java.util.List;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeTask implements Listener {
  @EventHandler
  public void onStarted(final BedwarsGameStartedEvent e) {
    for (String cmd : Config.timecommand_startcommand) {
      if (!cmd.equals("")) {
        if (cmd.contains("{player}")) {
          for (Player player : e.getGame().getPlayers())
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
                ColorUtil.color(cmd.replace("{player}", player.getName()))); 
          continue;
        } 
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
            ColorUtil.color(cmd));
      } 
    } 
    for (String cmds : Main.getInstance().getConfig().getConfigurationSection("timecommand").getKeys(false)) {
      (new BukkitRunnable() {
          int gametime;
          
          List<String> cmdlist;
          
          Boolean isExecuted;
          
          public void run() {
            if (e.getGame().getState() == GameState.RUNNING) {
              if (this.isExecuted.booleanValue()) {
                cancel();
                return;
              } 
              if (e.getGame().getTimeLeft() <= this.gametime) {
                this.isExecuted = Boolean.valueOf(true);
                for (String cmd : this.cmdlist) {
                  if (!cmd.equals("")) {
                    if (cmd.contains("{player}")) {
                      for (Player player : e.getGame().getPlayers())
                        Bukkit.getServer().dispatchCommand(
                                Bukkit.getServer().getConsoleSender(),
                            ColorUtil.color(cmd.replace("{player}", player.getName()))); 
                      continue;
                    } 
                    Bukkit.getServer().dispatchCommand(
                            Bukkit.getServer().getConsoleSender(),
                        ColorUtil.color(cmd));
                  } 
                } 
                cancel();
              } 
            } else {
              cancel();
            } 
          }
        }).runTaskTimer(Main.getInstance(), 0L, 21L);
    } 
  }
}
