package me.ram.bedwarsscoreboardaddon.edit;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsPlayerLeaveEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.game.TeamColor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class EditGame implements Listener {
  public EditGame() {
    onPacketSending();
  }
  
  public static void editGame(Player player, Game game) {
    openMenu(player, game);
    removeEditItem(player);
    giveItems(player, game);
  }
  
  private static void openMenu(Player player, Game game) {
    Inventory inventory = Bukkit.createInventory(null, 54, 
        "§e§d§i§t§8" + Config.getLanguage("inventory.edit_game") + " - " + game.getName());
    ItemStack itemStack = new ItemStack(Material.ENDER_PEARL);
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.set_lobby"));
    List<String> lore = new ArrayList<>();
    lore.add("");
    if (game.getLobby() == null) {
      lore.add(Config.getLanguage("item.edit_game.lore.set"));
    } else {
      lore.add(Config.getLanguage("item.edit_game.lore.complete"));
    } 
    itemMeta.setLore(lore);
    itemStack.setItemMeta(itemMeta);
    inventory.setItem(11, itemStack);
    itemStack.setType(Material.FIREWORK);
    itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.set_mix_players"));
    lore = new ArrayList<>();
    lore.add("");
    lore.add(Config.getLanguage("item.edit_game.lore.mix_players").replace("{players}", (new StringBuilder(String.valueOf(game.getMinPlayers()))).toString()));
    itemMeta.setLore(lore);
    itemStack.setItemMeta(itemMeta);
    inventory.setItem(12, itemStack);
    itemStack.setType(Material.SIGN);
    itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.edit_team"));
    lore = new ArrayList<>();
    lore.add("");
    lore.add(Config.getLanguage("item.edit_game.lore.browse"));
    itemMeta.setLore(lore);
    itemStack.setItemMeta(itemMeta);
    inventory.setItem(13, itemStack);
    itemStack.setType(Material.NETHER_STAR);
    itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.set_bed"));
    lore = new ArrayList<>();
    lore.add("");
    lore.add(Config.getLanguage("item.edit_game.lore.browse"));
    itemMeta.setLore(lore);
    itemStack.setItemMeta(itemMeta);
    inventory.setItem(14, itemStack);
    itemStack.setType(Material.FEATHER);
    itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.set_spawn"));
    lore = new ArrayList<>();
    lore.add("");
    lore.add(Config.getLanguage("item.edit_game.lore.browse"));
    itemMeta.setLore(lore);
    itemStack.setItemMeta(itemMeta);
    inventory.setItem(15, itemStack);
    itemStack.setType(Material.STORAGE_MINECART);
    itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.set_item_shop"));
    lore = new ArrayList<>();
    lore.add("");
    lore.add(Config.getLanguage("item.edit_game.lore.set"));
    itemMeta.setLore(lore);
    itemStack.setItemMeta(itemMeta);
    inventory.setItem(20, itemStack);
    itemStack.setType(Material.FIREBALL);
    itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.set_team_shop"));
    lore = new ArrayList<>();
    lore.add("");
    lore.add(Config.getLanguage("item.edit_game.lore.set"));
    itemMeta.setLore(lore);
    itemStack.setItemMeta(itemMeta);
    inventory.setItem(21, itemStack);
    itemStack.setType(Material.BLAZE_POWDER);
    itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.set_spawner"));
    lore = new ArrayList<>();
    lore.add("");
    lore.add(Config.getLanguage("item.edit_game.lore.browse"));
    itemMeta.setLore(lore);
    itemStack.setItemMeta(itemMeta);
    inventory.setItem(22, itemStack);
    itemStack.setType(Material.BLAZE_ROD);
    itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.set_region_loc1"));
    lore = new ArrayList<>();
    lore.add("");
    if (game.getLoc1() == null) {
      lore.add(Config.getLanguage("item.edit_game.lore.set"));
    } else {
      lore.add(Config.getLanguage("item.edit_game.lore.complete"));
    } 
    itemMeta.setLore(lore);
    itemStack.setItemMeta(itemMeta);
    inventory.setItem(23, itemStack);
    itemStack.setType(Material.STICK);
    itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.set_region_loc2"));
    lore = new ArrayList<>();
    lore.add("");
    if (game.getLoc2() == null) {
      lore.add(Config.getLanguage("item.edit_game.lore.set"));
    } else {
      lore.add(Config.getLanguage("item.edit_game.lore.complete"));
    } 
    itemMeta.setLore(lore);
    itemStack.setItemMeta(itemMeta);
    inventory.setItem(24, itemStack);
    itemStack.setType(Material.WOOL);
    itemStack.setDurability((short)3);
    itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.save_game"));
    itemMeta.setLore(new ArrayList());
    itemStack.setItemMeta(itemMeta);
    inventory.setItem(39, itemStack);
    itemStack.setDurability((short)5);
    itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.start_game"));
    itemStack.setItemMeta(itemMeta);
    inventory.setItem(40, itemStack);
    itemStack.setDurability((short)14);
    itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.stop_game"));
    itemStack.setItemMeta(itemMeta);
    inventory.setItem(41, itemStack);
    player.closeInventory();
    player.openInventory(inventory);
    Main.getInstance().getHolographicManager().displayGameLocation(player, game.getName());
  }
  
  private static void giveItems(Player player, Game game) {
    ItemStack itemStack = new ItemStack(Material.BOOK);
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.menu_item"));
    List<String> lore = new ArrayList<>();
    String line = "bwsba-editgame-menu-" + game.getName();
    line = "§" + line.replaceAll("(.{1})", "$1§");
    lore.add(line.substring(0, line.length() - 1));
    itemMeta.setLore(lore);
    itemStack.setItemMeta(itemMeta);
    player.getInventory().setItem(0, itemStack);
    itemStack.setType(Material.SKULL_ITEM);
    itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.align_angle_item"));
    lore.clear();
    line = "bwsba-editgame-align";
    line = "§" + line.replaceAll("(.{1})", "$1§");
    lore.add(line.substring(0, line.length() - 1));
    itemMeta.setLore(lore);
    itemStack.setItemMeta(itemMeta);
    player.getInventory().setItem(1, itemStack);
    itemStack.setType(Material.ARMOR_STAND);
    itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.align_location_item"));
    lore.clear();
    line = "bwsba-editgame-align2";
    line = "§" + line.replaceAll("(.{1})", "$1§");
    lore.add(line.substring(0, line.length() - 1));
    itemMeta.setLore(lore);
    itemStack.setItemMeta(itemMeta);
    player.getInventory().setItem(2, itemStack);
    Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
    if (plugin != null && plugin.isEnabled()) {
      itemStack.setType(Material.getMaterial(
            (((WorldEditPlugin)plugin).getWorldEdit().getPlatformManager().getConfiguration()).navigationWand));
      itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.teleport_tool"));
      lore.clear();
      line = "bwsba-editgame-teleport";
      line = "§" + line.replaceAll("(.{1})", "$1§");
      lore.add(line.substring(0, line.length() - 1));
      itemMeta.setLore(lore);
      itemStack.setItemMeta(itemMeta);
      player.getInventory().setItem(3, itemStack);
    } 
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInteract(PlayerInteractEvent e) {
    if (!e.getAction().equals(Action.RIGHT_CLICK_AIR) && !e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
      return; 
    ItemStack itemStack = e.getItem();
    if (itemStack == null || itemStack.getType().equals(Material.AIR))
      return; 
    ItemMeta itemMeta = itemStack.getItemMeta();
    if (!itemMeta.hasLore())
      return; 
    Player player = e.getPlayer();
    String l = ((String)itemMeta.getLore().get(0)).replace("§", "");
    if (l.startsWith("bwsba-editgame-menu-")) {
      e.setCancelled(true);
      if (player.hasPermission("bedwarsscoreboardaddon.edit")) {
        Game game = BedwarsRel.getInstance().getGameManager().getGame(l.substring(20, l.length()));
        if (game != null)
          openMenu(player, game); 
      } 
    } else if (l.equals("bwsba-editgame-align")) {
      e.setCancelled(true);
      if (player.hasPermission("bedwarsscoreboardaddon.edit"))
        alignAngle(player); 
    } else if (l.equals("bwsba-editgame-align2")) {
      e.setCancelled(true);
      if (player.hasPermission("bedwarsscoreboardaddon.edit"))
        alignLocation(player); 
    } 
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInteractEntity(PlayerInteractEntityEvent e) {
    ItemStack itemStack;
    Player player = e.getPlayer();
    itemStack = player.getItemInHand();
    if (itemStack == null || itemStack.getType().equals(Material.AIR))
      return; 
    ItemMeta itemMeta = itemStack.getItemMeta();
    if (!itemMeta.hasLore())
      return; 
    String l = ((String)itemMeta.getLore().get(0)).replace("§", "");
    if (l.startsWith("bwsba-editgame-menu-")) {
      e.setCancelled(true);
      if (player.hasPermission("bedwarsscoreboardaddon.edit")) {
        Game game = BedwarsRel.getInstance().getGameManager().getGame(l.substring(20, l.length()));
        if (game != null)
          openMenu(player, game); 
      } 
    } else if (l.equals("bwsba-editgame-align")) {
      e.setCancelled(true);
      if (player.hasPermission("bedwarsscoreboardaddon.edit"))
        alignAngle(player); 
    } else if (l.equals("bwsba-editgame-align2")) {
      e.setCancelled(true);
      if (player.hasPermission("bedwarsscoreboardaddon.edit"))
        alignLocation(player); 
    } 
  }
  
  private void alignLocation(Player player) {
    Location location = player.getLocation().clone();
    BigDecimal bd = new BigDecimal((new StringBuilder(String.valueOf(location.getX()))).toString());
    BigDecimal[] result = bd.divideAndRemainder(BigDecimal.valueOf(1L));
    double xd = Double.valueOf(result[1].toString()).doubleValue();
    List<Double> list = Arrays.asList(new Double[] { Double.valueOf(-1.0D), Double.valueOf(-0.5D), Double.valueOf(0.0D), Double.valueOf(0.5D), Double.valueOf(1.0D) });
    double a = Math.abs(((Double)list.get(0)).doubleValue() - xd);
    double nxd = ((Double)list.get(0)).doubleValue();
    for (Double i : list) {
      double j = Math.abs(i.doubleValue() - xd);
      if (j < a) {
        a = j;
        nxd = i.doubleValue();
      } 
    } 
    location.setX((int)location.getX() + nxd);
    bd = new BigDecimal((new StringBuilder(String.valueOf(location.getZ()))).toString());
    result = bd.divideAndRemainder(BigDecimal.valueOf(1L));
    double zd = Double.valueOf(result[1].toString()).doubleValue();
    list = Arrays.asList(new Double[] { Double.valueOf(-1.0D), Double.valueOf(-0.5D), Double.valueOf(0.0D), Double.valueOf(0.5D), Double.valueOf(1.0D) });
    a = Math.abs(((Double)list.get(0)).doubleValue() - zd);
    double nzd = ((Double)list.get(0)).doubleValue();
    for (Double i : list) {
      double j = Math.abs(i.doubleValue() - zd);
      if (j < a) {
        a = j;
        nzd = i.doubleValue();
      } 
    } 
    location.setZ((int)location.getZ() + nzd);
    player.teleport(location);
  }
  
  private void alignAngle(Player player) {
    Location location = player.getLocation().clone();
    List<Double> list = new ArrayList<>();
    for (double d1 = -360.0D; d1 <= 360.0D; d1 += 22.5D)
      list.add(Double.valueOf(d1)); 
    double yaw = player.getLocation().getYaw();
    double a = Math.abs(((Double)list.get(0)).doubleValue() - yaw);
    double nyaw = ((Double)list.get(0)).doubleValue();
    for (Double double_ : list) {
      double j = Math.abs(double_.doubleValue() - yaw);
      if (j < a) {
        a = j;
        nyaw = double_.doubleValue();
      } 
    } 
    location.setYaw((float)nyaw);
    list = new ArrayList<>();
    for (double i = -90.0D; i <= 90.0D; i += 22.5D)
      list.add(Double.valueOf(i)); 
    double pitch = player.getLocation().getPitch();
    a = Math.abs(((Double)list.get(0)).doubleValue() - pitch);
    double npitch = ((Double)list.get(0)).doubleValue();
    for (Double double_ : list) {
      double j = Math.abs(double_.doubleValue() - pitch);
      if (j < a) {
        a = j;
        npitch = double_.doubleValue();
      } 
    } 
    location.setPitch((float)npitch);
    player.teleport(location);
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onClick(InventoryClickEvent e) {
    Player player = (Player)e.getWhoClicked();
    Inventory inventory = e.getInventory();
    int slot = e.getRawSlot();
    String title = inventory.getTitle();
    String tit = Config.getLanguage("inventory.edit_game");
    if (title.startsWith("§e§d§i§t§8" + tit + " - ")) {
      e.setCancelled(true);
      String game_name = title.substring(13 + tit.length(), title.length());
      Game game = BedwarsRel.getInstance().getGameManager().getGame(game_name);
      if (game != null)
        switch (slot) {
          case 11:
            player.closeInventory();
            Bukkit.dispatchCommand((CommandSender)player, "bedwarsrel:bw setlobby " + game_name);
            Main.getInstance().getHolographicManager().displayGameLocation(player, game.getName());
            break;
          case 12:
            openAnvilInventory(player, Config.getLanguage("anvil.edit_game.set_mix_players"), 
                "bedwarsrel:bw setminplayers " + game_name + " {value}");
            break;
          case 13:
            openEditTeamsMenu(player, game);
            break;
          case 14:
            openEditTeamsLocation(player, game, true);
            break;
          case 15:
            openEditTeamsLocation(player, game, false);
            break;
          case 20:
            player.closeInventory();
            Bukkit.dispatchCommand((CommandSender)player, "bedwarsscoreboardaddon:bwsba shop set item " + game_name);
            Main.getInstance().getHolographicManager().displayGameLocation(player, game.getName());
            break;
          case 21:
            player.closeInventory();
            Bukkit.dispatchCommand((CommandSender)player, "bedwarsscoreboardaddon:bwsba shop set team " + game_name);
            Main.getInstance().getHolographicManager().displayGameLocation(player, game.getName());
            break;
          case 22:
            openEditSpawner(player, game);
            break;
          case 23:
            player.closeInventory();
            Bukkit.dispatchCommand((CommandSender)player, "bedwarsrel:bw setregion " + game_name + " loc1");
            Main.getInstance().getHolographicManager().displayGameLocation(player, game.getName());
            break;
          case 24:
            player.closeInventory();
            Bukkit.dispatchCommand((CommandSender)player, "bedwarsrel:bw setregion " + game_name + " loc2");
            Main.getInstance().getHolographicManager().displayGameLocation(player, game.getName());
            break;
          case 39:
            player.closeInventory();
            Bukkit.dispatchCommand((CommandSender)player, "bedwarsrel:bw save " + game_name);
            break;
          case 40:
            player.closeInventory();
            Bukkit.dispatchCommand((CommandSender)player, "bedwarsrel:bw start " + game_name);
            break;
          case 41:
            player.closeInventory();
            Bukkit.dispatchCommand((CommandSender)player, "bedwarsrel:bw stop " + game_name);
            break;
        }  
    } else if (title.startsWith("§e§d§i§t§t§e§a§m§8" + tit + " - ")) {
      e.setCancelled(true);
      String game_name = title.substring(21 + tit.length(), title.length());
      Game game = BedwarsRel.getInstance().getGameManager().getGame(game_name);
      if (game != null) {
        if (slot == 49) {
          openMenu(player, game);
          return;
        } 
        ItemStack itemStack = e.getCurrentItem();
        if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
          ItemMeta itemMeta = itemStack.getItemMeta();
          if (((String)itemMeta.getLore().get(0)).equals("§0")) {
            openAnvilInventory(player, Config.getLanguage("anvil.edit_game.set_team_name"), 
                "bedwarsrel:bw addteam " + game_name + " {value} " + 
                ColorUtil.remcolor(itemMeta.getDisplayName()));
          } else if (((String)itemMeta.getLore().get(0)).equals("§1")) {
            player.closeInventory();
            Bukkit.dispatchCommand((CommandSender)player, "bedwarsrel:bw removeteam " + game_name + " " + 
                ColorUtil.remcolor(itemMeta.getDisplayName()));
            Main.getInstance().getHolographicManager().displayGameLocation(player, game.getName());
          } 
        } 
      } 
    } else if (title.startsWith("§e§d§i§t§t§e§a§m§l§8" + tit + " - ")) {
      e.setCancelled(true);
      String game_name = title.substring(23 + tit.length(), title.length());
      Game game = BedwarsRel.getInstance().getGameManager().getGame(game_name);
      if (game != null) {
        if (slot == 49) {
          openMenu(player, game);
          return;
        } 
        ItemStack itemStack = e.getCurrentItem();
        if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
          ItemMeta itemMeta = itemStack.getItemMeta();
          if (((String)itemMeta.getLore().get(0)).equals("§0")) {
            player.closeInventory();
            Bukkit.dispatchCommand((CommandSender)player, "bedwarsrel:bw setbed " + game_name + " " + 
                ColorUtil.remcolor(itemMeta.getDisplayName()));
            Main.getInstance().getHolographicManager().displayGameLocation(player, game.getName());
          } else if (((String)itemMeta.getLore().get(0)).equals("§1")) {
            player.closeInventory();
            Bukkit.dispatchCommand((CommandSender)player, "bedwarsrel:bw setspawn " + game_name + " " + 
                ColorUtil.remcolor(itemMeta.getDisplayName()));
            Main.getInstance().getHolographicManager().displayGameLocation(player, game.getName());
          } 
        } 
      } 
    } else if (title.startsWith("§e§d§i§t§s§8" + tit + " - ")) {
      e.setCancelled(true);
      String game_name = title.substring(15 + tit.length(), title.length());
      Game game = BedwarsRel.getInstance().getGameManager().getGame(game_name);
      if (game != null) {
        if (slot == 49) {
          openMenu(player, game);
          return;
        } 
        ItemStack itemStack = e.getCurrentItem();
        if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
          ItemMeta itemMeta = itemStack.getItemMeta();
          player.closeInventory();
          Bukkit.dispatchCommand((CommandSender)player, 
              "bedwarsrel:bw setspawner " + game_name + " " + ((String)itemMeta.getLore().get(0)).replace("§", ""));
          Main.getInstance().getHolographicManager().displayGameLocation(player, game.getName());
        } 
      } 
    } 
  }
  
  @EventHandler
  public void onJoin(final PlayerJoinEvent e) {
    (new BukkitRunnable() {
        public void run() {
          EditGame.removeEditItem(e.getPlayer());
        }
      }).runTaskLater((Plugin)Main.getInstance(), 1L);
  }
  
  @EventHandler
  public void onLeave(final BedwarsPlayerLeaveEvent e) {
    (new BukkitRunnable() {
        public void run() {
          EditGame.removeEditItem(e.getPlayer());
        }
      }).runTaskLater((Plugin)Main.getInstance(), 1L);
  }
  
  @EventHandler
  public void onDisable(PluginDisableEvent e) {
    if (e.getPlugin().equals(Main.getInstance()) || e.getPlugin().equals(BedwarsRel.getInstance()))
      for (Player player : Bukkit.getOnlinePlayers())
        removeEditItem(player);  
  }
  
  private static void removeEditItem(Player player) {
    if (player.isOnline()) {
      ItemStack[] itemStacks = player.getInventory().getContents();
      for (int i = 0; i < itemStacks.length; i++) {
        ItemStack itemStack = itemStacks[i];
        if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
          ItemMeta itemMeta = itemStack.getItemMeta();
          if (itemMeta.hasLore()) {
            String l = ((String)itemMeta.getLore().get(0)).replace("§", "");
            if (l.startsWith("bwsba-editgame-"))
              try {
                player.getInventory().setItem(i, new ItemStack(Material.AIR));
              } catch (Exception exception) {} 
          } 
        } 
      } 
    } 
  }
  
  private static void openEditTeamsMenu(Player player, Game game) {
    Inventory inventory = Bukkit.createInventory(null, 54, 
        "§e§d§i§t§t§e§a§m§8" + Config.getLanguage("inventory.edit_game") + " - " + game.getName());
    List<TeamColor> colors = new ArrayList<>();
    colors.addAll(Arrays.asList(TeamColor.values()));
    List<ItemStack> items = new ArrayList<>();
    for (Team team : game.getTeams().values()) {
      TeamColor teamColor = team.getColor();
      ItemStack itemStack1 = new ItemStack(Material.WOOL);
      ItemMeta itemMeta1 = itemStack1.getItemMeta();
      itemStack1.setDurability(teamColor.getDyeColor().getWoolData());
      itemMeta1.setDisplayName(teamColor.getChatColor() + team.getName());
      List<String> lore = new ArrayList<>();
      lore.add("§1");
      lore.add(Config.getLanguage("item.edit_game.lore.max_players").replace("{players}", (
            new StringBuilder(String.valueOf(team.getMaxPlayers()))).toString()));
      lore.add("");
      lore.add(Config.getLanguage("item.edit_game.lore.remove"));
      itemMeta1.setLore(lore);
      itemStack1.setItemMeta(itemMeta1);
      items.add(itemStack1);
      colors.remove(teamColor);
    } 
    for (TeamColor teamColor : colors) {
      ItemStack itemStack1 = new ItemStack(Material.WOOL);
      ItemMeta itemMeta1 = itemStack1.getItemMeta();
      itemStack1.setDurability(teamColor.getDyeColor().getWoolData());
      itemMeta1.setDisplayName(teamColor.getChatColor() + teamColor.name());
      List<String> lore = new ArrayList<>();
      lore.add("§0");
      lore.add(Config.getLanguage("item.edit_game.lore.add"));
      itemMeta1.setLore(lore);
      itemStack1.setItemMeta(itemMeta1);
      items.add(itemStack1);
    } 
    int i = 10;
    for (ItemStack item : items) {
      if (i == 17) {
        i = 19;
      } else if (i == 26) {
        i = 28;
      } 
      inventory.setItem(i, item);
      i++;
    } 
    ItemStack itemStack = new ItemStack(Material.ARROW);
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.back"));
    itemStack.setItemMeta(itemMeta);
    inventory.setItem(49, itemStack);
    player.closeInventory();
    player.openInventory(inventory);
  }
  
  private static void openEditTeamsLocation(Player player, Game game, boolean isbed) {
    Inventory inventory = Bukkit.createInventory(null, 54, 
        "§e§d§i§t§t§e§a§m§l§8" + Config.getLanguage("inventory.edit_game") + " - " + game.getName());
    int i = 10;
    for (Team team : game.getTeams().values()) {
      if (i == 17) {
        i = 19;
      } else if (i == 26) {
        i = 28;
      } 
      TeamColor teamColor = team.getColor();
      ItemStack itemStack1 = new ItemStack(Material.WOOL);
      ItemMeta itemMeta1 = itemStack1.getItemMeta();
      itemStack1.setDurability(teamColor.getDyeColor().getWoolData());
      itemMeta1.setDisplayName(teamColor.getChatColor() + team.getName());
      List<String> lore = new ArrayList<>();
      lore.add(isbed ? "§0" : "§1");
      if (isbed) {
        if (team.getTargetHeadBlock() == null && team.getTargetFeetBlock() == null) {
          lore.add(Config.getLanguage("item.edit_game.lore.set"));
        } else {
          lore.add(Config.getLanguage("item.edit_game.lore.complete"));
        } 
      } else if (team.getSpawnLocation() == null) {
        lore.add(Config.getLanguage("item.edit_game.lore.set"));
      } else {
        lore.add(Config.getLanguage("item.edit_game.lore.complete"));
      } 
      itemMeta1.setLore(lore);
      itemStack1.setItemMeta(itemMeta1);
      inventory.setItem(i, itemStack1);
      i++;
    } 
    ItemStack itemStack = new ItemStack(Material.ARROW);
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.back"));
    itemStack.setItemMeta(itemMeta);
    inventory.setItem(49, itemStack);
    player.closeInventory();
    player.openInventory(inventory);
  }
  
  private static void openEditSpawner(Player player, Game game) {
    String title = Config.getLanguage("inventory.edit_game");
    Inventory inventory = Bukkit.createInventory(null, 54, "§e§d§i§t§s§8" + title + " - " + game.getName());
    FileConfiguration config = BedwarsRel.getInstance().getConfig();
    int i = 10;
    for (String key : BedwarsRel.getInstance().getConfig().getConfigurationSection("resource").getKeys(false)) {
      try {
        ItemStack itemStack1 = ItemStack.deserialize((Map<String, Object>) config.getList("resource." + key + ".item").get(0));
        ItemMeta itemMeta1 = itemStack1.getItemMeta();
        List<String> lore = new ArrayList<>();
        String line = key;
        line = "§" + line.replaceAll("(.{1})", "$1§");
        lore.add(line.substring(0, line.length() - 1));
        if (itemMeta1.hasLore()) {
          lore.addAll(itemMeta1.getLore());
          lore.add("");
        } 
        lore.add(Config.getLanguage("item.edit_game.lore.set"));
        itemMeta1.setLore(lore);
        itemStack1.setItemMeta(itemMeta1);
        if (i == 17) {
          i = 19;
        } else if (i == 26) {
          i = 28;
        } else if (i == 35) {
          i = 37;
        } else if (i == 45) {
          break;
        } 
        inventory.setItem(i, itemStack1);
        i++;
      } catch (Exception exception) {}
    } 
    ItemStack itemStack = new ItemStack(Material.ARROW);
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.back"));
    itemStack.setItemMeta(itemMeta);
    inventory.setItem(49, itemStack);
    player.closeInventory();
    player.openInventory(inventory);
  }
  
  private void onPacketSending() {
    ProtocolManager pm = ProtocolLibrary.getProtocolManager();
    PacketAdapter packetAdapter = new PacketAdapter((Plugin)Main.getInstance(), ListenerPriority.HIGHEST, 
        new PacketType[] { PacketType.Play.Client.WINDOW_CLICK }) {
        public void onPacketReceiving(PacketEvent e) {
          PacketContainer packet = e.getPacket();
          final Player player = e.getPlayer();
          if (e.getPacketType().equals(PacketType.Play.Client.WINDOW_CLICK) && (
            (Integer)packet.getIntegers().read(0)).intValue() == 0) {
            ItemStack itemStack = (ItemStack)packet.getItemModifier().read(0);
            if (((Integer)packet.getIntegers().read(1)).intValue() == 2 && 
              itemStack != null && !itemStack.getType().equals(Material.AIR)) {
              final ItemMeta itemMeta = itemStack.getItemMeta();
              player.closeInventory();
              if (itemMeta.getDisplayName() != null) {
                String command = ((String)itemMeta.getLore().get(0)).replace("§", "").replace("{value}", 
                    itemMeta.getDisplayName());
                String[] args = command.split(" ");
                if (args.length == 5 && args[1].equalsIgnoreCase("addteam")) {
                  EditGame.openAnvilInventory(player, 
                      Config.getLanguage("anvil.edit_game.set_team_max_players"), 
                      String.valueOf(command) + " {value}");
                } else {
                  Bukkit.dispatchCommand((CommandSender)player, command);
                  Game game = BedwarsRel.getInstance().getGameManager().getGame(args[2]);
                  if (game != null)
                    Main.getInstance().getHolographicManager().displayGameLocation(player, 
                        game.getName()); 
                } 
              } 
            } 
            if (((Integer)packet.getIntegers().read(1)).intValue() == 0 && itemStack != null && 
              !itemStack.getType().equals(Material.AIR)) {
              final ItemMeta itemMeta = itemStack.getItemMeta();
              (new BukkitRunnable() {
                  public void run() {
                    EditGame.setAnvilItem(player, itemMeta.getDisplayName(), itemMeta.getLore().get(0));
                  }
                }).runTaskLater((Plugin)Main.getInstance(), 1L);
            } 
          } 
        }
      };
    pm.addPacketListener((PacketListener)packetAdapter);
  }
  
  private static void openAnvilInventory(final Player player, final String game, final String str) {
    player.closeInventory();
    ProtocolManager pm = ProtocolLibrary.getProtocolManager();
    try {
      PacketContainer packet = pm.createPacket(PacketType.Play.Server.OPEN_WINDOW);
      packet.getIntegers().write(0, Integer.valueOf(0));
      packet.getIntegers().write(1, Integer.valueOf(0));
      packet.getIntegers().write(2, Integer.valueOf(0));
      packet.getStrings().write(0, "minecraft:anvil");
      pm.sendServerPacket(player, packet);
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } 
    (new BukkitRunnable() {
        public void run() {
          EditGame.setAnvilItem(player, game, str);
        }
      }).runTaskLater((Plugin)Main.getInstance(), 1L);
  }
  
  private static void setAnvilItem(Player player, String game, String str) {
    ProtocolManager pm = ProtocolLibrary.getProtocolManager();
    try {
      PacketContainer pack = pm.createPacket(PacketType.Play.Server.SET_SLOT);
      pack.getIntegers().write(0, Integer.valueOf(0));
      pack.getIntegers().write(1, Integer.valueOf(0));
      ItemStack itemStack = new ItemStack(Material.NAME_TAG);
      ItemMeta itemMeta = itemStack.getItemMeta();
      itemMeta.setDisplayName(game);
      String lore = str;
      lore = "§" + lore.replaceAll("(.{1})", "$1§");
      itemMeta.setLore(Arrays.asList(new String[] { lore.substring(0, lore.length() - 1) }));
      itemStack.setItemMeta(itemMeta);
      pack.getItemModifier().write(0, itemStack);
      pm.sendServerPacket(player, pack);
    } catch (InvocationTargetException ex) {
      ex.printStackTrace();
    } 
  }
}
