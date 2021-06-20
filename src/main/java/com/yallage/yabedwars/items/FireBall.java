package com.yallage.yabedwars.items;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameStartEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;

import java.util.HashMap;
import java.util.Map;

import com.yallage.yabedwars.YaBedwars;
import com.yallage.yabedwars.config.Config;
import com.yallage.yabedwars.event.BedwarsUseItemEvent;
import com.yallage.yabedwars.utils.LocationUtil;
import com.yallage.yabedwars.utils.TakeItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class FireBall implements Listener {
    private final Map<Player, Long> cooldown = new HashMap<>();

    @EventHandler
    public void onStart(BedwarsGameStartEvent e) {
        for (Player player : e.getGame().getPlayers()) {
            this.cooldown.remove(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractFireball(PlayerInteractEvent e) {
        if (!Config.items_fireball_enabled)
            return;
        Player player = e.getPlayer();
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (e.getItem() == null || game == null)
            return;
        if (!game.getPlayers().contains(player))
            return;
        if (game.getState() == GameState.RUNNING && (
                e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && e.getItem().getType() == (new ItemStack(Material.FIREBALL)).getType())
            if (System.currentTimeMillis() - this.cooldown.getOrDefault(player, 0L) <= (Config.items_fireball_cooldown * 1000L)) {
                e.setCancelled(true);
                player.sendMessage(Config.message_cooling.replace("{time}", String.valueOf(((Config.items_fireball_cooldown * 1000) - System.currentTimeMillis() + this.cooldown.getOrDefault(player, 0L)) / 1000L + 1L)));
            } else {
                ItemStack stack = e.getItem();
                BedwarsUseItemEvent bedwarsUseItemEvent = new BedwarsUseItemEvent(game, player, EnumItem.FireBall, stack);
                Bukkit.getPluginManager().callEvent(bedwarsUseItemEvent);
                if (!bedwarsUseItemEvent.isCancelled()) {
                    this.cooldown.put(player, System.currentTimeMillis());
                    Fireball fireball = player.launchProjectile(Fireball.class);
                    fireball.setYield(3.0F);
                    fireball.setBounce(false);
                    fireball.setShooter(player);
                    fireball.setIsIncendiary(false);
                    fireball.setMetadata("FireBall", new FixedMetadataValue(YaBedwars.getInstance(), game.getName() + "." + player.getName()));
                    TakeItemUtil.TakeItem(player, stack);
                }
                e.setCancelled(true);
            }
    }

    @EventHandler
    public void onFireballDamage(EntityDamageByEntityEvent e) {
        if (!Config.items_fireball_enabled)
            return;
        Entity entity = e.getEntity();
        Entity damager = e.getDamager();
        if (!damager.hasMetadata("FireBall"))
            return;
        if (!(entity instanceof Player) || !(damager instanceof Fireball))
            return;
        Player player = (Player) entity;
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null)
            return;
        if (game.getState() == GameState.RUNNING &&
                game.getPlayers().contains(player))
            e.setDamage(Config.items_fireball_damage);
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        if (!Config.items_fireball_enabled || !Config.items_fireball_ejection)
            return;
        Entity entity = e.getEntity();
        if (!entity.hasMetadata("FireBall"))
            return;
        if (!(entity instanceof org.bukkit.entity.WitherSkull) && entity instanceof Fireball) {
            Fireball fireball = (Fireball) e.getEntity();
            for (Player player : Bukkit.getOnlinePlayers()) {
                Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
                if (game != null && game.getState() == GameState.RUNNING && !game.isSpectator(player) && player.getGameMode() != GameMode.SPECTATOR && player.getGameMode() != GameMode.CREATIVE &&
                        e.getEntity().getWorld() == player.getWorld() &&
                        player.getLocation().distance(e.getEntity().getLocation()) <= 4.0D) {
                    player.damage(Config.items_fireball_damage, fireball);
                    player.setAllowFlight(true);
                    player.setVelocity(LocationUtil.getPosition(player.getLocation(), fireball.getLocation(), 1.5D).multiply(Config.items_fireball_velocity));
                    setAllowFlight(player);
                }
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
