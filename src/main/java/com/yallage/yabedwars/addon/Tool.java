package com.yallage.yabedwars.addon;

import com.yallage.yabedwars.config.Config;
import com.yallage.yabedwars.shop.NewHypixelShop;
import com.yallage.yabedwars.xpshop.XPItemShop;
import com.yallage.yabedwars.xpshop.XPVillagerTrade;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.*;
import io.github.bedwarsrel.villager.VillagerTrade;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Tool implements Listener {
    private static ItemStack _s(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.spigot().setUnbreakable(true);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
    public final static Map<ToolType, Map<ToolLevel, ItemStack>> toolValue = new HashMap<ToolType, Map<ToolLevel, ItemStack>>(){{
        put(ToolType.PICKAXE, new HashMap<ToolLevel, ItemStack>(){{
            put(ToolLevel.WOODEN, Config.getResourceItem("iron", 10));
            put(ToolLevel.IRON, Config.getResourceItem("iron", 10));
            put(ToolLevel.GOLDEN, Config.getResourceItem("gold", 3));
            put(ToolLevel.DIAMOND, Config.getResourceItem("gold", 6));
        }});
        put(ToolType.AXE, new HashMap<ToolLevel, ItemStack>(){{
            put(ToolLevel.WOODEN, Config.getResourceItem("iron", 10));
            put(ToolLevel.STONE, Config.getResourceItem("iron", 10));
            put(ToolLevel.IRON, Config.getResourceItem("gold", 3));
            put(ToolLevel.DIAMOND, Config.getResourceItem("gold", 6));
        }});
        put(ToolType.SHEAR, new HashMap<ToolLevel, ItemStack>(){{
            put(ToolLevel.WOODEN, Config.getResourceItem("iron", 20));
        }});
    }};
    public final static Map<ToolType, Map<ToolLevel, ItemStack>> toolItem = new HashMap<ToolType, Map<ToolLevel, ItemStack>>(){{
        put(ToolType.PICKAXE, new HashMap<ToolLevel, ItemStack>(){{
            put(ToolLevel.NONE, new ItemStack(Material.AIR));
            ItemStack wooden = new ItemStack(Material.WOOD_PICKAXE);
            wooden.addEnchantment(Enchantment.DIG_SPEED, 1);
            ItemStack iron = new ItemStack(Material.IRON_PICKAXE);
            iron.addEnchantment(Enchantment.DIG_SPEED, 2);
            ItemStack gold = new ItemStack(Material.GOLD_PICKAXE);
            gold.addEnchantment(Enchantment.DIG_SPEED, 3);
            ItemMeta meta = gold.getItemMeta();
            meta.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
            gold.setItemMeta(meta);
            ItemStack diamond = new ItemStack(Material.DIAMOND_PICKAXE);
            diamond.addEnchantment(Enchantment.DIG_SPEED, 3);
            put(ToolLevel.WOODEN, _s(wooden));
            put(ToolLevel.IRON, _s(iron));
            put(ToolLevel.GOLDEN, _s(gold));
            put(ToolLevel.DIAMOND, _s(diamond));
        }});
        put(ToolType.AXE, new HashMap<ToolLevel, ItemStack>(){{
            put(ToolLevel.NONE, new ItemStack(Material.AIR));
            ItemStack wooden = new ItemStack(Material.WOOD_AXE);
            wooden.addEnchantment(Enchantment.DIG_SPEED, 1);
            ItemStack stone = new ItemStack(Material.STONE_AXE);
            stone.addEnchantment(Enchantment.DIG_SPEED, 1);
            ItemStack iron = new ItemStack(Material.IRON_AXE);
            iron.addEnchantment(Enchantment.DIG_SPEED, 2);
            ItemStack diamond = new ItemStack(Material.DIAMOND_AXE);
            diamond.addEnchantment(Enchantment.DIG_SPEED, 3);
            put(ToolLevel.WOODEN, _s(wooden));
            put(ToolLevel.STONE, _s(stone));
            put(ToolLevel.IRON, _s(iron));
            put(ToolLevel.DIAMOND, _s(diamond));
        }});
        put(ToolType.SHEAR, new HashMap<ToolLevel, ItemStack>(){{
            put(ToolLevel.NONE, new ItemStack(Material.AIR));
            put(ToolLevel.WOODEN, _s(new ItemStack(Material.SHEARS)));
        }});
    }};
    public final static Map<Player, Map<ToolType, ToolLevel>> toolLevelMap = new HashMap<>();

    /**
     * 永久 剪刀	20 个铁锭
     * 斧头 (可升级)
     * 木斧 (效率 I) - 10 个铁锭
     *
     * 石斧 (效率 I) - 10 个铁锭
     *
     * 铁斧 (效率 II) - 3 个金锭
     *
     * 钻石斧 (效率 III) - 6 个金锭
     *
     * 镐子 (可升级)
     * 木镐 (效率 I) - 10 个铁锭
     *
     * 铁镐 (效率 II) - 10 个铁锭
     *
     * 金镐 (效率 III, 锋利 II) - 3 个金锭
     *
     * 钻石镐 (效率 III) - 6 个金锭
     */
    public enum ToolLevel {
        WOODEN, STONE, IRON, GOLDEN, DIAMOND, NONE
    }
    public enum ToolType {
        PICKAXE, AXE, SHEAR
    }

    public static ToolLevel nextLevel(ToolType type, ToolLevel level) {
        switch (type) {
            case AXE:
                switch (level) {
                    case NONE: return ToolLevel.WOODEN;
                    case WOODEN: return ToolLevel.STONE;
                    case STONE: return ToolLevel.IRON;
                    case IRON: return ToolLevel.DIAMOND;
                    case DIAMOND: return null;
                }
            case SHEAR:
                if (level == ToolLevel.NONE) return ToolLevel.WOODEN;
                return null;
            case PICKAXE:
                switch (level) {
                    case NONE: return ToolLevel.WOODEN;
                    case WOODEN: return ToolLevel.IRON;
                    case IRON: return ToolLevel.GOLDEN;
                    case GOLDEN: return ToolLevel.DIAMOND;
                    case DIAMOND: return null;
                }
        }
        return null;
    }

    public static ToolLevel downLevel(ToolType type, ToolLevel level) {
        switch (type) {
            case AXE:
                switch (level) {
                    case NONE: return ToolLevel.NONE;
                    case WOODEN:
                    case STONE:
                        return ToolLevel.WOODEN;
                    case IRON: return ToolLevel.STONE;
                    case DIAMOND: return ToolLevel.IRON;
                }
            case SHEAR:
                if (level == ToolLevel.NONE) return ToolLevel.NONE;
                return ToolLevel.WOODEN;
            case PICKAXE:
                switch (level) {
                    case NONE: return ToolLevel.NONE;
                    case WOODEN:
                    case IRON:
                        return ToolLevel.WOODEN;
                    case GOLDEN: return ToolLevel.IRON;
                    case DIAMOND: return ToolLevel.GOLDEN;
                }
        }
        return null;
    }

    public static ItemStack getToolShopItem(Player player, ToolType type) {
        ToolLevel nextLevel = nextLevel(type, toolLevelMap.get(player).getOrDefault(type, ToolLevel.NONE));
        VillagerTrade trade = new VillagerTrade(toolValue.get(type).get(nextLevel), toolItem.get(type).get(nextLevel));
        if (Config.isGameEnabledXP(BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player).getName()))
            trade = new XPVillagerTrade(trade);
        if (nextLevel != null) return XPItemShop.toItemStack(trade,
                player,
                BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player));
        ItemStack item;
        if (type == ToolType.SHEAR) item = toolItem.get(type).get(ToolLevel.WOODEN).clone();
        else item = toolItem.get(type).get(ToolLevel.DIAMOND).clone();
        NewHypixelShop.addUnusableLore(item);
        return item;
    }

    @EventHandler
    public void onDeath(BedwarsPlayerKilledEvent event) {
        for (Map.Entry<ToolType, ToolLevel> entry : toolLevelMap.get(event.getPlayer()).entrySet()) {
            toolLevelMap.get(event.getPlayer()).put(entry.getKey(), downLevel(entry.getKey(), entry.getValue()));
        }
    }

    @EventHandler
    public void onJoin(BedwarsPlayerJoinEvent event) {
        if (!toolLevelMap.containsKey(event.getPlayer())) toolLevelMap.put(event.getPlayer(), new HashMap<>());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        event.getPlayer().getInventory().addItem(
                toolItem.get(ToolType.PICKAXE).get(toolLevelMap.get(event.getPlayer()).getOrDefault(ToolType.PICKAXE, ToolLevel.NONE))
        );
        event.getPlayer().getInventory().addItem(
                toolItem.get(ToolType.AXE).get(toolLevelMap.get(event.getPlayer()).getOrDefault(ToolType.AXE, ToolLevel.NONE))
        );
        event.getPlayer().getInventory().addItem(
                toolItem.get(ToolType.SHEAR).get(toolLevelMap.get(event.getPlayer()).getOrDefault(ToolType.SHEAR, ToolLevel.NONE))
        );
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Material type = event.getItemDrop().getItemStack().getType();
        if (type.toString().contains("_PICKAXE") || type.toString().contains("_AXE") || type == Material.SHEARS) event.setCancelled(true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClick() == ClickType.NUMBER_KEY) {
            if (event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) == null) return;
            Material type = event.getWhoClicked().getInventory().getItem(event.getHotbarButton()).getType();
            if (type.toString().contains("_PICKAXE") || type.toString().contains("_AXE") || type == Material.SHEARS) {
                if (!Objects.equals(event.getClickedInventory().getName(), "container.inventory")) {
                    event.setCancelled(true);
                }
            }
        }
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
            Material type = event.getCurrentItem().getType();
            if (type.toString().contains("_PICKAXE") || type.toString().contains("_AXE") || type == Material.SHEARS) {
                if (!Objects.equals(event.getWhoClicked().getOpenInventory().getTopInventory().getName(), "container.crafting")) {
                    event.setCancelled(true);
                }
            }
        }
        if (event.getCursor() != null) {
            Material type = event.getCursor().getType();
            if (type.toString().contains("_PICKAXE") || type.toString().contains("_AXE") || type == Material.SHEARS) {
                if (event.getClickedInventory() != event.getWhoClicked().getInventory()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
