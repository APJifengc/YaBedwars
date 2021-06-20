package com.yallage.yabedwars.addon;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;

import java.util.HashMap;
import java.util.Map;

import com.yallage.yabedwars.YaBedwars;
import com.yallage.yabedwars.config.Config;
import com.yallage.yabedwars.events.BoardAddonSetHealthEvent;
import com.yallage.yabedwars.utils.ColorUtil;
import com.yallage.yabedwars.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HealthLevel {
    private final Map<String, String> leveltime;

    private Integer nowhealth;

    public Map<String, String> getLevelTime() {
        return this.leveltime;
    }

    public Integer getNowHealth() {
        return this.nowhealth;
    }

    public HealthLevel(final Game game) {
        this.leveltime = new HashMap<>();
        this.nowhealth = 20;
        (new BukkitRunnable() {
            public void run() {
                if (Config.sethealth_start_enabled)
                    for (Player player : game.getPlayers()) {
                        HealthLevel.this.nowhealth = Config.sethealth_start_health;
                        player.setMaxHealth(Config.sethealth_start_health);
                        player.setHealth(player.getMaxHealth());
                        if (!Config.start_title_enabled && (!Config.sethealth_start_title.equals("") ||
                                !Config.sethealth_start_subtitle.equals("")))
                            Utils.sendTitle(player, 10, 50, 10, Config.sethealth_start_title,
                                    Config.sethealth_start_subtitle);
                        if (!Config.sethealth_start_message.equals(""))
                            player.sendMessage(Config.sethealth_start_message);
                    }
            }
        }).runTaskLater(YaBedwars.getInstance(), 0L);
        for (String sh : YaBedwars.getInstance().getConfig().getConfigurationSection("sethealth").getKeys(false)) {
            if (!sh.equals("start"))
                (new BukkitRunnable() {
                    int gametime;

                    int maxhealth;

                    String title;

                    String subtitle;

                    String message;

                    Boolean isExecuted;

                    public void run() {
                        if (game.getState() == GameState.RUNNING) {
                            if (this.isExecuted) {
                                cancel();
                                return;
                            }
                            int remtime = game.getTimeLeft() - this.gametime;
                            String formatremtime = remtime / 60 + ":" + (
                                    (remtime % 60 < 10) ? ("0" + (remtime % 60)) : remtime % 60);
                            HealthLevel.this.leveltime.put(sh, formatremtime);
                            if (game.getTimeLeft() <= this.gametime) {
                                this.isExecuted = Boolean.TRUE;
                                BoardAddonSetHealthEvent setHealthEvent = new BoardAddonSetHealthEvent(game);
                                Bukkit.getPluginManager().callEvent(setHealthEvent);
                                if (setHealthEvent.isCancelled()) {
                                    cancel();
                                    return;
                                }
                                HealthLevel.this.nowhealth = this.maxhealth;
                                for (Player player : game.getPlayers()) {
                                    double dhealth = this.maxhealth - player.getMaxHealth();
                                    player.setMaxHealth(this.maxhealth);
                                    if (dhealth > 0.0D) {
                                        double nhealth = player.getHealth() + dhealth;
                                        nhealth = (nhealth > this.maxhealth) ? this.maxhealth : nhealth;
                                        player.setHealth(nhealth);
                                    }
                                    if (!this.title.equals("") || !this.subtitle.equals(""))
                                        Utils.sendTitle(player, 10, 50, 10, ColorUtil.color(this.title),
                                                ColorUtil.color(this.subtitle));
                                    if (!this.message.equals(""))
                                        player.sendMessage(ColorUtil.color(this.message));
                                }
                                PlaySound.playSound(game, Config.play_sound_sound_sethealth);
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
