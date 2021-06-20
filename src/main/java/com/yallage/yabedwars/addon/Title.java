package com.yallage.yabedwars.addon;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameOverEvent;
import io.github.bedwarsrel.events.BedwarsGameStartedEvent;
import io.github.bedwarsrel.events.BedwarsPlayerJoinedEvent;
import io.github.bedwarsrel.events.BedwarsTargetBlockDestroyedEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import com.yallage.yabedwars.YaBedwars;
import com.yallage.yabedwars.config.Config;
import com.yallage.yabedwars.utils.Utils;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Title implements Listener {
    private final Map<String, Integer> Times = new HashMap<>();

    @EventHandler
    public void start(final BedwarsGameStartedEvent e) {
        Game game = e.getGame();
        this.Times.put(e.getGame().getName(), e.getGame().getTimeLeft());
        if (Config.start_title_enabled) {
            for (Player player : e.getGame().getPlayers())
                Utils.clearTitle(player);
            int delay = game.getRegion().getWorld().getName().equals(game.getLobby().getWorld().getName()) ? 5 : 30;
            (new BukkitRunnable() {
                int rn = 0;

                public void run() {
                    if (this.rn < Config.start_title_title.size()) {
                        for (Player player : e.getGame().getPlayers())
                            Utils.sendTitle(player, 0, 80, 5, Config.start_title_title.get(this.rn),
                                    Config.start_title_subtitle);
                        this.rn++;
                    } else {
                        cancel();
                    }
                }
            }).runTaskTimer(YaBedwars.getInstance(), delay, 0L);
        }
        (new BukkitRunnable() {
            public void run() {
                PlaySound.playSound(e.getGame(), Config.play_sound_sound_start);
            }
        }).runTaskLater(YaBedwars.getInstance(), 30L);
    }

    @EventHandler
    public void onDestroyed(BedwarsTargetBlockDestroyedEvent e) {
        if (Config.destroyed_title_enabled)
            for (Player player : e.getTeam().getPlayers())
                Utils.sendTitle(player, 1, 30, 1, Config.destroyed_title_title, Config.destroyed_title_subtitle);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        final Player player = e.getPlayer();
        final Game getGame = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (getGame == null)
            return;
        if (Config.die_out_title_enabled)
            (new BukkitRunnable() {
                public void run() {
                    if (getGame.getState() == GameState.RUNNING &&
                            getGame.isSpectator(player))
                        Utils.sendTitle(player, 1, 80, 5, Config.die_out_title_title,
                                Config.die_out_title_subtitle);
                }
            }).runTaskLater(YaBedwars.getInstance(), 5L);
    }

    @EventHandler
    public void onOver(final BedwarsGameOverEvent e) {
        if (Config.victory_title_enabled) {
            final Team team = e.getWinner();
            int time = this.Times.getOrDefault(e.getGame().getName(), 3600) - e.getGame().getTimeLeft();
            final String formattime = time / 60 + ":" + ((time % 60 < 10) ? ("0" + (time % 60)) : time % 60);
            (new BukkitRunnable() {
                public void run() {
                    if (team != null && team.getPlayers() != null)
                        for (Player player : team.getPlayers()) {
                            if (player.isOnline())
                                Utils.clearTitle(player);
                        }
                }
            }).runTaskLater(YaBedwars.getInstance(), 1L);
            (new BukkitRunnable() {
                int rn = 0;

                public void run() {
                    if (this.rn < Config.victory_title_title.size()) {
                        if (team != null && team.getPlayers() != null) {
                            for (Player player : team.getPlayers()) {
                                if (player.isOnline())
                                    Utils.sendTitle(player, 0, 80, 5, Config.victory_title_title.get(this.rn).replace("{time}", formattime)
                                                    .replace("{color}", (CharSequence) team.getChatColor())
                                                    .replace("{team}", team.getName()),
                                            Config.victory_title_subtitle.replace("{time}", formattime)
                                                    .replace("{color}", (CharSequence) team.getChatColor())
                                                    .replace("{team}", team.getName()));
                            }
                            this.rn++;
                        } else {
                            cancel();
                        }
                    } else {
                        cancel();
                    }
                }
            }).runTaskTimer(YaBedwars.getInstance(), 40L, 0L);
        }
        (new BukkitRunnable() {
            public void run() {
                PlaySound.playSound(e.getGame(), Config.play_sound_sound_over);
            }
        }).runTaskLater(YaBedwars.getInstance(), 40L);
    }

    @EventHandler
    public void Join(BedwarsPlayerJoinedEvent e) {
        for (Player player : e.getGame().getPlayers()) {
            if (player.getName().contains(",") || player.getName().contains("[") || player.getName().contains("]"))
                player.kickPlayer("");
            if ((e.getGame().getState() == GameState.WAITING || e.getGame().getState() != GameState.RUNNING) &&
                    Config.jointitle_enabled)
                Utils.sendTitle(player, e.getPlayer(), 5, 50, 5,
                        Config.jointitle_title.replace("{player}", e.getPlayer().getName()),
                        Config.jointitle_subtitle.replace("{player}", e.getPlayer().getName()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void DamageTitle(final EntityDamageByEntityEvent e) {
        if (e.isCancelled() || !(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player))
            return;
        Game getGame = BedwarsRel.getInstance().getGameManager().getGameOfPlayer((Player) e.getDamager());
        if (getGame == null)
            return;
        if (Config.damagetitle_enabled && getGame.getState() == GameState.RUNNING) {
            if (!getGame.getPlayers().contains(e.getDamager()) ||
                    !getGame.getPlayers().contains(e.getEntity()))
                return;
            if (getGame.isSpectator((Player) e.getDamager()) || getGame.isSpectator((Player) e.getEntity()))
                return;
            final Damageable dp = (Damageable) e.getEntity();
            final Player victim = (Player) dp;
            final DecimalFormat df = new DecimalFormat("0.00");
            final DecimalFormat df2 = new DecimalFormat("#");
            if (e.isCancelled())
                return;
            if (!Config.damagetitle_title.equals("") || !Config.damagetitle_subtitle.equals(""))
                (new BukkitRunnable() {
                    public void run() {
                        Utils.sendTitle((Player) e.getDamager(), victim, 0, 20, 0,
                                Config.damagetitle_title.replace("{player}", victim.getName())
                                        .replace("{damage}", df.format(e.getDamage()))
                                        .replace("{health}", df2.format(dp.getHealth()))
                                        .replace("{maxhealth}", df2.format(dp.getMaxHealth())),
                                Config.damagetitle_subtitle.replace("{player}", victim.getName())
                                        .replace("{damage}", df.format(e.getDamage()))
                                        .replace("{health}", df2.format(dp.getHealth()))
                                        .replace("{maxhealth}", df2.format(dp.getMaxHealth())));
                    }
                }).runTaskLater(YaBedwars.getInstance(), 0L);
        }
    }

    @EventHandler
    public void BowDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Arrow))
            return;
        Arrow a1 = (Arrow) e.getDamager();
        if (a1.getShooter() instanceof Player) {
            final Player shooter = (Player) a1.getShooter();
            Game getGame = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(shooter);
            if (getGame == null)
                return;
            if (Config.bowdamage_enabled && getGame.getState() != GameState.WAITING &&
                    getGame.getState() == GameState.RUNNING) {
                final Damageable dp = (Damageable) e.getEntity();
                if (dp instanceof Player) {
                    final Player victim = (Player) dp;
                    final Integer damage = (int) e.getFinalDamage();
                    final DecimalFormat df2 = new DecimalFormat("#");
                    if (getGame.getPlayerTeam(shooter) == getGame.getPlayerTeam(victim))
                        e.setCancelled(true);
                    if (e.isCancelled())
                        return;
                    if (!dp.isDead() &&
                            dp.getHealth() > 0.0D)
                        (new BukkitRunnable() {
                            public void run() {
                                if (!Config.bowdamage_title.equals("") || !Config.bowdamage_subtitle.equals(""))
                                    Utils.sendTitle(shooter, victim, 0, 20, 0,
                                            Config.bowdamage_title.replace("{player}", victim.getName())
                                                    .replace("{damage}", damage.toString())
                                                    .replace("{health}", df2.format(dp.getHealth()))
                                                    .replace("{maxhealth}", df2.format(dp.getMaxHealth())),
                                            Config.bowdamage_subtitle.replace("{player}", victim.getName())
                                                    .replace("{damage}", damage.toString())
                                                    .replace("{health}", df2.format(dp.getHealth()))
                                                    .replace("{maxhealth}", df2.format(dp.getMaxHealth())));
                                if (!Config.bowdamage_message.equals(""))
                                    Utils.sendMessage(shooter, victim,
                                            Config.bowdamage_message.replace("{player}", victim.getName())
                                                    .replace("{damage}", damage.toString())
                                                    .replace("{health}", df2.format(dp.getHealth()))
                                                    .replace("{maxhealth}", df2.format(dp.getMaxHealth())));
                            }
                        }).runTaskLater(YaBedwars.getInstance(), 0L);
                }
            }
        }
    }
}
