package com.yallage.yabedwars.items;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameOverEvent;
import io.github.bedwarsrel.events.BedwarsGameStartEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yallage.yabedwars.YaBedwars;
import com.yallage.yabedwars.config.Config;
import com.yallage.yabedwars.event.BedwarsUseItemEvent;
import com.yallage.yabedwars.utils.TakeItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class WalkPlatform implements Listener {
    private final Map<Player, Long> cooldown = new HashMap<>();

    private final Map<String, Map<Block, BukkitTask>> blocktasks = new HashMap<>();

    private final Map<String, Map<Player, BukkitTask>> tasks = new HashMap<>();

    @EventHandler
    public void onStart(BedwarsGameStartEvent e) {
        for (Player player : e.getGame().getPlayers()) {
            this.cooldown.remove(player);
        }
        this.blocktasks.put(e.getGame().getName(), new HashMap<>());
        this.tasks.put(e.getGame().getName(), new HashMap<>());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!Config.items_walk_platform_enabled)
            return;
        Player player = e.getPlayer();
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (e.getItem() == null || game == null)
            return;
        if (game.isOverSet())
            return;
        if (!game.getPlayers().contains(player))
            return;
        if (game.isSpectator(player))
            return;
        if (game.getState() == GameState.RUNNING && (
                e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && e.getItem().getType() == Material.valueOf(Config.items_walk_platform_item)) {
            e.setCancelled(true);
            if (System.currentTimeMillis() - this.cooldown.getOrDefault(player, 0L) <= (Config.items_walk_platform_cooldown * 1000L)) {
                player.sendMessage(Config.message_cooling.replace("{time}", String.valueOf(((Config.items_walk_platform_cooldown * 1000) - System.currentTimeMillis() + this.cooldown.getOrDefault(player, 0L)) / 1000L + 1L)));
            } else {
                ItemStack stack = e.getItem();
                BedwarsUseItemEvent bedwarsUseItemEvent = new BedwarsUseItemEvent(game, player, EnumItem.WalkPlatform, stack);
                Bukkit.getPluginManager().callEvent(bedwarsUseItemEvent);
                if (!bedwarsUseItemEvent.isCancelled()) {
                    this.cooldown.put(player, System.currentTimeMillis());
                    TakeItemUtil.TakeItem(player, e.getItem());
                    if (this.tasks.get(game.getName()).containsKey(player))
                        ((BukkitTask) ((Map) this.tasks.get(game.getName())).get(player)).cancel();
                    runPlatform(player, game, game.getPlayerTeam(player));
                }
                e.setCancelled(true);
            }
        }
    }

    private void runPlatform(final Player player, final Game game, final Team team) {
        BukkitTask bukkittask = (new BukkitRunnable() {
            int i = 0;

            public void run() {
                if (this.i >= 20 * Config.items_walk_platform_break_time) {
                    cancel();
                    return;
                }
                this.i++;
                if (!player.isOnline()) {
                    cancel();
                    return;
                }
                if (player.isDead() || player.getGameMode() == GameMode.SPECTATOR || !game.getPlayers().contains(player) || game.isSpectator(player) || player.isSneaking()) {
                    cancel();
                    return;
                }
                Location location = player.getLocation().getBlock().getLocation().clone().add(0.0D, -1.0D, 0.0D);
                List<Block> blocks = new ArrayList<>();
                blocks.add(location.clone().add(1.0D, 0.0D, 1.0D).getBlock());
                blocks.add(location.clone().add(1.0D, 0.0D, 0.0D).getBlock());
                blocks.add(location.clone().add(1.0D, 0.0D, -1.0D).getBlock());
                blocks.add(location.clone().add(0.0D, 0.0D, 1.0D).getBlock());
                blocks.add(location.clone().add(0.0D, 0.0D, 0.0D).getBlock());
                blocks.add(location.clone().add(0.0D, 0.0D, -1.0D).getBlock());
                blocks.add(location.clone().add(-1.0D, 0.0D, 1.0D).getBlock());
                blocks.add(location.clone().add(-1.0D, 0.0D, 0.0D).getBlock());
                blocks.add(location.clone().add(-1.0D, 0.0D, -1.0D).getBlock());
                blocks.add(location.clone().add(2.0D, 0.0D, 1.0D).getBlock());
                blocks.add(location.clone().add(2.0D, 0.0D, 0.0D).getBlock());
                blocks.add(location.clone().add(2.0D, 0.0D, -1.0D).getBlock());
                blocks.add(location.clone().add(-2.0D, 0.0D, 1.0D).getBlock());
                blocks.add(location.clone().add(-2.0D, 0.0D, 0.0D).getBlock());
                blocks.add(location.clone().add(-2.0D, 0.0D, -1.0D).getBlock());
                blocks.add(location.clone().add(1.0D, 0.0D, 2.0D).getBlock());
                blocks.add(location.clone().add(0.0D, 0.0D, 2.0D).getBlock());
                blocks.add(location.clone().add(-1.0D, 0.0D, 2.0D).getBlock());
                blocks.add(location.clone().add(1.0D, 0.0D, -2.0D).getBlock());
                blocks.add(location.clone().add(0.0D, 0.0D, -2.0D).getBlock());
                blocks.add(location.clone().add(-1.0D, 0.0D, -2.0D).getBlock());
                for (Block block : blocks) {
                    if (block.getType() == Material.AIR && game.getRegion().isInRegion(block.getLocation())) {
                        game.getRegion().addPlacedBlock(block, null);
                        block.setType(Material.WOOL);
                        block.setData(team.getColor().getDyeColor().getWoolData());
                        WalkPlatform.this.blocktasks.get(game.getName()).put(block, (new BukkitRunnable() {
                            public void run() {
                                if (block.getType() == Material.WOOL)
                                    block.setType(Material.AIR);
                                ((Map) WalkPlatform.this.blocktasks.get(game.getName())).remove(block);
                            }
                        }).runTaskLater(YaBedwars.getInstance(), 1L));
                        continue;
                    }
                    if (WalkPlatform.this.blocktasks.get(game.getName()).containsKey(block)) {
                        ((BukkitTask) ((Map) WalkPlatform.this.blocktasks.get(game.getName())).get(block)).cancel();
                        WalkPlatform.this.blocktasks.get(game.getName()).put(block, (new BukkitRunnable() {
                            public void run() {
                                if (block.getType() == Material.WOOL && block.getData() == team.getColor().getDyeColor().getWoolData())
                                    block.setType(Material.AIR);
                                ((Map) WalkPlatform.this.blocktasks.get(game.getName())).remove(block);
                            }
                        }).runTaskLater(YaBedwars.getInstance(), 1L));
                    }
                }
            }
        }).runTaskTimer(YaBedwars.getInstance(), 0L, 1L);
        game.addRunningTask(bukkittask);
        this.tasks.get(game.getName()).put(player, bukkittask);
    }

    @EventHandler
    public void onOver(BedwarsGameOverEvent e) {
        this.blocktasks.get(e.getGame().getName()).values().forEach(BukkitTask::cancel);
        this.tasks.get(e.getGame().getName()).values().forEach(BukkitTask::cancel);
    }

    @EventHandler
    public void onDisable(PluginDisableEvent e) {
        if (e.getPlugin().equals(BedwarsRel.getInstance()) || e.getPlugin().equals(YaBedwars.getInstance()))
            for (Map<Block, BukkitTask> blocks : this.blocktasks.values()) {
                for (BukkitTask task : blocks.values())
                    task.cancel();
            }
    }
}
