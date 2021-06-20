package me.ram.bedwarsscoreboardaddon.addon;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsPlayerJoinEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.ScoreboardUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class LobbyScoreBoard implements Listener {
  private String title = "";
  
  private String getDate() {
    Date date = new Date();
    SimpleDateFormat format = new SimpleDateFormat(Config.date_format);
    return format.format(date);
  }
  
  public LobbyScoreBoard() {
    (new BukkitRunnable() {
        int i = 0;
        
        int tc = 0;
        
        public void run() {
          this.i--;
          if (this.i <= 0) {
            this.i = Config.lobby_scoreboard_interval;
            LobbyScoreBoard.this.title = Config.lobby_scoreboard_title.get(this.tc);
            this.tc++;
            if (this.tc >= Config.lobby_scoreboard_title.size())
              this.tc = 0; 
          } 
        }
      }).runTaskTimer(Main.getInstance(), 0L, 1L);
  }
  
  @EventHandler
  public void onJoin(final BedwarsPlayerJoinEvent e) {
    if (!Config.lobby_scoreboard_enabled)
      return; 
    final Game game = e.getGame();
    final Player player = e.getPlayer();
    BedwarsRel.getInstance().getConfig().set("lobby-scoreboard.content", getLine(player, game));
    final int tc = 0;
    (new BukkitRunnable() {
        int i = 0;
        
        public void run() {
          if (player.isOnline() && e.getGame().getPlayers().contains(player) && 
            e.getGame().getState() == GameState.WAITING) {
            this.i--;
            if (this.i <= 0) {
              this.i = Config.lobby_scoreboard_interval;
              LobbyScoreBoard.this.updateScoreboard(player, game, tc);
            } 
          } else {
            cancel();
          } 
        }
      }).runTaskTimer(Main.getInstance(), 0L, 1L);
  }
  
  private void updateScoreboard(Player player, Game game, int tc) {
    List<String> ncelements = new ArrayList<>();
    ncelements.add(this.title.replace("{game}", game.getName()));
    BedwarsRel.getInstance().getConfig().set("lobby-scoreboard.title", this.title);
    ncelements.addAll(getLine(player, game));
    ncelements = elementsPro(ncelements);
    if (ncelements.size() < 16) {
      int es = ncelements.size();
      for (int i = 0; i < 16 - es; i++)
        ncelements.add(1, null); 
    } 
    String[] scoreboardelements = ncelements.toArray(new String[ncelements.size()]);
    ScoreboardUtil.setScoreboard(player, scoreboardelements);
  }
  
  private List<String> getLine(Player player, Game game) {
    List<String> line = new ArrayList<>();
    String state = Config.lobby_scoreboard_state_waiting;
    String countdown = "null";
    int needplayers = game.getMinPlayers() - game.getPlayers().size();
    needplayers = (needplayers < 0) ? 0 : needplayers;
    if (game.getLobbyCountdown() != null) {
      state = Config.lobby_scoreboard_state_countdown;
      int lobbytime = game.getLobbyCountdown().getLobbytime();
      int counter = game.getLobbyCountdown().getCounter() + 1;
      counter = (counter > lobbytime) ? lobbytime : counter;
      countdown = (new StringBuilder(String.valueOf(counter))).toString();
    } 
    for (String li : Config.lobby_scoreboard_lines) {
      String l = li.replace("{date}", getDate()).replace("{state}", state).replace("{game}", game.getName())
        .replace("{players}", (new StringBuilder(String.valueOf(game.getPlayers().size()))).toString())
        .replace("{maxplayers}", (new StringBuilder(String.valueOf(game.getMaxPlayers()))).toString())
        .replace("{minplayers}", (new StringBuilder(String.valueOf(game.getMinPlayers()))).toString()).replace("{needplayers}", (new StringBuilder(String.valueOf(needplayers))).toString())
        .replace("{countdown}", countdown);
      line.add(l);
    } 
    return line;
  }
  
  private List<String> elementsPro(List<String> lines) {
    ArrayList<String> nclines = new ArrayList<>();
    for (String ls : lines) {
      String l = ls;
      if (l != null) {
        if (nclines.contains(l)) {
          for (int i = 0; i == 0; ) {
            l = l + "§r";
            if (!nclines.contains(l)) {
              nclines.add(l);
              break;
            } 
          } 
          continue;
        } 
        nclines.add(l);
        continue;
      } 
      nclines.add(l);
    } 
    return nclines;
  }
}
