package com.yallage.yabedwars.addon;

import com.yallage.yabedwars.YaBedwars;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import com.yallage.yabedwars.config.Config;
import com.yallage.yabedwars.events.BoardAddonDeathModeEvent;
import com.yallage.yabedwars.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathMode {
    private String deathmode_time;

    public String getDeathmodeTime() {
        return this.deathmode_time;
    }

    public DeathMode(final Game game) {
        (new BukkitRunnable() {
            Boolean isExecuted = Boolean.FALSE;

            public void run() {
                if (game.getState() != GameState.WAITING && game.getState() == GameState.RUNNING) {
                    int deathmodetime = game.getTimeLeft() - Config.deathmode_gametime;
                    DeathMode.this.deathmode_time = deathmodetime / 60 + ":" + (
                            (deathmodetime % 60 < 10) ? ("0" + (deathmodetime % 60)) : deathmodetime % 60);
                    if (Config.deathmode_enabled) {
                        if (this.isExecuted) {
                            cancel();
                            return;
                        }
                        if (game.getTimeLeft() <= Config.deathmode_gametime) {
                            this.isExecuted = Boolean.TRUE;
                            BoardAddonDeathModeEvent deathModeEvent = new BoardAddonDeathModeEvent(game);
                            Bukkit.getPluginManager().callEvent(deathModeEvent);
                            if (deathModeEvent.isCancelled()) {
                                cancel();
                                return;
                            }
                            for (Player player : game.getPlayers()) {
                                if (!Config.deathmode_title.equals("") || !Config.deathmode_subtitle.equals(""))
                                    Utils.sendTitle(player, 10, 80, 10, Config.deathmode_title,
                                            Config.deathmode_subtitle);
                                if (!Config.deathmode_message.equals(""))
                                    player.sendMessage(Config.deathmode_message);
                            }
                            for (Team team : game.getTeams().values())
                                DeathMode.this.destroyBlock(game, team);
                            PlaySound.playSound(game, Config.play_sound_sound_deathmode);
                        }
                    }
                } else {
                    cancel();
                }
            }
        }).runTaskTimer(YaBedwars.getInstance(), 0L, 21L);
    }

    private void destroyBlock(Game game, Team team) {
        Material type = team.getTargetHeadBlock().getBlock().getType();
        if (type.equals(game.getTargetMaterial()))
            if (type.equals(Material.BED_BLOCK)) {
                if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
                    team.getTargetFeetBlock().getBlock().setType(Material.AIR);
                } else {
                    team.getTargetHeadBlock().getBlock().setType(Material.AIR);
                }
            } else {
                team.getTargetHeadBlock().getBlock().setType(Material.AIR);
            }
    }
}
