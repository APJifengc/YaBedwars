package com.yallage.yabedwars.manager;

import java.util.HashMap;
import java.util.Map;

import com.yallage.yabedwars.arena.Arena;

public class ArenaManager {
    private final Map<String, Arena> arenas = new HashMap<>();

    public void addArena(String game, Arena arena) {
        this.arenas.put(game, arena);
    }

    public void removeArena(String game) {
        this.arenas.remove(game);
    }

    public Arena getArena(String game) {
        return this.arenas.get(game);
    }

    public Map<String, Arena> getArenas() {
        return this.arenas;
    }
}
