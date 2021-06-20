package me.ram.bedwarsscoreboardaddon.arena;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameOverEvent;
import io.github.bedwarsrel.events.BedwarsPlayerKilledEvent;
import io.github.bedwarsrel.events.BedwarsTargetBlockDestroyedEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.addon.DeathMode;
import me.ram.bedwarsscoreboardaddon.addon.HealthLevel;
import me.ram.bedwarsscoreboardaddon.addon.Holographic;
import me.ram.bedwarsscoreboardaddon.addon.InvisibilityPlayer;
import me.ram.bedwarsscoreboardaddon.addon.LobbyBlock;
import me.ram.bedwarsscoreboardaddon.addon.NoBreakBed;
import me.ram.bedwarsscoreboardaddon.addon.PlaySound;
import me.ram.bedwarsscoreboardaddon.addon.Rejoin;
import me.ram.bedwarsscoreboardaddon.addon.ResourceUpgrade;
import me.ram.bedwarsscoreboardaddon.addon.Respawn;
import me.ram.bedwarsscoreboardaddon.addon.ScoreBoard;
import me.ram.bedwarsscoreboardaddon.addon.TeamShop;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.storage.PlayerGameStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.server.PluginDisableEvent;

public class Arena {
  private Game game;
  
  private ScoreBoard scoreboard;
  
  private PlayerGameStorage playergamestorage;
  
  private DeathMode deathmode;
  
  private HealthLevel healthlevel;
  
  private NoBreakBed nobreakbed;
  
  private ResourceUpgrade resourceupgrade;
  
  private Holographic holographic;
  
  private TeamShop teamshop;
  
  private InvisibilityPlayer invisiblePlayer;
  
  private LobbyBlock lobbyblock;
  
  private Respawn respawn;
  
  private Rejoin rejoin;
  
  private Boolean isover;
  
  public Arena(Game game) {
    this.game = game;
    this.playergamestorage = new PlayerGameStorage(game);
    this.scoreboard = new ScoreBoard(this);
    this.deathmode = new DeathMode(game);
    this.healthlevel = new HealthLevel(game);
    this.nobreakbed = new NoBreakBed(game);
    this.resourceupgrade = new ResourceUpgrade(game);
    this.holographic = new Holographic(game, this.resourceupgrade);
    this.teamshop = new TeamShop(game);
    this.invisiblePlayer = new InvisibilityPlayer(game);
    this.lobbyblock = new LobbyBlock(game);
    this.respawn = new Respawn(game);
    this.rejoin = new Rejoin(game);
    this.isover = Boolean.valueOf(false);
  }
  
  public Game getGame() {
    return this.game;
  }
  
  public TeamShop getTeamShop() {
    return this.teamshop;
  }
  
  public ScoreBoard getScoreBoard() {
    return this.scoreboard;
  }
  
  public PlayerGameStorage getPlayerGameStorage() {
    return this.playergamestorage;
  }
  
  public DeathMode getDeathMode() {
    return this.deathmode;
  }
  
  public HealthLevel getHealthLevel() {
    return this.healthlevel;
  }
  
  public NoBreakBed getNoBreakBed() {
    return this.nobreakbed;
  }
  
  public ResourceUpgrade getResourceUpgrade() {
    return this.resourceupgrade;
  }
  
  public Holographic getHolographic() {
    return this.holographic;
  }
  
  public InvisibilityPlayer getInvisiblePlayer() {
    return this.invisiblePlayer;
  }
  
  public LobbyBlock getLobbyBlock() {
    return this.lobbyblock;
  }
  
  public Respawn getRespawn() {
    return this.respawn;
  }
  
  public Rejoin getRejoin() {
    return this.rejoin;
  }
  
  public Boolean isOver() {
    return this.isover;
  }
  
  public void onTargetBlockDestroyed(BedwarsTargetBlockDestroyedEvent e) {
    if (!isGamePlayer(e.getPlayer()).booleanValue())
      return; 
    Map<String, Integer> beds = this.playergamestorage.getPlayerBeds();
    Player player = e.getPlayer();
    if (beds.containsKey(player.getName())) {
      beds.put(player.getName(), Integer.valueOf(((Integer)beds.get(player.getName())).intValue() + 1));
    } else {
      beds.put(player.getName(), Integer.valueOf(1));
    } 
    this.holographic.onTargetBlockDestroyed(e);
  }
  
  public void onDeath(Player player) {
    if (!isGamePlayer(player).booleanValue())
      return; 
    Map<String, Integer> dies = this.playergamestorage.getPlayerDies();
    if (dies.containsKey(player.getName())) {
      dies.put(player.getName(), Integer.valueOf(((Integer)dies.get(player.getName())).intValue() + 1));
    } else {
      dies.put(player.getName(), Integer.valueOf(1));
    } 
    PlaySound.playSound(player, Config.play_sound_sound_death);
    this.respawn.onDeath(player, false);
  }
  
  public void onRespawn(Player player) {
    if (!isGamePlayer(player).booleanValue())
      return; 
    this.respawn.onRespawn(player);
  }
  
