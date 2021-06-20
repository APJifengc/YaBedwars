package me.ram.bedwarsscoreboardaddon.addon;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.events.BoardAddonPlayerRespawnEvent;
import me.ram.bedwarsscoreboardaddon.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Respawn {
  private Game game;
  
  private List<Player> players;
  
  public Respawn(Game game) {
    this.players = new ArrayList<>();
    this.game = game;
  }
  
  public Game getGame() {
    return this.game;
  }
  
  public void onRespawn(final Player player) {
    if (!Config.respawn_centre_enabled)
      return; 
    if (!this.players.contains(player))
      return; 
    World world = this.game.getRegion().getWorld();
    int i = 0;
    double x = 0.0D;
    double z = 0.0D;
    for (Team team : this.game.getTeams().values()) {
      if (team.getSpawnLocation().getWorld().getName().equals(world.getName())) {
        x += team.getSpawnLocation().getX();
        z += team.getSpawnLocation().getZ();
        i++;
      } 
    } 
    final Location location = new Location(world, x / Double.valueOf(i).doubleValue(), Config.respawn_centre_height, 
        z / Double.valueOf(i).doubleValue());
    (new BukkitRunnable() {
        public void run() {
          if (Respawn.this.players.contains(player)) {
            player.setVelocity(new Vector(0, 0, 0));
            player.teleport(location);
          } 
        }
      }).runTaskLater((Plugin)Main.getInstance(), 1L);
  }
  
  public void onDeath(final Player player, boolean rejoin) {
    if (!Config.respawn_enabled || this.game.isSpectator(player) || (this.game.getPlayerTeam(player).isDead(this.game) && !rejoin) || 
      this.players.contains(player))
      return; 
    this.players.add(player);
    int ateams = 0;
    for (Team team : this.game.getTeams().values()) {
      if (!team.isDead(this.game) || team.getPlayers().size() > 0)
        ateams++; 
    } 
    if (ateams <= 1)
      return; 
    final List<Player> invplayers = new ArrayList<>();
    final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    if (Config.invisibility_player_enabled)
      (new BukkitRunnable() {
          public void run() {
            if (player.isOnline() && Respawn.this.players.contains(player)) {
              for (Player p : Respawn.this.game.getPlayers()) {
                if (!p.getUniqueId().equals(player.getUniqueId()) && 
                  p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                  PacketContainer packet = protocolManager
                    .createPacket(PacketType.Play.Server.ENTITY_DESTROY);
                  packet.getIntegerArrays().write(0, new int[] { p.getEntityId() });
                  try {
                    protocolManager.sendServerPacket(player, packet);
                    if (!invplayers.contains(p))
                      invplayers.add(p); 
                  } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                  } 
                } 
              } 
            } else {
              cancel();
            } 
          }
        }).runTaskTimer((Plugin)Main.getInstance(), 1L, 1L); 
    player.setGameMode(GameMode.SPECTATOR);
    player.setVelocity(new Vector(0, 0, 0));
    (new BukkitRunnable() {
        public void run() {
          if (!player.isOnline()) {
            cancel();
            return;
          } 
          if (!player.isDead()) {
            if (!Config.respawn_respawning_title.equals("") || !Config.respawn_respawning_subtitle.equals(""))
              Utils.sendTitle(player, Integer.valueOf(0), Integer.valueOf(50), Integer.valueOf(0), 
                  Config.respawn_respawning_title.replace("{respawntime}", (
                    new StringBuilder(String.valueOf(Config.respawn_respawn_delay))).toString()), 
                  Config.respawn_respawning_subtitle.replace("{respawntime}", (
                    new StringBuilder(String.valueOf(Config.respawn_respawn_delay))).toString())); 
            (new BukkitRunnable() {
                int respawntime = Config.respawn_respawn_delay;
                
                public void run() {
                  if (!Respawn.this.players.contains(player)) {
                    player.setGameMode(GameMode.SURVIVAL);
                    cancel();
                    return;
                  } 
                  if (Respawn.this.game.getPlayerTeam(player) == null) {
                    player.setGameMode(GameMode.SURVIVAL);
                    cancel();
                    return;
                  } 
                  if (this.respawntime <= Config.respawn_respawn_delay && this.respawntime != 0) {
                    if (!Config.respawn_respawning_title.equals("") || 
                      !Config.respawn_respawning_subtitle.equals(""))
                      Utils.sendTitle(player, Integer.valueOf(3), Integer.valueOf(50), Integer.valueOf(0), 
                          Config.respawn_respawning_title.replace("{respawntime}", (new StringBuilder(String.valueOf(this.respawntime))).toString()), 
                          Config.respawn_respawning_subtitle.replace("{respawntime}", (
                            new StringBuilder(String.valueOf(this.respawntime))).toString())); 
                    if (!Config.respawn_respawning_message.equals(""))
                      player.sendMessage(Config.respawn_respawning_message.replace("{respawntime}", (
                            new StringBuilder(String.valueOf(this.respawntime))).toString())); 
                  } 
                  if (this.respawntime <= 0) {
                    cancel();
                    Respawn.this.players.remove(player);
                    player.setVelocity(new Vector(0, 0, 0));
                    player.teleport(Respawn.this.game.getPlayerTeam(player).getSpawnLocation());
                    player.setGameMode(GameMode.SURVIVAL);
                    if (!Config.respawn_respawned_title.equals("") || 
                      !Config.respawn_respawned_subtitle.equals(""))
                      Utils.sendTitle(player, Integer.valueOf(10), Integer.valueOf(50), Integer.valueOf(10), Config.respawn_respawned_title, 
                          Config.respawn_respawned_subtitle); 
                    if (!Config.respawn_respawned_message.equals(""))
                      player.sendMessage(Config.respawn_respawned_message); 
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5, 0), true);
                    Bukkit.getPluginManager().callEvent((Event)new BoardAddonPlayerRespawnEvent(Respawn.this.game, player));
                    if (Config.invisibility_player_enabled)
                      for (Player p : invplayers) {
                        if (p.isOnline()) {
                          PacketContainer packet = protocolManager
                            .createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
                          packet.getIntegers().write(0, Integer.valueOf(p.getEntityId()));
                          packet.getUUIDs().write(0, p.getUniqueId());
                          if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
                            try {
                              Method method = Utils.getNMSClass("MathHelper").getMethod("floor", double.class);
                              packet.getIntegers().write(1, (Integer) method.invoke(null, new Object[] { Double.valueOf(p.getLocation().getX() * 32.0D) }));
                              packet.getIntegers().write(2, (Integer) method.invoke(null, new Object[] { Double.valueOf(p.getLocation().getY() * 32.0D) }));
                              packet.getIntegers().write(3, (Integer) method.invoke(null, new Object[] { Double.valueOf(p.getLocation().getZ() * 32.0D) }));
                            } catch (Exception e) {
                              e.printStackTrace();
                            } 
                          } else {
                            packet.getDoubles().write(0, Double.valueOf(p.getLocation().getX()));
                            packet.getDoubles().write(1, Double.valueOf(p.getLocation().getY()));
                            packet.getDoubles().write(2, Double.valueOf(p.getLocation().getZ()));
                          } 
                          packet.getBytes().write(0, 
                              Byte.valueOf((byte)(int)(p.getLocation().getYaw() * 256.0F / 360.0F)));
                          packet.getBytes().write(1, 
                              Byte.valueOf((byte)(int)(p.getLocation().getPitch() * 256.0F / 360.0F)));
                          packet.getDataWatcherModifier().write(0, 
                              WrappedDataWatcher.getEntityWatcher((Entity)p));
                          try {
                            protocolManager.sendServerPacket(player, packet);
                          } catch (InvocationTargetException e1) {
                            e1.printStackTrace();
                          } 
                        } 
                      }  
                    return;
                  } 
                  this.respawntime--;
                }
              }).runTaskTimer((Plugin)Main.getInstance(), 30L, 21L);
            cancel();
          } 
        }
      }).runTaskTimer((Plugin)Main.getInstance(), 0L, 0L);
  }
}
