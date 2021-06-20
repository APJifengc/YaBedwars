package me.ram.bedwarsscoreboardaddon.events;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BedwarsTeamDeadEvent extends Event {
  private static HandlerList handlers = new HandlerList();
  
  private Game game;
  
  private Team team;
  
  public BedwarsTeamDeadEvent(Game game, Team team) {
    this.game = game;
    this.team = team;
  }
  
  public Game getGame() {
    return this.game;
  }
  
  public Team getTeam() {
    return this.team;
  }
  
  public HandlerList getHandlers() {
    return handlers;
  }
  
  public static HandlerList getHandlerList() {
    return handlers;
  }
}
