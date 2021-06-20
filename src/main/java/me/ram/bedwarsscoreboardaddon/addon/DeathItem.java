package me.ram.bedwarsscoreboardaddon.addon;

import io.github.bedwarsrel.events.BedwarsPlayerKilledEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import java.util.HashMap;
import java.util.Map;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.ItemUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class DeathItem implements Listener {
  @EventHandler
  public void onKilled(BedwarsPlayerKilledEvent e) {
    if (e.getKiller() == null || e.getPlayer() == null)
      return; 
    Player killer = e.getKiller();
    Player player = e.getPlayer();
    Game game = e.getGame();
    if (game.getState() != GameState.RUNNING)
      return; 
    if (game.getPlayerTeam(player) == null || game.getPlayerTeam(killer) == null)
      return; 
    if (!Config.deathitem_enabled)
      return; 
    if (game.isSpectator(killer) || killer.isDead() || killer.getGameMode().equals(GameMode.SPECTATOR))
      return; 
    Map<ItemStack, Integer> playeritems = new HashMap<>();
    ItemStack[] itemStacks = player.getInventory().getContents();
    for (int i = 0; i < itemStacks.length; i++) {
      ItemStack itemStack = itemStacks[i];
      if (itemStack != null && !itemStack.getType().equals(Material.AIR))
        try {
          for (String items : Config.deathitem_items) {
            if (itemStack.getType().equals(Material.valueOf(items))) {
              Boolean l = Boolean.valueOf(true);
              for (ItemStack item : playeritems.keySet()) {
                if (item.getType() == itemStack.getType()) {
                  playeritems.put(item, Integer.valueOf(((Integer)playeritems.get(item)).intValue() + itemStack.getAmount()));
                  l = Boolean.valueOf(false);
                  break;
                } 
              } 
              if (l.booleanValue())
                playeritems.put(itemStack, Integer.valueOf(itemStack.getAmount())); 
            } 
          } 
        } catch (Exception exception) {} 
    } 
    for (ItemStack item : playeritems.keySet()) {
      ItemStack itemStack = item.clone();
      itemStack.setAmount((Integer) playeritems.get(item));
      killer.getInventory().addItem(itemStack);
      String itemName = item.getType().name();
      if (Config.deathitem_item_name_chinesize)
        itemName = ItemUtil.getRealName(item);
      killer.sendMessage(String.valueOf(ItemUtil.getRealColor(item)) + Config.deathitem_message
          .replace("{amount}", playeritems.get(item).toString()).replace("{item}", itemName));
    } 
  }
}
