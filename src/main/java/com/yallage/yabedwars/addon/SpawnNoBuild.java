package com.yallage.yabedwars.addon;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.ResourceSpawner;
import io.github.bedwarsrel.game.Team;
import com.yallage.yabedwars.config.Config;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Bed;

public class SpawnNoBuild implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent e) {
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(e.getPlayer());
        if (game == null || !Config.spawn_no_build_enabled)
            return;
        if (game.getState() == GameState.RUNNING) {
            Block block = e.getBlock();
            Player player = e.getPlayer();
            for (Team team : game.getTeams().values()) {
                if (team.getSpawnLocation()
                        .distance(block.getLocation().clone().add(0.5D, 0.0D, 0.5D)) <= Config.spawn_no_build_spawn_range) {
                    e.setCancelled(true);
                    player.sendMessage(Config.spawn_no_build_message);
                    return;
                }
            }
            for (ResourceSpawner spawner : game.getResourceSpawners()) {
                if (spawner.getLocation().distance(
                        block.getLocation().clone().add(0.5D, 0.0D, 0.5D)) <= Config.spawn_no_build_resource_range) {
                    e.setCancelled(true);
                    player.sendMessage(Config.spawn_no_build_message);
                    return;
                }
            }
        }
    }
    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(event.getPlayer());
            Block block = event.getClickedBlock();
            if (block.getType() == Material.CHEST) {
                for (Team team : game.getTeams().values()) {
                    if (team.getSpawnLocation()
                            .distance(block.getLocation().clone().add(0.5D, 0.0D, 0.5D)) <= Config.spawn_no_build_spawn_range) {
                        if (game.getPlayerTeam(event.getPlayer()) != team && !team.isDead(game)) {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage(ChatColor.RED + "这不是你队伍的箱子！");
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFluidPlace(PlayerBucketEmptyEvent e) {
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(e.getPlayer());
        if (game == null || !Config.spawn_no_build_enabled)
            return;
        if (game.getState() == GameState.RUNNING) {
            Block block = e.getBlockClicked().getRelative(e.getBlockFace());
            Player player = e.getPlayer();
            for (Team team : game.getTeams().values()) {
                if (team.getSpawnLocation()
                        .distance(block.getLocation().clone().add(0.5D, 0.0D, 0.5D)) <= Config.spawn_no_build_spawn_range) {
                    e.setCancelled(true);
                    player.sendMessage(Config.spawn_no_build_message);
                    return;
                }
            }
            for (ResourceSpawner spawner : game.getResourceSpawners()) {
                if (spawner.getLocation().distance(
                        block.getLocation().clone().add(0.5D, 0.0D, 0.5D)) <= Config.spawn_no_build_resource_range) {
                    e.setCancelled(true);
                    player.sendMessage(Config.spawn_no_build_message);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFluidFlow(BlockFromToEvent e) {
        Game game = BedwarsRel.getInstance().getGameManager().getGameByLocation(e.getToBlock().getLocation());
        if (game == null || !Config.spawn_no_build_enabled)
            return;
        if (game.getState() == GameState.RUNNING) {
            Block block = e.getToBlock();
            for (Team team : game.getTeams().values()) {
                if (team.getSpawnLocation()
                        .distance(block.getLocation().clone().add(0.5D, 0.0D, 0.5D)) <= Config.spawn_no_build_spawn_range) {
                    e.setCancelled(true);
                    return;
                }
            }
            for (ResourceSpawner spawner : game.getResourceSpawners()) {
                if (spawner.getLocation().distance(
                        block.getLocation().clone().add(0.5D, 0.0D, 0.5D)) <= Config.spawn_no_build_resource_range) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }
}
