package com.yallage.yabedwars.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ListUtils {
    public static List<String> newList(String... listitem) {
        List<String> list = new ArrayList<>();
        byte b;
        int i;
        String[] arrayOfString;
        for (i = (arrayOfString = listitem).length, b = 0; b < i; ) {
            String l = arrayOfString[b];
            list.add(l);
            b++;
        }
        return list;
    }

    public static List<String> hashSetToList(HashSet<String> set) {
        List<String> list = new ArrayList<>();
        list.addAll(set);
        return list;
    }
}
