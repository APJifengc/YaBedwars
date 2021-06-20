package com.yallage.yabedwars.events;

import io.github.bedwarsrel.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BoardAddonPlayerOpenItemShopEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Game game;

    private final Player player;

    private Boolean cancelled = Boolean.FALSE;

    public BoardAddonPlayerOpenItemShopEvent(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    public Game getGame() {
        return this.game;
    }

    public Player getPlayer() {
        return this.player;
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
