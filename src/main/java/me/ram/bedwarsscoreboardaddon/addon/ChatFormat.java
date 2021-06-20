package me.ram.bedwarsscoreboardaddon.addon;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import me.ram.bedwarsscoreboardaddon.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatFormat implements Listener {
  @EventHandler
  public void onCmd(PlayerCommandPreprocessEvent e) {
    if (!Config.chatformat_enabled)
      return; 
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(e.getPlayer());
    if (game == null)
      return; 
    Player player = e.getPlayer();
    if (game.getState() == GameState.RUNNING && game.getPlayers().contains(player) && 
      game.getPlayerTeam(player) != null && e.getMessage().length() > 7) {
      String msg = Config.chatformat_ingame_all;
      String prefix = e.getMessage().substring(0, 7);
      if (!prefix.equals("/shout "))
        return;
      String playermsg = e.getMessage();
      playermsg = playermsg.substring(7);
      for (Player p : game.getPlayers())
        p.sendMessage(msg.replace("{player}", player.getName()).replace("{message}", playermsg)
            .replace("{color}", (CharSequence)game.getPlayerTeam(player).getChatColor())
            .replace("{team}", game.getPlayerTeam(player).getName())); 
      e.setCancelled(true);
      return;
    } 
  }
  
  @EventHandler
  public void onChat(AsyncPlayerChatEvent e) {
    if (!Config.chatformat_enabled)
      return; 
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(e.getPlayer());
    boolean papi = false;
    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
      papi = true; 
    if (game == null)
      return; 
    Player player = e.getPlayer();
    if (game.getState() == GameState.WAITING && game.getPlayers().contains(player) && 
      game.getPlayerTeam(player) != null) {
      String msg = Config.chatformat_lobby_team;
      Team team = game.getPlayerTeam(player);
      msg = msg.replace("{player}", player.getName()).replace("{message}", e.getMessage())
        .replace("{color}", team.getChatColor().toString()).replace("{team}", team.getName());
      for (Player p : game.getPlayers())
        p.sendMessage(msg); 
      e.setCancelled(true);
      return;
    } 
    if (game.getState() == GameState.WAITING && game.getPlayers().contains(player)) {
      String msg = Config.chatformat_lobby;
      msg = msg.replace("{player}", player.getName()).replace("{message}", e.getMessage());
      for (Player p : game.getPlayers())
        p.sendMessage(msg); 
      e.setCancelled(true);
      return;
    } 
    if (game.getState() == GameState.RUNNING && game.getPlayers().contains(player)) {
      String msg = Config.chatformat_spectator;
      if (game.isSpectator(player) || game.getPlayerTeam(player) == null) {
        msg = msg.replace("{player}", player.getName()).replace("{message}", e.getMessage());
        for (Player p : game.getPlayers())
          p.sendMessage(msg); 
        e.setCancelled(true);
        return;
      } 
    } 
    if (game.getState() == GameState.RUNNING && game.getPlayers().contains(player) && 
      game.getPlayerTeam(player) != null) {
      String msg = Config.chatformat_ingame_all;
      String prefix = "";
      boolean all = false;
      for (String prefixs : Config.chatformat_all_prefix) {
        if (prefixs.length() <= e.getMessage().length()) {
          String fp = e.getMessage().substring(0, prefixs.length());
          if (fp.equals(prefixs)) {
            all = true;
            prefix = prefixs;
          } 
        } 
      }
      if (all) {
        String playermsg = e.getMessage();
        playermsg = playermsg.substring(prefix.length(), playermsg.length());
        msg = msg.replace("{player}", player.getName()).replace("{message}", playermsg)
          .replace("{color}", (CharSequence)game.getPlayerTeam(player).getChatColor())
          .replace("{team}", game.getPlayerTeam(player).getName());
        for (Player p : game.getPlayers())
          p.sendMessage(msg); 
        e.setCancelled(true);
        return;
      } 
    } 
    if (game.getState() == GameState.RUNNING && game.getPlayers().contains(player) && 
      game.getPlayerTeam(player) != null) {
      String msg = Config.chatformat_ingame;
      msg = msg.replace("{player}", player.getName()).replace("{message}", e.getMessage())
        .replace("{color}", (CharSequence)game.getPlayerTeam(player).getChatColor())
        .replace("{team}", game.getPlayerTeam(player).getName());
      for (Player p : game.getPlayerTeam(player).getPlayers())
        p.sendMessage(msg); 
      e.setCancelled(true);
      return;
    } 
  }
}
