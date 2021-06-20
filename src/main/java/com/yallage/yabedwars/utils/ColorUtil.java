package com.yallage.yabedwars.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class ColorUtil {
    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> listColor(List<String> list) {
        List<String> clist = new ArrayList<>();
        for (String l : list)
            clist.add(ChatColor.translateAlternateColorCodes('&', l));
        return clist;
    }

    public static String remColor(String s) {
        return ChatColor.stripColor(s);
    }

    public static List<String> remListColor(List<String> list) {
        List<String> clist = new ArrayList<>();
        for (String l : list)
            clist.add(ChatColor.stripColor(l));
        return clist;
    }
}
