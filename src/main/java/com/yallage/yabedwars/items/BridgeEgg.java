package com.yallage.yabedwars.items;

import com.yallage.yabedwars.YaBedwars;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameStartEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yallage.yabedwars.config.Config;
import com.yallage.yabedwars.event.BedwarsUseItemEvent;
import com.yallage.yabedwars.utils.LocationUtil;
import com.yallage.yabedwars.utils.TakeItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BridgeEgg implements Listener {
    private final Map<Player, Long> cooldown = new HashMap<>();

    @EventHandler
    public void onStart(BedwarsGameStartEvent e) {
        for (Player player : e.getGame().getPlayers()) {
            this.cooldown.remove(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (!Config.items_bridge_egg_enabled)
            return;
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (e.getItem() == null || game == null)
            return;
        if (game.isOverSet() || game.getState() != GameState.RUNNING)
            return;
        if (!game.getPlayers().contains(player))
            return;
        if (game.getState() != GameState.WAITING && game.getState() == GameState.RUNNING && (
                e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && e.getItem().getType() == (new ItemStack(Material.EGG)).getType())
            if (System.currentTimeMillis() - this.cooldown.getOrDefault(player, 0L) <= (Config.items_bridge_egg_cooldown * 1000L)) {
                e.setCancelled(true);
                player.sendMessage(Config.message_cooling.replace("{time}", String.valueOf(((Config.items_bridge_egg_cooldown * 1000) - System.currentTimeMillis() + this.cooldown.getOrDefault(player, 0L)) / 1000L + 1L)));
            } else {
                ItemStack stack = e.getItem();
                BedwarsUseItemEvent bedwarsUseItemEvent = new BedwarsUseItemEvent(game, player, EnumItem.BridgeEgg, stack);
                Bukkit.getPluginManager().callEvent(bedwarsUseItemEvent);
                if (!bedwarsUseItemEvent.isCancelled()) {
                    this.cooldown.put(player, System.currentTimeMillis());
                    Egg egg = player.launchProjectile(Egg.class);
                    egg.setBounce(false);
                    egg.setShooter(player);
                    setblock(game, egg, player);
                    TakeItemUtil.TakeItem(player, stack);
                }
                e.setCancelled(true);
            }
    }

    public void setblock(final Game game, final Egg egg, final Player player) {
        new BukkitRunnable() {

            @Override
            public void run() {
                (new BukkitRunnable() {
                    int i = 0;

                    public void run() {
                        if (!egg.isDead()) {
                            (new BukkitRunnable() {
                                Location location = egg.getLocation();

                                public void run() {
                                    if (game.isOverSet() || game.getState() != GameState.RUNNING) {
                                        cancel();
                                        return;
                                    }
                                    this.location.setX((int) this.location.getX());
                                    this.location.setY((int) this.location.getY());
                                    this.location.setZ((int) this.location.getZ());
                                    List<Location> blocklocation = new ArrayList<>();
                                    blocklocation.add(this.location);
                                    Vector vector = egg.getVelocity();
                                    double x = (vector.getX() > 0.0D) ? vector.getX() : -vector.getX();
                                    double y = (vector.getY() > 0.0D) ? vector.getY() : -vector.getY();
                                    double z = (vector.getZ() > 0.0D) ? vector.getZ() : -vector.getZ();
                                    if (y < x || y < z) {
                                        blocklocation.add(LocationUtil.getLocation(this.location, -1, 0, -1));
                                        blocklocation.add(LocationUtil.getLocation(this.location, -1, 0, 0));
                                        blocklocation.add(LocationUtil.getLocation(this.location, 0, 0, -1));
                                    } else {
                                        blocklocation.add(LocationUtil.getLocation(this.location, 0, 1, 0));
                                        blocklocation.add(LocationUtil.getLocation(this.location, -1, 1, -1));
                                        blocklocation.add(LocationUtil.getLocation(this.location, -1, 1, 0));
                                        blocklocation.add(LocationUtil.getLocation(this.location, 0, 1, -1));
                                        blocklocation.add(LocationUtil.getLocation(this.location, -1, 0, -1));
                                        blocklocation.add(LocationUtil.getLocation(this.location, -1, 0, 0));
                                        blocklocation.add(LocationUtil.getLocation(this.location, 0, 0, -1));
                                    }
                                    for (Location loc : blocklocation) {
                                        Block block = loc.getBlock();
                                        if (block.getType() == (new ItemStack(Material.AIR)).getType() && !block.equals(player.getLocation().getBlock()) && !block.equals(player.getLocation().clone().add(0.0D, 1.0D, 0.0D).getBlock()) && game.getRegion().isInRegion(loc) && i < Config.items_bridge_egg_maxblock) {
                                            BlockPlaceEvent event = new BlockPlaceEvent(loc.getBlock(),loc.getBlock().getState(), null, null, player, true);
                                            Bukkit.getPluginManager().callEvent(event);
                                            if (event.isCancelled()) {
                                                egg.remove();
                                                cancel();
                                                return;
                                            }
                                            loc.getBlock().setType(Material.WOOL);
                                            loc.getBlock().setData(game.getPlayerTeam(player).getColor().getDyeColor().getWoolData());
                                            i++;
                                            game.getRegion().addPlacedBlock(loc.getBlock(), null);
                                        }
                                    }
                                }
                            }).runTaskLater(YaBedwars.getInstance(), 5L);
                        } else {
                            cancel();
                        }
                    }
                }).runTaskTimer(YaBedwars.getInstance(), 0L, 0L);
            }
        }.runTaskLater(YaBedwars.getInstance(), 3);
    }
}
