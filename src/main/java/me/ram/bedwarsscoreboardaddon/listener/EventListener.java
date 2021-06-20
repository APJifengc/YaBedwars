package me.ram.bedwarsscoreboardaddon.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameEndEvent;
import io.github.bedwarsrel.events.BedwarsGameOverEvent;
import io.github.bedwarsrel.events.BedwarsGameStartedEvent;
import io.github.bedwarsrel.events.BedwarsOpenTeamSelectionEvent;
import io.github.bedwarsrel.events.BedwarsPlayerJoinedEvent;
import io.github.bedwarsrel.events.BedwarsPlayerKilledEvent;
import io.github.bedwarsrel.events.BedwarsPlayerLeaveEvent;
import io.github.bedwarsrel.events.BedwarsTargetBlockDestroyedEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.utils.ChatWriter;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.addon.SelectTeam;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.edit.EditGame;
import me.ram.bedwarsscoreboardaddon.events.BedwarsTeamDeadEvent;
import me.ram.bedwarsscoreboardaddon.utils.ScoreboardUtil;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
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
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

public class EventListener implements Listener {
  private Map<String, Map<Event, PacketListener>> deathevents;
  
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
    Main.getInstance().getArenaManager().addArena(game.getName(), arena);
    (new BukkitRunnable() {
        public void run() {
          if (Main.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
            ((Arena)Main.getInstance().getArenaManager().getArenas().get(game.getName())).getScoreBoard().updateScoreboard(); 
        }
      }).runTaskLater((Plugin)Main.getInstance(), 2L);
  }
  
