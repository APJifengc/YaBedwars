package me.ram.bedwarsscoreboardaddon.events;

import io.github.bedwarsrel.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BoardAddonPlayerShootWitherBowEvent extends Event {
  private static HandlerList handlers = new HandlerList();
  
  private Game game;
  
  private Player player;
  
  private WitherSkull witherSkull;
  
  private Boolean cancelled = Boolean.valueOf(false);
  
  public BoardAddonPlayerShootWitherBowEvent(Game game, Player player, WitherSkull witherSkull) {
    this.game = game;
    this.player = player;
    this.witherSkull = witherSkull;
  }
  
  public Game getGame() {
    return this.game;
  }
  
  public Player getPlayer() {
    return this.player;
  }
  
  public WitherSkull getWitherSkull() {
    return this.witherSkull;
  }
  
  public boolean isCancelled() {
    return this.cancelled.booleanValue();
  }
  
  public void setCancelled(boolean cancel) {
    this.cancelled = Boolean.valueOf(cancel);
  }
  
  public HandlerList getHandlers() {
    return handlers;
  }
  
  public static HandlerList getHandlerList() {
    return handlers;
  }
}
