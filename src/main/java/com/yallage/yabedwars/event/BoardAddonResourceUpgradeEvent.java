package com.yallage.yabedwars.event;

import io.github.bedwarsrel.game.Game;

import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BoardAddonResourceUpgradeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Game game;

    private List<String> upgrade;

    private Boolean cancelled = Boolean.FALSE;

    public BoardAddonResourceUpgradeEvent(Game game, List<String> upgrade) {
        this.game = game;
        this.upgrade = upgrade;
    }

    public Game getGame() {
        return this.game;
    }

    public List<String> getUpgrade() {
        return this.upgrade;
    }

    public void setUpgrade(List<String> upgrade) {
        this.upgrade = upgrade;
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
