package me.ram.bedwarsenderchest;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameOverEvent;
import io.github.bedwarsrel.events.BedwarsGameStartEvent;
import io.github.bedwarsrel.events.BedwarsPlayerLeaveEvent;
import io.github.bedwarsrel.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
  public void onEnable() {
    Bukkit.getPluginManager().registerEvents(this, (Plugin)this);
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInteract(PlayerInteractEvent e) {
    if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getClickedBlock().getType() != Material.ENDER_CHEST)
      return; 
    Player player = e.getPlayer();
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
    if (game == null || game.isSpectator(player))
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
  
  @EventHandler
  public void onOver(BedwarsGameOverEvent e) {
    for (Player player : e.getGame().getPlayers())
      player.getEnderChest().clear(); 
  }
}
