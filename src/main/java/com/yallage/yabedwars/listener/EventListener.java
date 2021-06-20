package com.yallage.yabedwars.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.collect.ImmutableMap;
import com.yallage.yabedwars.ShopReplacer;
import com.yallage.yabedwars.YaBedwars;
import com.yallage.yabedwars.addon.SelectTeam;
import com.yallage.yabedwars.api.XPManager;
import com.yallage.yabedwars.api.events.BedwarsXPDeathDropXPEvent;
import com.yallage.yabedwars.arena.Arena;
import com.yallage.yabedwars.config.Config;
import com.yallage.yabedwars.edit.EditGame;
import com.yallage.yabedwars.events.BedwarsTeamDeadEvent;
import com.yallage.yabedwars.utils.ResourceUtils;
import com.yallage.yabedwars.utils.ScoreboardUtil;
import com.yallage.yabedwars.utils.SoundMachine;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.*;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.utils.ChatWriter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class EventListener implements Listener {
    private final Map<String, Map<Event, PacketListener>> deathevents;

    public EventListener() {
        this.deathevents = new HashMap<>();
        onPacketReceiving();
        onPacketSending();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onStarted(BedwarsGameStartedEvent e) {
        final Game game = e.getGame();
        Map<Player, Scoreboard> scoreboards = ScoreboardUtil.getScoreboards();
        for (Player player : game.getPlayers()) {
            if (scoreboards.containsKey(player))
                ScoreboardUtil.removePlayer(player);
        }
        Arena arena = new Arena(game);
        YaBedwars.getInstance().getArenaManager().addArena(game.getName(), arena);
        (new BukkitRunnable() {
            public void run() {
                if (YaBedwars.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
                    YaBedwars.getInstance().getArenaManager().getArenas().get(game.getName()).getScoreBoard().updateScoreboard();
            }
        }).runTaskLater(YaBedwars.getInstance(), 2L);
    }

    @EventHandler
    public void onOver(BedwarsGameOverEvent e) {
        e.getGame().setOver(true);
        Game game = e.getGame();
        if (YaBedwars.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
            YaBedwars.getInstance().getArenaManager().getArenas().get(game.getName()).onOver(e);
      for (Player player : e.getGame().getPlayers())
        player.getEnderChest().clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onToggleFlight(PlayerToggleFlightEvent e) {
        final Player player = e.getPlayer();
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null)
            return;
        if (!game.getPlayers().contains(player))
            return;
        if (game.isSpectator(player))
            return;
        if (game.getState() == GameState.RUNNING) {
            e.setCancelled(true);
            player.getLocation();
            final Location location = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
            final Vector vector = new Vector(player.getVelocity().getX(), player.getVelocity().getY(), player.getVelocity().getZ());
            (new BukkitRunnable() {
                public void run() {
                    location.setYaw(player.getLocation().getYaw());
                    location.setPitch(player.getLocation().getPitch());
                    player.teleport(location);
                    player.setVelocity(vector);
                }
            }).runTaskLaterAsynchronously(YaBedwars.getInstance(), 1L);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
        final Player player = e.getPlayer();
        String message = e.getMessage();
        final String[] args = message.split(" ");
        if (args[0].equalsIgnoreCase("/bwsbatp")) {
            e.setCancelled(true);
            if (args.length == 8 && player.hasPermission("bedwarsscoreboardaddon.shop.teleport")) {
                String loc = message.substring(10 + args[1].length());
                Location location = toLocation(loc);
                if (location != null) {
                    player.teleport(location);
                    YaBedwars.getInstance().getHolographicManager().displayGameLocation(player, args[1]);
                }
            }
            return;
        }
        if (args[0].equalsIgnoreCase("/bw") || args[0].equalsIgnoreCase("/bedwarsrel:bw")) {
            if (args.length > 3 && args[1].equalsIgnoreCase("addgame"))
                try {
                    Integer.valueOf(args[3]);
                    (new BukkitRunnable() {
                        public void run() {
                            Game game = BedwarsRel.getInstance().getGameManager().getGame(args[2]);
                            EditGame.editGame(player, game);
                        }
                    }).runTask(YaBedwars.getInstance());
                } catch (Exception ignored) {
                }
            return;
        }
        if (!args[0].equalsIgnoreCase("/rejoin"))
            return;
        e.setCancelled(true);
        if (!Config.rejoin_enabled)
            return;
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game != null)
            return;
        for (Arena arena : YaBedwars.getInstance().getArenaManager().getArenas().values()) {
            if (arena.getRejoin().getPlayers().containsKey(player.getName())) {
                arena.getGame().playerJoins(player);
                return;
            }
        }
        player.sendMessage(Config.rejoin_message_error);
    }

    @EventHandler
    public void onPlayerJoined(BedwarsPlayerJoinedEvent e) {
        Player player = e.getPlayer();
        Game game = e.getGame();
        if (YaBedwars.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
            YaBedwars.getInstance().getArenaManager().getArenas().get(game.getName()).onPlayerJoined(player);
        if (game.getState() == GameState.WAITING)
            for (Arena arena : YaBedwars.getInstance().getArenaManager().getArenas().values())
                arena.getRejoin().removePlayer(player.getName());
        YaBedwars.getInstance().getHolographicManager().remove(player);
    }

    @EventHandler
    public void onPlayerLeave(BedwarsPlayerLeaveEvent e) {
        Game game = e.getGame();
        Team team = e.getTeam();
        if (team == null)
            return;
        Player player = e.getPlayer();
        int players = 0;
        for (Player p : team.getPlayers()) {
            if (!game.isSpectator(p))
                players++;
        }
        if (game.getState() == GameState.RUNNING && !game.isSpectator(player) && players <= 1) {
            Bukkit.getPluginManager().callEvent(new BedwarsTeamDeadEvent(game, team));
            if (Config.rejoin_enabled) {
                destroyBlock(game, team);
                if (YaBedwars.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
                    YaBedwars.getInstance().getArenaManager().getArenas().get(game.getName()).getRejoin().removeTeam(team.getName());
            }
        }
        if (YaBedwars.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
            YaBedwars.getInstance().getArenaManager().getArenas().get(game.getName()).onPlayerLeave(e.getPlayer());
        if (player.isOnline()) {
            ProtocolManager m = ProtocolLibrary.getProtocolManager();
            try {
                PacketContainer packet = m.createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
                packet.getIntegers().write(0, 1);
                packet.getStrings().write(0, "bwsba-game-list");
                packet.getStrings().write(1, "bwsba-game-list");
                m.sendServerPacket(player, packet);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                PacketContainer packet = m.createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
                packet.getIntegers().write(0, 1);
                packet.getStrings().write(0, "bwsba-game-name");
                packet.getStrings().write(1, "bwsba-game-name");
                m.sendServerPacket(player, packet);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        YaBedwars.getInstance().getHolographicManager().remove(player);
        ScoreboardUtil.removePlayer(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnd(BedwarsGameEndEvent e) {
        Game game = e.getGame();
        if (YaBedwars.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
            YaBedwars.getInstance().getArenaManager().getArenas().get(game.getName()).onEnd();
        YaBedwars.getInstance().getArenaManager().removeArena(game.getName());
        game.kickAllPlayers();
    }

    @EventHandler
    public void onTargetBlockDestroyed(BedwarsTargetBlockDestroyedEvent e) {
        final Game game = e.getGame();
        if (YaBedwars.getInstance().getArenaManager().getArenas().containsKey(game.getName())) {
            YaBedwars.getInstance().getArenaManager().getArenas().get(game.getName()).onTargetBlockDestroyed(e);
            (new BukkitRunnable() {
                public void run() {
                    if (YaBedwars.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
                        YaBedwars.getInstance().getArenaManager().getArenas().get(game.getName()).getScoreBoard().updateScoreboard();
                }
            }).runTaskLater(YaBedwars.getInstance(), 1L);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null)
            return;
        if (YaBedwars.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
            YaBedwars.getInstance().getArenaManager().getArenas().get(game.getName()).onDeath(player);
        Team team = game.getPlayerTeam(player);
        if (team == null)
            return;
        int players = 0;
        for (Player p : team.getPlayers()) {
            if (!game.isSpectator(p))
                players++;
        }
        if (game.getState() == GameState.RUNNING && players <= 1 && !game.isSpectator(player) && team.isDead(game)) {
            Bukkit.getPluginManager().callEvent(new BedwarsTeamDeadEvent(game, team));
            if (YaBedwars.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
                YaBedwars.getInstance().getArenaManager().getArenas().get(game.getName()).getRejoin().removeTeam(team.getName());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game != null && YaBedwars.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
            YaBedwars.getInstance().getArenaManager().getArenas().get(game.getName()).onRespawn(player);
    }

    @EventHandler
    public void onPlayerKilled(BedwarsPlayerKilledEvent e) {
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(e.getKiller());
        if (game != null && YaBedwars.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
            YaBedwars.getInstance().getArenaManager().getArenas().get(game.getName()).onPlayerKilled(e);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeathLowest(PlayerDeathEvent e) {
        if (!Config.final_killed_enabled)
            return;
        Player player = e.getEntity();
        Player killer = player.getKiller();
        if (killer == null)
            return;
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null || game.getState() != GameState.RUNNING || game.isSpectator(player) ||
                !game.getPlayers().contains(killer) || game.isSpectator(killer) ||
                !game.getPlayerTeam(player).isDead(game))
            return;
        Map<Event, PacketListener> map = this.deathevents.getOrDefault(game.getName(), new HashMap<>());
        map.put(e, addPacketListener(killer, game.getPlayerTeam(killer), player, game.getPlayerTeam(player)));
        this.deathevents.put(game.getName(), map);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeathHighest(PlayerDeathEvent e) {
        if (!Config.final_killed_enabled)
            return;
        Player player = e.getEntity();
        Player killer = player.getKiller();
        if (killer == null)
            return;
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null || game.getState() != GameState.RUNNING || game.isSpectator(player) ||
                !game.getPlayers().contains(killer) || game.isSpectator(killer) ||
                !game.getPlayerTeam(player).isDead(game))
            return;
        Map<Event, PacketListener> map = this.deathevents.getOrDefault(game.getName(), new HashMap<>());
        if (!map.containsKey(e))
            return;
        ProtocolLibrary.getProtocolManager().removePacketListener(map.get(e));
        map.remove(e);
        this.deathevents.put(game.getName(), map);
        String hearts = "";
        DecimalFormat format = new DecimalFormat("#");
        double health = killer.getHealth() / killer.getMaxHealth() * killer.getHealthScale();
        if (!BedwarsRel.getInstance().getBooleanConfig("hearts-in-halfs", true)) {
            format = new DecimalFormat("#.#");
            health /= 2.0D;
        }
        if (BedwarsRel.getInstance().getBooleanConfig("hearts-on-death", true))
            hearts = "[" + ChatColor.RED + "❤" + format.format(health) + ChatColor.GOLD + "]";
        String string = Config.final_killed_message
                .replace("{player}", Game.getPlayerWithTeamString(player, game.getPlayerTeam(player), ChatColor.GOLD))
                .replace("{killer}",
                        Game.getPlayerWithTeamString(killer, game.getPlayerTeam(killer), ChatColor.GOLD, hearts));
        for (Player p : game.getPlayers()) {
            if (p.isOnline())
                p.sendMessage(string);
        }
    }

    private PacketListener addPacketListener(final Player killer, final Team killerTeam, final Player player, final Team deathTeam) {
        PacketAdapter packetAdapter = new PacketAdapter(YaBedwars.getInstance(),
                PacketType.Play.Server.CHAT) {
            public void onPacketSending(PacketEvent e) {
                Player p = e.getPlayer();
                WrappedChatComponent chat = e.getPacket().getChatComponents().read(0);
                String hearts = "";
                DecimalFormat format = new DecimalFormat("#");
                double health = killer.getHealth() / killer.getMaxHealth() * killer.getHealthScale();
                if (!BedwarsRel.getInstance().getBooleanConfig("hearts-in-halfs", true)) {
                    format = new DecimalFormat("#.#");
                    health /= 2.0D;
                }
                if (BedwarsRel.getInstance().getBooleanConfig("hearts-on-death", true))
                    hearts = "[" + ChatColor.RED + "❤" + format.format(health) + ChatColor.GOLD + "]";
                WrappedChatComponent[] chats = WrappedChatComponent.fromChatMessage(
                        ChatWriter.pluginMessage(ChatColor.GOLD + BedwarsRel._l(p, "ingame.player.killed",
                                ImmutableMap.of("killer",
                                        Game.getPlayerWithTeamString(killer, killerTeam, ChatColor.GOLD, hearts),
                                        "player", Game.getPlayerWithTeamString(player, deathTeam, ChatColor.GOLD)))));
                byte b;
                int i;
                WrappedChatComponent[] arrayOfWrappedChatComponent1;
                for (i = (arrayOfWrappedChatComponent1 = chats).length, b = 0; b < i; ) {
                    WrappedChatComponent c = arrayOfWrappedChatComponent1[b];
                    if (chat.getJson().equals(c.getJson())) {
                        e.setCancelled(true);
                        break;
                    }
                    b++;
                }
            }
        };
        ProtocolLibrary.getProtocolManager().addPacketListener(packetAdapter);
        return packetAdapter;
    }

    @EventHandler
    public void onDisable(PluginDisableEvent e) {
        if (e.getPlugin().equals(YaBedwars.getInstance()) || e.getPlugin().equals(BedwarsRel.getInstance())) {
            for (Arena arena : YaBedwars.getInstance().getArenaManager().getArenas().values())
                arena.onDisable(e);
            YaBedwars.getInstance().getHolographicManager().removeAll();
        }
    }

    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
        for (Arena arena : YaBedwars.getInstance().getArenaManager().getArenas().values())
            arena.onArmorStandManipulate(e);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        for (Arena arena : YaBedwars.getInstance().getArenaManager().getArenas().values())
            arena.onClick(e);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCitizensEnable(CitizensEnableEvent e) {
        File folder = Config.getNPCFile();
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(folder);
        if (yamlConfiguration.getKeys(false).contains("npcs")) {
            List<String> npcs = yamlConfiguration.getStringList("npcs");
            List<NPC> gamenpcs = new ArrayList<>();
            for (NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
                if (npcs.contains(String.valueOf(npc.getId())))
                    gamenpcs.add(npc);
            }
            for (NPC npc : gamenpcs)
                CitizensAPI.getNPCRegistry().deregister(npc);
            yamlConfiguration.set("npcs", new ArrayList());
            try {
                yamlConfiguration.save(folder);
            } catch (IOException ignored) {
            }
        }
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent e) {
        if (!Config.invisibility_player_enabled)
            return;
        if (e.getItem().getType() != Material.POTION && e.getItem().getType() != Material.GOLDEN_APPLE &&
                e.getItem().getType() != Material.ROTTEN_FLESH && e.getItem().getType() != Material.RAW_FISH &&
                e.getItem().getType() != Material.RAW_CHICKEN && e.getItem().getType() != Material.SPIDER_EYE &&
                e.getItem().getType() != Material.POISONOUS_POTATO)
            return;
        final Player player = e.getPlayer();
        final Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null)
            return;
        if (game.getState() != GameState.RUNNING)
            return;
        if (game.isSpectator(player))
            return;
        if (!YaBedwars.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
            return;
        if (e.getItem().getType() == Material.POTION)
            if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                (new BukkitRunnable() {
                    PotionEffect peffect;

                    int duration;

                    public void run() {
                        if (player.hasPotionEffect(PotionEffectType.INVISIBILITY) &&
                                this.duration < Objects.requireNonNull(EventListener.this.getPotionEffect(player)).getDuration())
                            YaBedwars.getInstance().getArenaManager().getArenas().get(game.getName()).getInvisiblePlayer()
                                    .hidePlayer(player);
                    }
                }).runTaskLater(YaBedwars.getInstance(), 1L);
            } else {
                (new BukkitRunnable() {
                    public void run() {
                        if (player.hasPotionEffect(PotionEffectType.INVISIBILITY))
                            YaBedwars.getInstance().getArenaManager().getArenas().get(game.getName()).getInvisiblePlayer()
                                    .hidePlayer(player);
                    }
                }).runTaskLater(YaBedwars.getInstance(), 1L);
            }
        (new BukkitRunnable() {
            public void run() {
                if (player.hasPotionEffect(PotionEffectType.INVISIBILITY) &&
                        Config.invisibility_player_hide_particles)
                    for (PotionEffect effect : player.getActivePotionEffects())
                        player.addPotionEffect(new PotionEffect(effect.getType(), effect.getDuration(),
                                effect.getAmplifier(), true, false), true);
            }
        }).runTaskLater(YaBedwars.getInstance(), 1L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (!Config.invisibility_player_enabled)
            return;
        if (e.isCancelled())
            return;
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player))
            return;
        Player player = (Player) e.getEntity();
        if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY))
            return;
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null)
            return;
        if (game.getState() != GameState.RUNNING)
            return;
        if (game.isSpectator(player))
            return;
        if (!YaBedwars.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
            return;
        if (Config.invisibility_player_damage_show_player) {
            YaBedwars.getInstance().getArenaManager().getArenas().get(game.getName()).getInvisiblePlayer()
                    .removePlayer(player);
        } else {
            YaBedwars.getInstance().getArenaManager().getArenas().get(game.getName()).getInvisiblePlayer()
                    .showPlayerArmor(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemMerge(ItemMergeEvent e) {
        for (Arena arena : YaBedwars.getInstance().getArenaManager().getArenas().values())
            arena.onItemMerge(e);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if (Config.hunger_change)
            return;
        if (!(e.getEntity() instanceof Player))
            return;
        Player player = (Player) e.getEntity();
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null)
            return;
        if (game.getState() != GameState.RUNNING)
            return;
        e.setFoodLevel(20);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
        if (!Config.clear_bottle)
            return;
        if (e.getItem().getType() != Material.POTION)
            return;
        final Player player = e.getPlayer();
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null)
            return;
        if (game.getState() != GameState.RUNNING)
            return;
        if (game.isSpectator(player))
            return;
        (new BukkitRunnable() {
            public void run() {
                if (player.getInventory().getItemInHand().getType() == Material.GLASS_BOTTLE)
                    player.getInventory().setItemInHand(new ItemStack(Material.AIR));
            }
        }).runTaskLater(YaBedwars.getInstance(), 0L);
    }



    @EventHandler(priority = EventPriority.HIGHEST)
    public void onOpenTeamSelection(BedwarsOpenTeamSelectionEvent e) {
        if (!Config.select_team_enabled)
            return;
        e.setCancelled(true);
        SelectTeam.openSelectTeam(e.getGame(), (Player) e.getPlayer());
    }

    @EventHandler
    public void onChangedWorld(PlayerChangedWorldEvent e) {
        YaBedwars.getInstance().getHolographicManager().remove(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        YaBedwars.getInstance().getHolographicManager().remove(e.getPlayer());
    }

    private void onPacketReceiving() {
        PacketAdapter packetAdapter = new PacketAdapter(YaBedwars.getInstance(), ListenerPriority.HIGHEST,
                PacketType.Play.Client.BLOCK_DIG, PacketType.Play.Client.WINDOW_CLICK) {
            public void onPacketReceiving(PacketEvent e) {
                final Player player = e.getPlayer();
                PacketContainer packet = e.getPacket();
                if (e.getPacketType() == PacketType.Play.Client.BLOCK_DIG) {
                    if (!packet.getPlayerDigTypes().read(0).equals(EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK))
                        return;
                    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
                    if (game == null || game.getState() != GameState.RUNNING || !game.isSpectator(player))
                        return;
                    e.setCancelled(true);
                    BlockPosition position = packet.getBlockPositionModifier().read(0);
                    Location location = new Location(e.getPlayer().getWorld(), position.getX(), position.getY(),
                            position.getZ());
                    location.getBlock().getState().update();
                } else if (e.getPacketType() == PacketType.Play.Client.WINDOW_CLICK) {
                    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
                    if (game == null || game.getState() != GameState.RUNNING || game.isSpectator(player))
                        return;
                    int slot = packet.getIntegers().read(1);
                    if (slot < 0)
                        return;
                    ItemStack itemStack = player.getOpenInventory().getItem(slot);
                    if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore())
                        return;
                    List<String> lore = itemStack.getItemMeta().getLore();
                    ItemStack leggings = player.getInventory().getLeggings();
                    ItemStack boots = player.getInventory().getBoots();
                    if (leggings == null || boots == null)
                        return;
                    if ((lore.contains("§a§r§m§o§r§0§0§1") && ((leggings.getType() == Material.CHAINMAIL_LEGGINGS &&
                            leggings.getType() == Material.CHAINMAIL_LEGGINGS) || (
                            leggings.getType() == Material.IRON_LEGGINGS &&
                                    leggings.getType() == Material.IRON_LEGGINGS) || (
                            leggings.getType() == Material.DIAMOND_LEGGINGS &&
                                    leggings.getType() == Material.DIAMOND_LEGGINGS))) || (
                            lore.contains("§a§r§m§o§r§0§0§2") && ((leggings.getType() == Material.IRON_LEGGINGS &&
                                    leggings.getType() == Material.IRON_LEGGINGS) || (
                                    leggings.getType() == Material.DIAMOND_LEGGINGS &&
                                            leggings.getType() == Material.DIAMOND_LEGGINGS))) || (
                            lore.contains("§a§r§m§o§r§0§0§3") && leggings.getType() == Material.DIAMOND_LEGGINGS &&
                                    leggings.getType() == Material.DIAMOND_LEGGINGS)) {
                        e.setCancelled(true);
                        (new BukkitRunnable() {
                            public void run() {
                                if (player.isOnline())
                                    player.updateInventory();
                            }
                        }).runTaskLater(YaBedwars.getInstance(), 1L);
                    }
                }
            }
        };
        ProtocolLibrary.getProtocolManager().addPacketListener(packetAdapter);
    }

    private void onPacketSending() {
        PacketAdapter packetAdapter = new PacketAdapter(YaBedwars.getInstance(), ListenerPriority.HIGHEST,
                PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE,
                PacketType.Play.Server.SCOREBOARD_OBJECTIVE, PacketType.Play.Server.SCOREBOARD_SCORE,
                PacketType.Play.Server.SCOREBOARD_TEAM) {
            public void onPacketSending(PacketEvent e) {
                PacketContainer packet = e.getPacket();
                if (e.getPacketType().equals(PacketType.Play.Server.SCOREBOARD_SCORE) && packet.getScoreboardActions().read(0).equals(EnumWrappers.ScoreboardAction.REMOVE) && packet.getStrings().read(1).equals("") && EventListener.this.getPlayer(packet.getStrings().read(0)) != null)
                    e.setCancelled(true);
            }
        };
        ProtocolLibrary.getProtocolManager().addPacketListener(packetAdapter);
    }

    private void destroyBlock(Game game, Team team) {
        Material type = team.getTargetHeadBlock().getBlock().getType();
        if (type.equals(game.getTargetMaterial()))
            if (type.equals(Material.BED_BLOCK)) {
                if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
                    team.getTargetFeetBlock().getBlock().setType(Material.AIR);
                } else {
                    team.getTargetHeadBlock().getBlock().setType(Material.AIR);
                }
            } else {
                team.getTargetHeadBlock().getBlock().setType(Material.AIR);
            }
    }

    private Player getPlayer(String name) {
        if (name == null)
            return null;
        Player player = Bukkit.getPlayer(name);
        if (player == null)
            return null;
        if (player.getName().equals(name))
            return player;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equals(name))
                return player;
        }
        return null;
    }

    private PotionEffect getPotionEffect(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.INVISIBILITY))
                return effect;
        }
        return null;
    }

    private Location toLocation(String loc) {
        try {
            String[] ary = loc.split(", ");
            if (Bukkit.getWorld(ary[0]) != null) {
                Location location = new Location(Bukkit.getWorld(ary[0]), Double.parseDouble(ary[1]),
                        Double.parseDouble(ary[2]), Double.parseDouble(ary[3]));
                if (ary.length > 4) {
                    location.setYaw(Float.parseFloat(ary[4]));
                    location.setPitch(Float.parseFloat(ary[5]));
                }
                return location;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

  @EventHandler
  public void onItemPickup(PlayerPickupItemEvent e) {
    int count;
    Game bw = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(e.getPlayer());
    if (bw == null)
      return;
    if (!Config.isGameEnabledXP(bw.getName()))
      return;
    Player p = e.getPlayer();
    Item entity = e.getItem();
    ItemStack stack = entity.getItemStack();
    if (stack.hasItemMeta() && stack.getItemMeta().getDisplayName().equals("§b§l&BedwarsXP_DropedXP")) {
      count = Integer.parseInt(stack.getItemMeta().getLore().get(0));
    } else {
      count = ResourceUtils.convertResToXP(stack);
    }
    if (count == 0)
      return;
    XPManager xpman = XPManager.getXPManager(bw.getName());
    if (Config.maxXP != 0 &&
            xpman.getXP(p) >= Config.maxXP) {
      e.setCancelled(true);
      entity.setPickupDelay(10);
      xpman.sendMaxXPMessage(p);
      return;
    }
    int added = xpman.getXP(p) + count;
    int leftXP = 0;
    if (Config.maxXP != 0 &&
            added > Config.maxXP) {
      leftXP = added - Config.maxXP;
      added = Config.maxXP;
    }
    xpman.setXP(p, added);
    p.playSound(p.getLocation(), SoundMachine.get("ORB_PICKUP", "ENTITY_EXPERIENCE_ORB_PICKUP"), 0.2F, 1.5F);
    xpman.sendXPMessage(p, count);
    if (leftXP > 0) {
      e.setCancelled(true);
      ItemStack s = stack.clone();
      ItemMeta meta = s.getItemMeta();
      meta.setDisplayName("§b§l&BedwarsXP_DropedXP");
      meta.setLore(Collections.singletonList(String.valueOf(leftXP)));
      s.setItemMeta(meta);
      entity.setItemStack(s);
      entity.setPickupDelay(10);
    } else {
      e.setCancelled(true);
      entity.remove();
    }
  }

  @EventHandler
  public void onAnvilOpen(InventoryOpenEvent e) {
    e.getPlayer();
    e.getInventory();
    Game bw = BedwarsRel.getInstance().getGameManager().getGameOfPlayer((Player) e.getPlayer());
    if (bw == null)
      return;
    if (!Config.isGameEnabledXP(bw.getName()))
      return;
    if (e.getInventory().getType().equals(InventoryType.ANVIL))
      e.setCancelled(true);
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent e) {
    Game bw = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(e.getEntity());
    if (bw == null)
      return;
    if (!Config.isGameEnabledXP(bw.getName()))
      return;
    XPManager xpman = XPManager.getXPManager(bw.getName());
    Player p = e.getEntity();
    int costed = (int) (xpman.getXP(p) * Config.deathCost);
    int dropped = 0;
    if (Config.deathDrop > 0.0D)
      dropped = (int) (costed * Config.deathDrop);
    BedwarsXPDeathDropXPEvent event = new BedwarsXPDeathDropXPEvent(bw.getName(), p, dropped, costed);
    Bukkit.getPluginManager().callEvent(event);
    costed = event.getXPCosted();
    dropped = event.getXPDropped();
    int to = xpman.getXP(p) - costed;
    if (to < 0)
      to = 0;
    e.setNewLevel(to);
    xpman.setXP(p, to);
    if (Config.deathDrop > 0.0D) {
      if (dropped < 1)
        return;
      ItemStack dropStack = new ItemStack(Material.EXP_BOTTLE, 1);
      ItemMeta meta = dropStack.getItemMeta();
      meta.setDisplayName("§b§l&BedwarsXP_DropedXP");
      meta.setLore(Collections.singletonList(String.valueOf(dropped)));
      meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, 1, true);
      dropStack.setItemMeta(meta);
      Item droppedItem = p.getWorld().dropItemNaturally(p.getLocation().add(0.0D, 1.0D, 0.0D), dropStack);
      droppedItem.setPickupDelay(40);
    }
  }

  @EventHandler
  public void onBedWarsStart(BedwarsGameStartEvent e) {
    if (e.isCancelled())
      return;
    if (!Config.isGameEnabledXP(e.getGame().getName()))
      return;
    ShopReplacer.replaceShop(e.getGame().getName(), Bukkit.getConsoleSender());
  }

  @EventHandler
  public void onBedWarsEnd(BedwarsGameEndEvent e) {
    if (!Config.isGameEnabledXP(e.getGame().getName()))
      return;
    XPManager.reset(e.getGame().getName());
  }

  @EventHandler
  public void onPlayerTeleport(PlayerTeleportEvent e) {
    final Game bw = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(e.getPlayer());
    if (bw == null)
      return;
    if (!Config.isGameEnabledXP(bw.getName()))
      return;
    final Player p = e.getPlayer();
    Bukkit.getScheduler().runTaskLater(YaBedwars.getInstance(), () -> XPManager.getXPManager(bw.getName()).updateXPBar(p), 5L);
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent e) {
    Game bw = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(e.getPlayer());
    if (bw == null)
      return;
    if (!Config.isGameEnabledXP(bw.getName()))
      return;
    XPManager.getXPManager(bw.getName()).updateXPBar(e.getPlayer());
  }
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInteract(PlayerInteractEvent e) {
    if (e.isCancelled()) return;
    if (e.getItem() == null || (
            e.getItem().getType() != Material.WATER_BUCKET && e.getItem().getType() != Material.LAVA_BUCKET) ||
            e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
    Player player = e.getPlayer();
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
    if (game == null) return;
    if (game.isSpectator(player) || player.getGameMode() == GameMode.SPECTATOR)
      return;
    game.getRegion().addPlacedBlock(e.getClickedBlock().getRelative(e.getBlockFace()), null);
    if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getClickedBlock().getType() != Material.ENDER_CHEST)
      return;
    if (game.isSpectator(player))
      return;
    e.setCancelled(false);
  }

  @EventHandler
  public void onLeave(BedwarsPlayerLeaveEvent e) {
    e.getPlayer().getEnderChest().clear();
  }

  @EventHandler
  public void onStart(BedwarsGameStartEvent e) {
    for (Player player : e.getGame().getPlayers())
      player.getEnderChest().clear();
  }
}
