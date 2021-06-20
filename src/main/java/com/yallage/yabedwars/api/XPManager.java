package com.yallage.yabedwars.api;

import java.util.HashMap;
import java.util.UUID;

import com.yallage.yabedwars.config.Config;
import com.yallage.yabedwars.utils.ActionBarUtils;
import org.bukkit.entity.Player;

public class XPManager {
    private static final HashMap<String, XPManager> managerMap = new HashMap<>();

    private final HashMap<UUID, Integer> xp = new HashMap<>();

    public static XPManager getXPManager(String bedwarsGame) {
        if (!managerMap.containsKey(bedwarsGame))
            managerMap.put(bedwarsGame, new XPManager());
        return managerMap.get(bedwarsGame);
    }

    public static void reset(String bedwarsGame) {
        (getXPManager(bedwarsGame)).xp.clear();
        managerMap.remove(bedwarsGame);
    }

    public void updateXPBar(Player player) {
        player.setLevel(get(player));
    }

    private void set(Player player, int count) {
        this.xp.put(player.getUniqueId(), count);
        updateXPBar(player);
    }

    private int get(Player player) {
        Integer value = this.xp.get(player.getUniqueId());
        if (value == null) {
            value = 0;
            this.xp.put(player.getUniqueId(), 0);
        }
        return value;
    }

    public void setXP(Player player, int count) {
        set(player, count);
    }

    public int getXP(Player player) {
        return get(player);
    }

    public void addXP(Player player, int count) {
        set(player, get(player) + count);
    }

    public boolean takeXP(Player player, int count) {
        if (!hasEnoughXP(player, count))
            return false;
        set(player, get(player) - count);
        return true;
    }

    public boolean hasEnoughXP(Player player, int count) {
        return (get(player) >= count);
    }

    private final HashMap<UUID, Long> messageTimeMap = new HashMap<>();

    private final HashMap<UUID, Integer> messageCountMap = new HashMap<>();

    public void sendXPMessage(Player player, int count) {
        if (!this.messageTimeMap.containsKey(player.getUniqueId()))
            this.messageTimeMap.put(player.getUniqueId(), System.currentTimeMillis());
        if (!this.messageCountMap.containsKey(player.getUniqueId()))
            this.messageCountMap.put(player.getUniqueId(), 0);
        if (System.currentTimeMillis() - this.messageTimeMap.get(player.getUniqueId()) > 500L)
            this.messageCountMap.put(player.getUniqueId(), 0);
        this.messageTimeMap.put(player.getUniqueId(), System.currentTimeMillis());
        int c = this.messageCountMap.get(player.getUniqueId()) + count;
        this.messageCountMap.put(player.getUniqueId(), c);
        if (!Config.xpMessage.equals(""))
            ActionBarUtils.sendActionBar(player, Config.xpMessage.replaceAll("%xp%", Integer.toString(c)));
    }

    public void sendMaxXPMessage(Player player) {
        if (!Config.maxXPMessage.equals(""))
            ActionBarUtils.sendActionBar(player, Config.maxXPMessage);
    }
}
