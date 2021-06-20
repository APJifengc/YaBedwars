package com.yallage.yabedwars;

import com.yallage.yabedwars.config.Config;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.shop.NewItemShop;
import io.github.bedwarsrel.villager.MerchantCategory;
import io.github.bedwarsrel.villager.MerchantCategoryComparator;
import io.github.bedwarsrel.villager.VillagerTrade;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yallage.yabedwars.YaBedwars;
import com.yallage.yabedwars.utils.ListUtils;
import com.yallage.yabedwars.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopReplacer implements Runnable {
    final Game game;

    final CommandSender s;

    public static void replaceShop(String bw, CommandSender sender) {
        if (!Config.isGameEnabledXP(bw))
            return;
        Bukkit.getScheduler().runTaskLater(YaBedwars.getInstance(), new ShopReplacer(bw, sender), 20L);
    }

    public ShopReplacer(String e, CommandSender sender) {
        this.s = sender;
        this.game = BedwarsRel.getInstance().getGameManager().getGame(e);
    }

    public void run() {
        HashMap<Material, MerchantCategory> map = this.game.getItemShopCategories();
        if (Config.fullXPBedwars)
            for (Map.Entry<Material, MerchantCategory> en : map.entrySet()) {
                MerchantCategory m = en.getValue();
                ArrayList<VillagerTrade> t = m.getOffers();
                ArrayList<XPVillagerTrade> n = new ArrayList<>();
                for (VillagerTrade villagerTrade : t) n.add(new XPVillagerTrade(villagerTrade));
                try {
                    ReflectionUtils.setPrivateValue(m, "offers", n);
                } catch (Exception e1) {
                    this.s.sendMessage("§6§l[BedwarsXP] §c为地图 " + this.game.getName() + " 替换原始商店为经验商店失败");
                    e1.printStackTrace();
                }
                map.put(en.getKey(), m);
            }
        if (Config.addResShop) {
            ArrayList<VillagerTrade> trades = new ArrayList<>();
            for (String key : Config.resourceskey) {
                List<Map<String, Object>> resourceList = (List<Map<String, Object>>) BedwarsRel.getInstance().getConfig().getList("resource." + key + ".item");
                for (Map<String, Object> resource : resourceList) {
                    ItemStack itemStack = ItemStack.deserialize(resource);
                    trades.add(new XPVillagerTrade(itemStack));
                }
            }
            MerchantCategory mc = new MerchantCategory("§6§l经验兑换资源", Material.EXP_BOTTLE, trades, ListUtils.newList("§a将你的经验兑换成物品"), 3, "bw.base");
            map.put(Material.EXP_BOTTLE, mc);
        }
        try {
            Field itemShops = ReflectionUtils.getField(this.game, "newItemShops");
            itemShops.setAccessible(true);
            HashMap<Player, NewItemShop> shops = new HashMap<>();
            List<MerchantCategory> order = new ArrayList<>(map.values());
            order.sort(new MerchantCategoryComparator());
            for (Player pl : this.game.getPlayers()) {
                XPItemShop Shop = new XPItemShop(order, this.game);
                shops.put(pl, Shop);
            }
            ReflectionUtils.setPrivateValue(this.game, "newItemShops", shops);
            this.s.sendMessage("§6§l[BedwarsXP] §b为地图 " + this.game.getName() + " 替换经验商店成功!");
        } catch (Exception e) {
            this.s.sendMessage("§6§l[BedwarsXP] §c为地图 " + this.game.getName() + " 初始化经验商店时出错");
            e.printStackTrace();
        }
    }
}
