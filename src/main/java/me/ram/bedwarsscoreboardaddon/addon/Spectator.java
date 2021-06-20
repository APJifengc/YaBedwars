package me.ram.bedwarsscoreboardaddon.addon;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameStartedEvent;
import io.github.bedwarsrel.events.BedwarsPlayerJoinEvent;
import io.github.bedwarsrel.events.BedwarsPlayerLeaveEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import ldcr.BedwarsXP.api.XPManager;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.LocationUtil;
import me.ram.bedwarsscoreboardaddon.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Spectator implements Listener {
  private final List<Player> players = new ArrayList<>();
  
  private ItemStack speeditem;
  
  private ItemStack joinitem;
  
  private final List<Material> resitems = new ArrayList<>();
  
  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    e.getPlayer().setFlySpeed(0.1F);
    e.getPlayer().removePotionEffect(PotionEffectType.SPEED);
    e.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
  }
  
  @EventHandler
  public void onLeave(BedwarsPlayerLeaveEvent e) {
    e.getPlayer().setFlySpeed(0.1F);
    e.getPlayer().removePotionEffect(PotionEffectType.SPEED);
    e.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
  }
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void onPickupItem(PlayerPickupItemEvent e) {
    if (!Config.spectator_enabled)
      return; 
    Player player = e.getPlayer();
    Game getGame = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
    if (getGame == null)
      return; 
    if (getGame.getPlayers().contains(player) && getGame.isSpectator(player)) {
      e.setCancelled(true);
      Item entity = e.getItem();
      if (this.resitems != null && this.resitems.contains(entity.getItemStack().getType())) {
        if (Bukkit.getPluginManager().isPluginEnabled("BedwarsXP"))
          XPManager.getXPManager(getGame.getName()).setXP(player, 0); 
        if (!entity.isDead())
          entity.remove(); 
        Item item = player.getWorld().dropItem(entity.getLocation(), entity.getItemStack());
        item.setVelocity(entity.getVelocity());
        item.setPickupDelay(0);
      } 
    } 
  }
  
  @EventHandler
  public void onDamage(EntityDamageEvent e) {
    if (e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
      Player player = (Player)e.getEntity();
      Game getGame = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
      if (getGame == null)
        return; 
      if (getGame.getPlayers().contains(player) && 
        getGame.isSpectator(player))
        player.teleport(player.getLocation().add(0.0D, 5.0D, 0.0D)); 
    } 
  }
  
  @EventHandler
  public void onInteractEntity(PlayerInteractEntityEvent e) {
    if (!Config.spectator_enabled)
      return; 
    Player player = e.getPlayer();
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
    if (game == null)
      return; 
    Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
    if (arena != null && arena.isOver().booleanValue())
      return; 
    if (!(e.getRightClicked() instanceof Player))
      return; 
    Player target = (Player)e.getRightClicked();
    if (!game.getPlayers().contains(player) || !game.getPlayers().contains(target))
      return; 
    if (game.getState() != GameState.RUNNING)
      return; 
    if (!game.isSpectator(player) || game.isSpectator(target))
      return; 
    player.setGameMode(GameMode.SPECTATOR);
    player.setSpectatorTarget(e.getRightClicked());
  }
  
  private List<Material> getResource() {
    List<Material> items = new ArrayList<>();
    ConfigurationSection config = BedwarsRel.getInstance().getConfig().getConfigurationSection("resource");
    for (String res : config.getKeys(false)) {
      List<Map<String, Object>> list = (List<Map<String, Object>>) BedwarsRel.getInstance().getConfig()
        .getList("resource." + res + ".item");
      for (Map<String, Object> resource : list) {
        ItemStack itemStack = ItemStack.deserialize(resource);
        items.add(itemStack.getType());
      } 
    } 
    return items;
  }
  
  @EventHandler
  public void onStarted(final BedwarsGameStartedEvent e) {
    for (Player player : e.getGame().getPlayers()) {
      player.setFlySpeed(0.1F);
      player.removePotionEffect(PotionEffectType.SPEED);
      player.removePotionEffect(PotionEffectType.INVISIBILITY);
    } 
    this.resitems.clear();
    this.resitems.addAll(getResource());
    (new BukkitRunnable() {
        public void run() {
          if (e.getGame().getState() == GameState.RUNNING) {
            if (Config.spectator_enabled)
              for (Player player : e.getGame().getPlayers()) {
                if (player.getSpectatorTarget() == null) {
                  if (Spectator.this.players.contains(player)) {
                    Spectator.this.players.remove(player);
                    if (!Config.spectator_quit_spectator_title.equals("") || 
                      !Config.spectator_quit_spectator_subtitle.equals("")) {
                      Utils.sendTitle(player, Integer.valueOf(1), Integer.valueOf(30), Integer.valueOf(1), Config.spectator_quit_spectator_title, 
                          Config.spectator_quit_spectator_subtitle);
                      player.setGameMode(GameMode.SURVIVAL);
                      player.setAllowFlight(true);
                      player.addPotionEffect(
                          new PotionEffect(PotionEffectType.INVISIBILITY, 2147483647, 0), 
                          true);
                    } 
                  } 
                  continue;
                } 
                if (player.getSpectatorTarget() instanceof Player) {
                  Player spectatorTarget = (Player)player.getSpectatorTarget();
                  if (!Spectator.this.players.contains(player))
                    if (e.getGame().getPlayers().contains(spectatorTarget)) {
                      if (e.getGame().getPlayers().contains(spectatorTarget) && 
                        !e.getGame().isSpectator(spectatorTarget) && 
                        e.getGame().isSpectator(player)) {
                        Spectator.this.players.add(player);
                        if (!Config.spectator_spectator_target_title.equals("") || 
                          !Config.spectator_spectator_target_subtitle.equals(""))
                          Utils.sendTitle(player, Integer.valueOf(1), Integer.valueOf(30), Integer.valueOf(1), 
                              Config.spectator_spectator_target_title
                              .replace("{player}", spectatorTarget.getName())
                              .replace("{color}", 
                                (CharSequence)e.getGame().getPlayerTeam(spectatorTarget)
                                .getChatColor())
                              .replace("{team}", 
                                e.getGame().getPlayerTeam(spectatorTarget)
                                .getName()), 
                              Config.spectator_spectator_target_subtitle
                              .replace("{player}", spectatorTarget.getName())
                              .replace("{color}", 
                                (CharSequence)e.getGame().getPlayerTeam(spectatorTarget)
                                .getChatColor())
                              .replace("{team}", e.getGame()
                                .getPlayerTeam(spectatorTarget).getName())); 
                      } else {
                        player.setSpectatorTarget(null);
                      } 
                    } else {
                      player.setSpectatorTarget(null);
                    }  
                  if (!e.getGame().getPlayers().contains(spectatorTarget))
                    player.setSpectatorTarget(null); 
                  continue;
                } 
                player.setSpectatorTarget(null);
              }  
          } else {
            cancel();
          } 
        }
      }).runTaskTimer(Main.getInstance(), 0L, 0L);
    (new BukkitRunnable() {
        public void run() {
          if (e.getGame().getState() == GameState.RUNNING) {
            if (Config.spectator_enabled && Config.spectator_speed_enabled) {
              ItemStack itemStack = new ItemStack(Material.getMaterial(Config.spectator_speed_item));
              ItemMeta itemMeta = itemStack.getItemMeta();
              itemMeta.setDisplayName(Config.spectator_speed_item_name);
              itemMeta.setLore(Config.spectator_speed_item_lore);
              itemStack.setItemMeta(itemMeta);
              Spectator.this.speeditem = itemStack;
              Game game = e.getGame();
              for (Player player : game.getPlayers()) {
                if (game.isSpectator(player))
                  player.getInventory().setItem(Config.spectator_speed_slot - 1, itemStack); 
              } 
            } 
            if (Config.spectator_enabled && Config.spectator_fast_join_enabled) {
              ItemStack itemStack = new ItemStack(Material.getMaterial(Config.spectator_fast_join_item));
              ItemMeta itemMeta = itemStack.getItemMeta();
              itemMeta.setDisplayName(Config.spectator_fast_join_item_name);
              itemMeta.setLore(Config.spectator_fast_join_item_lore);
              itemStack.setItemMeta(itemMeta);
              Spectator.this.joinitem = itemStack;
              Game game = e.getGame();
              for (Player player : game.getPlayers()) {
                if (game.isSpectator(player))
                  player.getInventory().setItem(Config.spectator_fast_join_slot - 1, itemStack); 
              } 
            } 
          } else {
            cancel();
          } 
        }
      }).runTaskTimer(Main.getInstance(), 0L, 5L);
    Timer logTimer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
          if (e.getGame().getState() == GameState.RUNNING) {
            if (Config.spectator_enabled) {
              Game game = e.getGame();
              for (Player player : game.getPlayers()) {
                if (game.isSpectator(player)) {
                  if (player.getLocation().getY() < 0.0D)
                    player.teleport(player.getLocation().add(0.0D, 128.0D, 0.0D)); 
                  for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), 2.0D, 3.5D, 
                      2.0D)) {
                    if (entity instanceof Item) {
                      Item item = (Item)entity;
                      if (Spectator.this.resitems.contains(item.getItemStack().getType()) || 
                        item.getItemStack().getType().equals(Material.EXP_BOTTLE)) {
                        if (player.getGameMode() != GameMode.SPECTATOR) {
                          player.teleport(LocationUtil.getPosition(player.getLocation(), 
                                item.getLocation()));
                          player.setVelocity(
                              LocationUtil.getPositionVector(player.getLocation(), item.getLocation())
                              .multiply(0.07D));
                        } 
                        break;
                      } 
                    } 
                    if (entity instanceof org.bukkit.entity.Fireball || entity instanceof org.bukkit.entity.WitherSkull || 
                      entity instanceof org.bukkit.entity.TNTPrimed) {
                      if (player.getGameMode() != GameMode.SPECTATOR) {
                        player.teleport(LocationUtil.getPosition(player.getLocation(), 
                              entity.getLocation()));
                        player.setVelocity(
                            LocationUtil.getPositionVector(player.getLocation(), entity.getLocation())
                            .multiply(0.07D));
                      } 
                      break;
                    } 
                  } 
                } 
              } 
            } 
          } else {
            cancel();
          } 
        }
      };
    logTimer.scheduleAtFixedRate(task, 500L, 10L);
  }
  
  @EventHandler
  public void onRespawn(PlayerRespawnEvent e) {
    final Player player = e.getPlayer();
    final Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
    if (game == null)
      return; 
    Team team = game.getPlayerTeam(player);
    if (Config.spectator_enabled && game.getState() == GameState.RUNNING && team != null && team.isDead(game)) {
      if (Config.spectator_centre_enabled) {
        World world = game.getRegion().getWorld();
        int i = 0;
        double x = 0.0D;
        double z = 0.0D;
        for (Team t : game.getTeams().values()) {
          if (t.getSpawnLocation().getWorld().getName().equals(world.getName())) {
            x += t.getSpawnLocation().getX();
            z += t.getSpawnLocation().getZ();
            i++;
          } 
        } 
        final Location location = new Location(world, x / Double.valueOf(i).doubleValue(), Config.spectator_centre_height, 
            z / Double.valueOf(i).doubleValue());
        (new BukkitRunnable() {
            public void run() {
              if (player.isOnline() && game.getState() == GameState.RUNNING && game.isSpectator(player)) {
                player.setVelocity(new Vector(0, 0, 0));
                player.teleport(location);
              } 
            }
          }).runTaskLater(Main.getInstance(), 1L);
      } 
      (new BukkitRunnable() {
          public void run() {
            if (player.isOnline() && game.getState() == GameState.RUNNING && game.isSpectator(player)) {
              player.setGameMode(GameMode.SURVIVAL);
              player.setAllowFlight(true);
              player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2147483647, 0), 
                  true);
            } 
          }
        }).runTaskLater(Main.getInstance(), 20L);
    } 
  }
  
  @EventHandler
  public void onJoinGame(BedwarsPlayerJoinEvent e) {
    final Game game = e.getGame();
    final Player player = e.getPlayer();
    Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
    if (Config.spectator_enabled && game.getState() == GameState.RUNNING && 
      !arena.getRejoin().getPlayers().containsKey(player.getName())) {
      if (Config.spectator_centre_enabled) {
        World world = game.getRegion().getWorld();
        int i = 0;
        double x = 0.0D;
        double z = 0.0D;
        for (Team team : game.getTeams().values()) {
          if (team.getSpawnLocation().getWorld().getName().equals(world.getName())) {
            x += team.getSpawnLocation().getX();
            z += team.getSpawnLocation().getZ();
            i++;
          } 
        } 
        final Location location = new Location(world, x / Double.valueOf(i).doubleValue(), Config.spectator_centre_height, 
            z / Double.valueOf(i).doubleValue());
        player.setVelocity(new Vector(0, 0, 0));
        player.teleport(location);
        (new BukkitRunnable() {
            public void run() {
              if (player.isOnline() && game.getPlayers().contains(player) && 
                game.getState() == GameState.RUNNING && game.isSpectator(player)) {
                player.setVelocity(new Vector(0, 0, 0));
                player.teleport(location);
              } 
            }
          }).runTaskLater(Main.getInstance(), 20L);
      } 
      (new BukkitRunnable() {
          public void run() {
            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2147483647, 0), true);
          }
        }).runTaskLater(Main.getInstance(), 20L);
    } 
  }
  
  @EventHandler
  public void onClick(InventoryClickEvent e) {
    if (!Config.spectator_enabled || !Config.spectator_speed_enabled)
      return; 
    Player player = (Player)e.getWhoClicked();
    Game getGame = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
    if (getGame == null)
      return; 
    if (!getGame.getPlayers().contains(player))
      return; 
    if (getGame.getState() != GameState.RUNNING)
      return; 
    if (!getGame.isSpectator(player))
      return; 
    Inventory inventory = e.getInventory();
    if (inventory.getTitle().equals(Config.spectator_speed_gui_title)) {
      e.setCancelled(true);
      int slot = e.getRawSlot();
      if (slot == 11) {
        player.setFlySpeed(0.1F);
        player.removePotionEffect(PotionEffectType.SPEED);
      } else if (slot == 12) {
        player.setFlySpeed(0.2F);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2147483647, 0), true);
      } else if (slot == 13) {
        player.setFlySpeed(0.3F);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2147483647, 1), true);
      } else if (slot == 14) {
        player.setFlySpeed(0.4F);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2147483647, 2), true);
      } else if (slot == 15) {
        player.setFlySpeed(0.5F);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2147483647, 3), true);
      } else {
        return;
      } 
      player.closeInventory();
    } 
    if (e.getCurrentItem() != null) {
      if (e.getCurrentItem().isSimilar(this.speeditem)) {
        e.setCancelled(true);
        openInventory(player);
      } 
      if (e.getCurrentItem().isSimilar(this.joinitem)) {
        e.setCancelled(true);
      } 
    } 
  }
  
  public void openInventory(Player player) {
    Inventory inventory = Bukkit.createInventory(null, 27, Config.spectator_speed_gui_title);
    ItemStack itemStack = new ItemStack(Material.LEATHER_BOOTS);
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(Config.spectator_speed_no_speed);
    itemStack.setItemMeta(itemMeta);
    inventory.setItem(11, itemStack);
    itemStack = new ItemStack(Material.CHAINMAIL_BOOTS);
    itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(Config.spectator_speed_speed_1);
    itemStack.setItemMeta(itemMeta);
    inventory.setItem(12, itemStack);
    itemStack = new ItemStack(Material.IRON_BOOTS);
    itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(Config.spectator_speed_speed_2);
    itemStack.setItemMeta(itemMeta);
    inventory.setItem(13, itemStack);
    itemStack = new ItemStack(Material.GOLD_BOOTS);
    itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(Config.spectator_speed_speed_3);
    itemStack.setItemMeta(itemMeta);
    inventory.setItem(14, itemStack);
    itemStack = new ItemStack(Material.DIAMOND_BOOTS);
    itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(Config.spectator_speed_speed_4);
    itemStack.setItemMeta(itemMeta);
    inventory.setItem(15, itemStack);
    player.openInventory(inventory);
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onOpen(InventoryOpenEvent e) {
    if (e.getInventory().getTitle().equals(Config.spectator_speed_gui_title))
      e.setCancelled(false); 
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInteract(PlayerInteractEvent e) {
    if (!Config.spectator_enabled || !Config.spectator_speed_enabled)
      return; 
    Player player = e.getPlayer();
    Game getGame = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
    if (getGame == null)
      return; 
    if (!getGame.getPlayers().contains(player))
      return; 
    if (getGame.getState() != GameState.RUNNING)
      return; 
    if (!getGame.isSpectator(player))
      return; 
    if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
      e.setCancelled(true); 
    if (this.players.contains(player) && (
      e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK))) {
      getGame.openSpectatorCompass(player);
      e.setCancelled(true);
      return;
    } 
    if (e.getItem() != null && e.getItem().isSimilar(this.speeditem) && (
      e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
      openInventory(player);
      e.setCancelled(true);
    } 
    if (e.getItem() != null && e.getItem().isSimilar(this.joinitem) && (
      e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
      e.setCancelled(true);
    } 
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInteractSpectator(PlayerInteractEvent e) {
    if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.isCancelled())
      return; 
    if (!Config.spectator_enabled)
      return; 
    Player player = e.getPlayer();
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
    if (game == null)
      return; 
    if (!game.getPlayers().contains(player))
      return; 
    if (game.getState() != GameState.RUNNING)
      return; 
    if (game.isSpectator(player))
      return; 
    ItemStack itemStack = e.getItem();
    if (itemStack == null)
      return; 
    if (!itemStack.getType().isBlock())
      return; 
    Location location = e.getClickedBlock().getRelative(e.getBlockFace()).getLocation().add(0.5D, 0.5D, 0.5D);
    for (Entity entity : location.getWorld().getNearbyEntities(location, 0.51D, 1.5D, 0.51D)) {
      if (entity instanceof Player) {
        Player p = (Player)entity;
        if (game.getPlayers().contains(p) && game.isSpectator(p))
          p.teleport(p.getLocation().clone().add(0.0D, 2.0D, 0.0D)); 
      } 
    } 
  }
}
