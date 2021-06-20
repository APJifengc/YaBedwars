package com.yallage.yabedwars.addon;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameStartedEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.yallage.yabedwars.YaBedwars;
import com.yallage.yabedwars.config.Config;
import com.yallage.yabedwars.event.BoardAddonPlayerShootWitherBowEvent;
import com.yallage.yabedwars.manager.PlaceholderManager;
import com.yallage.yabedwars.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class WitherBow implements Listener {
    public static final Map<String, PlaceholderManager> placeholdermanager = new HashMap<>();

    public Map<String, PlaceholderManager> getPlaceholderManager() {
        return placeholdermanager;
    }

    private String getDate() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(Config.date_format);
        return format.format(date);
    }

    private String getGameTime(int time) {
        return String.valueOf(time / 60);
    }

    private String getFormattedTimeLeft(int time) {
        int min;
        int sec;
        String minStr;
        String secStr;
        min = (int) Math.floor((time / 60));
        sec = time % 60;
        minStr = (min < 10) ? ("0" + min) : String.valueOf(min);
        secStr = (sec < 10) ? ("0" + sec) : String.valueOf(sec);
        return minStr + ":" + secStr;
    }

    @EventHandler
    public void onStarted(final BedwarsGameStartedEvent e) {
        final Game game = e.getGame();
        placeholdermanager.put(game.getName(), new PlaceholderManager());
        (new BukkitRunnable() {
            Boolean isExecuted = Boolean.FALSE;

            public void run() {
                e.getGame().getState();
                if (e.getGame().getState() != GameState.WAITING && e.getGame().getState() == GameState.RUNNING) {
                    for (Player player : e.getGame().getPlayers()) {
                        int wither = e.getGame().getTimeLeft() - Config.witherbow_gametime;
                        String format = wither / 60 + ":" + ((wither % 60 < 10) ? ("0" + (wither % 60)) : wither % 60);
                        String bowtime = null;
                        if (wither > 0)
                            bowtime = format;
                        if (wither <= 0)
                            bowtime = Config.witherbow_already_starte;
                        if (e.getGame().getPlayerTeam(player) != null &&
                                player.getLocation().getWorld() == e.getGame().getPlayerTeam(player).getSpawnLocation()
                                        .getWorld()) {
                            Team playerteam = game.getPlayerTeam(player);
                            String ab = Config.actionbar
                                    .replace("{team_peoples}", String.valueOf(e.getGame().getPlayerTeam(player).getPlayers().size()))
                                    .replace("{bowtime}", bowtime)
                                    .replace("{color}", e.getGame().getPlayerTeam(player).getChatColor().toString())
                                    .replace("{team}", e.getGame().getPlayerTeam(player).getName())
                                    .replace("{range}", String.valueOf((int) player.getLocation().distance(e.getGame().getPlayerTeam(player).getSpawnLocation())))

                                    .replace("{time}", WitherBow.this.getGameTime(e.getGame().getTimeLeft()))
                                    .replace("{formattime}", WitherBow.this.getFormattedTimeLeft(e.getGame().getTimeLeft()))
                                    .replace("{game}", e.getGame().getName()).replace("{date}", WitherBow.this.getDate())
                                    .replace("{online}", String.valueOf(Bukkit.getOnlinePlayers().size()));
                            if (WitherBow.placeholdermanager.containsKey(game.getName())) {
                                for (String placeholder : WitherBow.placeholdermanager.get(game.getName()).getGamePlaceholder().keySet()) {
                                    ab = ab.replace(placeholder, WitherBow.placeholdermanager.get(game.getName())
                                            .getGamePlaceholder().get(placeholder));
                                }
                                if (playerteam == null) {
                                    Iterator<String> iterator = WitherBow.placeholdermanager.get(game.getName()).getTeamPlaceholders().keySet().iterator();
                                    while (iterator.hasNext()) {
                                        String teamname = iterator.next();
                                        for (String placeholder : (Iterable<String>) ((Map) WitherBow.placeholdermanager.get(game.getName()).getTeamPlaceholders().get(teamname)).keySet()) {
                                            ab = ab.replace(placeholder, "");
                                        }
                                    }
                                } else if (WitherBow.placeholdermanager.get(game.getName()).getTeamPlaceholders()
                                        .containsKey(playerteam.getName())) {
                                    for (String placeholder : WitherBow.placeholdermanager.get(game.getName()).getTeamPlaceholder(playerteam.getName()).keySet()) {
                                        ab = ab.replace(placeholder, WitherBow.placeholdermanager.get(game.getName())
                                                .getTeamPlaceholder(playerteam.getName()).get(placeholder));
                                    }
                                } else {
                                    for (String teamname : WitherBow.placeholdermanager.get(game.getName()).getTeamPlaceholders().keySet()) {
                                        for (String placeholder : (Iterable<String>) ((Map) WitherBow.placeholdermanager.get(game.getName()).getTeamPlaceholders().get(teamname)).keySet()) {
                                            ab = ab.replace(placeholder, "");
                                        }
                                    }
                                }
                                if (WitherBow.placeholdermanager.get(game.getName()).getPlayerPlaceholders()
                                        .containsKey(player.getName())) {
                                    for (String placeholder : WitherBow.placeholdermanager.get(game.getName()).getPlayerPlaceholder(player.getName()).keySet()) {
                                        ab = ab.replace(placeholder, WitherBow.placeholdermanager.get(game.getName())
                                                .getPlayerPlaceholder(player.getName()).get(placeholder));
                                    }
                                } else {
                                    for (String playername : WitherBow.placeholdermanager.get(game.getName()).getPlayerPlaceholders().keySet()) {
                                        for (String placeholder : (Iterable<String>) ((Map) WitherBow.placeholdermanager.get(game.getName()).getPlayerPlaceholders().get(playername)).keySet()) {
                                            ab = ab.replace(placeholder, "");
                                        }
                                    }
                                }
                            }
                            Utils.sendPlayerActionbar(player, ab);
                        }
                        if (!this.isExecuted && e.getGame().getTimeLeft() <= Config.witherbow_gametime &&
                                Config.witherbow_enabled) {
                            this.isExecuted = Boolean.TRUE;
                            if (!Config.witherbow_title.equals("") || !Config.witherbow_subtitle.equals(""))
                                Utils.sendTitle(player, 10, 50, 10, Config.witherbow_title, Config.witherbow_subtitle);
                            if (!Config.witherbow_message.equals(""))
                                player.sendMessage(Config.witherbow_message);
                            PlaySound.playSound(game, Config.play_sound_sound_enable_witherbow);
                        }
                    }
                } else {
                    cancel();
                }
            }
        }).runTaskTimer(YaBedwars.getInstance(), 0L, 21L);
    }

    @EventHandler
    public void onShootBow(EntityShootBowEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;
        Player player = (Player) e.getEntity();
        Game getGame = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (getGame == null)
            return;
        if (getGame.getTimeLeft() <= Config.witherbow_gametime && Config.witherbow_enabled &&
                getGame.getPlayerTeam(player) != null && getGame.getState() == GameState.RUNNING) {
            WitherSkull skull = player.launchProjectile(WitherSkull.class);
            BoardAddonPlayerShootWitherBowEvent shootWitherBowEvent = new BoardAddonPlayerShootWitherBowEvent(getGame,
                    player, skull);
            BedwarsRel.getInstance().getServer().getPluginManager().callEvent(shootWitherBowEvent);
            if (shootWitherBowEvent.isCancelled()) {
                skull.remove();
                return;
            }
            player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 5);
            PlaySound.playSound(player, Config.play_sound_sound_witherbow);
            skull.setYield(4.0F);
            skull.setVelocity(e.getProjectile().getVelocity());
            skull.setShooter(player);
            e.setCancelled(true);
            player.updateInventory();
        }
    }

    @EventHandler
    public void Damage(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        Entity damager = e.getDamager();
        if (entity instanceof Player && damager instanceof WitherSkull) {
            WitherSkull skull = (WitherSkull) damager;
            Player shooter = (Player) skull.getShooter();
            Player player = (Player) entity;
            Game getGame = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
            if (getGame == null)
                return;
            if (getGame.getPlayerTeam(shooter) == null || getGame.getPlayerTeam(player) == null) {
                e.setCancelled(true);
                return;
            }
            if (getGame.getPlayerTeam(shooter) == getGame.getPlayerTeam(player)) {
                e.setCancelled(true);
                return;
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 1));
        }
    }
}
