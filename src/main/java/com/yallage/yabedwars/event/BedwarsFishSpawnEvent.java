package com.yallage.yabedwars.event;

import io.github.bedwarsrel.game.Game;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BedwarsFishSpawnEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Game game;

    private final Location location;

    private Boolean cancelled = Boolean.FALSE;

    public BedwarsFishSpawnEvent(Game game, Location location) {
        this.game = game;
        this.location = location;
    }

    public Game getGame() {
        return this.game;
    }

    public Location getLocation() {
        return this.location;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
