package me.ram.bedwarsscoreboardaddon.addon;

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
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.events.BoardAddonPlayerShootWitherBowEvent;
import me.ram.bedwarsscoreboardaddon.manager.PlaceholderManager;
import me.ram.bedwarsscoreboardaddon.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

public class WitherBow implements Listener {
  public static Map<String, PlaceholderManager> placeholdermanager = new HashMap<>();
  
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
    int min = 0;
    int sec = 0;
    String minStr = "";
    String secStr = "";
    min = (int)Math.floor((time / 60));
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
        Boolean isExecuted = Boolean.valueOf(false);
        
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
                  .replace("{team_peoples}", (
                    new StringBuilder(String.valueOf(e.getGame().getPlayerTeam(player).getPlayers().size()))).toString())
                  .replace("{bowtime}", bowtime)
                  .replace("{color}", (CharSequence)e.getGame().getPlayerTeam(player).getChatColor())
                  .replace("{team}", e.getGame().getPlayerTeam(player).getName())
                  .replace("{range}", (
                    new StringBuilder(
                      String.valueOf((int)player.getLocation().distance(e.getGame().getPlayerTeam(player).getSpawnLocation())))).toString())
                  
                  .replace("{time}", WitherBow.this.getGameTime(e.getGame().getTimeLeft()))
                  .replace("{formattime}", WitherBow.this.getFormattedTimeLeft(e.getGame().getTimeLeft()))
                  .replace("{game}", e.getGame().getName()).replace("{date}", WitherBow.this.getDate())
                  .replace("{online}", (new StringBuilder(String.valueOf(Bukkit.getOnlinePlayers().size()))).toString());
                if (WitherBow.placeholdermanager.containsKey(game.getName())) {
                  Iterator<String> iterator = WitherBow.placeholdermanager.get(game.getName()).getGamePlaceholder().keySet().iterator();
                  while (iterator.hasNext()) {
                    String placeholder = iterator.next();
                    ab = ab.replace(placeholder, WitherBow.placeholdermanager.get(game.getName())
                        .getGamePlaceholder().get(placeholder));
                  } 
                  if (playerteam == null) {
                    iterator = WitherBow.placeholdermanager.get(game.getName()).getTeamPlaceholders().keySet().iterator();
                    while (iterator.hasNext()) {
                      String teamname = iterator.next();
                      Iterator<String> iterator1 = ((Map) WitherBow.placeholdermanager.get(game.getName()).getTeamPlaceholders().get(teamname)).keySet().iterator();
                      while (iterator1.hasNext()) {
                        String placeholder = iterator1.next();
                        ab = ab.replace(placeholder, "");
                      } 
                    } 
                  } else if (WitherBow.placeholdermanager.get(game.getName()).getTeamPlaceholders()
                    .containsKey(playerteam.getName())) {
                    iterator = WitherBow.placeholdermanager.get(game.getName()).getTeamPlaceholder(playerteam.getName()).keySet().iterator();
                    while (iterator.hasNext()) {
                      String placeholder = iterator.next();
                      ab = ab.replace(placeholder, WitherBow.placeholdermanager.get(game.getName())
                          .getTeamPlaceholder(playerteam.getName()).get(placeholder));
                    } 
                  } else {
                    iterator = WitherBow.placeholdermanager.get(game.getName()).getTeamPlaceholders().keySet().iterator();
                    while (iterator.hasNext()) {
                      String teamname = iterator.next();
                      Iterator<String> iterator1 = ((Map) WitherBow.placeholdermanager.get(game.getName()).getTeamPlaceholders().get(teamname)).keySet().iterator();
                      while (iterator1.hasNext()) {
                        String placeholder = iterator1.next();
                        ab = ab.replace(placeholder, "");
                      } 
                    } 
                  } 
                  if (WitherBow.placeholdermanager.get(game.getName()).getPlayerPlaceholders()
                    .containsKey(player.getName())) {
                    iterator = WitherBow.placeholdermanager.get(game.getName()).getPlayerPlaceholder(player.getName()).keySet().iterator();
                    while (iterator.hasNext()) {
                      String placeholder = iterator.next();
                      ab = ab.replace(placeholder, WitherBow.placeholdermanager.get(game.getName())
                          .getPlayerPlaceholder(player.getName()).get(placeholder));
                    } 
                  } else {
                    iterator = WitherBow.placeholdermanager.get(game.getName()).getPlayerPlaceholders().keySet().iterator();
                    while (iterator.hasNext()) {
                      String playername = iterator.next();
                      Iterator<String> iterator1 = ((Map) WitherBow.placeholdermanager.get(game.getName()).getPlayerPlaceholders().get(playername)).keySet().iterator();
                      while (iterator1.hasNext()) {
                        String placeholder = iterator1.next();
                        ab = ab.replace(placeholder, "");
                      } 
                    } 
                  } 
                } 
                Utils.sendPlayerActionbar(player, ab);
              } 
              if (!this.isExecuted.booleanValue() && e.getGame().getTimeLeft() <= Config.witherbow_gametime && 
                Config.witherbow_enabled) {
                this.isExecuted = Boolean.valueOf(true);
                if (!Config.witherbow_title.equals("") || !Config.witherbow_subtitle.equals(""))
                  Utils.sendTitle(player, Integer.valueOf(10), Integer.valueOf(50), Integer.valueOf(10), Config.witherbow_title, Config.witherbow_subtitle); 
                if (!Config.witherbow_message.equals(""))
                  player.sendMessage(Config.witherbow_message); 
                PlaySound.playSound(game, Config.play_sound_sound_enable_witherbow);
              } 
            } 
          } else {
            cancel();
          } 
        }
      }).runTaskTimer(Main.getInstance(), 0L, 21L);
  }
  
  @EventHandler
  public void onShootBow(EntityShootBowEvent e) {
    if (!(e.getEntity() instanceof Player))
      return; 
    Player player = (Player)e.getEntity();
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
      WitherSkull skull = (WitherSkull)damager;
      Player shooter = (Player)skull.getShooter();
      Player player = (Player)entity;
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
