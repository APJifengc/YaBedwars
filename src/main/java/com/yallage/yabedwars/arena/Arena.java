package com.yallage.yabedwars.arena;

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

import com.yallage.yabedwars.YaBedwars;
import com.yallage.yabedwars.addon.DeathMode;
import com.yallage.yabedwars.addon.HealthLevel;
import com.yallage.yabedwars.addon.Holographic;
import com.yallage.yabedwars.addon.InvisibilityPlayer;
import com.yallage.yabedwars.addon.LobbyBlock;
import com.yallage.yabedwars.addon.NoBreakBed;
import com.yallage.yabedwars.addon.PlaySound;
import com.yallage.yabedwars.addon.Rejoin;
import com.yallage.yabedwars.addon.ResourceUpgrade;
import com.yallage.yabedwars.addon.Respawn;
import com.yallage.yabedwars.addon.ScoreBoard;
import com.yallage.yabedwars.addon.TeamShop;
import com.yallage.yabedwars.config.Config;
import com.yallage.yabedwars.storage.PlayerGameStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.server.PluginDisableEvent;

public class Arena {
    private final Game game;

    private final ScoreBoard scoreboard;

    private final PlayerGameStorage playergamestorage;

    private final DeathMode deathmode;

    private final HealthLevel healthlevel;

    private final NoBreakBed nobreakbed;

    private final ResourceUpgrade resourceupgrade;

    private final Holographic holographic;

    private final TeamShop teamshop;

    private final InvisibilityPlayer invisiblePlayer;

    private final LobbyBlock lobbyblock;

    private final Respawn respawn;

    private final Rejoin rejoin;

    private Boolean isOver;

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
        this.isOver = Boolean.FALSE;
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
        return this.isOver;
    }

    public void onTargetBlockDestroyed(BedwarsTargetBlockDestroyedEvent e) {
        if (!isGamePlayer(e.getPlayer()))
            return;
        Map<String, Integer> beds = this.playergamestorage.getPlayerBeds();
        Player player = e.getPlayer();
        if (beds.containsKey(player.getName())) {
            beds.put(player.getName(), beds.get(player.getName()) + 1);
        } else {
            beds.put(player.getName(), 1);
        }
        this.holographic.onTargetBlockDestroyed(e);
    }

    public void onDeath(Player player) {
        if (!isGamePlayer(player))
            return;
        Map<String, Integer> dies = this.playergamestorage.getPlayerDies();
        if (dies.containsKey(player.getName())) {
            dies.put(player.getName(), dies.get(player.getName()) + 1);
        } else {
            dies.put(player.getName(), 1);
        }
        PlaySound.playSound(player, Config.play_sound_sound_death);
        this.respawn.onDeath(player, false);
    }

    public void onRespawn(Player player) {
        if (!isGamePlayer(player))
            return;
        this.respawn.onRespawn(player);
    }

    public void onPlayerKilled(BedwarsPlayerKilledEvent e) {
        if (!isGamePlayer(e.getPlayer()) || !isGamePlayer(e.getKiller()))
            return;
        Player player = e.getPlayer();
        Player killer = e.getKiller();
        if (!this.game.getPlayers().contains(player) || !this.game.getPlayers().contains(killer) || this.game.isSpectator(player) ||
                this.game.isSpectator(killer))
            return;
        Map<String, Integer> totalKills = this.playergamestorage.getPlayerTotalKills();
        Map<String, Integer> kills = this.playergamestorage.getPlayerKills();
        Map<String, Integer> finalKills = this.playergamestorage.getPlayerFinalKills();
        if (!this.game.getPlayerTeam(player).isDead(this.game))
            if (kills.containsKey(killer.getName())) {
                kills.put(killer.getName(), kills.get(killer.getName()) + 1);
            } else {
                kills.put(killer.getName(), 1);
            }
        if (this.game.getPlayerTeam(player).isDead(this.game))
            if (finalKills.containsKey(killer.getName())) {
                finalKills.put(killer.getName(), finalKills.get(killer.getName()) + 1);
            } else {
                finalKills.put(killer.getName(), 1);
            }
        if (totalKills.containsKey(killer.getName())) {
            totalKills.put(killer.getName(), totalKills.get(killer.getName()) + 1);
        } else {
            totalKills.put(killer.getName(), 1);
        }
        PlaySound.playSound(killer, Config.play_sound_sound_kill);
    }

    public void onOver(BedwarsGameOverEvent e) {
        if (e.getGame().getName().equals(this.game.getName())) {
            this.isOver = Boolean.TRUE;
            if (Config.overstats_enabled && e.getWinner() != null) {
                Team winner = e.getWinner();
                Map<String, Integer> totalKills = this.playergamestorage.getPlayerTotalKills();
                int kills_1 = 0;
                int kills_2 = 0;
                int kills_3 = 0;
                String kills_1_player = "none";
                String kills_2_player = "none";
                String kills_3_player = "none";
                for (String player : totalKills.keySet()) {
                    int k = totalKills.get(player);
                    if (k > 0 && k > kills_1) {
                        kills_1_player = player;
                        kills_1 = k;
                    }
                }
                for (String player : totalKills.keySet()) {
                    int k = totalKills.get(player);
                    if (k > kills_2 && k <= kills_1 && !player.equals(kills_1_player)) {
                        kills_2_player = player;
                        kills_2 = k;
                    }
                }
                for (String player : totalKills.keySet()) {
                    int k = totalKills.get(player);
                    if (k > kills_3 && k <= kills_2 && !player.equals(kills_1_player) &&
                            !player.equals(kills_2_player)) {
                        kills_3_player = player;
                        kills_3 = k;
                    }
                }
                List<String> WinTeamPlayers = new ArrayList<>();
                for (Player teamPlayer : winner.getPlayers())
                    WinTeamPlayers.add(teamPlayer.getName());
                String str1 = WinTeamPlayers.toString().replace("[", "").replace("]", "");
                for (Player player : this.game.getPlayers()) {
                    for (String os : Config.overstats_message)
                        player.sendMessage(os.replace("{color}", winner.getChatColor().toString())
                                .replace("{win_team}", winner.getName())
                                .replace("{win_team_players}", str1)
                                .replace("{first_1_kills_player}", kills_1_player)
                                .replace("{first_2_kills_player}", kills_2_player)
                                .replace("{first_3_kills_player}", kills_3_player)
                                .replace("{first_1_kills}", String.valueOf(kills_1)).replace("{first_2_kills}", String.valueOf(kills_2))
                                .replace("{first_3_kills}", String.valueOf(kills_3)));
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
        if (e.getPlugin().equals(YaBedwars.getInstance())) {
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
            return Boolean.FALSE;
        if (!game.getName().equals(this.game.getName()))
            return Boolean.FALSE;
        if (game.isSpectator(player))
            return Boolean.FALSE;
        return Boolean.TRUE;
    }
}
