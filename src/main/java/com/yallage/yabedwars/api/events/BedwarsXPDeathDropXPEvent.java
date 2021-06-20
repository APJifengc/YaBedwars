package com.yallage.yabedwars.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BedwarsXPDeathDropXPEvent extends Event {
    public HandlerList getHandlers() {
        return new HandlerList();
    }

    private String game;

    private final Player player;

    private int deathCosted;

    private int deathDropped;

    public BedwarsXPDeathDropXPEvent(String game, Player p, int dropped, int costed) {
        this.game = game;
        this.player = p;
        this.deathCosted = costed;
        this.deathDropped = dropped;
    }

    public String getGameName() {
        return this.game;
    }

    public Player getDeadPlayer() {
        return this.player;
    }

    public int getXPCosted() {
        return this.deathCosted;
    }

    public void setXPCosted(int drop) {
        this.deathCosted = drop;
    }

    public int getXPDropped() {
        return this.deathDropped;
    }

    public void setXPDropped(int deathDropped) {
        this.deathDropped = deathDropped;
    }
}
