package me.ram.bedwarsscoreboardaddon.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import io.github.bedwarsrel.game.Game;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardUtil {
  private static Map<Player, Scoreboard> scoreboards = new HashMap<>();
  
  private static Map<Player, Map<Player, Integer>> player_health = new HashMap<>();
  
  private static String[] cutUnranked(String[] content) {
    String[] elements = Arrays.<String>copyOf(content, 16);
    if (elements[0] == null)
      elements[0] = "BedWars"; 
    if (elements[0].length() > 32)
      elements[0] = elements[0].substring(0, 32); 
    for (int i = 1; i < elements.length; i++) {
      if (elements[i] != null && elements[i].length() > 40)
        elements[i] = elements[i].substring(0, 40); 
    } 
    return elements;
  }
  
  public static Map<Player, Scoreboard> getScoreboards() {
    return scoreboards;
  }
  
  public static void removePlayer(Player player) {
    if (scoreboards.containsKey(player))
      scoreboards.remove(player); 
    if (player_health.containsKey(player))
      player_health.remove(player); 
  }
  
  public static void setScoreboard(Player p, String[] elements) {
    elements = cutUnranked(elements);
    try {
      if (p.getScoreboard() == null || p.getScoreboard() == Bukkit.getScoreboardManager().getMainScoreboard() || 
        p.getScoreboard().getObjectives().size() != 1)
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()); 
      if (p.getScoreboard().getObjective("bwsba-lobby") == null) {
        p.getScoreboard().registerNewObjective("bwsba-lobby", "dummy");
        p.getScoreboard().getObjective("bwsba-lobby").setDisplaySlot(DisplaySlot.SIDEBAR);
      } 
      p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(elements[0]);
      for (int i = 1; i < elements.length; i++) {
        if (elements[i] != null && p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(elements[i])
          .getScore() != 16 - i) {
          p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(elements[i]).setScore(16 - i);
          for (String string : p.getScoreboard().getEntries()) {
            if (p.getScoreboard().getObjective("bwsba-lobby").getScore(string).getScore() == 16 - i && 
              !string.equals(elements[i]))
              p.getScoreboard().resetScores(string); 
          } 
        } 
      } 
      for (String entry : p.getScoreboard().getEntries()) {
        boolean toErase = true;
        byte b;
        int j;
        String[] arrayOfString;
        for (j = (arrayOfString = elements).length, b = 0; b < j; ) {
          String element = arrayOfString[b];
          if (element != null && element.equals(entry) && p.getScoreboard().getObjective("bwsba-lobby")
            .getScore(entry).getScore() == 16 - Arrays.<String>asList(elements).indexOf(element)) {
            toErase = false;
            break;
          } 
          b++;
        } 
        if (toErase)
          p.getScoreboard().resetScores(entry); 
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public static void setScoreboard(Player p, String[] elements, Game game) {
    if (!scoreboards.containsKey(p))
      scoreboards.put(p, Bukkit.getScoreboardManager().getNewScoreboard()); 
    elements = cutUnranked(elements);
    Scoreboard scoreboard = scoreboards.get(p);
    try {
      if (scoreboard.getObjective("bwsba-game") == null) {
        scoreboard.registerNewObjective("bwsba-game", "dummy");
        scoreboard.getObjective("bwsba-game").setDisplaySlot(DisplaySlot.SIDEBAR);
      } 
      ProtocolManager m = ProtocolLibrary.getProtocolManager();
      if (p.getScoreboard() == null || !p.getScoreboard().equals(scoreboard)) {
        if (Config.tab_health) {
          try {
            PacketContainer packet = m.createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
            packet.getIntegers().write(0, Integer.valueOf(0));
            packet.getStrings().write(0, "bwsba-game-list");
            packet.getStrings().write(1, "bwsba-game-list");
            m.sendServerPacket(p, packet);
          } catch (Exception e) {
            e.printStackTrace();
          } 
          try {
            PacketContainer packet = m.createPacket(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
            packet.getIntegers().write(0, Integer.valueOf(0));
            packet.getStrings().write(0, "bwsba-game-list");
            m.sendServerPacket(p, packet);
          } catch (Exception e) {
            e.printStackTrace();
          } 
        } 
        if (Config.tag_health) {
          try {
            PacketContainer packet = m.createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
            packet.getIntegers().write(0, Integer.valueOf(0));
            packet.getStrings().write(0, "bwsba-game-name");
            packet.getStrings().write(1, "bwsba-game-name");
            m.sendServerPacket(p, packet);
          } catch (Exception e) {
            e.printStackTrace();
          } 
          try {
            PacketContainer packet = m.createPacket(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
            packet.getIntegers().write(0, Integer.valueOf(2));
            packet.getStrings().write(0, "bwsba-game-name");
            m.sendServerPacket(p, packet);
          } catch (Exception e) {
            e.printStackTrace();
          } 
          try {
            PacketContainer packet = m.createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
            packet.getIntegers().write(0, Integer.valueOf(2));
            packet.getStrings().write(0, "bwsba-game-name");
            packet.getStrings().write(1, "§c❤");
            m.sendServerPacket(p, packet);
          } catch (Exception e) {
            e.printStackTrace();
          } 
        } 
      } 
      scoreboard.getObjective(DisplaySlot.SIDEBAR).setDisplayName(elements[0]);
      for (int i = 1; i < elements.length; i++) {
        if (elements[i] != null && 
          scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(elements[i]).getScore() != 16 - i) {
          scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(elements[i]).setScore(16 - i);
          for (String string : scoreboard.getEntries()) {
            if (scoreboard.getObjective("bwsba-game").getScore(string).getScore() == 16 - i && 
              !string.equals(elements[i]))
              scoreboard.resetScores(string); 
          } 
        } 
      } 
      for (String entry : scoreboard.getEntries()) {
        boolean toErase = true;
        byte b;
        int j;
        String[] arrayOfString;
        for (j = (arrayOfString = elements).length, b = 0; b < j; ) {
          String element = arrayOfString[b];
          if (element != null && element.equals(entry) && scoreboard.getObjective("bwsba-game")
            .getScore(entry).getScore() == 16 - Arrays.<String>asList(elements).indexOf(element)) {
            toErase = false;
            break;
          } 
          b++;
        } 
        if (toErase)
          scoreboard.resetScores(entry); 
      } 
      if (!player_health.containsKey(p))
        player_health.put(p, new HashMap<>()); 
      Map<Player, Integer> map = player_health.get(p);
      for (Player pl : game.getPlayers()) {
        DecimalFormat format = new DecimalFormat("##");
        int j = Integer.valueOf(format.format(pl.getHealth())).intValue();
        if (((Integer)map.getOrDefault(pl, Integer.valueOf(0))).intValue() != j) {
          if (Config.tab_health)
            try {
              PacketContainer packet = m.createPacket(PacketType.Play.Server.SCOREBOARD_SCORE);
              packet.getIntegers().write(0, Integer.valueOf(j));
              packet.getStrings().write(0, pl.getName());
              packet.getStrings().write(1, "bwsba-game-list");
              m.sendServerPacket(p, packet);
            } catch (Exception e) {
              e.printStackTrace();
            }  
          if (Config.tag_health)
            try {
              PacketContainer packet = m.createPacket(PacketType.Play.Server.SCOREBOARD_SCORE);
              packet.getIntegers().write(0, Integer.valueOf(j));
              packet.getStrings().write(0, pl.getName());
              packet.getStrings().write(1, "bwsba-game-name");
              m.sendServerPacket(p, packet);
            } catch (Exception e) {
              e.printStackTrace();
            }  
          map.put(pl, Integer.valueOf(j));
        } 
      }
      io.github.bedwarsrel.game.Team playerteam = game.getPlayerTeam(p);
      List<UUID> players = Main.getInstance().getArenaManager().getArena(game.getName()).getInvisiblePlayer()
        .getPlayers();
      for (io.github.bedwarsrel.game.Team t : game.getTeams().values()) {
        Team team = scoreboard.getTeam(game.getName() + ":" + t.getName());
        if (team == null)
          team = scoreboard.registerNewTeam(String.valueOf(String.valueOf(game.getName())) + ":" + t.getName()); 
        if (!Config.playertag_prefix.equals(""))
          team.setPrefix(Config.playertag_prefix.replace("{color}", (CharSequence)t.getChatColor()).replace("{team}", 
                t.getName())); 
        if (!Config.playertag_suffix.equals(""))
          team.setSuffix(Config.playertag_suffix.replace("{color}", (CharSequence)t.getChatColor()).replace("{team}", 
                t.getName())); 
        team.setAllowFriendlyFire(false);
        for (Player pl : t.getPlayers()) {
          if (!team.hasPlayer((OfflinePlayer)pl)) {
            if (!players.contains(pl.getUniqueId())) {
              team.addPlayer((OfflinePlayer)pl);
              continue;
            } 
            if (playerteam != null && playerteam.getPlayers().contains(pl))
              team.addPlayer((OfflinePlayer)pl); 
          } 
        } 
      } 
      if (playerteam != null && players.contains(p.getUniqueId()))
        for (io.github.bedwarsrel.game.Team t : game.getTeams().values()) {
          if (!t.getName().equals(playerteam.getName()))
            for (Player player : t.getPlayers()) {
              Scoreboard scoreboard2 = player.getScoreboard();
              for (Team team : scoreboard2.getTeams()) {
                if (playerteam.getPlayers().contains(p))
                  team.removePlayer(p);
              } 
            }  
        }  
      if (p.getScoreboard() == null || !p.getScoreboard().equals(scoreboard))
        p.setScoreboard(scoreboard); 
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
}
