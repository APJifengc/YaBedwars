package me.ram.bedwarsscoreboardaddon.events;

import io.github.bedwarsrel.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BoardAddonPlayerRespawnEvent extends Event {
  private static HandlerList handlers = new HandlerList();
  
  private Game game;
  
  private Player player;
  
  public BoardAddonPlayerRespawnEvent(Game game, Player player) {
    this.game = game;
    this.player = player;
  }
  
  public Game getGame() {
    return this.game;
  }
  
  public Player getPlayer() {
    return this.player;
  }
  
  public HandlerList getHandlers() {
    return handlers;
  }
  
  public static HandlerList getHandlerList() {
    return handlers;
  }
}
