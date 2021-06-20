package com.yallage.yabedwars.addon;

import io.github.bedwarsrel.events.BedwarsGameStartedEvent;
import io.github.bedwarsrel.game.GameState;

import java.util.List;

import com.yallage.yabedwars.YaBedwars;
import com.yallage.yabedwars.config.Config;
import com.yallage.yabedwars.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
        for (String cmds : YaBedwars.getInstance().getConfig().getConfigurationSection("timecommand").getKeys(false)) {
            (new BukkitRunnable() {
                int gametime;

                List<String> cmdlist;

                Boolean isExecuted;

                public void run() {
                    if (e.getGame().getState() == GameState.RUNNING) {
                        if (this.isExecuted) {
                            cancel();
                            return;
                        }
                        if (e.getGame().getTimeLeft() <= this.gametime) {
                            this.isExecuted = Boolean.TRUE;
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
            }).runTaskTimer(YaBedwars.getInstance(), 0L, 21L);
        }
    }
}
