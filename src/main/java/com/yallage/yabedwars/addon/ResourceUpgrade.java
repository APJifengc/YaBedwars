package com.yallage.yabedwars.addon;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsResourceSpawnEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.ResourceSpawner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yallage.yabedwars.YaBedwars;
import com.yallage.yabedwars.config.Config;
import com.yallage.yabedwars.events.BoardAddonResourceUpgradeEvent;
import com.yallage.yabedwars.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ResourceUpgrade implements Listener {
    private final Map<Material, Integer> Interval;

    private final Map<Material, Integer> SpawnTime;

    private final Map<String, String> UpgTime;

    private final Map<Material, String> Level;

    public Map<String, String> getUpgTime() {
        return this.UpgTime;
    }

    public Map<Material, Integer> getSpawnTime() {
        return this.SpawnTime;
    }

    public Map<Material, String> getLevel() {
        return this.Level;
    }

    public ResourceUpgrade(final Game game) {
        this.Interval = new HashMap<>();
        this.SpawnTime = new HashMap<>();
        this.UpgTime = new HashMap<>();
        this.Level = new HashMap<>();
        for (ResourceSpawner spawner : game.getResourceSpawners()) {
            for (ItemStack itemStack : spawner.getResources()) {
                this.Level.put(itemStack.getType(), "I");
                this.Interval.put(itemStack.getType(), spawner.getInterval() / 50);
            }
            Location sloc = spawner.getLocation();
            for (ItemStack itemStack : spawner.getResources()) {
                (new BukkitRunnable() {
                    Location loc;

                    int i;

                    public void run() {
                        if (game.getState() != GameState.WAITING && game.getState() == GameState.RUNNING) {
                            ResourceUpgrade.this.SpawnTime.put(itemStack.getType(), this.i / 20 + 1);
                            if (this.i <= 0) {
                                this.i = ResourceUpgrade.this.Interval.get(itemStack.getType());
                                int es = 0;
                                for (Entity entity : this.loc.getWorld().getNearbyEntities(this.loc, 2.0D, 2.0D, 2.0D)) {
                                    if (entity instanceof Item) {
                                        Item item = (Item) entity;
                                        if (item.getItemStack().getType() == itemStack.getType())
                                            es += item.getItemStack().getAmount();
                                    }
                                }
                                boolean drop = true;
                                if (Config.resourcelimit_enabled)
                                    for (String[] rl : Config.resourcelimit_limit) {
                                        if (Material.valueOf(rl[0]) == itemStack.getType() &&
                                                es >= Integer.valueOf(rl[1]))
                                            drop = false;
                                    }
                                Block block = this.loc.getBlock();
                                Boolean inchest = (block.getType().equals(Material.CHEST) &&
                                        BedwarsRel.getInstance().getBooleanConfig("spawn-resources-in-chest", true));
                                if (drop || inchest) {
                                    BedwarsResourceSpawnEvent event = new BedwarsResourceSpawnEvent(game, this.loc,
                                            itemStack.clone());
                                    Bukkit.getPluginManager().callEvent(event);
                                    if (!event.isCancelled())
                                        if (inchest && spawner.canContainItem(((Chest) block.getState()).getInventory(),
                                                itemStack)) {
                                            ((Chest) block.getState()).getInventory().addItem(itemStack);
                                        } else if (drop) {
                                            Item item = this.loc.getWorld().dropItemNaturally(this.loc, itemStack);
                                            item.setPickupDelay(0);
                                            item.setVelocity(item.getVelocity().multiply(spawner.getSpread()));
                                        }
                                }
                            }
                            this.i--;
                        } else {
                            cancel();
                        }
                    }
                }).runTaskTimer(YaBedwars.getInstance(), 0L, 1L);
            }
        }
        for (String rs : YaBedwars.getInstance().getConfig().getConfigurationSection("resourceupgrade").getKeys(false)) {
            (new BukkitRunnable() {
                int gametime;

                List<String> upgrade;

                String message;

                Boolean isExecuted;

                public void run() {
                    if (game.getState() == GameState.RUNNING) {
                        if (this.isExecuted) {
                            cancel();
                            return;
                        }
                        int remtime = game.getTimeLeft() - this.gametime;
                        String formatremtime = remtime / 60 + ":" + (
                                (remtime % 60 < 10) ? ("0" + (remtime % 60)) : remtime % 60);
                        ResourceUpgrade.this.UpgTime.put(rs, formatremtime);
                        if (game.getTimeLeft() <= this.gametime) {
                            this.isExecuted = Boolean.TRUE;
                            BoardAddonResourceUpgradeEvent resourceUpgradeEvent = new BoardAddonResourceUpgradeEvent(
                                    game, this.upgrade);
                            Bukkit.getPluginManager().callEvent(resourceUpgradeEvent);
                            if (resourceUpgradeEvent.isCancelled()) {
                                cancel();
                                return;
                            }
                            for (String upg : resourceUpgradeEvent.getUpgrade()) {
                                String[] ary = upg.split(",");
                                if (ResourceUpgrade.this.Level.containsKey(Material.valueOf(ary[0]))) {
                                    ResourceUpgrade.this.Level.put(Material.valueOf(ary[0]), ResourceUpgrade.this.getLevel(ResourceUpgrade.this.Level.get(Material.valueOf(ary[0]))));
                                    ResourceUpgrade.this.Interval.put(Material.valueOf(ary[0]), Integer.valueOf(ary[1]));
                                }
                            }
                            for (Player player : game.getPlayers())
                                player.sendMessage(ColorUtil.color(this.message));
                            PlaySound.playSound(game, Config.play_sound_sound_upgrade);
                            cancel();
                        }
                    } else {
                        cancel();
                    }
                }
            }).runTaskTimer(YaBedwars.getInstance(), 0L, 21L);
        }
    }

    public String getLevel(String level) {
        String l = "I";
        if (level.equals("I"))
            l = "II";
        if (level.equals("II"))
            l = "III";
        if (level.equals("III"))
            l = "IV";
        if (level.equals("IV"))
            l = "V";
        if (level.equals("V"))
            l = "VI";
        if (level.equals("VI"))
            l = "VII";
        if (level.equals("VII"))
            l = "VIII";
        if (level.equals("VIII"))
            l = "IX";
        if (level.equals("IX"))
            l = "X";
        if (level.equals("X"))
            l = "X";
        return l;
    }
}