  public void onPlayerKilled(BedwarsPlayerKilledEvent e) {
    if (!isGamePlayer(e.getPlayer()).booleanValue() || !isGamePlayer(e.getKiller()).booleanValue())
      return; 
    Player player = e.getPlayer();
    Player killer = e.getKiller();
    if (!this.game.getPlayers().contains(player) || !this.game.getPlayers().contains(killer) || this.game.isSpectator(player) || 
      this.game.isSpectator(killer))
      return; 
    Map<String, Integer> totalkills = this.playergamestorage.getPlayerTotalKills();
    Map<String, Integer> kills = this.playergamestorage.getPlayerKills();
    Map<String, Integer> finalkills = this.playergamestorage.getPlayerFinalKills();
    if (!this.game.getPlayerTeam(player).isDead(this.game))
      if (kills.containsKey(killer.getName())) {
        kills.put(killer.getName(), Integer.valueOf(((Integer)kills.get(killer.getName())).intValue() + 1));
      } else {
        kills.put(killer.getName(), Integer.valueOf(1));
      }  
    if (this.game.getPlayerTeam(player).isDead(this.game))
      if (finalkills.containsKey(killer.getName())) {
        finalkills.put(killer.getName(), Integer.valueOf(((Integer)finalkills.get(killer.getName())).intValue() + 1));
      } else {
        finalkills.put(killer.getName(), Integer.valueOf(1));
      }  
    if (totalkills.containsKey(killer.getName())) {
      totalkills.put(killer.getName(), Integer.valueOf(((Integer)totalkills.get(killer.getName())).intValue() + 1));
    } else {
      totalkills.put(killer.getName(), Integer.valueOf(1));
    } 
    PlaySound.playSound(killer, Config.play_sound_sound_kill);
  }
  
  public void onOver(BedwarsGameOverEvent e) {
    if (e.getGame().getName().equals(this.game.getName())) {
      this.isover = Boolean.valueOf(true);
      if (Config.overstats_enabled && e.getWinner() != null) {
        Team winner = e.getWinner();
        Map<String, Integer> totalkills = this.playergamestorage.getPlayerTotalKills();
        int kills_1 = 0;
        int kills_2 = 0;
        int kills_3 = 0;
        String kills_1_player = "none";
        String kills_2_player = "none";
        String kills_3_player = "none";
        for (String player : totalkills.keySet()) {
          int k = ((Integer)totalkills.get(player)).intValue();
          if (k > 0 && k > kills_1) {
            kills_1_player = player;
            kills_1 = k;
          } 
        } 
        for (String player : totalkills.keySet()) {
          int k = ((Integer)totalkills.get(player)).intValue();
          if (k > kills_2 && k <= kills_1 && !player.equals(kills_1_player)) {
            kills_2_player = player;
            kills_2 = k;
          } 
        } 
        for (String player : totalkills.keySet()) {
          int k = ((Integer)totalkills.get(player)).intValue();
          if (k > kills_3 && k <= kills_2 && !player.equals(kills_1_player) && 
            !player.equals(kills_2_player)) {
            kills_3_player = player;
            kills_3 = k;
          } 
        } 
        List<String> WinTeamPlayers = new ArrayList<>();
        for (Player teamplayer : winner.getPlayers())
          WinTeamPlayers.add(teamplayer.getName()); 
        List<String> list1 = WinTeamPlayers;
        String str1 = list1.toString().replace("[", "").replace("]", "");
        for (Player player : this.game.getPlayers()) {
          for (String os : Config.overstats_message)
            player.sendMessage(os.replace("{color}", (CharSequence)winner.getChatColor())
                .replace("{win_team}", winner.getName())
                .replace("{win_team_players}", str1)
                .replace("{first_1_kills_player}", kills_1_player)
                .replace("{first_2_kills_player}", kills_2_player)
                .replace("{first_3_kills_player}", kills_3_player)
                .replace("{first_1_kills}", (new StringBuilder(String.valueOf(kills_1))).toString()).replace("{first_2_kills}", (new StringBuilder(String.valueOf(kills_2))).toString())
                .replace("{first_3_kills}", (new StringBuilder(String.valueOf(kills_3))).toString())); 
        } 
      } 
      this.nobreakbed.onOver();
      this.holographic.remove();
      this.lobbyblock.recovery();
    } 
  }
  
  public void onEnd() {
    this.lobbyblock.recovery();
  }
  
  public void onDisable(PluginDisableEvent e) {
    if (e.getPlugin().equals(Main.getInstance())) {
      this.holographic.remove();
      this.lobbyblock.recovery();
    } 
  }
  
  public void onArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
    this.holographic.onArmorStandManipulate(e);
  }
  
  public void onClick(InventoryClickEvent e) {
    this.teamshop.onClick(e);
    this.teamshop.onClickDefense(e);
    this.teamshop.onClickHaste(e);
    this.teamshop.onClickHeal(e);
    this.teamshop.onClickProtection(e);
    this.teamshop.onClickSharpness(e);
    this.teamshop.onClickTrap(e);
  }
  
  public void onItemMerge(ItemMergeEvent e) {
    if (!Config.item_merge && this.game.getRegion().isInRegion(e.getEntity().getLocation()))
      e.setCancelled(true); 
  }
  
  public void onPlayerLeave(Player player) {
    this.holographic.onPlayerLeave(player);
    if (Config.rejoin_enabled) {
      if (this.game.getState() == GameState.RUNNING && !this.game.isSpectator(player)) {
        Team team = this.game.getPlayerTeam(player);
        if (team != null && 
          team.getPlayers().size() > 1 && !team.isDead(this.game)) {
          this.rejoin.addPlayer(player);
          return;
        } 
      } 
      this.rejoin.removePlayer(player.getName());
    } 
  }
  
  public void onPlayerJoined(Player player) {
    if (Config.rejoin_enabled)
      this.rejoin.rejoin(player); 
    this.holographic.onPlayerJoin(player);
  }
  
  private Boolean isGamePlayer(Player player) {
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
    if (game == null)
      return Boolean.valueOf(false); 
    if (!game.getName().equals(this.game.getName()))
      return Boolean.valueOf(false); 
    if (game.isSpectator(player))
      return Boolean.valueOf(false); 
    return Boolean.valueOf(true);
  }
}
