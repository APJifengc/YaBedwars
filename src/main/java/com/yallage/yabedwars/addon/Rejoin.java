package com.yallage.yabedwars.addon;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.PlayerSettings;
import io.github.bedwarsrel.game.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.yallage.yabedwars.YaBedwars;
import com.yallage.yabedwars.arena.Arena;
import com.yallage.yabedwars.config.Config;
import com.yallage.yabedwars.events.BoardAddonPlayerAddRejoinEvent;
import com.yallage.yabedwars.events.BoardAddonPlayerRejoinEvent;
import com.yallage.yabedwars.events.BoardAddonPlayerRejoinedEvent;
import com.yallage.yabedwars.events.BoardAddonPlayerRemoveRejoinEvent;
import com.yallage.yabedwars.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Rejoin {
    private final Game game;

    private final Map<String, RejoinData> players;

    private final Map<String, List<String>> teams;

    public Rejoin(Game game) {
        this.game = game;
        this.players = new HashMap<>();
        this.teams = new HashMap<>();
    }

    public void addPlayer(Player player) {
        Team team = this.game.getPlayerTeam(player);
        if (team == null)
            return;
        BoardAddonPlayerAddRejoinEvent event = new BoardAddonPlayerAddRejoinEvent(this.game, player, this);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            this.players.put(player.getName(), new RejoinData(player, this.game, this));
            List<String> list = this.teams.getOrDefault(team.getName(), new ArrayList<>());
            list.add(player.getName());
            this.teams.put(team.getName(), list);
        }
    }

    public void removeTeam(String team) {
        if (this.teams.containsKey(team)) {
            for (String player : this.teams.get(team))
                removePlayer(player);
            this.teams.remove(team);
        }
    }

    public void removePlayer(String player) {
        if (this.players.containsKey(player)) {
            this.players.remove(player);
            Bukkit.getPluginManager().callEvent(new BoardAddonPlayerRemoveRejoinEvent(this.game, player, this));
        }
    }

    public Map<String, RejoinData> getPlayers() {
        return this.players;
    }

    public void rejoin(Player player) {
        if (this.players.containsKey(player.getName())) {
            BoardAddonPlayerRejoinEvent event = new BoardAddonPlayerRejoinEvent(this.game, player, this);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled())
                this.players.get(player.getName()).rejoin();
        }
    }

    private static class RejoinData {
        private UUID uuid;

        private Game game;

        private Rejoin rejoin;

        private String teamname;

        private PlayerSettings playersettings;

        private List<ItemStack> armors;

        public RejoinData(Player player, Game game, Rejoin rejoin) {
            Team team = game.getPlayerTeam(player);
            if (team == null)
                return;
            this.teamname = team.getName();
            this.uuid = player.getUniqueId();
            this.game = game;
            this.rejoin = rejoin;
            this.playersettings = game.getPlayerSettings(player);
            this.armors = new ArrayList<>();
            if (Config.giveitem_keeparmor) {
                if (player.getInventory().getHelmet() != null) {
                    this.armors.add(player.getInventory().getHelmet());
                } else {
                    this.armors.add(new ItemStack(Material.AIR));
                }
                if (player.getInventory().getChestplate() != null) {
                    this.armors.add(player.getInventory().getChestplate());
                } else {
                    this.armors.add(new ItemStack(Material.AIR));
                }
                if (player.getInventory().getLeggings() != null) {
                    this.armors.add(player.getInventory().getLeggings());
                } else {
                    this.armors.add(new ItemStack(Material.AIR));
                }
                if (player.getInventory().getBoots() != null) {
                    this.armors.add(player.getInventory().getBoots());
                } else {
                    this.armors.add(new ItemStack(Material.AIR));
                }
            }
        }

        public void rejoin() {
            if (this.teamname == null)
                return;
            final Player player = Bukkit.getPlayer(this.uuid);
            if (player == null || !player.isOnline())
                return;
            BedwarsRel.getInstance().getGameManager().addGamePlayer(player, this.game);
            if (!this.game.getPlayers().contains(player))
                this.game.getPlayers().add(player);
            this.game.getFreePlayers().remove(player);
            this.game.getPlayerSettings().put(player, this.playersettings);
            final Team team = this.game.getTeam(this.teamname);
            if (team == null)
                return;
            team.addPlayer(player);
            player.setVelocity(new Vector(0, 0, 0));
            player.setGameMode(GameMode.SPECTATOR);
            for (Player p : this.game.getPlayers())
                p.sendMessage(Config.rejoin_message_rejoin.replace("{player}", player.getName()));
            Utils.clearTitle(player);
            (new BukkitRunnable() {
                public void run() {
                    if (player.isOnline() && RejoinData.this.game.getState() == GameState.RUNNING &&
                            RejoinData.this.game.getPlayers().contains(player) &&
                            YaBedwars.getInstance().getArenaManager().getArenas().containsKey(RejoinData.this.game.getName())) {
                        player.getInventory().clear();
                        player.getInventory().setHelmet(new ItemStack(Material.AIR));
                        player.getInventory().setChestplate(new ItemStack(Material.AIR));
                        player.getInventory().setLeggings(new ItemStack(Material.AIR));
                        player.getInventory().setBoots(new ItemStack(Material.AIR));
                        GiveItem.giveItem(player, team);
                        if (Config.compass_enabled)
                            Compass.giveCompass(player);
                        if (Config.giveitem_keeparmor && RejoinData.this.armors.size() > 0) {
                            player.getInventory().setHelmet(RejoinData.this.armors.get(0));
                            player.getInventory().setChestplate(RejoinData.this.armors.get(1));
                            player.getInventory().setLeggings(RejoinData.this.armors.get(2));
                            player.getInventory().setBoots(RejoinData.this.armors.get(3));
                        }
                        Arena arena = YaBedwars.getInstance().getArenaManager().getArenas().get(RejoinData.this.game.getName());
                        player.setMaxHealth(arena.getHealthLevel().getNowHealth());
                        player.setHealth(player.getMaxHealth());
                        if (Config.respawn_enabled) {
                            Respawn respawn = arena.getRespawn();
                            respawn.onDeath(player, true);
                            respawn.onRespawn(player);
                        } else {
                            player.setVelocity(new Vector(0, 0, 0));
                            player.teleport(team.getSpawnLocation());
                        }
                    }
                }
            }).runTaskLater(YaBedwars.getInstance(), 16L);
            Bukkit.getPluginManager().callEvent(new BoardAddonPlayerRejoinedEvent(this.game, player, this.rejoin));
        }
    }
}
