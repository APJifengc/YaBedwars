package me.ram.bedwarsscoreboardaddon.events;

import io.github.bedwarsrel.game.Game;
import me.ram.bedwarsscoreboardaddon.addon.Rejoin;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BoardAddonPlayerAddRejoinEvent extends Event {
  private static final HandlerList handlers = new HandlerList();
  
  private final Game game;
  
  private final Player player;
  
  private Boolean cancelled = Boolean.valueOf(false);
  
  private final Rejoin rejoin;
  
  public BoardAddonPlayerAddRejoinEvent(Game game, Player player, Rejoin rejoin) {
    this.game = game;
    this.player = player;
    this.rejoin = rejoin;
  }
  
  public Game getGame() {
    return this.game;
  }
  
  public Player getPlayer() {
    return this.player;
  }
  
  public Rejoin getRejoin() {
    return this.rejoin;
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
