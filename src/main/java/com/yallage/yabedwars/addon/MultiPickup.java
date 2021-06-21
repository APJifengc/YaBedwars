package com.yallage.yabedwars.addon;

import com.yallage.yabedwars.config.Config;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class MultiPickup implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (event.getRemaining() == -50) return;
        if (Config.resources.containsKey(event.getItem().getItemStack().getType())) {
            Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(event.getPlayer());
            Location location = event.getItem().getLocation();
            location.getWorld().getNearbyEntities(location, 0.5, 0.5, 0.5).forEach(e -> {
                if (e instanceof Player) {
                    Player player = (Player) e;
                    if (player != event.getPlayer() && !game.isSpectator(player)) {
                        PlayerPickupItemEvent newEvent = new PlayerPickupItemEvent(player, event.getItem(), -50);
                        Bukkit.getPluginManager().callEvent(newEvent);
                        if (!newEvent.isCancelled()) {
                            player.getInventory().addItem(event.getItem().getItemStack());
                        }
                    }
                }
            });
        }
    }
}
