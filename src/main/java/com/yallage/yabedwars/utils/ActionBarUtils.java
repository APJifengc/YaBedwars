package com.yallage.yabedwars.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionBarUtils {
    public static Plugin plugin;

    public static String nmsVer;

    private static boolean useOldMethods = false;

    public static void load() {
        nmsVer = Bukkit.getServer().getClass().getPackage().getName();
        nmsVer = nmsVer.substring(nmsVer.lastIndexOf(".") + 1);
        if (nmsVer.equalsIgnoreCase("v1_8_R1") ||
                nmsVer.equalsIgnoreCase("v1_7_"))
            useOldMethods = true;
    }

    public static void sendActionBar(Player player, String message) {
        try {
            Object ppoc;
            Class<?> c1 = Class.forName("org.bukkit.craftbukkit." + nmsVer +
                    ".entity.CraftPlayer");
            Object p = c1.cast(player);
            Class<?> c4 = Class.forName("net.minecraft.server." + nmsVer +
                    ".PacketPlayOutChat");
            Class<?> c5 = Class.forName("net.minecraft.server." + nmsVer +
                    ".Packet");
            if (useOldMethods) {
                Class<?> c2 = Class.forName("net.minecraft.server." + nmsVer +
                        ".ChatSerializer");
                Class<?> c3 = Class.forName("net.minecraft.server." + nmsVer +
                        ".IChatBaseComponent");
                Method m3 = c2.getDeclaredMethod("a", String.class);
                Object cbc = c3.cast(m3.invoke(c2, "{\"text\": \"" + message +
                        "\"}"));
                ppoc = c4.getConstructor(new Class[]{c3, byte.class}).newInstance(cbc, (byte) 2);
            } else {
                Class<?> c2 = Class.forName("net.minecraft.server." + nmsVer +
                        ".ChatComponentText");
                Class<?> c3 = Class.forName("net.minecraft.server." + nmsVer +
                        ".IChatBaseComponent");
                Object o = c2.getConstructor(new Class[]{String.class}).newInstance(message);
                ppoc = c4.getConstructor(new Class[]{c3, byte.class}).newInstance(o, (byte) 2);
            }
            Method m1 = c1.getDeclaredMethod("getHandle");
            Object h = m1.invoke(p);
            Field f1 = h.getClass().getDeclaredField("playerConnection");
            Object pc = f1.get(h);
            Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
            m5.invoke(pc, ppoc);
        } catch (Exception ex) {
            ex.printStackTrace();
            player.sendMessage(message);
        }
    }

    public static void sendActionBar(final Player player, final String message, int duration) {
        sendActionBar(player, message);
        if (duration >= 0)
            (new BukkitRunnable() {
                public void run() {
                    ActionBarUtils.sendActionBar(player, "");
                }
            }).runTaskLater(plugin, (duration + 1));
        while (duration > 60) {
            duration -= 60;
            int scheduler = duration % 60;
            (new BukkitRunnable() {
                public void run() {
                    ActionBarUtils.sendActionBar(player, message);
                }
            }).runTaskLater(plugin, scheduler);
        }
    }

    public static void sendActionBarToAllPlayers(String message) {
        sendActionBarToAllPlayers(message, -1);
    }

    public static void sendActionBarToAllPlayers(String message, int duration) {
        for (Player p : Bukkit.getOnlinePlayers())
            sendActionBar(p, message, duration);
    }
}
