package me.ram.bedwarsscoreboardaddon.addon;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsTargetBlockDestroyedEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.ResourceSpawner;
import io.github.bedwarsrel.game.Team;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.api.HolographicAPI;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Holographic {
  private final Game game;
  
  private final List<HolographicAPI> ablocks;
  
  private final List<HolographicAPI> atitles;
  
  private final List<HolographicAPI> btitles;
  
  private final Map<String, HolographicAPI> pbtitles;
  
  private final ResourceUpgrade resourceupgrade;
  
  private final HashMap<HolographicAPI, Location> armorloc = new HashMap<>();
  
  private final HashMap<HolographicAPI, Boolean> armorupward = new HashMap<>();
  
  private final HashMap<HolographicAPI, Integer> armoralgebra = new HashMap<>();
  
  public Holographic(final Game game, ResourceUpgrade resourceupgrade) {
    this.game = game;
    this.ablocks = new ArrayList<>();
    this.atitles = new ArrayList<>();
    this.btitles = new ArrayList<>();
    this.pbtitles = new HashMap<>();
    this.resourceupgrade = resourceupgrade;
    if (Config.holographic_resource_enabled)
      for (String r : Config.holographic_resource)
        setArmorStand(game, r);  
    (new BukkitRunnable() {
        public void run() {
          for (Team team : game.getTeams().values()) {
            Location location = team.getTargetHeadBlock().clone().add(0.5D, 0.0D, 0.5D);
            final HolographicAPI holo = new HolographicAPI(location.clone().add(0.0D, -1.25D, 0.0D), 
                Config.holographic_bedtitle_bed_alive);
            for (Player player : team.getPlayers())
              holo.display(player); 
            Holographic.this.pbtitles.put(team.getName(), holo);
            (new BukkitRunnable() {
                public void run() {
                  if (game.getState() != GameState.RUNNING || team.isDead(game)) {
                    cancel();
                    holo.remove();
                  } 
                }
              }).runTaskTimer(Main.getInstance(), 1L, 1L);
          } 
        }
      }).runTaskLater(Main.getInstance(), 20L);
    (new BukkitRunnable() {
        public void run() {
          if (game.getState() != GameState.RUNNING || game.getPlayers().size() < 1) {
            cancel();
            for (HolographicAPI holo : Holographic.this.ablocks)
              holo.remove(); 
            for (HolographicAPI holo : Holographic.this.atitles)
              holo.remove(); 
            for (HolographicAPI holo : Holographic.this.btitles)
              holo.remove(); 
            for (HolographicAPI holo : Holographic.this.pbtitles.values())
              holo.remove(); 
          } 
        }
      }).runTaskTimer(Main.getInstance(), 1L, 1L);
  }
  
  public Game getGame() {
    return this.game;
  }
  
  public void onTargetBlockDestroyed(BedwarsTargetBlockDestroyedEvent e) {
    if (Config.holographic_bed_title_enabled) {
      Team team = e.getTeam();
      final Game game = e.getGame();
      if (this.pbtitles.containsKey(team.getName()))
        this.pbtitles.get(team.getName()).remove();
      Location loc = team.getTargetHeadBlock().clone().add(0.0D, -1.0D, 0.0D);
      if (loc.getX() == loc.getBlock().getLocation().getX())
        loc.add(0.5D, 0.0D, 0.0D); 
      if (loc.getZ() == loc.getBlock().getLocation().getZ())
        loc.add(0.0D, 0.0D, 0.5D); 
      if (!loc.getBlock().getChunk().isLoaded())
        loc.getBlock().getChunk().load(true); 
      final HolographicAPI holo = new HolographicAPI(loc, Config.holographic_bedtitle_bed_destroyed.replace("{player}", 
            game.getPlayerTeam(e.getPlayer()).getChatColor() + e.getPlayer().getName()));
      this.btitles.add(holo);
      for (Player player : game.getPlayers())
        holo.display(player); 
      (new BukkitRunnable() {
          public void run() {
            if (game.getState() == GameState.RUNNING) {
              if (!holo.getLocation().getBlock().getChunk().isLoaded())
                holo.getLocation().getBlock().getChunk().load(true); 
            } else {
              cancel();
            } 
          }
        }).runTaskTimer(Main.getInstance(), 0L, 0L);
    } 
  }
  
  private void setArmorStand(final Game game, String res) {
    for (ResourceSpawner spawner : game.getResourceSpawners()) {
      for (ItemStack itemStack : spawner.getResources()) {
        if (itemStack.getType() == Material.getMaterial(
            Main.getInstance().getConfig().getInt("holographic.resource.resources." + res + ".item"))) {
          if (!spawner.getLocation().getBlock().getChunk().isLoaded())
            spawner.getLocation().getBlock().getChunk().load(true); 
          final HolographicAPI holo = new HolographicAPI(spawner.getLocation().clone().add(0.0D, Main.getInstance()
                .getConfig().getDouble("holographic.resource.resources." + res + ".height"), 0.0D), null);
          holo.setEquipment(Arrays.asList(new ItemStack(Material.getMaterial(Main.getInstance().getConfig()
                      .getInt("holographic.resource.resources." + res + ".block")))));
          (new BukkitRunnable() {
              public void run() {
                for (Player player : game.getPlayers())
                  holo.display(player); 
              }
            }).runTaskLater(Main.getInstance(), 20L);
          ArrayList<String> titles = new ArrayList<>();
          Iterator<String> iterator = Main.getInstance().getConfig().getStringList("holographic.resource.resources." + res + ".title").iterator();
          while (iterator.hasNext()) {
            String title = iterator.next();
            titles.add(ColorUtil.color(title));
          } 
          setArmorStandRun(game, holo, titles, itemStack);
        } 
      } 
    } 
  }
  
  private void setArmorStandRun(final Game game, final HolographicAPI holo, ArrayList<String> titles, ItemStack itemStack) {
    Collections.reverse(titles);
    Location aslocation = holo.getLocation().clone().add(0.0D, 0.35D, 0.0D);
    for (String title : titles) {
      setTitle(game, aslocation, title, itemStack);
      aslocation.add(0.0D, 0.4D, 0.0D);
    } 
    this.ablocks.add(holo);
    (new BukkitRunnable() {
        double y;
        
        public void run() {
          if (game.getState() == GameState.RUNNING) {
            if (!holo.getLocation().getBlock().getChunk().isLoaded())
              holo.getLocation().getBlock().getChunk().load(true); 
            Holographic.this.moveArmorStand(holo, this.y, game);
          } else {
            Holographic.this.armorloc.remove(holo);
            Holographic.this.armorupward.remove(holo);
            Holographic.this.armoralgebra.remove(holo);
            holo.remove();
            Holographic.this.ablocks.remove(holo);
            cancel();
          } 
        }
      }).runTaskTimer(Main.getInstance(), 1L, 1L);
  }
  
  private void setTitle(final Game game, Location location, final String title, final ItemStack itemStack) {
    if (!location.getBlock().getChunk().isLoaded())
      location.getBlock().getChunk().load(true); 
    final HolographicAPI holo = new HolographicAPI(location, " ");
    this.atitles.add(holo);
    (new BukkitRunnable() {
        public void run() {
          for (Player player : game.getPlayers())
            holo.display(player); 
        }
      }).runTaskLater(Main.getInstance(), 20L);
    (new BukkitRunnable() {
        public void run() {
          if (game.getState() == GameState.RUNNING) {
            if (!holo.getLocation().getBlock().getChunk().isLoaded())
              holo.getLocation().getBlock().getChunk().load(true); 
            String customName = title;
            customName = customName.replace("{level}", Holographic.this.resourceupgrade.getLevel().get(itemStack.getType()));
            for (Material sitem : Holographic.this.resourceupgrade.getSpawnTime().keySet()) {
              if (itemStack.getType() == sitem)
                customName = customName.replace("{generate_time}", 
                    Holographic.this.resourceupgrade.getSpawnTime().get(sitem).toString());
            } 
            holo.setTitle(customName);
          } else {
            holo.remove();
            Holographic.this.atitles.remove(holo);
            cancel();
            return;
          } 
        }
      }).runTaskTimer(Main.getInstance(), 5L, 5L);
  }
  
  public void onPlayerLeave(Player player) {
    if (player.isOnline())
      for (HolographicAPI holo : this.pbtitles.values())
        holo.destroy(player);  
  }
  
  public void onPlayerJoin(final Player player) {
    if (this.game.getState() != GameState.RUNNING)
      return; 
    (new BukkitRunnable() {
        public void run() {
          if (Holographic.this.game.getState() == GameState.RUNNING && player.isOnline() && Holographic.this.game.getPlayers().contains(player)) {
            for (HolographicAPI holo : Holographic.this.ablocks)
              holo.display(player); 
            for (HolographicAPI holo : Holographic.this.atitles)
              holo.display(player); 
            for (HolographicAPI holo : Holographic.this.btitles)
              holo.display(player); 
            Arena arena = Main.getInstance().getArenaManager().getArena(Holographic.this.game.getName());
            if (arena.getRejoin().getPlayers().containsKey(player.getName())) {
              Team team = Holographic.this.game.getPlayerTeam(player);
              if (team != null && !team.isDead(Holographic.this.game))
                Holographic.this.pbtitles.get(team.getName()).display(player);
            } 
          } 
        }
      }).runTaskLater(Main.getInstance(), 10L);
  }
  
  public void remove() {
    for (HolographicAPI holo : this.ablocks)
      holo.remove(); 
    for (HolographicAPI holo : this.atitles)
      holo.remove(); 
    for (HolographicAPI holo : this.btitles)
      holo.remove(); 
    for (HolographicAPI holo : this.pbtitles.values())
      holo.remove(); 
  }
  
  public void onArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
    Player player = e.getPlayer();
    Game getGame = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
    if (getGame == null)
      return; 
    if (!getGame.getPlayers().contains(player))
      return; 
    if (getGame.getState() != GameState.WAITING && getGame.getState() == GameState.RUNNING)
      e.setCancelled(true); 
  }
  
  private void moveArmorStand(HolographicAPI holo, double height, Game game) {
    if (!this.armorloc.containsKey(holo))
      this.armorloc.put(holo, holo.getLocation().clone()); 
    if (!this.armorupward.containsKey(holo))
      this.armorupward.put(holo, Boolean.valueOf(true)); 
    if (!this.armoralgebra.containsKey(holo))
      this.armoralgebra.put(holo, Integer.valueOf(0)); 
    this.armoralgebra.put(holo, Integer.valueOf(this.armoralgebra.get(holo).intValue() + 1));
    Location location = this.armorloc.get(holo);
    if (location.getY() >= height + 0.3D) {
      this.armoralgebra.put(holo, Integer.valueOf(0));
      this.armorupward.put(holo, Boolean.valueOf(false));
    } else if (location.getY() <= height - 0.3D) {
      this.armoralgebra.put(holo, Integer.valueOf(0));
      this.armorupward.put(holo, Boolean.valueOf(true));
    } 
    Integer algebra = this.armoralgebra.get(holo);
    if (39 > algebra.intValue()) {
      if (this.armorupward.get(holo).booleanValue()) {
        location.setY(location.getY() + 0.015D);
      } else {
        location.setY(location.getY() - 0.015D);
      } 
    } else if (algebra.intValue() >= 50) {
      this.armoralgebra.put(holo, Integer.valueOf(0));
      this.armorupward.put(holo, Boolean.valueOf(!this.armorupward.get(holo).booleanValue()));
    } 
    Float turn = Float.valueOf(1.0F);
    if (!this.armorupward.get(holo).booleanValue())
      turn = Float.valueOf(-turn.floatValue()); 
    Float changeyaw = Float.valueOf(0.0F);
    if (algebra.intValue() == 1 || algebra.intValue() == 40) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 2.0F * turn.floatValue());
    } else if (algebra.intValue() == 2 || algebra.intValue() == 39) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 3.0F * turn.floatValue());
    } else if (algebra.intValue() == 3 || algebra.intValue() == 38) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 4.0F * turn.floatValue());
    } else if (algebra.intValue() == 4 || algebra.intValue() == 37) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 5.0F * turn.floatValue());
    } else if (algebra.intValue() == 5 || algebra.intValue() == 36) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 6.0F * turn.floatValue());
    } else if (algebra.intValue() == 6 || algebra.intValue() == 35) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 7.0F * turn.floatValue());
    } else if (algebra.intValue() == 7 || algebra.intValue() == 34) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 8.0F * turn.floatValue());
    } else if (algebra.intValue() == 8 || algebra.intValue() == 33) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 9.0F * turn.floatValue());
    } else if (algebra.intValue() == 9 || algebra.intValue() == 32) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 10.0F * turn.floatValue());
    } else if (algebra.intValue() == 10 || algebra.intValue() == 31) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 11.0F * turn.floatValue());
    } else if (algebra.intValue() == 11 || algebra.intValue() == 30) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 11.0F * turn.floatValue());
    } else if (algebra.intValue() == 12 || algebra.intValue() == 29) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 12.0F * turn.floatValue());
    } else if (algebra.intValue() == 13 || algebra.intValue() == 28) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 12.0F * turn.floatValue());
    } else if (algebra.intValue() == 14 || algebra.intValue() == 27) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 13.0F * turn.floatValue());
    } else if (algebra.intValue() == 15 || algebra.intValue() == 26) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 13.0F * turn.floatValue());
    } else if (algebra.intValue() == 16 || algebra.intValue() == 25) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 14.0F * turn.floatValue());
    } else if (algebra.intValue() == 17 || algebra.intValue() == 24) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 14.0F * turn.floatValue());
    } else if (algebra.intValue() == 18 || algebra.intValue() == 23) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 15.0F * turn.floatValue());
    } else if (algebra.intValue() == 19 || algebra.intValue() == 22) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 15.0F * turn.floatValue());
    } else if (algebra.intValue() == 20 || algebra.intValue() == 21) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 16.0F * turn.floatValue());
    } else if (algebra.intValue() == 41) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 2.0F * turn.floatValue());
    } else if (algebra.intValue() == 42) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 2.0F * turn.floatValue());
    } else if (algebra.intValue() == 43) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 2.0F * turn.floatValue());
    } else if (algebra.intValue() == 44) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + 1.0F * turn.floatValue());
    } else if (algebra.intValue() == 45) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + -1.0F * turn.floatValue());
    } else if (algebra.intValue() == 46) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + -1.0F * turn.floatValue());
    } else if (algebra.intValue() == 47) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + -2.0F * turn.floatValue());
    } else if (algebra.intValue() == 48) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + -2.0F * turn.floatValue());
    } else if (algebra.intValue() == 49) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + -2.0F * turn.floatValue());
    } else if (algebra.intValue() == 50) {
      changeyaw = Float.valueOf(changeyaw.floatValue() + -2.0F * turn.floatValue());
    } 
    double yaw = location.getYaw();
    yaw += changeyaw.floatValue() * Config.holographic_resource_speed;
    yaw = (yaw > 360.0D) ? (yaw - 360.0D) : yaw;
    yaw = (yaw < -360.0D) ? (yaw + 360.0D) : yaw;
    location.setYaw((float)yaw);
    holo.teleport(location);
  }
}
