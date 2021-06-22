package com.yallage.yabedwars.addon;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class VoidProtection implements Listener {
    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (event.getPlayer().getLocation().getY() < 60) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getLocation().getY() < 60) {
            event.setCancelled(true);
        }
    }
}
