package com.yallage.yabedwars.addon;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.yallage.yabedwars.YaBedwars;
import com.yallage.yabedwars.config.Config;
import com.yallage.yabedwars.utils.Utils;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class InvisibilityPlayer {
    private final Game game;

    private final List<UUID> players;

    private final List<UUID> hplayers;

    public InvisibilityPlayer(Game game) {
        this.game = game;
        this.players = new ArrayList<>();
        this.hplayers = new ArrayList<>();
    }

    public List<UUID> getPlayers() {
        return this.players;
    }

    public void removePlayer(Player player) {
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        this.players.remove(player.getUniqueId());
    }

    public void showPlayerArmor(Player player) {
        this.hplayers.remove(player.getUniqueId());
        showArmor(player);
    }

    public Game getGame() {
        return this.game;
    }

    public void hidePlayer(final Player player) {
        if (!this.hplayers.contains(player.getUniqueId()))
            this.hplayers.add(player.getUniqueId());
        if (this.players.contains(player.getUniqueId()))
            return;
        this.players.add(player.getUniqueId());
        final BukkitTask task = (new BukkitRunnable() {
            public void run() {
                if (Config.invisibility_player_footstep)
                    (new BukkitRunnable() {
                        Location loc;

                        public void run() {
                            if (player.isOnline() && (this.loc.getX() != player.getLocation().getX() ||
                                    this.loc.getY() != player.getLocation().getY() ||
                                    this.loc.getZ() != player.getLocation().getZ()))
                                player.getWorld()
                                        .playEffect(
                                                player.getLocation().clone().add((Math.random() - Math.random()) * 0.5D,
                                                        0.05D, (Math.random() - Math.random()) * 0.5D),
                                                Effect.FOOTSTEP, 0);
                        }
                    }).runTaskLater(YaBedwars.getInstance(), 8L);
            }
        }).runTaskTimer(YaBedwars.getInstance(), 1L, 8L);
        (new BukkitRunnable() {
            public void run() {
                if (InvisibilityPlayer.this.game.getState() != GameState.RUNNING || !player.isOnline() ||
                        !player.hasPotionEffect(PotionEffectType.INVISIBILITY) ||
                        !InvisibilityPlayer.this.players.contains(player.getUniqueId()) || !InvisibilityPlayer.this.game.getPlayers().contains(player) ||
                        InvisibilityPlayer.this.game.isSpectator(player)) {
                    cancel();
                    task.cancel();
                    InvisibilityPlayer.this.players.remove(player.getUniqueId());
                    InvisibilityPlayer.this.hplayers.remove(player.getUniqueId());
                    if (player.isOnline()) {
                        InvisibilityPlayer.this.showArmor(player);
                        if (Config.invisibility_player_hide_particles)
                            for (PotionEffect effect : player.getActivePotionEffects())
                                player.addPotionEffect(new PotionEffect(effect.getType(), effect.getDuration(),
                                        effect.getAmplifier(), false, true), true);
                    }
                    return;
                }
                if (InvisibilityPlayer.this.hplayers.contains(player.getUniqueId()))
                    InvisibilityPlayer.this.hideArmor(player);
            }
        }).runTaskTimer(YaBedwars.getInstance(), 2L, 1L);
    }

    private void hideArmor(Player player) {
        if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
            try {
                Constructor constructor = Objects.requireNonNull(Utils.getNMSClass("PacketPlayOutEntityEquipment")).getConstructor(int.class,
                        int.class, Utils.getNMSClass("ItemStack"));
                Object as = Objects.requireNonNull(Utils.getClass("inventory.CraftItemStack")).getMethod("asNMSCopy", new Class[]{ItemStack.class}).invoke(null, new ItemStack(Material.AIR));
                Object packet1 = constructor.newInstance(player.getEntityId(), 1, as);
                Object packet2 = constructor.newInstance(player.getEntityId(), 2, as);
                Object packet3 = constructor.newInstance(player.getEntityId(), 3, as);
                Object packet4 = constructor.newInstance(player.getEntityId(), 4, as);
                List<Player> players = this.game.getPlayerTeam(player).getPlayers();
                for (Player p : this.game.getPlayers()) {
                    if (p != player && !players.contains(p)) {
                        Utils.sendPacket(p, packet1);
                        Utils.sendPacket(p, packet2);
                        Utils.sendPacket(p, packet3);
                        Utils.sendPacket(p, packet4);
                    }
                }
            } catch (Exception ignored) {
            }
        } else {
            try {
                Constructor constructor = Objects.requireNonNull(Utils.getNMSClass("PacketPlayOutEntityEquipment")).getConstructor(int.class,
                        Utils.getNMSClass("EnumItemSlot"), Utils.getNMSClass("ItemStack"));
                Object as = Objects.requireNonNull(Utils.getClass("inventory.CraftItemStack")).getMethod("asNMSCopy", new Class[]{ItemStack.class}).invoke(null, new ItemStack(Material.AIR));
                Object packet1 = constructor.newInstance(player.getEntityId(),
                        Objects.requireNonNull(Utils.getNMSClass("EnumItemSlot")).getField("FEET").get(null), as);
                Object packet2 = constructor.newInstance(player.getEntityId(),
                        Objects.requireNonNull(Utils.getNMSClass("EnumItemSlot")).getField("LEGS").get(null), as);
                Object packet3 = constructor.newInstance(player.getEntityId(),
                        Objects.requireNonNull(Utils.getNMSClass("EnumItemSlot")).getField("CHEST").get(null), as);
                Object packet4 = constructor.newInstance(player.getEntityId(),
                        Objects.requireNonNull(Utils.getNMSClass("EnumItemSlot")).getField("HEAD").get(null), as);
                List<Player> players = this.game.getPlayerTeam(player).getPlayers();
                for (Player p : this.game.getPlayers()) {
                    if (p != player && !players.contains(p)) {
                        Utils.sendPacket(p, packet1);
                        Utils.sendPacket(p, packet2);
                        Utils.sendPacket(p, packet3);
                        Utils.sendPacket(p, packet4);
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void showArmor(Player player) {
        if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
            try {
                Constructor constructor = Objects.requireNonNull(Utils.getNMSClass("PacketPlayOutEntityEquipment")).getConstructor(int.class,
                        int.class, Utils.getNMSClass("ItemStack"));
                Object as = Objects.requireNonNull(Utils.getClass("inventory.CraftItemStack")).getMethod("asNMSCopy", new Class[]{ItemStack.class}).invoke(null, new ItemStack(Material.AIR));
                Method method = Objects.requireNonNull(Utils.getClass("inventory.CraftItemStack")).getMethod("asNMSCopy", ItemStack.class);
                Object packet1 = constructor.newInstance(player.getEntityId(), 1,
                        method.invoke(null, player.getInventory().getBoots()));
                Object packet2 = constructor.newInstance(player.getEntityId(), 2,
                        method.invoke(null, player.getInventory().getLeggings()));
                Object packet3 = constructor.newInstance(player.getEntityId(), 3,
                        method.invoke(null, player.getInventory().getChestplate()));
                Object packet4 = constructor.newInstance(player.getEntityId(), 4,
                        method.invoke(null, player.getInventory().getHelmet()));
                List<Player> players = this.game.getPlayerTeam(player).getPlayers();
                for (Player p : this.game.getPlayers()) {
                    if (p != player && !players.contains(p)) {
                        Utils.sendPacket(p, packet1);
                        Utils.sendPacket(p, packet2);
                        Utils.sendPacket(p, packet3);
                        Utils.sendPacket(p, packet4);
                    }
                }
            } catch (Exception ignored) {
            }
        } else {
            try {
                Constructor constructor = Objects.requireNonNull(Utils.getNMSClass("PacketPlayOutEntityEquipment")).getConstructor(int.class,
                        Utils.getNMSClass("EnumItemSlot"), Utils.getNMSClass("ItemStack"));
                Object as = Objects.requireNonNull(Utils.getClass("inventory.CraftItemStack")).getMethod("asNMSCopy", new Class[]{ItemStack.class}).invoke(null, new ItemStack(Material.AIR));
                Method method = Objects.requireNonNull(Utils.getClass("inventory.CraftItemStack")).getMethod("asNMSCopy", ItemStack.class);
                Object packet1 = constructor.newInstance(player.getEntityId(),
                        Objects.requireNonNull(Utils.getNMSClass("EnumItemSlot")).getField("FEET").get(null),
                        method.invoke(null, player.getInventory().getBoots()));
                Object packet2 = constructor.newInstance(player.getEntityId(),
                        Objects.requireNonNull(Utils.getNMSClass("EnumItemSlot")).getField("LEGS").get(null),
                        method.invoke(null, player.getInventory().getLeggings()));
                Object packet3 = constructor.newInstance(player.getEntityId(),
                        Objects.requireNonNull(Utils.getNMSClass("EnumItemSlot")).getField("CHEST").get(null),
                        method.invoke(null, player.getInventory().getChestplate()));
                Object packet4 = constructor.newInstance(player.getEntityId(),
                        Objects.requireNonNull(Utils.getNMSClass("EnumItemSlot")).getField("HEAD").get(null),
                        method.invoke(null, player.getInventory().getHelmet()));
                List<Player> players = this.game.getPlayerTeam(player).getPlayers();
                for (Player p : this.game.getPlayers()) {
                    if (p != player && !players.contains(p)) {
                        Utils.sendPacket(p, packet1);
                        Utils.sendPacket(p, packet2);
                        Utils.sendPacket(p, packet3);
                        Utils.sendPacket(p, packet4);
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }
}
