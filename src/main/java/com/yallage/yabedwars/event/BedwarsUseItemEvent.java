package com.yallage.yabedwars.event;

import io.github.bedwarsrel.game.Game;
import com.yallage.yabedwars.EnumItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class BedwarsUseItemEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Game game;

    private final Player player;

    private final EnumItem itemtype;

    private final ItemStack consumeitem;

    private Boolean cancelled = Boolean.FALSE;

    public BedwarsUseItemEvent(Game game, Player player, EnumItem itemtype, ItemStack consumeitem) {
        this.game = game;
        this.player = player;
        this.itemtype = itemtype;
        this.consumeitem = consumeitem;
    }

    public Game getGame() {
        return this.game;
    }

    public Player getPlayer() {
        return this.player;
    }

    public EnumItem getItemType() {
        return this.itemtype;
    }

    public ItemStack getConsumeItem() {
        return this.consumeitem;
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
