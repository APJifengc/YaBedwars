package me.ram.bedwarsitemaddon.items;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameOverEvent;
import io.github.bedwarsrel.events.BedwarsGameStartEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.ram.bedwarsitemaddon.EnumItem;
import me.ram.bedwarsitemaddon.Main;
import me.ram.bedwarsitemaddon.config.Config;
import me.ram.bedwarsitemaddon.event.BedwarsUseItemEvent;
import me.ram.bedwarsitemaddon.utils.LocationUtil;
import me.ram.bedwarsitemaddon.utils.TakeItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Trampoline implements Listener {
  private final Map<String, List<Location>> vblocks = new HashMap<>();
  
  private Map<String, List<Location>> blocks = new HashMap<>();
  
  private final Map<String, List<Player>> nofall = new HashMap<>();
  
  private final Map<Player, Long> cooldown = new HashMap<>();
  
  @EventHandler
  public void onOver(BedwarsGameOverEvent e) {
    Game game = e.getGame();
    for (Location location : this.blocks.get(game.getName()))
      location.getBlock().setType(Material.AIR); 
    this.blocks.put(game.getName(), new ArrayList<>());
  }
  
  @EventHandler
  public void onDisable(PluginDisableEvent e) {
    if (e.getPlugin().equals(BedwarsRel.getInstance()) || e.getPlugin().equals(Main.getInstance())) {
      for (List<Location> locations : this.blocks.values()) {
        for (Location location : locations)
          location.getBlock().setType(Material.AIR); 
      } 
      this.blocks = new HashMap<>();
    } 
  }
  
  @EventHandler
  public void onPlace(BlockPlaceEvent e) {
    if (!Config.items_trampoline_enabled)
      return; 
    Player player = e.getPlayer();
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
    if (game == null)
      return; 
    if (!game.getPlayers().contains(player))
      return; 
    if (game.getState() == GameState.RUNNING && 
      e.getBlock().getType() == (new ItemStack(Material.valueOf(Config.items_trampoline_item))).getType())
      e.setCancelled(true); 
  }
  
  @EventHandler
  public void onInteract(PlayerInteractEvent e) {
    if (!Config.items_trampoline_enabled)
      return; 
    Player player = e.getPlayer();
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
    if (e.getItem() == null || game == null)
      return; 
    if (game.isOverSet())
      return; 
    if (!game.getPlayers().contains(player))
      return; 
    if (game.getState() == GameState.RUNNING && (
      e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && e.getItem().getType() == (new ItemStack(Material.valueOf(Config.items_trampoline_item))).getType())
      if (System.currentTimeMillis() - this.cooldown.getOrDefault(player, Long.valueOf(0L)).longValue() <= (Config.items_trampoline_cooldown * 1000)) {
        e.setCancelled(true);
        player.sendMessage(Config.message_cooling.replace("{time}", (new StringBuilder(String.valueOf(((Config.items_trampoline_cooldown * 1000) - System.currentTimeMillis() + this.cooldown.getOrDefault(player, Long.valueOf(0L)).longValue()) / 1000L + 1L))).toString()));
      } else {
        ItemStack stack = e.getItem();
        Location location1 = player.getLocation();
        Location location2 = player.getLocation();
        int r = 0;
        if (Config.items_trampoline_size <= 1) {
          r = 3;
        } else if (Config.items_trampoline_size == 2) {
          r = 4;
        } else if (Config.items_trampoline_size >= 3) {
          r = 5;
        } 
        location1.add(r, 0.0D, r);
        location2.add(-r, 1.0D, -r);
        if (enoughSpace(location1, location2)) {
          BedwarsUseItemEvent bedwarsUseItemEvent = new BedwarsUseItemEvent(game, player, EnumItem.Trampoline, stack);
          Bukkit.getPluginManager().callEvent(bedwarsUseItemEvent);
          if (!bedwarsUseItemEvent.isCancelled()) {
            this.cooldown.put(player, Long.valueOf(System.currentTimeMillis()));
            setTrampolineBlock(game, player.getLocation(), player, Config.items_trampoline_size);
            player.teleport(player.getLocation().add(0.0D, 2.0D, 0.0D));
            player.setVelocity(new Vector(0.0D, Config.items_trampoline_velocity, 0.0D));
            TakeItemUtil.TakeItem(player, stack);
          } 
        } else {
          player.sendMessage(Config.items_trampoline_lack_space);
          return;
        } 
        e.setCancelled(true);
      }  
  }
  
  private void setTrampolineBlock(Game game, Location location, Player player, int size) {
    if (size <= 1) {
      setBlock(game, LocationUtil.getLocation(location, 2, 0, 2), Material.FENCE, (byte)0);
      setBlock(game, LocationUtil.getLocation(location, -2, 0, 2), Material.FENCE, (byte)0);
      setBlock(game, LocationUtil.getLocation(location, 2, 0, -2), Material.FENCE, (byte)0);
      setBlock(game, LocationUtil.getLocation(location, -2, 0, -2), Material.FENCE, (byte)0);
      setBlock(game, LocationUtil.getLocation(location, 2, 1, 2), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 1, 1, 2), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 0, 1, 2), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -1, 1, 2), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -2, 1, 2), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 2, 1, 1), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 1, 1, 1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 0, 1, 1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -1, 1, 1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -2, 1, 1), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 2, 1, 0), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 1, 1, 0), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 0, 1, 0), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -1, 1, 0), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -2, 1, 0), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 2, 1, -1), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 1, 1, -1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 0, 1, -1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -1, 1, -1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -2, 1, -1), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 2, 1, -2), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 1, 1, -2), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 0, 1, -2), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -1, 1, -2), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -2, 1, -2), Material.WOOL, (byte)11);
    } else if (size == 2) {
      setBlock(game, LocationUtil.getLocation(location, 3, 0, 3), Material.FENCE, (byte)0);
      setBlock(game, LocationUtil.getLocation(location, -3, 0, 3), Material.FENCE, (byte)0);
      setBlock(game, LocationUtil.getLocation(location, 3, 0, -3), Material.FENCE, (byte)0);
      setBlock(game, LocationUtil.getLocation(location, -3, 0, -3), Material.FENCE, (byte)0);
      setBlock(game, LocationUtil.getLocation(location, 3, 1, 3), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 2, 1, 3), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 1, 1, 3), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 0, 1, 3), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -1, 1, 3), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -2, 1, 3), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -3, 1, 3), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 3, 1, 2), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 3, 1, 1), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 3, 1, 0), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 3, 1, -1), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 3, 1, -2), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -3, 1, 2), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -3, 1, 1), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -3, 1, 0), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -3, 1, -1), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -3, 1, -2), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 3, 1, -3), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 2, 1, -3), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 1, 1, -3), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 0, 1, -3), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -1, 1, -3), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -2, 1, -3), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -3, 1, -3), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 2, 1, 2), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 1, 1, 2), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 0, 1, 2), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -1, 1, 2), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -2, 1, 2), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 2, 1, 1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 1, 1, 1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 0, 1, 1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -1, 1, 1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -2, 1, 1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 2, 1, 0), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 1, 1, 0), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 0, 1, 0), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -1, 1, 0), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -2, 1, 0), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 2, 1, -1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 1, 1, -1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 0, 1, -1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -1, 1, -1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -2, 1, -1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 2, 1, -2), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 1, 1, -2), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 0, 1, -2), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -1, 1, -2), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -2, 1, -2), Material.WOOL, (byte)15);
    } else if (size >= 3) {
      setBlock(game, LocationUtil.getLocation(location, 4, 0, 4), Material.FENCE, (byte)0);
      setBlock(game, LocationUtil.getLocation(location, -4, 0, 4), Material.FENCE, (byte)0);
      setBlock(game, LocationUtil.getLocation(location, 4, 0, -4), Material.FENCE, (byte)0);
      setBlock(game, LocationUtil.getLocation(location, -4, 0, -4), Material.FENCE, (byte)0);
      setBlock(game, LocationUtil.getLocation(location, 4, 1, 4), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 3, 1, 4), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 2, 1, 4), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 1, 1, 4), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 0, 1, 4), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -1, 1, 4), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -2, 1, 4), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -3, 1, 4), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -4, 1, 4), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 4, 1, 3), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 4, 1, 2), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 4, 1, 1), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 4, 1, 0), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 4, 1, -1), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 4, 1, -2), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 4, 1, -3), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 4, 1, -4), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -4, 1, 3), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -4, 1, 2), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -4, 1, 1), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -4, 1, 0), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -4, 1, -1), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -4, 1, -2), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -4, 1, -3), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 3, 1, -4), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 2, 1, -4), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 1, 1, -4), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 0, 1, -4), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -1, 1, -4), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -2, 1, -4), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -3, 1, -4), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, -4, 1, -4), Material.WOOL, (byte)11);
      setBlock(game, LocationUtil.getLocation(location, 3, 1, 3), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 2, 1, 3), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 1, 1, 3), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 0, 1, 3), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -1, 1, 3), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -2, 1, 3), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -3, 1, 3), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 3, 1, 2), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 3, 1, 1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 3, 1, 0), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 3, 1, -1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 3, 1, -2), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -3, 1, 2), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -3, 1, 1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -3, 1, 0), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -3, 1, -1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -3, 1, -2), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 3, 1, -3), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 2, 1, -3), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 1, 1, -3), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 0, 1, -3), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -1, 1, -3), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -2, 1, -3), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -3, 1, -3), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 2, 1, 2), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 1, 1, 2), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 0, 1, 2), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -1, 1, 2), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -2, 1, 2), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 2, 1, 1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 1, 1, 1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 0, 1, 1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -1, 1, 1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -2, 1, 1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 2, 1, 0), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 1, 1, 0), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 0, 1, 0), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -1, 1, 0), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -2, 1, 0), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 2, 1, -1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 1, 1, -1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 0, 1, -1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -1, 1, -1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -2, 1, -1), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 2, 1, -2), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 1, 1, -2), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, 0, 1, -2), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -1, 1, -2), Material.WOOL, (byte)15);
      setBlock(game, LocationUtil.getLocation(location, -2, 1, -2), Material.WOOL, (byte)15);
    } 
    if (!this.nofall.get(game.getName()).contains(player))
      this.nofall.get(game.getName()).add(player);
    player.setAllowFlight(true);
  }
  
  private void setBlock(final Game game, final Location location, Material material, byte data) {
    Location loc = location.getBlock().getLocation();
    for (Player player : LocationUtil.getLocationPlayers(loc))
      player.teleport(player.getLocation().add(0.0D, 2.0D, 0.0D)); 
    for (Player player : LocationUtil.getLocationPlayers(loc.add(0.0D, -1.0D, 0.0D)))
      player.teleport(player.getLocation().add(0.0D, 1.0D, 0.0D)); 
    final Block block = location.getBlock();
    block.setType(material);
    block.setData(data);
    this.blocks.get(game.getName()).add(location);
    if (data == 15 || data == 11)
      this.vblocks.get(game.getName()).add(location);
    (new BukkitRunnable() {
        public void run() {
          block.setType(Material.AIR);
          Trampoline.this.vblocks.get(game.getName()).remove(location);
          Trampoline.this.blocks.get(game.getName()).remove(location);
        }
      }).runTaskLater(Main.getInstance(), (Config.items_trampoline_staytime * 20));
  }
  
  private boolean enoughSpace(Location location1, Location location2) {
    boolean enough = true;
    Location location = location1.getBlock().getLocation();
    for (Iterator<Integer> iterator = getAllNumber((int)location1.getX(), (int)location2.getX()).iterator(); iterator.hasNext(); ) {
      int X = iterator.next().intValue();
      location.setX(X);
      for (Iterator<Integer> iterator1 = getAllNumber((int)location1.getY(), (int)location2.getY()).iterator(); iterator1.hasNext(); ) {
        int Y = iterator1.next().intValue();
        location.setY(Y);
        for (Iterator<Integer> iterator2 = getAllNumber((int)location1.getZ(), (int)location2.getZ()).iterator(); iterator2.hasNext(); ) {
          int Z = iterator2.next().intValue();
          location.setZ(Z);
          if (location.getBlock() != null && 
            location.getBlock().getType() != Material.AIR)
            enough = false; 
        } 
      } 
    } 
    return enough;
  }
  
  private List<Integer> getAllNumber(int a, int b) {
    List<Integer> nums = new ArrayList<>();
    int min = a;
    int max = b;
    if (a > b) {
      min = b;
      max = a;
    } 
    for (int i = min; i < max + 1; i++)
      nums.add(Integer.valueOf(i)); 
    return nums;
  }
  
  @EventHandler
  private void onDamage(EntityDamageEvent e) {
    if (!(e.getEntity() instanceof Player))
      return; 
    Player player = (Player)e.getEntity();
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
    if (game == null)
      return; 
    if (game.getState() != GameState.RUNNING)
      return; 
    if (!game.getPlayers().contains(player))
      return; 
    if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
      Location location = player.getLocation();
      location.add(0.0D, -1.0D, 0.0D);
      for (Location b : this.vblocks.get(game.getName())) {
        if ((int)location.getX() == (int)b.getX() && (int)location.getY() == (int)b.getY() && (int)location.getZ() == (int)b.getZ())
          e.setCancelled(true); 
      } 
    } 
  }
  
  @EventHandler
  private void onStart(BedwarsGameStartEvent e) {
    for (Player player : e.getGame().getPlayers()) {
      this.cooldown.remove(player);
    } 
    final Game game = e.getGame();
    this.vblocks.put(game.getName(), new ArrayList<>());
    this.blocks.put(game.getName(), new ArrayList<>());
    this.nofall.put(game.getName(), new ArrayList<>());
    (new BukkitRunnable() {
        public void run() {
          if (game.getState() == GameState.RUNNING) {
            for (Player player : game.getPlayers()) {
              if (!game.isSpectator(player)) {
                Location location = player.getLocation().add(0.0D, -1.0D, 0.0D);
                boolean fb = true;
                for (Location b : Trampoline.this.vblocks.get(game.getName())) {
                  if ((int)location.getX() == (int)b.getX() && (int)location.getY() == (int)b.getY() && (int)location.getZ() == (int)b.getZ()) {
                    player.setVelocity(new Vector(0.0D, Config.items_trampoline_velocity, 0.0D));
                    if (!Trampoline.this.nofall.get(game.getName()).contains(player))
                      Trampoline.this.nofall.get(game.getName()).add(player);
                    player.setAllowFlight(true);
                    fb = false;
                  } 
                } 
                if (fb) {
                  Block block = location.getBlock();
                  Material mate = block.getType();
                  if (mate != null && 
                    mate != Material.AIR && Trampoline.this.nofall.get(game.getName()).contains(player)) {
                    Trampoline.this.nofall.get(game.getName()).remove(player);
                    if (player.getGameMode() != GameMode.SPECTATOR)
                      (new BukkitRunnable() {
                          public void run() {
                            player.setAllowFlight(false);
                          }
                        }).runTaskLater(Main.getInstance(), 10L); 
                  } 
                } 
              } 
            } 
          } else {
            cancel();
          } 
        }
      }).runTaskTimer(Main.getInstance(), 0L, 0L);
  }
}
