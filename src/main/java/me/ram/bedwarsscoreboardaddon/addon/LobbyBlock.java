package me.ram.bedwarsscoreboardaddon.addon;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.config.Config;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class LobbyBlock {
  private Game game;
  
  private Map<Block, BlockStorage> lobbyblocks = new HashMap<>();
  
  public LobbyBlock(final Game game) {
    this.game = game;
    Location lobby = game.getLobby().clone();
    if (!Config.lobby_block_enabled)
      return; 
    Block block = lobby.getBlock();
    if (block == null || block.getType().equals(Material.AIR))
      block = lobby.clone().add(0.0D, -1.0D, 0.0D).getBlock(); 
    if (block == null || block.getType().equals(Material.AIR))
      return; 
    removeBlock(block.getLocation());
    (new BukkitRunnable() {
        public void run() {
          if (game.getState() != GameState.RUNNING) {
            cancel();
            LobbyBlock.this.recovery();
          } 
        }
      }).runTaskTimer((Plugin)Main.getInstance(), 0L, 0L);
  }
  
  public void recovery() {
    for (Block block : this.lobbyblocks.keySet()) {
      this.game.getRegion().removePlacedBlock(block);
      ((BlockStorage)this.lobbyblocks.get(block)).getBlock(block);
    } 
    this.lobbyblocks.clear();
  }
  
  public Game getGame() {
    return this.game;
  }
  
  private void removeBlock(Location loc) {
    Location location = loc.clone();
    Location location1 = location.clone().add(Config.lobby_block_position_1_x, Config.lobby_block_position_1_y, 
        Config.lobby_block_position_1_z);
    Location location2 = location.clone().add(Config.lobby_block_position_2_x, Config.lobby_block_position_2_y, 
        Config.lobby_block_position_2_z);
    for (Iterator<Integer> iterator = getAllNumber((int)location1.getX(), (int)location2.getX()).iterator(); iterator.hasNext(); ) {
      int X = ((Integer)iterator.next()).intValue();
      location.setX(X);
      for (Iterator<Integer> iterator1 = getAllNumber((int)location1.getY(), (int)location2.getY()).iterator(); iterator1.hasNext(); ) {
        int Y = ((Integer)iterator1.next()).intValue();
        location.setY(Y);
        for (Iterator<Integer> iterator2 = getAllNumber((int)location1.getZ(), (int)location2.getZ()).iterator(); iterator2.hasNext(); ) {
          int Z = ((Integer)iterator2.next()).intValue();
          location.setZ(Z);
          Block block = location.getBlock();
          if (block != null && !block.getType().equals(Material.AIR)) {
            this.lobbyblocks.put(block, new BlockStorage(block));
            block.setType(Material.AIR);
          } 
        } 
      } 
    } 
  }
  
  private List<Integer> getAllNumber(int a, int b) {
    List<Integer> nums = new ArrayList<>();
    int min = a;
    int max = b;
    if (a > b) {
      min = b;
      max = a;
    } 
    for (int i = min; i < max + 1; i++)
      nums.add(Integer.valueOf(i)); 
    return nums;
  }
  
  private class BlockStorage {
    private Material type;
    
    private byte data;
    
    private MaterialData materialData;
    
    private BlockStorage(Block block) {
      this.type = block.getType();
      this.data = block.getData();
      this.materialData = block.getState().getData();
    }
    
    private Block getBlock(Block block) {
      block.setType(this.type);
      block.setData(this.data);
      block.getState().setData(this.materialData);
      return block;
    }
  }
}
