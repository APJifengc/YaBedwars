package com.yallage.yabedwars.items;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameStartEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;

import java.util.HashMap;
import java.util.Map;

import com.yallage.yabedwars.EnumItem;
import com.yallage.yabedwars.YaBedwars;
import com.yallage.yabedwars.config.Config;
import com.yallage.yabedwars.event.BedwarsUseItemEvent;
import com.yallage.yabedwars.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class LightTNT implements Listener {
    private final Map<Player, Long> cooldown = new HashMap<>();

    @EventHandler
    public void onStart(BedwarsGameStartEvent e) {
        for (Player player : e.getGame().getPlayers()) {
            this.cooldown.remove(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!Config.items_tnt_enabled)
            return;
        Player player = e.getPlayer();
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null)
            return;
        if (!game.getPlayers().contains(player))
            return;
        if (game.getState() == GameState.RUNNING &&
                e.getBlock().getType() == (new ItemStack(Material.TNT)).getType() && !e.isCancelled())
            if (System.currentTimeMillis() - this.cooldown.getOrDefault(player, 0L) <= (Config.items_tnt_cooldown * 1000L)) {
                e.setCancelled(true);
                player.sendMessage(Config.message_cooling.replace("{time}", String.valueOf(((Config.items_tnt_cooldown * 1000L) - System.currentTimeMillis() + this.cooldown.getOrDefault(player, 0L)) / 1000L + 1L)));
            } else {
                BedwarsUseItemEvent bedwarsUseItemEvent = new BedwarsUseItemEvent(game, player, EnumItem.LightTNT, new ItemStack(Material.TNT));
                Bukkit.getPluginManager().callEvent(bedwarsUseItemEvent);
                if (!bedwarsUseItemEvent.isCancelled()) {
                    this.cooldown.put(player, System.currentTimeMillis());
                    e.getBlock().setType(Material.AIR);
                    TNTPrimed tnt = e.getBlock().getLocation().getWorld().spawn(e.getBlock().getLocation().add(0.5D, 0.0D, 0.5D), TNTPrimed.class);
                    tnt.setYield(3.0F);
                    tnt.setIsIncendiary(false);
                    tnt.setFuseTicks(Config.items_tnt_fuse_ticks);
                    tnt.setMetadata("LightTNT", new FixedMetadataValue(YaBedwars.getInstance(), game.getName() + "." + player.getName()));
                } else {
                    e.setCancelled(true);
                }
            }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!Config.items_tnt_enabled)
            return;
        Entity damager = e.getDamager();
        if (!damager.hasMetadata("LightTNT"))
            return;
        Entity entity = e.getEntity();
        if (!(entity instanceof Player))
            return;
        Player player = (Player) entity;
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null)
            return;
        if (damager instanceof TNTPrimed) {
            if (!game.getPlayers().contains(player))
                return;
            if (game.isSpectator(player))
                return;
            if (game.getState() == GameState.RUNNING) {
                if (Config.items_tnt_ejection) {
                    player.setAllowFlight(true);
                    player.setVelocity(LocationUtil.getPosition(player.getLocation(), damager.getLocation(), 1.5D).multiply(Config.items_tnt_velocity));
                    setAllowFlight(player);
                }
                e.setDamage(Config.items_tnt_damage);
            }
        }
    }

    public void setAllowFlight(final Player player) {
        (new BukkitRunnable() {
            public void run() {
                Location blockloc = player.getLocation().add(0.0D, -1.0D, 0.0D);
                Block block = blockloc.getBlock();
                Material mate = block.getType();
                if (mate != null &&
                        mate != Material.AIR) {
                    if (player.getGameMode() != GameMode.SPECTATOR)
                        (new BukkitRunnable() {
                            public void run() {
                                player.setAllowFlight(false);
                            }
                        }).runTaskLater(YaBedwars.getInstance(), 10L);
                    cancel();
                }
            }
        }).runTaskTimer(YaBedwars.getInstance(), 5L, 0L);
    }
}
