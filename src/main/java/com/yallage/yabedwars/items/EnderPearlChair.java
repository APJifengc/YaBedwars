package com.yallage.yabedwars.items;

import com.yallage.yabedwars.YaBedwars;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameStartEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;

import java.util.HashMap;
import java.util.Map;

import com.yallage.yabedwars.config.Config;
import com.yallage.yabedwars.event.BedwarsUseItemEvent;
import com.yallage.yabedwars.utils.TakeItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class EnderPearlChair implements Listener {
    private final Map<Player, Long> cooldown = new HashMap<>();

    @EventHandler
    public void onStart(BedwarsGameStartEvent e) {
        for (Player player : e.getGame().getPlayers()) {
            this.cooldown.remove(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e) {
        if (!Config.items_ender_pearl_chair_enabled)
            return;
        Player player = e.getPlayer();
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (e.getItem() == null || game == null)
            return;
        if (game.getState() != GameState.RUNNING)
            return;
        if (!game.getPlayers().contains(player))
            return;
        if (game.getState() == GameState.RUNNING && (
                e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && e.getItem().getType() == (new ItemStack(Material.ENDER_PEARL)).getType())
            if (System.currentTimeMillis() - this.cooldown.getOrDefault(player, 0L) <= (Config.items_ender_pearl_chair_cooldown * 1000L)) {
                e.setCancelled(true);
                player.sendMessage(Config.message_cooling.replace("{time}", String.valueOf(((Config.items_ender_pearl_chair_cooldown * 1000) - System.currentTimeMillis() + this.cooldown.getOrDefault(player, 0L)) / 1000L + 1L)));
            } else {
                ItemStack stack = e.getItem();
                BedwarsUseItemEvent bedwarsUseItemEvent = new BedwarsUseItemEvent(game, player, EnumItem.EnderPearlChair, stack);
                Bukkit.getPluginManager().callEvent(bedwarsUseItemEvent);
                if (!bedwarsUseItemEvent.isCancelled()) {
                    this.cooldown.put(player, System.currentTimeMillis());
                    EnderPearl enderpearl = player.launchProjectile(EnderPearl.class);
                    enderpearl.setShooter(player);
                    enderpearl.setPassenger(player);
                    removeEnderPearl(player, enderpearl);
                    TakeItemUtil.TakeItem(player, stack);
                }
                e.setCancelled(true);
            }
    }

    public void removeEnderPearl(final Player player, final EnderPearl enderpearl) {
        (new BukkitRunnable() {
            public void run() {
                Location blockloc = player.getLocation();
                Block block = blockloc.getBlock();
                Material mate = block.getType();
                if (mate != null &&
                        mate != Material.AIR) {
                    player.teleport(player.getLocation().add(0.0D, 1.0D, 0.0D));
                    enderpearl.remove();
                    cancel();
                    return;
                }
                if (enderpearl.isDead()) {
                    cancel();
                    return;
                }
                if (enderpearl.getPassenger() == null) {
                    enderpearl.remove();
                    cancel();
                }
            }
        }).runTaskTimer(YaBedwars.getInstance(), 0L, 0L);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (Config.items_ender_pearl_chair_enabled && e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.FALL && e.getDamage() == 5.0D && e.getEntity().getLocation().getY() - e.getEntity().getLocation().getBlock().getLocation().getY() != 0.0D) {
            Player player = (Player) e.getEntity();
            if (BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player) != null)
                e.setCancelled(true);
        }
    }
}
