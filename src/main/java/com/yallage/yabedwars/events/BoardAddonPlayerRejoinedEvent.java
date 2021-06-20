package com.yallage.yabedwars.events;

import io.github.bedwarsrel.game.Game;
import com.yallage.yabedwars.addon.Rejoin;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BoardAddonPlayerRejoinedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Game game;

    private final Player player;

    private final Rejoin rejoin;

    public BoardAddonPlayerRejoinedEvent(Game game, Player player, Rejoin rejoin) {
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

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
