package com.yallage.yabedwars.items;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;

import java.util.ArrayList;
import java.util.List;

import com.yallage.yabedwars.config.Config;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ExplosionProof implements Listener {
    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        if (!Config.items_explosion_proof_enabled)
            return;
        Location location = e.getLocation().getBlock().getLocation();
        Game game = null;
        for (Game g : BedwarsRel.getInstance().getGameManager().getGames()) {
            if (g.getState() == GameState.RUNNING && g.getRegion().isInRegion(location))
                game = g;
        }
        if (game == null)
            return;
        List<Block> blocklist = new ArrayList<>(e.blockList());
        List<Block> glass = new ArrayList<>();
        for (Block block : e.blockList()) {
            if (block.getType() == Material.GLASS || block.getType() == Material.STAINED_GLASS)
                glass.add(block);
        }
        e.blockList().removeAll(glass);
        List<Block> blocks = new ArrayList<>();
        for (Block block : e.blockList()) {
            for (Block glassblock : glass) {
                if (location.distance(block.getLocation()) > location.distance(glassblock.getLocation()) && location.distance(block.getLocation()) > block.getLocation().distance(glassblock.getLocation()))
                    blocks.add(block);
            }
        }
        e.blockList().removeAll(blocks);
        List<Block> addblocks = new ArrayList<>();
        addblocks.add(location.clone().add(0.0D, 1.0D, 0.0D).getBlock());
        addblocks.add(location.clone().add(0.0D, -1.0D, 0.0D).getBlock());
        addblocks.add(location.clone().add(0.0D, 0.0D, 1.0D).getBlock());
        addblocks.add(location.clone().add(0.0D, 0.0D, -1.0D).getBlock());
        addblocks.add(location.clone().add(1.0D, 0.0D, 0.0D).getBlock());
        addblocks.add(location.clone().add(-1.0D, 0.0D, 0.0D).getBlock());
        for (Block block : addblocks) {
            if (blocklist.contains(block) && block.getType() != Material.GLASS && block.getType() != Material.STAINED_GLASS)
                e.blockList().add(block);
        }
    }
}
