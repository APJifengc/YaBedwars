package me.ram.bedwarsscoreboardaddon.events;

import io.github.bedwarsrel.game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BoardAddonDeathModeEvent extends Event {
  private static final HandlerList handlers = new HandlerList();
  
  private final Game game;
  
  private Boolean cancelled = Boolean.valueOf(false);
  
  public BoardAddonDeathModeEvent(Game game) {
    this.game = game;
  }
  
  public Game getGame() {
    return this.game;
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
