package ldcr.BedwarsXP;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameEndEvent;
import io.github.bedwarsrel.events.BedwarsGameStartEvent;
import io.github.bedwarsrel.game.Game;
import java.util.Arrays;
import ldcr.BedwarsXP.Utils.ResourceUtils;
import ldcr.BedwarsXP.Utils.SoundMachine;
import ldcr.BedwarsXP.XPShop.ShopReplacer;
import ldcr.BedwarsXP.api.XPManager;
import ldcr.BedwarsXP.api.events.BedwarsXPDeathDropXPEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EventListeners implements Listener {
  @EventHandler
  public void onItemPickup(PlayerPickupItemEvent e) {
    int count;
    Game bw = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(e.getPlayer());
    if (bw == null)
      return; 
    if (!Config.isGameEnabledXP(bw.getName()))
      return; 
    Player p = e.getPlayer();
    Item entity = e.getItem();
    ItemStack stack = entity.getItemStack();
    if (stack.hasItemMeta() && stack.getItemMeta().getDisplayName().equals("§b§l&BedwarsXP_DropedXP")) {
      count = Integer.valueOf(stack.getItemMeta().getLore().get(0)).intValue();
    } else {
      count = ResourceUtils.convertResToXP(stack);
    } 
    if (count == 0)
      return; 
    XPManager xpman = XPManager.getXPManager(bw.getName());
    if (Config.maxXP != 0 && 
      xpman.getXP(p) >= Config.maxXP) {
      e.setCancelled(true);
      entity.setPickupDelay(10);
      xpman.sendMaxXPMessage(p);
      return;
    } 
    int added = xpman.getXP(p) + count;
    int leftXP = 0;
    if (Config.maxXP != 0 && 
      added > Config.maxXP) {
      leftXP = added - Config.maxXP;
      added = Config.maxXP;
    } 
    xpman.setXP(p, added);
    p.playSound(p.getLocation(), SoundMachine.get("ORB_PICKUP", "ENTITY_EXPERIENCE_ORB_PICKUP"), 0.2F, 1.5F);
    xpman.sendXPMessage(p, count);
    if (leftXP > 0) {
      e.setCancelled(true);
      ItemStack s = stack.clone();
      ItemMeta meta = s.getItemMeta();
      meta.setDisplayName("§b§l&BedwarsXP_DropedXP");
      meta.setLore(Arrays.asList(new String[] { String.valueOf(leftXP) }));
      s.setItemMeta(meta);
      entity.setItemStack(s);
      entity.setPickupDelay(10);
    } else {
      e.setCancelled(true);
      entity.remove();
    } 
  }
  
  @EventHandler
  public void onAnvilOpen(InventoryOpenEvent e) {
    if (e.getPlayer().equals(null))
      return; 
    if (e.getInventory().equals(null))
      return; 
    Game bw = BedwarsRel.getInstance().getGameManager().getGameOfPlayer((Player)e.getPlayer());
    if (bw == null)
      return; 
    if (!Config.isGameEnabledXP(bw.getName()))
      return; 
    if (e.getInventory().getType().equals(InventoryType.ANVIL))
      e.setCancelled(true); 
  }
  
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent e) {
    Game bw = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(e.getEntity());
    if (bw == null)
      return; 
    if (!Config.isGameEnabledXP(bw.getName()))
      return; 
    XPManager xpman = XPManager.getXPManager(bw.getName());
    Player p = e.getEntity();
    int costed = (int)(xpman.getXP(p) * Config.deathCost);
    int dropped = 0;
    if (Config.deathDrop > 0.0D)
      dropped = (int)(costed * Config.deathDrop); 
    BedwarsXPDeathDropXPEvent event = new BedwarsXPDeathDropXPEvent(bw.getName(), p, dropped, costed);
    Bukkit.getPluginManager().callEvent((Event)event);
    costed = event.getXPCosted();
    dropped = event.getXPDropped();
    int to = xpman.getXP(p) - costed;
    if (to < 0)
      to = 0; 
    e.setNewLevel(to);
    xpman.setXP(p, to);
    if (Config.deathDrop > 0.0D) {
      if (dropped < 1)
        return; 
      ItemStack dropStack = new ItemStack(Material.EXP_BOTTLE, 1);
      ItemMeta meta = dropStack.getItemMeta();
      meta.setDisplayName("§b§l&BedwarsXP_DropedXP");
      meta.setLore(Arrays.asList(new String[] { String.valueOf(dropped) }));
      meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, 1, true);
      dropStack.setItemMeta(meta);
      Item droppedItem = p.getWorld().dropItemNaturally(p.getLocation().add(0.0D, 1.0D, 0.0D), dropStack);
      droppedItem.setPickupDelay(40);
    } 
  }
  
  @EventHandler
  public void onBedWarsStart(BedwarsGameStartEvent e) {
    if (e.isCancelled())
      return; 
    if (!Config.isGameEnabledXP(e.getGame().getName()))
      return; 
    ShopReplacer.replaceShop(e.getGame().getName(), BedwarsXP.log);
  }
  
  @EventHandler
  public void onBedWarsEnd(BedwarsGameEndEvent e) {
    if (!Config.isGameEnabledXP(e.getGame().getName()))
      return; 
    XPManager.reset(e.getGame().getName());
  }
  
  @EventHandler
  public void onPlayerTeleport(PlayerTeleportEvent e) {
    final Game bw = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(e.getPlayer());
    if (bw == null)
      return; 
    if (!Config.isGameEnabledXP(bw.getName()))
      return; 
    final Player p = e.getPlayer();
    Bukkit.getScheduler().runTaskLater(BedwarsXP.plugin, new Runnable() {
          public void run() {
            XPManager.getXPManager(bw.getName()).updateXPBar(p);
          }
        },  5L);
  }
  
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent e) {
    Game bw = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(e.getPlayer());
    if (bw == null)
      return; 
    if (!Config.isGameEnabledXP(bw.getName()))
      return; 
    XPManager.getXPManager(bw.getName()).updateXPBar(e.getPlayer());
  }
}
