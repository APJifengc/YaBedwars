package me.ram.bedwarsscoreboardaddon.addon;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ldcr.BedwarsXP.api.XPManager;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;
import me.ram.bedwarsscoreboardaddon.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TeamShop {
  private final Game game;
  
  private final Map<String, Integer> haste;
  
  private final Map<String, Integer> sharpness;
  
  private final Map<String, Integer> protection;
  
  private final Map<String, Integer> trap;
  
  private final Map<String, Integer> defense;
  
  private final Map<String, Integer> heal;
  
  public void openTeamShop(Player player) {
    if (this.game != null && Config.teamshop_enabled && 
      this.game.getState() != GameState.WAITING && this.game.getState() == GameState.RUNNING && 
      this.game.getPlayerTeam(player) != null && 
      !this.game.isSpectator(player) && player.getGameMode() != GameMode.SPECTATOR) {
      Inventory inventory = Bukkit.createInventory(null, 45, Config.teamshop_title);
      setTeamShopItem(player, inventory);
      player.closeInventory();
      player.openInventory(inventory);
    } 
  }
  
  public void setTeamShopItem(Player player, Inventory inventory) {
    inventory.clear();
    ItemStack haste_itemStack = new ItemStack(Material.valueOf(Config.teamshop_upgrade_fast_dig_item));
    ItemStack sharpness_itemStack = new ItemStack(Material.valueOf(Config.teamshop_upgrade_sword_sharpness_item));
    ItemStack protection_itemStack = new ItemStack(Material.valueOf(Config.teamshop_upgrade_armor_protection_item));
    ItemStack trap_itemStack = new ItemStack(Material.valueOf(Config.teamshop_upgrade_trap_item));
    ItemStack defense_itemStack = new ItemStack(Material.valueOf(Config.teamshop_upgrade_defense_item));
    ItemStack heal_itemStack = new ItemStack(Material.valueOf(Config.teamshop_upgrade_heal_item));
    ItemMeta haste_itemMeta = haste_itemStack.getItemMeta();
    ItemMeta sharpness_itemMeta = sharpness_itemStack.getItemMeta();
    ItemMeta protection_itemMeta = protection_itemStack.getItemMeta();
    ItemMeta trap_itemMeta = trap_itemStack.getItemMeta();
    ItemMeta defense_itemMeta = defense_itemStack.getItemMeta();
    ItemMeta heal_itemMeta = heal_itemStack.getItemMeta();
    haste_itemMeta.setDisplayName(Config.teamshop_upgrade_fast_dig_name);
    sharpness_itemMeta.setDisplayName(Config.teamshop_upgrade_sword_sharpness_name);
    protection_itemMeta.setDisplayName(Config.teamshop_upgrade_armor_protection_name);
    trap_itemMeta.setDisplayName(Config.teamshop_upgrade_trap_name);
    defense_itemMeta.setDisplayName(Config.teamshop_upgrade_defense_name);
    heal_itemMeta.setDisplayName(Config.teamshop_upgrade_heal_name);
    haste_itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    sharpness_itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    protection_itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    trap_itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    defense_itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    heal_itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    haste_itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
    sharpness_itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
    protection_itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
    trap_itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
    defense_itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
    heal_itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
    int hi = getPlayerTeamUpgradeLevel(player, this.haste, this.game);
    if (hi == 0) {
      haste_itemMeta.setLore(Config.teamshop_upgrade_fast_dig_level_1_lore);
    } else if (hi == 1) {
      haste_itemMeta.setLore(Config.teamshop_upgrade_fast_dig_level_2_lore);
    } else if (hi == 2) {
      haste_itemMeta.setLore(Config.teamshop_upgrade_fast_dig_level_full_lore);
    } 
    int si = getPlayerTeamUpgradeLevel(player, this.sharpness, this.game);
    if (si == 0) {
      sharpness_itemMeta.setLore(Config.teamshop_upgrade_sword_sharpness_level_1_lore);
    } else if (si == 1) {
      sharpness_itemMeta.setLore(Config.teamshop_upgrade_sword_sharpness_level_2_lore);
    } else if (si == 2) {
      sharpness_itemMeta.setLore(Config.teamshop_upgrade_sword_sharpness_level_full_lore);
    } 
    int pi = getPlayerTeamUpgradeLevel(player, this.protection, this.game);
    if (pi == 0) {
      protection_itemMeta.setLore(Config.teamshop_upgrade_armor_protection_level_1_lore);
    } else if (pi == 1) {
      protection_itemMeta.setLore(Config.teamshop_upgrade_armor_protection_level_2_lore);
    } else if (pi == 2) {
      protection_itemMeta.setLore(Config.teamshop_upgrade_armor_protection_level_3_lore);
    } else if (pi == 3) {
      protection_itemMeta.setLore(Config.teamshop_upgrade_armor_protection_level_4_lore);
    } else if (pi == 4) {
      protection_itemMeta.setLore(Config.teamshop_upgrade_armor_protection_level_full_lore);
    } 
    int ti = getPlayerTeamUpgradeLevel(player, this.trap, this.game);
    if (ti == 0) {
      trap_itemMeta.setLore(Config.teamshop_upgrade_trap_level_1_lore);
    } else if (ti == 1) {
      trap_itemMeta.setLore(Config.teamshop_upgrade_trap_level_full_lore);
    } 
    int di = getPlayerTeamUpgradeLevel(player, this.defense, this.game);
    if (di == 0) {
      defense_itemMeta.setLore(Config.teamshop_upgrade_defense_level_1_lore);
    } else if (di == 1) {
      defense_itemMeta.setLore(Config.teamshop_upgrade_defense_level_full_lore);
    } 
    int hei = getPlayerTeamUpgradeLevel(player, this.heal, this.game);
    if (hei == 0) {
      heal_itemMeta.setLore(Config.teamshop_upgrade_heal_level_1_lore);
    } else if (hei == 1) {
      heal_itemMeta.setLore(Config.teamshop_upgrade_heal_level_full_lore);
    } 
    haste_itemStack.setItemMeta(haste_itemMeta);
    sharpness_itemStack.setItemMeta(sharpness_itemMeta);
    protection_itemStack.setItemMeta(protection_itemMeta);
    trap_itemStack.setItemMeta(trap_itemMeta);
    defense_itemStack.setItemMeta(defense_itemMeta);
    heal_itemStack.setItemMeta(heal_itemMeta);
    inventory.setItem(13, haste_itemStack);
    inventory.setItem(11, sharpness_itemStack);
    inventory.setItem(12, protection_itemStack);
    inventory.setItem(31, trap_itemStack);
    inventory.setItem(14, defense_itemStack);
    inventory.setItem(15, heal_itemStack);
    ItemStack glasspane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)7);
    ItemMeta glasspaneMeta = glasspane.getItemMeta();
    glasspaneMeta.setDisplayName(getItemName(Config.teamshop_frame));
    glasspaneMeta.setLore(getItemLore(Config.teamshop_frame));
    glasspane.setItemMeta(glasspaneMeta);
    inventory.setItem(18, glasspane);
    inventory.setItem(19, glasspane);
    inventory.setItem(20, glasspane);
    inventory.setItem(21, glasspane);
    inventory.setItem(22, glasspane);
    inventory.setItem(23, glasspane);
    inventory.setItem(24, glasspane);
    inventory.setItem(25, glasspane);
    inventory.setItem(26, glasspane);
    player.updateInventory();
  }
  
  private int getPlayerTeamUpgradeLevel(Player player, Map<String, Integer> map, Game g) {
    int i = 0;
    for (String team : map.keySet()) {
      if (team.equals(g.getPlayerTeam(player).getName())) {
        i = Integer.valueOf(map.get(team).intValue()).intValue();
        break;
      } 
    } 
    return i;
  }
  
  public TeamShop(final Game game) {
    this.game = game;
    this.haste = new HashMap<>();
    this.sharpness = new HashMap<>();
    this.protection = new HashMap<>();
    this.trap = new HashMap<>();
    this.defense = new HashMap<>();
    this.heal = new HashMap<>();
    final BukkitTask task = (new BukkitRunnable() {
        public void run() {
          for (String ts : TeamShop.this.heal.keySet()) {
            Team team = game.getTeam(ts);
            if (team != null && TeamShop.this.heal.get(ts).intValue() > 0) {
              Location location = team.getTargetHeadBlock().clone().add(0.5D, 1.0D, 0.5D);
              for (Player player : team.getPlayers()) {
                for (int i = 0; i < 10; i++)
                  player.playEffect(location.clone().add((
                        Math.random() - Math.random()) * Config.teamshop_upgrade_heal_trigger_range, (
                        Math.random() - Math.random()) * Config.teamshop_upgrade_heal_trigger_range, (
                        Math.random() - Math.random()) * Config.teamshop_upgrade_heal_trigger_range), 
                      Effect.HAPPY_VILLAGER, 0); 
              } 
            } 
          } 
        }
      }).runTaskTimer(Main.getInstance(), 0L, 40L);
    (new BukkitRunnable() {
        public void run() {
          if (game.getState() == GameState.RUNNING) {
            for (String hastes : TeamShop.this.haste.keySet()) {
              Team team = game.getTeam(hastes);
              if (team != null)
                for (Player p : game.getTeam(hastes).getPlayers())
                  p.addPotionEffect(
                      new PotionEffect(PotionEffectType.FAST_DIGGING, 30, TeamShop.this.haste.get(hastes).intValue() - 1),
                      true);  
            } 
            for (Player player : game.getPlayers()) {
              int i = (player.getInventory().getContents()).length;
              ItemStack[] stacks = player.getInventory().getContents();
              for (int j = 0; j < i; j++) {
                ItemStack stack = stacks[j];
                if (stack != null && (stack.getType().name().contains("_SWORD") || 
                  stack.getType().name().contains("_AXE"))) {
                  ItemStack itemStack = stack;
                  ItemMeta itemMeta = itemStack.getItemMeta();
                  int l = TeamShop.this.getPlayerTeamUpgradeLevel(player, TeamShop.this.sharpness, game);
                  if (l > 0) {
                    if (itemMeta.getLore() == null) {
                      TeamShop.this.setSharpness(player, itemStack, itemMeta, j, l, true);
                    } else if (!itemMeta.getLore().contains("§s§1§0§0§0§" + l)) {
                      TeamShop.this.setSharpness(player, itemStack, itemMeta, j, l, true);
                    } 
                  } else if (itemMeta.getLore() != null) {
                    if (itemMeta.getLore().contains("§s§1§0§0§0§1"))
                      TeamShop.this.setSharpness(player, itemStack, itemMeta, j, 1, false); 
                    if (itemMeta.getLore().contains("§s§1§0§0§0§2"))
                      TeamShop.this.setSharpness(player, itemStack, itemMeta, j, 2, false); 
                  } 
                } 
              } 
            } 
            for (Player player : game.getPlayers()) {
              int i = (player.getInventory().getContents()).length + 4;
              ItemStack[] stacks = player.getInventory().getContents();
              for (int m = 0; m < i; m++) {
                int j = m - 4;
                ItemStack stack = null;
                if (j >= 0) {
                  stack = stacks[j];
                } else if (j == -1) {
                  stack = player.getInventory().getHelmet();
                } else if (j == -2) {
                  stack = player.getInventory().getChestplate();
                } else if (j == -3) {
                  stack = player.getInventory().getLeggings();
                } else if (j == -4) {
                  stack = player.getInventory().getBoots();
                } 
                if (stack != null && (stack.getType().equals(Material.CHAINMAIL_HELMET) || 
                  stack.getType().equals(Material.DIAMOND_HELMET) || 
                  stack.getType().equals(Material.GOLD_HELMET) || 
                  stack.getType().equals(Material.IRON_HELMET) || 
                  stack.getType().equals(Material.LEATHER_HELMET) || 
                  stack.getType().equals(Material.CHAINMAIL_CHESTPLATE) || 
                  stack.getType().equals(Material.DIAMOND_CHESTPLATE) || 
                  stack.getType().equals(Material.GOLD_CHESTPLATE) || 
                  stack.getType().equals(Material.IRON_CHESTPLATE) || 
                  stack.getType().equals(Material.LEATHER_CHESTPLATE) || 
                  stack.getType().equals(Material.CHAINMAIL_LEGGINGS) || 
                  stack.getType().equals(Material.DIAMOND_LEGGINGS) || 
                  stack.getType().equals(Material.GOLD_LEGGINGS) || 
                  stack.getType().equals(Material.IRON_LEGGINGS) || 
                  stack.getType().equals(Material.LEATHER_LEGGINGS) || 
                  stack.getType().equals(Material.CHAINMAIL_BOOTS) || 
                  stack.getType().equals(Material.DIAMOND_BOOTS) || 
                  stack.getType().equals(Material.GOLD_BOOTS) || 
                  stack.getType().equals(Material.IRON_BOOTS) || 
                  stack.getType().equals(Material.LEATHER_BOOTS))) {
                  ItemStack itemStack = stack;
                  ItemMeta itemMeta = itemStack.getItemMeta();
                  int l = TeamShop.this.getPlayerTeamUpgradeLevel(player, TeamShop.this.protection, game);
                  if (l > 0) {
                    if (itemMeta.getLore() == null) {
                      TeamShop.this.setProtection(player, itemStack, itemMeta, j, l, true);
                    } else if (!itemMeta.getLore().contains("§a§1§0§0§0§" + l)) {
                      TeamShop.this.setProtection(player, itemStack, itemMeta, j, l, true);
                    } 
                  } else if (itemMeta.getLore() != null) {
                    if (itemMeta.getLore().contains("§a§1§0§0§0§1")) {
                      TeamShop.this.setProtection(player, itemStack, itemMeta, j, 1, false);
                    } else if (itemMeta.getLore().contains("§a§1§0§0§0§2")) {
                      TeamShop.this.setProtection(player, itemStack, itemMeta, j, 2, false);
                    } else if (itemMeta.getLore().contains("§a§1§0§0§0§3")) {
                      TeamShop.this.setProtection(player, itemStack, itemMeta, j, 3, false);
                    } else if (itemMeta.getLore().contains("§a§1§0§0§0§4")) {
                      TeamShop.this.setProtection(player, itemStack, itemMeta, j, 4, false);
                    } 
                  } 
                } 
              } 
            } 
            label186: for (String ts : TeamShop.this.trap.keySet()) {
              Team team = game.getTeam(ts);
              if (team != null && TeamShop.this.trap.get(ts).intValue() > 0)
                for (Player player : game.getPlayers()) {
                  if (!game.isSpectator(player) && player.getGameMode() != GameMode.SPECTATOR && 
                    team.getTargetFeetBlock().distance(
                      player.getLocation()) <= Config.teamshop_upgrade_trap_trigger_range && 
                    team != game.getPlayerTeam(player)) {
                    TeamShop.this.trap.remove(ts);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1), 
                        true);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 1), true);
                    if (!Config.teamshop_upgrade_trap_trigger_title.equals("") || 
                      !Config.teamshop_upgrade_trap_trigger_subtitle.equals(""))
                      for (Player teamplayers : team.getPlayers())
                        Utils.sendTitle(teamplayers, Integer.valueOf(5), Integer.valueOf(80), Integer.valueOf(5), 
                            Config.teamshop_upgrade_trap_trigger_title, 
                            Config.teamshop_upgrade_trap_trigger_subtitle);  
                    if (team.getPlayers().size() > 0) {
                      TeamShop.this.reOpenTeamShop(team.getPlayers().get(0));
                      break label186;
                    } 
                    break label186;
                  } 
                }  
            } 
            label184: for (String ts : TeamShop.this.defense.keySet()) {
              Team team = game.getTeam(ts);
              if (team != null && TeamShop.this.defense.get(ts).intValue() > 0)
                for (Player player : game.getPlayers()) {
                  if (!game.isSpectator(player) && player.getGameMode() != GameMode.SPECTATOR && 
                    team.getTargetFeetBlock().distance(
                      player.getLocation()) <= Config.teamshop_upgrade_defense_trigger_range && 
                    team != game.getPlayerTeam(player)) {
                    if (Config.teamshop_upgrade_defense_permanent.booleanValue()) {
                      player.addPotionEffect(
                          new PotionEffect(PotionEffectType.SLOW_DIGGING, 30, 0), true);
                      continue;
                    } 
                    player.addPotionEffect(
                        new PotionEffect(PotionEffectType.SLOW_DIGGING, 200, 0), true);
                    TeamShop.this.defense.remove(ts);
                    if (team.getPlayers().size() > 0) {
                      TeamShop.this.reOpenTeamShop(team.getPlayers().get(0));
                      break label184;
                    } 
                    break label184;
                  } 
                }  
            } 
            for (String ts : TeamShop.this.heal.keySet()) {
              Team team = game.getTeam(ts);
              if (team != null && TeamShop.this.heal.get(ts).intValue() > 0)
                for (Player player : game.getPlayers()) {
                  if (!game.isSpectator(player) && player.getGameMode() != GameMode.SPECTATOR && 
                    team.getTargetFeetBlock().distance(
                      player.getLocation()) <= Config.teamshop_upgrade_heal_trigger_range && 
                    team == game.getPlayerTeam(player))
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 30, 1), 
                        true); 
                }  
            } 
          } else {
            cancel();
            task.cancel();
          } 
        }
      }).runTaskTimer(Main.getInstance(), 0L, 5L);
  }
  
  private void setSharpness(Player player, ItemStack itemStack, ItemMeta itemMeta, int j, int k, boolean b) {
    List<String> lores = new ArrayList<>();
    int ol = 0;
    if (itemMeta.getLore() != null)
      for (String lore : itemMeta.getLore()) {
        lores.add(lore);
        if (lore.equals("§s§1§0§0§0§1"))
          ol = 1; 
        if (lore.equals("§s§1§0§0§0§2"))
          ol = 2; 
      }  
    if (b) {
      lores.remove("§s§1§0§0§0§1");
      lores.remove("§s§1§0§0§0§2");
      lores.add("§s§1§0§0§0§" + k);
      itemMeta.setLore(lores);
      itemStack.setItemMeta(itemMeta);
      int el = itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL) - ol;
      itemStack.removeEnchantment(Enchantment.DAMAGE_ALL);
      int nl = el + k;
      if (nl > 0)
        itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, nl); 
    } else {
      lores.remove("§s§1§0§0§0§1");
      lores.remove("§s§1§0§0§0§2");
      itemMeta.setLore(lores);
      itemStack.setItemMeta(itemMeta);
      int el = itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL) - k;
      itemStack.removeEnchantment(Enchantment.DAMAGE_ALL);
      if (el > 0)
        itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, el); 
    } 
    player.getInventory().setItem(j, itemStack);
  }
  
  private void setProtection(Player player, ItemStack itemStack, ItemMeta itemMeta, int j, int k, boolean b) {
    List<String> lores = new ArrayList<>();
    int ol = 0;
    if (itemMeta.getLore() != null)
      for (String lore : itemMeta.getLore()) {
        lores.add(lore);
        if (lore.equals("§a§1§0§0§0§1"))
          ol = 1; 
        if (lore.equals("§a§1§0§0§0§2"))
          ol = 2; 
        if (lore.equals("§a§1§0§0§0§3"))
          ol = 3; 
        if (lore.equals("§a§1§0§0§0§4"))
          ol = 4; 
      }  
    if (b) {
      lores.remove("§a§1§0§0§0§1");
      lores.remove("§a§1§0§0§0§2");
      lores.remove("§a§1§0§0§0§3");
      lores.remove("§a§1§0§0§0§4");
      lores.add("§a§1§0§0§0§" + k);
      itemMeta.setLore(lores);
      itemStack.setItemMeta(itemMeta);
      int el = itemStack.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) - ol;
      itemStack.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
      int nl = el + k;
      if (nl > 0)
        itemStack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, nl); 
    } else {
      lores.remove("§a§1§0§0§0§1");
      lores.remove("§a§1§0§0§0§2");
      lores.remove("§a§1§0§0§0§3");
      lores.remove("§a§1§0§0§0§4");
      itemMeta.setLore(lores);
      itemStack.setItemMeta(itemMeta);
      int el = itemStack.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) - k;
      itemStack.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
      if (el > 0)
        itemStack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, el); 
    } 
    if (j >= 0) {
      player.getInventory().setItem(j, itemStack);
    } else if (j == -1) {
      player.getInventory().setHelmet(itemStack);
    } else if (j == -2) {
      player.getInventory().setChestplate(itemStack);
    } else if (j == -3) {
      player.getInventory().setLeggings(itemStack);
    } else if (j == -4) {
      player.getInventory().setBoots(itemStack);
    } 
  }
  
  public void onClickHaste(InventoryClickEvent e) {
    Player player = (Player)e.getWhoClicked();
    Inventory inventory = e.getInventory();
    if (inventory.getTitle().equals(Config.teamshop_title)) {
      e.setCancelled(true);
      if (e.getRawSlot() == 13) {
        Team team = this.game.getPlayerTeam(player);
        if (!this.haste.containsKey(team.getName()) || this.haste.get(team.getName()).intValue() == 1) {
          String[] ary = Config.teamshop_upgrade_fast_dig_level_cost
            .get(Integer.valueOf(this.haste.getOrDefault(team.getName(), Integer.valueOf(0)).intValue() + 1)).split(",");
          if (ary[0].equals("XP")) {
            if (Bukkit.getPluginManager().isPluginEnabled("BedwarsXP"))
              if (XPManager.getXPManager(this.game.getName()).getXP(player) >= Integer.valueOf(ary[1]).intValue()) {
                XPManager.getXPManager(this.game.getName()).takeXP(player, Integer.valueOf(ary[1]).intValue());
                this.haste.put(team.getName(), Integer.valueOf(this.haste.getOrDefault(team.getName(), Integer.valueOf(0)).intValue() + 1));
                reOpenTeamShop(player);
                for (Player p : team.getPlayers())
                  p.sendMessage(Config.teamshop_message.replace("{player}", player.getName())
                      .replace("{upgrade}", 
                        ColorUtil.remcolor(Config.teamshop_upgrade_fast_dig_name))
                      .replace("{level}", getLevel(this.haste.get(team.getName()).intValue())));
              } else {
                player.sendMessage(Config.teamshop_no_resource);
                PlaySound.playSound(player, Config.play_sound_sound_no_resource);
              }  
          } else if (isEnough(player, ary)) {
            takeItem(player, ary);
            this.haste.put(team.getName(), Integer.valueOf(this.haste.getOrDefault(team.getName(), Integer.valueOf(0)).intValue() + 1));
            reOpenTeamShop(player);
            for (Player p : team.getPlayers())
              p.sendMessage(Config.teamshop_message.replace("{player}", player.getName())
                  .replace("{upgrade}", ColorUtil.remcolor(Config.teamshop_upgrade_fast_dig_name))
                  .replace("{level}", getLevel(this.haste.get(team.getName()).intValue())));
          } else {
            player.sendMessage(Config.teamshop_no_resource);
            PlaySound.playSound(player, Config.play_sound_sound_no_resource);
          } 
        } 
      } 
    } 
  }
  
  public void onClickSharpness(InventoryClickEvent e) {
    Player player = (Player)e.getWhoClicked();
    Inventory inventory = e.getInventory();
    if (inventory.getTitle().equals(Config.teamshop_title)) {
      e.setCancelled(true);
      if (e.getRawSlot() == 11) {
        Team team = this.game.getPlayerTeam(player);
        if (!this.sharpness.containsKey(team.getName()) || this.sharpness.get(team.getName()).intValue() == 1) {
          String[] ary = Config.teamshop_upgrade_sword_sharpness_level_cost
            .get(Integer.valueOf(this.sharpness.getOrDefault(team.getName(), Integer.valueOf(0)).intValue() + 1)).split(",");
          if (ary[0].equals("XP")) {
            if (Bukkit.getPluginManager().isPluginEnabled("BedwarsXP"))
              if (XPManager.getXPManager(this.game.getName()).getXP(player) >= Integer.valueOf(ary[1]).intValue()) {
                XPManager.getXPManager(this.game.getName()).takeXP(player, Integer.valueOf(ary[1]).intValue());
                this.sharpness.put(team.getName(), Integer.valueOf(this.sharpness.getOrDefault(team.getName(), Integer.valueOf(0)).intValue() + 1));
                reOpenTeamShop(player);
                for (Player p : team.getPlayers())
                  p.sendMessage(Config.teamshop_message.replace("{player}", player.getName())
                      .replace("{upgrade}", 
                        ColorUtil.remcolor(Config.teamshop_upgrade_sword_sharpness_name))
                      .replace("{level}", getLevel(this.sharpness.get(team.getName()).intValue())));
              } else {
                player.sendMessage(Config.teamshop_no_resource);
                PlaySound.playSound(player, Config.play_sound_sound_no_resource);
              }  
          } else if (isEnough(player, ary)) {
            takeItem(player, ary);
            this.sharpness.put(team.getName(), Integer.valueOf(this.sharpness.getOrDefault(team.getName(), Integer.valueOf(0)).intValue() + 1));
            reOpenTeamShop(player);
            for (Player p : team.getPlayers())
              p.sendMessage(Config.teamshop_message.replace("{player}", player.getName())
                  .replace("{upgrade}", 
                    ColorUtil.remcolor(Config.teamshop_upgrade_sword_sharpness_name))
                  .replace("{level}", getLevel(this.sharpness.get(team.getName()).intValue())));
          } else {
            player.sendMessage(Config.teamshop_no_resource);
            PlaySound.playSound(player, Config.play_sound_sound_no_resource);
          } 
        } 
      } 
    } 
  }
  
  public void onClickProtection(InventoryClickEvent e) {
    Player player = (Player)e.getWhoClicked();
    Inventory inventory = e.getInventory();
    if (inventory.getTitle().equals(Config.teamshop_title)) {
      e.setCancelled(true);
      if (e.getRawSlot() == 12) {
        Team team = this.game.getPlayerTeam(player);
        if (!this.protection.containsKey(team.getName()) || this.protection.get(team.getName()).intValue() < 4) {
          String[] ary = Config.teamshop_upgrade_armor_protection_level_cost
            .get(Integer.valueOf(this.protection.getOrDefault(team.getName(), Integer.valueOf(0)).intValue() + 1)).split(",");
          if (ary[0].equals("XP")) {
            if (Bukkit.getPluginManager().isPluginEnabled("BedwarsXP"))
              if (XPManager.getXPManager(this.game.getName()).getXP(player) >= Integer.valueOf(ary[1]).intValue()) {
                XPManager.getXPManager(this.game.getName()).takeXP(player, Integer.valueOf(ary[1]).intValue());
                this.protection.put(team.getName(), Integer.valueOf(this.protection.getOrDefault(team.getName(), Integer.valueOf(0)).intValue() + 1));
                reOpenTeamShop(player);
                for (Player p : team.getPlayers())
                  p.sendMessage(Config.teamshop_message.replace("{player}", player.getName())
                      .replace("{upgrade}", 
                        ColorUtil.remcolor(Config.teamshop_upgrade_armor_protection_name))
                      .replace("{level}", getLevel(this.protection.get(team.getName()).intValue())));
              } else {
                player.sendMessage(Config.teamshop_no_resource);
                PlaySound.playSound(player, Config.play_sound_sound_no_resource);
              }  
          } else if (isEnough(player, ary)) {
            takeItem(player, ary);
            this.protection.put(team.getName(), Integer.valueOf(this.protection.getOrDefault(team.getName(), Integer.valueOf(0)).intValue() + 1));
            reOpenTeamShop(player);
            for (Player p : team.getPlayers())
              p.sendMessage(Config.teamshop_message.replace("{player}", player.getName())
                  .replace("{upgrade}", 
                    ColorUtil.remcolor(Config.teamshop_upgrade_armor_protection_name))
                  .replace("{level}", getLevel(this.protection.get(team.getName()).intValue())));
          } else {
            player.sendMessage(Config.teamshop_no_resource);
            PlaySound.playSound(player, Config.play_sound_sound_no_resource);
          } 
        } 
      } 
    } 
  }
  
  public void onClickTrap(InventoryClickEvent e) {
    Player player = (Player)e.getWhoClicked();
    Inventory inventory = e.getInventory();
    if (inventory.getTitle().equals(Config.teamshop_title)) {
      e.setCancelled(true);
      if (e.getRawSlot() == 31) {
        Team team = this.game.getPlayerTeam(player);
        if (!this.trap.containsKey(team.getName())) {
          String[] ary = Config.teamshop_upgrade_trap_level_1_cost.split(",");
          if (ary[0].equals("XP")) {
            if (Bukkit.getPluginManager().isPluginEnabled("BedwarsXP"))
              if (XPManager.getXPManager(this.game.getName()).getXP(player) >= Integer.valueOf(ary[1]).intValue()) {
                XPManager.getXPManager(this.game.getName()).takeXP(player, Integer.valueOf(ary[1]).intValue());
                this.trap.put(team.getName(), Integer.valueOf(1));
                reOpenTeamShop(player);
                for (Player p : this.game.getPlayerTeam(player).getPlayers())
                  p.sendMessage(Config.teamshop_message.replace("{player}", player.getName())
                      .replace("{upgrade}", ColorUtil.remcolor(Config.teamshop_upgrade_trap_name))
                      .replace("{level}", "")); 
              } else {
                player.sendMessage(Config.teamshop_no_resource);
                PlaySound.playSound(player, Config.play_sound_sound_no_resource);
              }  
          } else if (isEnough(player, ary)) {
            takeItem(player, ary);
            this.trap.put(team.getName(), Integer.valueOf(1));
            reOpenTeamShop(player);
            for (Player p : team.getPlayers())
              p.sendMessage(Config.teamshop_message.replace("{player}", player.getName())
                  .replace("{upgrade}", ColorUtil.remcolor(Config.teamshop_upgrade_trap_name))
                  .replace("{level}", "")); 
          } else {
            player.sendMessage(Config.teamshop_no_resource);
            PlaySound.playSound(player, Config.play_sound_sound_no_resource);
          } 
        } 
      } 
    } 
  }
  
  public void onClickDefense(InventoryClickEvent e) {
    Player player = (Player)e.getWhoClicked();
    Inventory inventory = e.getInventory();
    if (inventory.getTitle().equals(Config.teamshop_title)) {
      e.setCancelled(true);
      if (e.getRawSlot() == 14) {
        Team team = this.game.getPlayerTeam(player);
        if (!this.defense.containsKey(team.getName())) {
          String[] ary = Config.teamshop_upgrade_defense_level_1_cost.split(",");
          if (ary[0].equals("XP")) {
            if (Bukkit.getPluginManager().isPluginEnabled("BedwarsXP"))
              if (XPManager.getXPManager(this.game.getName()).getXP(player) >= Integer.valueOf(ary[1]).intValue()) {
                XPManager.getXPManager(this.game.getName()).takeXP(player, Integer.valueOf(ary[1]).intValue());
                this.defense.put(team.getName(), Integer.valueOf(1));
                reOpenTeamShop(player);
                for (Player p : team.getPlayers())
                  p.sendMessage(Config.teamshop_message.replace("{player}", player.getName())
                      .replace("{upgrade}", 
                        ColorUtil.remcolor(Config.teamshop_upgrade_defense_name))
                      .replace("{level}", "")); 
              } else {
                player.sendMessage(Config.teamshop_no_resource);
                PlaySound.playSound(player, Config.play_sound_sound_no_resource);
              }  
          } else if (isEnough(player, ary)) {
            takeItem(player, ary);
            this.defense.put(team.getName(), Integer.valueOf(1));
            reOpenTeamShop(player);
            for (Player p : team.getPlayers())
              p.sendMessage(Config.teamshop_message.replace("{player}", player.getName())
                  .replace("{upgrade}", ColorUtil.remcolor(Config.teamshop_upgrade_defense_name))
                  .replace("{level}", "")); 
          } else {
            player.sendMessage(Config.teamshop_no_resource);
            PlaySound.playSound(player, Config.play_sound_sound_no_resource);
          } 
        } 
      } 
    } 
  }
  
  public void onClickHeal(InventoryClickEvent e) {
    Player player = (Player)e.getWhoClicked();
    Inventory inventory = e.getInventory();
    if (inventory.getTitle().equals(Config.teamshop_title)) {
      e.setCancelled(true);
      if (e.getRawSlot() == 15) {
        Team team = this.game.getPlayerTeam(player);
        if (!this.heal.containsKey(team.getName())) {
          String[] ary = Config.teamshop_upgrade_heal_level_1_cost.split(",");
          if (ary[0].equals("XP")) {
            if (Bukkit.getPluginManager().isPluginEnabled("BedwarsXP"))
              if (XPManager.getXPManager(this.game.getName()).getXP(player) >= Integer.valueOf(ary[1]).intValue()) {
                XPManager.getXPManager(this.game.getName()).takeXP(player, Integer.valueOf(ary[1]).intValue());
                this.heal.put(team.getName(), Integer.valueOf(1));
                reOpenTeamShop(player);
                for (Player p : team.getPlayers())
                  p.sendMessage(Config.teamshop_message.replace("{player}", player.getName())
                      .replace("{upgrade}", ColorUtil.remcolor(Config.teamshop_upgrade_heal_name))
                      .replace("{level}", "")); 
              } else {
                player.sendMessage(Config.teamshop_no_resource);
                PlaySound.playSound(player, Config.play_sound_sound_no_resource);
              }  
          } else if (isEnough(player, ary)) {
            takeItem(player, ary);
            this.heal.put(team.getName(), Integer.valueOf(1));
            reOpenTeamShop(player);
            for (Player p : team.getPlayers())
              p.sendMessage(Config.teamshop_message.replace("{player}", player.getName())
                  .replace("{upgrade}", ColorUtil.remcolor(Config.teamshop_upgrade_heal_name))
                  .replace("{level}", "")); 
          } else {
            player.sendMessage(Config.teamshop_no_resource);
            PlaySound.playSound(player, Config.play_sound_sound_no_resource);
          } 
        } 
      } 
    } 
  }
  
  public void onClick(InventoryClickEvent e) {
    final Player player = (Player)e.getWhoClicked();
    (new BukkitRunnable() {
        public void run() {
          int i = (player.getInventory().getContents()).length;
          ItemStack[] stacks = player.getInventory().getContents();
          for (int j = 0; j < i; j++) {
            ItemStack stack = stacks[j];
            if (stack != null) {
              ItemMeta meta = stack.getItemMeta();
              if (meta.getLore() != null) {
                if (meta.getLore().contains("§a§r§m§o§r§0§0§1")) {
                  stack.setAmount(0);
                  player.getInventory().setItem(j, stack);
                  player.updateInventory();
                  ItemStack leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
                  ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS);
                  ItemMeta leggingsMeta = leggings.getItemMeta();
                  ItemMeta bootsMeta = leggings.getItemMeta();
                  leggingsMeta.spigot().setUnbreakable(true);
                  bootsMeta.spigot().setUnbreakable(true);
                  leggings.setItemMeta(leggingsMeta);
                  boots.setItemMeta(bootsMeta);
                  player.getInventory().setLeggings(leggings);
                  player.getInventory().setBoots(boots);
                  break;
                } 
                if (meta.getLore().contains("§a§r§m§o§r§0§0§2")) {
                  stack.setAmount(0);
                  player.getInventory().setItem(j, stack);
                  player.updateInventory();
                  ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
                  ItemStack boots = new ItemStack(Material.IRON_BOOTS);
                  ItemMeta leggingsMeta = leggings.getItemMeta();
                  ItemMeta bootsMeta = leggings.getItemMeta();
                  leggingsMeta.spigot().setUnbreakable(true);
                  bootsMeta.spigot().setUnbreakable(true);
                  leggings.setItemMeta(leggingsMeta);
                  boots.setItemMeta(bootsMeta);
                  player.getInventory().setLeggings(leggings);
                  player.getInventory().setBoots(boots);
                  break;
                } 
                if (meta.getLore().contains("§a§r§m§o§r§0§0§3")) {
                  stack.setAmount(0);
                  player.getInventory().setItem(j, stack);
                  player.updateInventory();
                  ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
                  ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
                  ItemMeta leggingsMeta = leggings.getItemMeta();
                  ItemMeta bootsMeta = leggings.getItemMeta();
                  leggingsMeta.spigot().setUnbreakable(true);
                  bootsMeta.spigot().setUnbreakable(true);
                  leggings.setItemMeta(leggingsMeta);
                  boots.setItemMeta(bootsMeta);
                  player.getInventory().setLeggings(leggings);
                  player.getInventory().setBoots(boots);
                  break;
                } 
                if (stack.getType() != Material.WOOD_SWORD && 
                  meta.getLore().contains("§s§w§o§r§d")) {
                  player.getInventory().remove(Material.WOOD_SWORD);
                  List<String> lore = meta.getLore();
                  lore.remove("§s§w§o§r§d");
                  meta.setLore(lore);
                  stack.setItemMeta(meta);
                  player.getInventory().setItem(j, stack);
                  player.updateInventory();
                  break;
                } 
              } 
            } 
          } 
        }
      }).runTaskLater(Main.getInstance(), 1L);
  }
  
  private boolean isEnough(Player player, String[] ary) {
    int k = 0;
    int i = (player.getInventory().getContents()).length;
    ItemStack[] stacks = player.getInventory().getContents();
    for (int j = 0; j < i; j++) {
      ItemStack stack = stacks[j];
      if (stack != null && 
        stack.getType().equals(Material.valueOf(ary[0])))
        k += stack.getAmount(); 
    }
    return k >= Integer.valueOf(ary[1]).intValue();
  }
  
  private void takeItem(Player player, String[] ary) {
    int ta = Integer.valueOf(ary[1]).intValue();
    int i = (player.getInventory().getContents()).length;
    ItemStack[] stacks = player.getInventory().getContents();
    for (int j = 0; j < i; j++) {
      ItemStack stack = stacks[j];
      if (stack != null && 
        stack.getType().equals(Material.valueOf(ary[0])) && ta > 0) {
        if (stack.getAmount() >= ta) {
          stack.setAmount(stack.getAmount() - ta);
          ta = 0;
        } else if (stack.getAmount() < ta) {
          ta -= stack.getAmount();
          stack.setAmount(0);
        } 
        player.getInventory().setItem(j, stack);
      } 
    } 
  }
  
  private void reOpenTeamShop(Player player) {
    if (this.game.getPlayerTeam(player) != null)
      for (Player p : this.game.getPlayerTeam(player).getPlayers()) {
        if (p.getOpenInventory() != null && 
          p.getOpenInventory().getTitle().equals(Config.teamshop_title))
          setTeamShopItem(p, p.getOpenInventory().getTopInventory()); 
      }  
  }
  
  private String getLevel(int i) {
    switch (i) {
      case 1:
        return "I";
      case 2:
        return "II";
      case 3:
        return "III";
      case 4:
        return "IV";
    } 
    return "";
  }
  
  private String getItemName(List<String> list) {
    if (list.size() > 0)
      return list.get(0); 
    return "§f";
  }
  
  private List<String> getItemLore(List<String> list) {
    List<String> lore = new ArrayList<>();
    if (list.size() > 1) {
      lore.addAll(list);
      lore.remove(0);
    } 
    return lore;
  }
}
