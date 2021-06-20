package com.yallage.yabedwars.items;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameOverEvent;
import io.github.bedwarsrel.events.BedwarsGameStartEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.utils.SoundMachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yallage.yabedwars.EnumItem;
import com.yallage.yabedwars.YaBedwars;
import com.yallage.yabedwars.config.Config;
import com.yallage.yabedwars.event.BedwarsUseItemEvent;
import com.yallage.yabedwars.utils.LocationUtil;
import com.yallage.yabedwars.utils.NMS;
import com.yallage.yabedwars.utils.TakeItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class Parachute implements Listener {
    private Map<String, List<ArmorStand>> armorstands = new HashMap<>();

    private final Map<Player, Long> cooldown = new HashMap<>();

    private final Map<String, Map<Player, Integer>> ejection = new HashMap<>();

    @EventHandler
    public void onStart(BedwarsGameStartEvent e) {
        for (Player player : e.getGame().getPlayers()) {
            this.cooldown.remove(player);
        }
        Game game = e.getGame();
        this.armorstands.put(game.getName(), new ArrayList<>());
        this.ejection.put(game.getName(), new HashMap<>());
    }

    @EventHandler
    public void onOver(BedwarsGameOverEvent e) {
        Game game = e.getGame();
        for (ArmorStand armorstand : this.armorstands.get(game.getName())) {
            if (!armorstand.isDead())
                armorstand.remove();
        }
        this.armorstands.put(game.getName(), new ArrayList<>());
    }

    @EventHandler
    public void onDisable(PluginDisableEvent e) {
        if (e.getPlugin().equals(BedwarsRel.getInstance()) || e.getPlugin().equals(YaBedwars.getInstance())) {
            for (List<ArmorStand> as : this.armorstands.values()) {
                for (ArmorStand armorstand : as) {
                    if (!armorstand.isDead())
                        armorstand.remove();
                }
            }
            this.armorstands = new HashMap<>();
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerArmorStandManipulateEvent e) {
        if (!Config.items_parachute_enabled)
            return;
        Player player = e.getPlayer();
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null)
            return;
        if (!game.getPlayers().contains(player))
            return;
        if (game.getState() == GameState.RUNNING)
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (!Config.items_parachute_enabled)
            return;
        Player player = e.getPlayer();
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null)
            return;
        if (!game.getPlayers().contains(player))
            return;
        if (game.getState() == GameState.RUNNING &&
                e.getBlock().getType() == (new ItemStack(Material.valueOf(Config.items_parachute_item))).getType())
            e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!Config.items_parachute_enabled)
            return;
        Player player = e.getPlayer();
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (e.getItem() == null || game == null)
            return;
        if (!game.getPlayers().contains(player))
            return;
        if (game.getState() == GameState.RUNNING && (
                e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && e.getItem().getType() == (new ItemStack(Material.valueOf(Config.items_parachute_item))).getType())
            if (System.currentTimeMillis() - this.cooldown.getOrDefault(player, 0L) <= (Config.items_parachute_cooldown * 1000L)) {
                e.setCancelled(true);
                player.sendMessage(Config.message_cooling.replace("{time}", String.valueOf(((Config.items_parachute_cooldown * 1000) - System.currentTimeMillis() + this.cooldown.getOrDefault(player, 0L)) / 1000L + 1L)));
            } else {
                ItemStack stack = e.getItem();
                BedwarsUseItemEvent bedwarsUseItemEvent = new BedwarsUseItemEvent(game, player, EnumItem.Parachute, stack);
                Bukkit.getPluginManager().callEvent(bedwarsUseItemEvent);
                if (!bedwarsUseItemEvent.isCancelled()) {
                    this.cooldown.put(player, System.currentTimeMillis());
                    this.ejection.get(game.getName()).put(player, (Integer) ((Map) this.ejection.get(game.getName())).getOrDefault(player, Integer.valueOf(0)) + 1);
                    player.getWorld().playSound(player.getLocation(), SoundMachine.get("FIREWORK_LARGE_BLAST", "ENTITY_FIREWORK_LARGE_BLAST"), 1.0F, 1.0F);
                    player.getWorld().playEffect(player.getLocation(), Effect.EXPLOSION_LARGE, 1);
                    player.setSneaking(true);
                    setParachute(game, player);
                    TakeItemUtil.TakeItem(player, stack);
                }
                e.setCancelled(true);
            }
    }

    public void setParachute(final Game game, final Player player) {
        (new BukkitRunnable() {
            public void run() {
                player.setSneaking(false);
                player.setVelocity(new Vector(0.0D, Config.items_parachute_velocity, 0.0D));
            }
        }).runTaskLater(YaBedwars.getInstance(), 1L);
        final int ei = (Integer) ((Map) this.ejection.get(game.getName())).get(player);
        (new BukkitRunnable() {
            boolean po = false;

            ArmorStand armorStand1;

            ArmorStand armorStand2;

            ArmorStand armorStand3;

            ArmorStand armorStand4;

            ArmorStand armorStand5;

            public void run() {
                if (ei != (Integer) ((Map) Parachute.this.ejection.get(game.getName())).get(player)) {
                    if (player.getGameMode() != GameMode.SPECTATOR)
                        player.setAllowFlight(false);
                    if (this.po) {
                        this.armorStand1.remove();
                        this.armorStand2.remove();
                        this.armorStand3.remove();
                        this.armorStand4.remove();
                        this.armorStand5.remove();
                    }
                    cancel();
                    return;
                }
                if (!player.isOnline()) {
                    if (player.getGameMode() != GameMode.SPECTATOR)
                        player.setAllowFlight(false);
                    if (this.po) {
                        this.armorStand1.remove();
                        this.armorStand2.remove();
                        this.armorStand3.remove();
                        this.armorStand4.remove();
                        this.armorStand5.remove();
                    }
                    Parachute.this.ejection.get(game.getName()).put(player, 0);
                    cancel();
                    return;
                }
                if (player.isDead()) {
                    if (player.getGameMode() != GameMode.SPECTATOR)
                        player.setAllowFlight(false);
                    if (this.po) {
                        this.armorStand1.remove();
                        this.armorStand2.remove();
                        this.armorStand3.remove();
                        this.armorStand4.remove();
                        this.armorStand5.remove();
                    }
                    cancel();
                    return;
                }
                player.setAllowFlight(true);
                if (player.isSneaking()) {
                    if (player.getGameMode() != GameMode.SPECTATOR)
                        player.setAllowFlight(false);
                    if (this.po) {
                        this.armorStand1.remove();
                        this.armorStand2.remove();
                        this.armorStand3.remove();
                        this.armorStand4.remove();
                        this.armorStand5.remove();
                    }
                    player.setVelocity(new Vector(0, 0, 0));
                    cancel();
                    return;
                }
                if (!this.po)
                    player.setVelocity(player.getVelocity());
                if (!this.po && player.getVelocity().getY() < 0.0D) {
                    this.armorStand1 = player.getWorld().spawn(LocationUtil.getLocationYaw(player.getLocation(), 0.0D, 2.0D, 0.0D), ArmorStand.class);
                    this.armorStand1.setVisible(false);
                    this.armorStand1.setGravity(false);
                    this.armorStand1.setBasePlate(false);
                    this.armorStand1.setHelmet(new ItemStack(Material.CARPET));
                    this.armorStand2 = player.getWorld().spawn(LocationUtil.getLocationYaw(player.getLocation(), 0.61D, 1.97D, 0.0D), ArmorStand.class);
                    this.armorStand2.setVisible(false);
                    this.armorStand2.setGravity(false);
                    this.armorStand2.setBasePlate(false);
                    this.armorStand2.setHelmet(new ItemStack(Material.CARPET));
                    this.armorStand2.setHeadPose(EulerAngle.ZERO.setZ(0.1D));
                    this.armorStand3 = player.getWorld().spawn(LocationUtil.getLocationYaw(player.getLocation(), 1.2D, 1.85D, 0.0D), ArmorStand.class);
                    this.armorStand3.setVisible(false);
                    this.armorStand3.setGravity(false);
                    this.armorStand3.setBasePlate(false);
                    this.armorStand3.setHelmet(new ItemStack(Material.CARPET));
                    this.armorStand3.setHeadPose(EulerAngle.ZERO.setZ(0.3D));
                    this.armorStand4 = player.getWorld().spawn(LocationUtil.getLocationYaw(player.getLocation(), -0.61D, 1.97D, 0.0D), ArmorStand.class);
                    this.armorStand4.setVisible(false);
                    this.armorStand4.setGravity(false);
                    this.armorStand4.setBasePlate(false);
                    this.armorStand4.setHelmet(new ItemStack(Material.CARPET));
                    this.armorStand4.setHeadPose(EulerAngle.ZERO.setZ(-0.1D));
                    this.armorStand5 = player.getWorld().spawn(LocationUtil.getLocationYaw(player.getLocation(), -1.2D, 1.85D, 0.0D), ArmorStand.class);
                    this.armorStand5.setVisible(false);
                    this.armorStand5.setGravity(false);
                    this.armorStand5.setBasePlate(false);
                    this.armorStand5.setHelmet(new ItemStack(Material.CARPET));
                    this.armorStand5.setHeadPose(EulerAngle.ZERO.setZ(-0.3D));
                    this.po = true;
                    player.getWorld().playSound(player.getLocation(), SoundMachine.get("HORSE_ARMOR", "ENTITY_HORSE_ARMOR"), 1.0F, 1.0F);
                    Parachute.this.armorstands.get(game.getName()).add(this.armorStand1);
                    Parachute.this.armorstands.get(game.getName()).add(this.armorStand2);
                    Parachute.this.armorstands.get(game.getName()).add(this.armorStand3);
                    Parachute.this.armorstands.get(game.getName()).add(this.armorStand4);
                    Parachute.this.armorstands.get(game.getName()).add(this.armorStand5);
                }
                if (player.getVelocity().getY() > 0.0D && this.po) {
                    if (player.getGameMode() != GameMode.SPECTATOR)
                        player.setAllowFlight(false);
                    if (this.po) {
                        this.armorStand1.remove();
                        this.armorStand2.remove();
                        this.armorStand3.remove();
                        this.armorStand4.remove();
                        this.armorStand5.remove();
                    }
                    cancel();
                    return;
                }
                if (player.getVelocity().getY() < 0.0D) {
                    Vector vector = player.getLocation().getDirection().multiply(Config.items_parachute_gliding_velocity);
                    vector.setY(Config.items_parachute_landing_velocity * -1.0D);
                    player.setVelocity(vector);
                }
                if (this.po) {
                    if (!this.armorStand1.getLocation().getBlock().getChunk().isLoaded())
                        this.armorStand1.getLocation().getBlock().getChunk().load(true);
                    if (!this.armorStand2.getLocation().getBlock().getChunk().isLoaded())
                        this.armorStand2.getLocation().getBlock().getChunk().load(true);
                    if (!this.armorStand3.getLocation().getBlock().getChunk().isLoaded())
                        this.armorStand3.getLocation().getBlock().getChunk().load(true);
                    if (!this.armorStand4.getLocation().getBlock().getChunk().isLoaded())
                        this.armorStand4.getLocation().getBlock().getChunk().load(true);
                    if (!this.armorStand5.getLocation().getBlock().getChunk().isLoaded())
                        this.armorStand5.getLocation().getBlock().getChunk().load(true);
                    NMS.teleportEntity(game, this.armorStand1, LocationUtil.getLocationYaw(player.getLocation(), 0.0D, 2.5D, 0.0D));
                    NMS.teleportEntity(game, this.armorStand2, LocationUtil.getLocationYaw(player.getLocation(), 0.61D, 2.47D, 0.0D));
                    NMS.teleportEntity(game, this.armorStand3, LocationUtil.getLocationYaw(player.getLocation(), 1.2D, 2.35D, 0.0D));
                    NMS.teleportEntity(game, this.armorStand4, LocationUtil.getLocationYaw(player.getLocation(), -0.61D, 2.47D, 0.0D));
                    NMS.teleportEntity(game, this.armorStand5, LocationUtil.getLocationYaw(player.getLocation(), -1.2D, 2.35D, 0.0D));
                }
                Location blockloc = player.getLocation();
                blockloc = blockloc.add(0.0D, -1.0D, 0.0D);
                Block block = blockloc.getBlock();
                Material mate = block.getType();
                if (mate != null &&
                        mate != Material.AIR) {
                    (new BukkitRunnable() {
                        public void run() {
                            if (player.getGameMode() != GameMode.SPECTATOR)
                                player.setAllowFlight(false);
                        }
                    }).runTaskLater(YaBedwars.getInstance(), 10L);
                    if (this.po) {
                        this.armorStand1.remove();
                        this.armorStand2.remove();
                        this.armorStand3.remove();
                        this.armorStand4.remove();
                        this.armorStand5.remove();
                    }
                    cancel();
                }
            }
        }).runTaskTimer(YaBedwars.getInstance(), 5L, 0L);
    }
}
