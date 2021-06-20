package me.ram.bedwarsscoreboardaddon.addon;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.manager.PlaceholderManager;
import me.ram.bedwarsscoreboardaddon.utils.ScoreboardUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ScoreBoard {
  private Arena arena;
  
  private Game game;
  
  private int tc = 0;
  
  private Map<String, String> timerplaceholder;
  
  private PlaceholderManager placeholdermanager;
  
  private Map<String, String> teamstatus;
  
  private String over_plan_info;
  
  private String over_plan_time;
  
  public ScoreBoard(Arena arena) {
    this.arena = arena;
    this.game = arena.getGame();
    this.placeholdermanager = new PlaceholderManager();
    this.teamstatus = new HashMap<>();
    this.timerplaceholder = new HashMap<>();
    for (String id : Config.timer.keySet()) {
      (new BukkitRunnable() {
          int i;
          
          public void run() {
            if (ScoreBoard.this.game.getState() == GameState.RUNNING) {
              String format = String.valueOf(this.i / 60) + ":" + ((this.i % 60 < 10) ? ("0" + (this.i % 60)) : this.i % 60);
              ScoreBoard.this.timerplaceholder.put("{timer_" + id + "}", format);
              this.i--;
            } else {
              cancel();
            } 
          }
        }).runTaskTimer((Plugin)Main.getInstance(), 0L, 21L);
    } 
    (new BukkitRunnable() {
        int i = Config.scoreboard_interval;
        
        public void run() {
          this.i--;
          if (this.i <= 0) {
            this.i = Config.scoreboard_interval;
            if (ScoreBoard.this.game.getState() != GameState.WAITING && ScoreBoard.this.game.getState() == GameState.RUNNING) {
              ScoreBoard.this.updateScoreboard();
            } else {
              cancel();
            } 
            return;
          } 
        }
      }).runTaskTimer((Plugin)Main.getInstance(), 0L, 1L);
    (new BukkitRunnable() {
        public void run() {
          for (BukkitTask task : ScoreBoard.this.game.getRunningTasks())
            task.cancel(); 
          ScoreBoard.this.game.getRunningTasks().clear();
          ScoreBoard.this.startTimerCountdown(ScoreBoard.this.game);
        }
      }).runTaskLater((Plugin)Main.getInstance(), 19L);
  }
  
  public PlaceholderManager getPlaceholderManager() {
    return this.placeholdermanager;
  }
  
  public void setTeamStatusFormat(String team, String status) {
    this.teamstatus.put(team, status);
  }
  
  public void removeTeamStatusFormat(String team) {
    this.teamstatus.remove(team);
  }
  
  public Map<String, String> getTeamStatusFormat() {
    return this.teamstatus;
  }
  
  private String getGameTime(int time) {
    return String.valueOf(time / 60);
  }
  
  private void startTimerCountdown(final Game game) {
    BukkitRunnable task = new BukkitRunnable() {
        public void run() {
          if (game.getTimeLeft() == 0) {
            game.setOver(true);
            game.getCycle().checkGameOver();
            cancel();
            return;
          } 
          game.setTimeLeft(game.getTimeLeft() - 1);
        }
      };
    game.addRunningTask(task.runTaskTimer((Plugin)BedwarsRel.getInstance(), 0L, 20L));
  }
  
  public void updateScoreboard() {
    List<String> scoreboard_lines;
    this.tc++;
    List<String> lines = new ArrayList<>();
    String plan_info = "null";
    String plan_time = "null";
    for (String plan : Config.planinfo) {
      if (this.game.getTimeLeft() <= Main.getInstance().getConfig().getInt("planinfo." + plan + ".startametime") && 
        this.game.getTimeLeft() > Main.getInstance().getConfig().getInt("planinfo." + plan + ".endametime")) {
        plan_info = Main.getInstance().getConfig().getString("planinfo." + plan + ".planinfo");
        plan_time = Main.getInstance().getConfig().getString("planinfo." + plan + ".plantime");
      } 
    } 
    if (this.game.getTimeLeft() == 1) {
      this.over_plan_info = plan_info;
      this.over_plan_time = plan_time;
    } else if (this.game.getTimeLeft() < 1) {
      plan_info = this.over_plan_info;
      plan_time = this.over_plan_time;
    } 
    int ats = 0;
    int rts = 0;
    for (Team team : this.game.getTeams().values()) {
      if (!team.isDead(this.game))
        ats++; 
      if (team.getPlayers().size() > 0)
        rts++; 
    } 
    int wither = this.game.getTimeLeft() - Config.witherbow_gametime;
    String format = String.valueOf(wither / 60) + ":" + ((wither % 60 < 10) ? ("0" + (wither % 60)) : wither % 60);
    String bowtime = null;
    if (wither > 0)
      bowtime = format; 
    if (wither <= 0)
      bowtime = Config.witherbow_already_starte; 
    String Title = "";
    if (this.tc >= Config.scoreboard_title.size())
      this.tc = 0; 
    int tcs = 0;
    for (String title : Config.scoreboard_title) {
      if (this.tc == tcs)
        Title = title.replace("{game}", this.game.getName()).replace("{time}", 
            getFormattedTimeLeft(this.game.getTimeLeft())); 
      tcs++;
    } 
    String teams = (new StringBuilder(String.valueOf(this.game.getTeams().size()))).toString();
    if (Config.scoreboard_lines.containsKey(teams)) {
      scoreboard_lines = (List<String>)Config.scoreboard_lines.get(teams);
    } else if (Config.scoreboard_lines.containsKey("default")) {
      scoreboard_lines = (List<String>)Config.scoreboard_lines.get("default");
    } else {
      scoreboard_lines = Arrays.asList(new String[] { "", "{team_status}", "" });
    } 
    for (Player player : this.game.getPlayers()) {
      ChatColor chatColor = ChatColor.WHITE;
      Team playerteam = this.game.getPlayerTeam(player);
      lines.clear();
      String tks = "0";
      String ks = "0";
      String fks = "0";
      String dis = "0";
      String bes = "0";
      Map<String, Integer> totalkills = this.arena.getPlayerGameStorage().getPlayerTotalKills();
      Map<String, Integer> kills = this.arena.getPlayerGameStorage().getPlayerKills();
      Map<String, Integer> finalkills = this.arena.getPlayerGameStorage().getPlayerFinalKills();
      Map<String, Integer> dies = this.arena.getPlayerGameStorage().getPlayerDies();
      Map<String, Integer> beds = this.arena.getPlayerGameStorage().getPlayerBeds();
      if (totalkills.containsKey(player.getName()))
        tks = totalkills.get(player.getName()).toString();
      if (kills.containsKey(player.getName()))
        ks = kills.get(player.getName()).toString();
      if (finalkills.containsKey(player.getName()))
        fks = (String)finalkills.get(player.getName()).toString();
      if (dies.containsKey(player.getName()))
        dis = (String)dies.get(player.getName()).toString();
      if (beds.containsKey(player.getName()))
        bes = (String)beds.get(player.getName()).toString();
      String p_t_c = "§f";
      String p_t_ps = "";
      String p_t = "";
      String p_t_b_s = "";
      if (this.game.getPlayerTeam(player) != null) {
        chatColor = this.game.getPlayerTeam(player).getChatColor();
        p_t_ps = (new StringBuilder(String.valueOf(this.game.getPlayerTeam(player).getPlayers().size()))).toString();
        p_t = this.game.getPlayerTeam(player).getName();
        p_t_b_s = getTeamBedStatus(this.game, this.game.getPlayerTeam(player));
      } 
      for (String ls : scoreboard_lines) {
        if (ls.contains("{team_status}")) {
          for (Team t : this.game.getTeams().values()) {
            String you = "";
            if (this.game.getPlayerTeam(player) != null)
              if (this.game.getPlayerTeam(player) == t) {
                you = Config.scoreboard_you;
              } else {
                you = "";
              }  
            if (this.teamstatus.containsKey(t.getName())) {
              lines.add(((String)this.teamstatus.get(t.getName())).replace("{you}", you));
              continue;
            } 
            lines.add(ls.replace("{team_status}", 
                  getTeamStatusFormat(this.game, t).replace("{you}", you)));
          } 
          continue;
        } 
        String date = (new SimpleDateFormat(Config.date_format)).format(new Date());
        String addline = ls.replace("{planinfo}", plan_info).replace("{plantime}", plan_time)
          .replace("{death_mode}", this.arena.getDeathMode().getDeathmodeTime())
          .replace("{remain_teams}", (new StringBuilder(String.valueOf(rts))).toString()).replace("{alive_teams}", (new StringBuilder(String.valueOf(ats))).toString())
          .replace("{teams}", (new StringBuilder(String.valueOf(this.game.getTeams().size()))).toString()).replace("{color}", chatColor.toString())
          .replace("{team_peoples}", p_t_ps).replace("{player_name}", player.getName())
          .replace("{team}", p_t).replace("{beds}", bes).replace("{dies}", dis)
          .replace("{totalkills}", tks).replace("{finalkills}", fks).replace("{kills}", ks)
          .replace("{time}", getGameTime(this.game.getTimeLeft()))
          .replace("{formattime}", getFormattedTimeLeft(this.game.getTimeLeft()))
          .replace("{game}", this.game.getName()).replace("{date}", date)
          .replace("{online}", (new StringBuilder(String.valueOf(this.game.getPlayers().size()))).toString()).replace("{bowtime}", bowtime)
          .replace("{team_bed_status}", p_t_b_s)
          .replace("{no_break_bed}", this.arena.getNoBreakBed().getTime());
        for (String formattime : this.arena.getHealthLevel().getLevelTime().keySet())
          addline = addline.replace("{sethealthtime_" + formattime + "}", 
              this.arena.getHealthLevel().getLevelTime().get(formattime)); 
        for (String formattime : this.arena.getResourceUpgrade().getUpgTime().keySet())
          addline = addline.replace("{resource_upgrade_" + formattime + "}", 
              this.arena.getResourceUpgrade().getUpgTime().get(formattime)); 
        for (String placeholder : this.placeholdermanager.getGamePlaceholder().keySet())
          addline = addline.replace(placeholder, 
              (CharSequence)this.placeholdermanager.getGamePlaceholder().get(placeholder)); 
        for (Team t : this.game.getTeams().values()) {
          if (addline.contains("{team_" + t.getName() + "_status}")) {
            String stf = getTeamStatusFormat(this.game, t);
            if (this.game.getPlayerTeam(player) == null) {
              stf = stf.replace("{you}", "");
            } else if (this.game.getPlayerTeam(player) == t) {
              stf = stf.replace("{you}", Config.scoreboard_you);
            } else {
              stf = stf.replace("{you}", "");
            } 
            addline = addline.replace("{team_" + t.getName() + "_status}", stf);
          } 
          if (addline.contains("{team_" + t.getName() + "_bed_status}"))
            addline = addline.replace("{team_" + t.getName() + "_bed_status}", 
                getTeamBedStatus(this.game, t)); 
          if (addline.contains("{team_" + t.getName() + "_peoples}"))
            addline = addline.replace("{team_" + t.getName() + "_peoples}", (new StringBuilder(String.valueOf(t.getPlayers().size()))).toString()); 
        } 
        if (playerteam == null) {
          for (String teamname : this.placeholdermanager.getTeamPlaceholders().keySet()) {
            for (String placeholder : (this.placeholdermanager.getTeamPlaceholders().get(teamname)).keySet())
              addline = addline.replace(placeholder, "");
          }
        } else if (this.placeholdermanager.getTeamPlaceholders().containsKey(playerteam.getName())) {
          Iterator<String> iterator = this.placeholdermanager.getTeamPlaceholder(playerteam.getName()).keySet().iterator();
          while (iterator.hasNext()) {
            String placeholder = iterator.next();
            addline = addline.replace(placeholder, 
                (CharSequence)this.placeholdermanager.getTeamPlaceholder(playerteam.getName()).get(placeholder));
          } 
        } else {
          for (String teamname : this.placeholdermanager.getTeamPlaceholders().keySet()) {
            for (String placeholder : (this.placeholdermanager.getTeamPlaceholders().get(teamname)).keySet())
              addline = addline.replace(placeholder, ""); 
          } 
        } 
        if (this.placeholdermanager.getPlayerPlaceholders().containsKey(player.getName())) {
          for (String placeholder : this.placeholdermanager.getPlayerPlaceholder(player.getName()).keySet())
            addline = addline.replace(placeholder, 
                (CharSequence)this.placeholdermanager.getPlayerPlaceholder(player.getName()).get(placeholder)); 
        } else {
          for (String playername : this.placeholdermanager.getPlayerPlaceholders().keySet()) {
            for (String placeholder : (this.placeholdermanager.getPlayerPlaceholders().get(playername)).keySet()) {
              addline = addline.replace(placeholder, "");
            } 
          } 
        } 
        for (String placeholder : this.timerplaceholder.keySet())
          addline = addline.replace(placeholder, this.timerplaceholder.get(placeholder));
        if (lines.contains(addline)) {
          lines.add(conflict(lines, addline));
          continue;
        } 
        lines.add(addline);
      } 
      String title = Title;
      List<String> elements = new ArrayList<>();
      elements.add(title);
      elements.addAll(lines);
      if (elements.size() < 16) {
        int es = elements.size();
        for (int i = 0; i < 16 - es; i++)
          elements.add(1, null); 
      } 
      List<String> ncelements = elementsPro(elements);
      String[] scoreboardelements = ncelements.<String>toArray(new String[ncelements.size()]);
      ScoreboardUtil.setScoreboard(player, scoreboardelements, this.game);
    } 
  }
  
  private String getFormattedTimeLeft(int time) {
    int min = 0;
    int sec = 0;
    String minStr = "";
    String secStr = "";
    min = (int)Math.floor((time / 60));
    sec = time % 60;
    minStr = (min < 10) ? ("0" + String.valueOf(min)) : String.valueOf(min);
    secStr = (sec < 10) ? ("0" + String.valueOf(sec)) : String.valueOf(sec);
    return String.valueOf(minStr) + ":" + secStr;
  }
  
  private List<String> elementsPro(List<String> lines) {
    ArrayList<String> nclines = new ArrayList<>();
    for (String ls : lines) {
      String l = ls;
      if (l != null) {
        if (nclines.contains(l)) {
          for (int i = 0; i == 0; ) {
            l = String.valueOf(l) + "§r";
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
  
  private String conflict(List<String> lines, String line) {
    String l = line;
    for (int i = 0; i == 0; ) {
      l = String.valueOf(l) + "§r";
      if (!lines.contains(l))
        return l; 
    } 
    return l;
  }
  
  private String getTeamBedStatus(Game game, Team team) {
    return team.isDead(game) ? Config.scoreboard_team_bed_status_bed_destroyed : 
      Config.scoreboard_team_bed_status_bed_alive;
  }
  
  private String getTeamStatusFormat(Game game, Team team) {
    String alive = Config.scoreboard_team_status_format_bed_alive;
    String destroyed = Config.scoreboard_team_status_format_bed_destroyed;
    String status = team.isDead(game) ? destroyed : alive;
    if (team.isDead(game) && team.getPlayers().size() <= 0)
      status = Config.scoreboard_team_status_format_team_dead; 
    return status.replace("{bed_status}", getTeamBedStatus(game, team))
      .replace("{color}", (CharSequence)team.getChatColor()).replace("{team}", team.getName())
      .replace("{players}", (new StringBuilder(String.valueOf(team.getPlayers().size()))).toString());
  }
}