  @EventHandler
  public void onOver(BedwarsGameOverEvent e) {
    Game game = e.getGame();
    if (Main.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
      ((Arena)Main.getInstance().getArenaManager().getArenas().get(game.getName())).onOver(e); 
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
    final Player player = e.getPlayer();
    String message = e.getMessage();
    final String[] args = message.split(" ");
    if (args[0].equalsIgnoreCase("/bwsbatp")) {
      e.setCancelled(true);
      if (args.length == 8 && player.hasPermission("bedwarsscoreboardaddon.shop.teleport")) {
        String loc = message.substring(10 + args[1].length(), message.length());
        Location location = toLocation(loc);
        if (location != null) {
          player.teleport(location);
          Main.getInstance().getHolographicManager().displayGameLocation(player, args[1]);
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
            }).runTask((Plugin)Main.getInstance());
        } catch (Exception exception) {} 
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
    for (Arena arena : Main.getInstance().getArenaManager().getArenas().values()) {
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
    if (Main.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
      ((Arena)Main.getInstance().getArenaManager().getArenas().get(game.getName())).onPlayerJoined(player); 
    if (game.getState() == GameState.WAITING)
      for (Arena arena : Main.getInstance().getArenaManager().getArenas().values())
        arena.getRejoin().removePlayer(player.getName());  
    Main.getInstance().getHolographicManager().remove(player);
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
      Bukkit.getPluginManager().callEvent((Event)new BedwarsTeamDeadEvent(game, team));
      if (Config.rejoin_enabled) {
        destroyBlock(game, team);
        if (Main.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
          ((Arena)Main.getInstance().getArenaManager().getArenas().get(game.getName())).getRejoin().removeTeam(team.getName()); 
      } 
    } 
    if (Main.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
      ((Arena)Main.getInstance().getArenaManager().getArenas().get(game.getName())).onPlayerLeave(e.getPlayer()); 
    if (player.isOnline()) {
      ProtocolManager m = ProtocolLibrary.getProtocolManager();
      try {
        PacketContainer packet = m.createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        packet.getIntegers().write(0, Integer.valueOf(1));
        packet.getStrings().write(0, "bwsba-game-list");
        packet.getStrings().write(1, "bwsba-game-list");
        m.sendServerPacket(player, packet);
      } catch (Exception ex) {
        ex.printStackTrace();
      } 
      try {
        PacketContainer packet = m.createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        packet.getIntegers().write(0, Integer.valueOf(1));
        packet.getStrings().write(0, "bwsba-game-name");
        packet.getStrings().write(1, "bwsba-game-name");
        m.sendServerPacket(player, packet);
      } catch (Exception ex) {
        ex.printStackTrace();
      } 
    } 
    Main.getInstance().getHolographicManager().remove(player);
    ScoreboardUtil.removePlayer(player);
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onEnd(BedwarsGameEndEvent e) {
    Game game = e.getGame();
    if (Main.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
      ((Arena)Main.getInstance().getArenaManager().getArenas().get(game.getName())).onEnd(); 
    Main.getInstance().getArenaManager().removeArena(game.getName());
    game.kickAllPlayers();
  }
  
  @EventHandler
  public void onTargetBlockDestroyed(BedwarsTargetBlockDestroyedEvent e) {
    final Game game = e.getGame();
    if (Main.getInstance().getArenaManager().getArenas().containsKey(game.getName())) {
      ((Arena)Main.getInstance().getArenaManager().getArenas().get(game.getName())).onTargetBlockDestroyed(e);
      (new BukkitRunnable() {
          public void run() {
            if (Main.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
              ((Arena)Main.getInstance().getArenaManager().getArenas().get(game.getName())).getScoreBoard().updateScoreboard(); 
          }
        }).runTaskLater((Plugin)Main.getInstance(), 1L);
    } 
  }
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void onDeath(PlayerDeathEvent e) {
    Player player = e.getEntity();
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
    if (game == null)
      return; 
    if (Main.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
      ((Arena)Main.getInstance().getArenaManager().getArenas().get(game.getName())).onDeath(player); 
    Team team = game.getPlayerTeam(player);
    if (team == null)
      return; 
    int players = 0;
    for (Player p : team.getPlayers()) {
      if (!game.isSpectator(p))
        players++; 
    } 
    if (game.getState() == GameState.RUNNING && team != null && players <= 1 && !game.isSpectator(player) && team.isDead(game)) {
      Bukkit.getPluginManager().callEvent((Event)new BedwarsTeamDeadEvent(game, team));
      if (Main.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
        ((Arena)Main.getInstance().getArenaManager().getArenas().get(game.getName())).getRejoin().removeTeam(team.getName()); 
    } 
  }
  
  @EventHandler
  public void onRespawn(PlayerRespawnEvent e) {
    Player player = e.getPlayer();
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
    if (game != null && Main.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
      ((Arena)Main.getInstance().getArenaManager().getArenas().get(game.getName())).onRespawn(player); 
  }
  
  @EventHandler
  public void onPlayerKilled(BedwarsPlayerKilledEvent e) {
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(e.getKiller());
    if (game != null && Main.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
      ((Arena)Main.getInstance().getArenaManager().getArenas().get(game.getName())).onPlayerKilled(e); 
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
    PacketAdapter packetAdapter = new PacketAdapter((Plugin)Main.getInstance(), 
        new PacketType[] { PacketType.Play.Server.CHAT }) {
        public void onPacketSending(PacketEvent e) {
          Player p = e.getPlayer();
          WrappedChatComponent chat = (WrappedChatComponent)e.getPacket().getChatComponents().read(0);
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
              ChatWriter.pluginMessage(ChatColor.GOLD + BedwarsRel._l((CommandSender)p, "ingame.player.killed", 
                  (Map)ImmutableMap.of("killer", 
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
    ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)packetAdapter);
    return (PacketListener)packetAdapter;
  }
  
  @EventHandler
  public void onDisable(PluginDisableEvent e) {
    if (e.getPlugin().equals(Main.getInstance()) || e.getPlugin().equals(BedwarsRel.getInstance())) {
      for (Arena arena : Main.getInstance().getArenaManager().getArenas().values())
        arena.onDisable(e); 
      Main.getInstance().getHolographicManager().removeAll();
    } 
  }
  
  @EventHandler
  public void onArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
    for (Arena arena : Main.getInstance().getArenaManager().getArenas().values())
      arena.onArmorStandManipulate(e); 
  }
  
  @EventHandler
  public void onClick(InventoryClickEvent e) {
    for (Arena arena : Main.getInstance().getArenaManager().getArenas().values())
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
        if (npcs.contains((new StringBuilder(String.valueOf(npc.getId()))).toString()))
          gamenpcs.add(npc); 
      } 
      for (NPC npc : gamenpcs)
        CitizensAPI.getNPCRegistry().deregister(npc); 
      yamlConfiguration.set("npcs", new ArrayList());
      try {
        yamlConfiguration.save(folder);
      } catch (IOException iOException) {}
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
    if (!Main.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
      return; 
    if (e.getItem().getType() == Material.POTION)
      if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
        (new BukkitRunnable() {
            PotionEffect peffect;
            
            int duration;
            
            public void run() {
              if (player.hasPotionEffect(PotionEffectType.INVISIBILITY) && 
                this.duration < EventListener.this.getPotionEffect(player, PotionEffectType.INVISIBILITY).getDuration())
                ((Arena)Main.getInstance().getArenaManager().getArenas().get(game.getName())).getInvisiblePlayer()
                  .hidePlayer(player); 
            }
          }).runTaskLater((Plugin)Main.getInstance(), 1L);
      } else {
        (new BukkitRunnable() {
            public void run() {
              if (player.hasPotionEffect(PotionEffectType.INVISIBILITY))
                ((Arena)Main.getInstance().getArenaManager().getArenas().get(game.getName())).getInvisiblePlayer()
                  .hidePlayer(player); 
            }
          }).runTaskLater((Plugin)Main.getInstance(), 1L);
      }  
    (new BukkitRunnable() {
        public void run() {
          if (player.hasPotionEffect(PotionEffectType.INVISIBILITY) && 
            Config.invisibility_player_hide_particles)
            for (PotionEffect effect : player.getActivePotionEffects())
              player.addPotionEffect(new PotionEffect(effect.getType(), effect.getDuration(), 
                    effect.getAmplifier(), true, false), true);  
        }
      }).runTaskLater((Plugin)Main.getInstance(), 1L);
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onDamageByEntity(EntityDamageByEntityEvent e) {
    if (!Config.invisibility_player_enabled)
      return; 
    if (e.isCancelled())
      return; 
    if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player))
      return; 
    Player player = (Player)e.getEntity();
    if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY))
      return; 
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
    if (game == null)
      return; 
    if (game.getState() != GameState.RUNNING)
      return; 
    if (game.isSpectator(player))
      return; 
    if (!Main.getInstance().getArenaManager().getArenas().containsKey(game.getName()))
      return; 
    if (Config.invisibility_player_damage_show_player) {
      ((Arena)Main.getInstance().getArenaManager().getArenas().get(game.getName())).getInvisiblePlayer()
        .removePlayer(player);
    } else {
      ((Arena)Main.getInstance().getArenaManager().getArenas().get(game.getName())).getInvisiblePlayer()
        .showPlayerArmor(player);
    } 
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onItemMerge(ItemMergeEvent e) {
    for (Arena arena : Main.getInstance().getArenaManager().getArenas().values())
      arena.onItemMerge(e); 
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onFoodLevelChange(FoodLevelChangeEvent e) {
    if (Config.hunger_change)
      return; 
    if (!(e.getEntity() instanceof Player))
      return; 
    Player player = (Player)e.getEntity();
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
      }).runTaskLater((Plugin)Main.getInstance(), 0L);
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInteract(PlayerInteractEvent e) {
    if (e.isCancelled())
      return; 
    if (e.getItem() == null || (
      e.getItem().getType() != Material.WATER_BUCKET && e.getItem().getType() != Material.LAVA_BUCKET) || 
      e.getAction() != Action.RIGHT_CLICK_BLOCK)
      return; 
    Player player = e.getPlayer();
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
    if (game == null)
      return; 
    if (game.isSpectator(player) || player.getGameMode() == GameMode.SPECTATOR)
      return; 
    game.getRegion().addPlacedBlock(e.getClickedBlock().getRelative(e.getBlockFace()), null);
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onOpenTeamSelection(BedwarsOpenTeamSelectionEvent e) {
    if (!Config.select_team_enabled)
      return; 
    e.setCancelled(true);
    SelectTeam.openSelectTeam(e.getGame(), (Player)e.getPlayer());
  }
  
  @EventHandler
  public void onChangedWorld(PlayerChangedWorldEvent e) {
    Main.getInstance().getHolographicManager().remove(e.getPlayer());
  }
  
  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    Main.getInstance().getHolographicManager().remove(e.getPlayer());
  }
  
  private void onPacketReceiving() {
    PacketAdapter packetAdapter = new PacketAdapter((Plugin)Main.getInstance(), ListenerPriority.HIGHEST, 
        new PacketType[] { PacketType.Play.Client.BLOCK_DIG, PacketType.Play.Client.WINDOW_CLICK }) {
        public void onPacketReceiving(PacketEvent e) {
          final Player player = e.getPlayer();
          PacketContainer packet = e.getPacket();
          if (e.getPacketType() == PacketType.Play.Client.BLOCK_DIG) {
            if (!((EnumWrappers.PlayerDigType)packet.getPlayerDigTypes().read(0)).equals(EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK))
              return; 
            Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
            if (game == null || game.getState() != GameState.RUNNING || !game.isSpectator(player))
              return; 
            e.setCancelled(true);
            BlockPosition position = (BlockPosition)packet.getBlockPositionModifier().read(0);
            Location location = new Location(e.getPlayer().getWorld(), position.getX(), position.getY(), 
                position.getZ());
            location.getBlock().getState().update();
          } else if (e.getPacketType() == PacketType.Play.Client.WINDOW_CLICK) {
            Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
            if (game == null || game.getState() != GameState.RUNNING || game.isSpectator(player))
              return; 
            int slot = ((Integer)packet.getIntegers().read(1)).intValue();
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
                }).runTaskLater((Plugin)Main.getInstance(), 1L);
            } 
          } 
        }
      };
    ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)packetAdapter);
  }
  
  private void onPacketSending() {
    PacketAdapter packetAdapter = new PacketAdapter((Plugin)Main.getInstance(), ListenerPriority.HIGHEST, 
        new PacketType[] { PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE, 
          PacketType.Play.Server.SCOREBOARD_OBJECTIVE, PacketType.Play.Server.SCOREBOARD_SCORE, 
          PacketType.Play.Server.SCOREBOARD_TEAM }) {
        public void onPacketSending(PacketEvent e) {
          PacketContainer packet = e.getPacket();
          if (e.getPacketType().equals(PacketType.Play.Server.SCOREBOARD_SCORE) && (
            (EnumWrappers.ScoreboardAction)packet.getScoreboardActions().read(0)).equals(EnumWrappers.ScoreboardAction.REMOVE) && (
            (String)packet.getStrings().read(1)).equals("") && EventListener.this.getPlayer((String)packet.getStrings().read(0)) != null)
            e.setCancelled(true); 
        }
      };
    ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)packetAdapter);
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
  
  private PotionEffect getPotionEffect(Player player, PotionEffectType type) {
    for (PotionEffect effect : player.getActivePotionEffects()) {
      if (effect.getType().equals(type))
        return effect; 
    } 
    return null;
  }
  
  private Location toLocation(String loc) {
    try {
      String[] ary = loc.split(", ");
      if (Bukkit.getWorld(ary[0]) != null) {
        Location location = new Location(Bukkit.getWorld(ary[0]), Double.valueOf(ary[1]).doubleValue(), 
            Double.valueOf(ary[2]).doubleValue(), Double.valueOf(ary[3]).doubleValue());
        if (ary.length > 4) {
          location.setYaw(Float.valueOf(ary[4]).floatValue());
          location.setPitch(Float.valueOf(ary[5]).floatValue());
        } 
        return location;
      } 
    } catch (Exception e) {
      return null;
    } 
    return null;
  }
}
