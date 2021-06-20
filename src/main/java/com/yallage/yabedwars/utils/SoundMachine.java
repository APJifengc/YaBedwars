package com.yallage.yabedwars.utils;

import org.bukkit.Sound;

public class SoundMachine {
    public static Sound get(String v18, String v19) {
        Sound sound = null;
        try {
            sound = Sound.valueOf(v18);
        } catch (Exception ignored) {
        }
        return sound;
    }
}
