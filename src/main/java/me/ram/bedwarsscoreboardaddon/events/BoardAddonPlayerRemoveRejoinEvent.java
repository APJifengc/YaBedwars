package me.ram.bedwarsscoreboardaddon.events;

import io.github.bedwarsrel.game.Game;
import me.ram.bedwarsscoreboardaddon.addon.Rejoin;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BoardAddonPlayerRemoveRejoinEvent extends Event {
  private static HandlerList handlers = new HandlerList();
  
  private Game game;
  
  private String player;
  
  private Rejoin rejoin;
  
  public BoardAddonPlayerRemoveRejoinEvent(Game game, String player, Rejoin rejoin) {
    this.game = game;
    this.player = player;
    this.rejoin = rejoin;
  }
  
  public Game getGame() {
    return this.game;
  }
  
  public String getPlayer() {
    return this.player;
  }
  
  public Rejoin getRejoin() {
    return this.rejoin;
  }
  
  public HandlerList getHandlers() {
    return handlers;
  }
  
  public static HandlerList getHandlerList() {
    return handlers;
  }
}
