package me.ram.bedwarsitemaddon.listener;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameOverEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import me.ram.bedwarsitemaddon.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class EventListener implements Listener {
  @EventHandler
  public void onOver(BedwarsGameOverEvent e) {
    e.getGame().setOver(true);
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
        }).runTaskLaterAsynchronously(Main.getInstance(), 1L);
    } 
  }
}
