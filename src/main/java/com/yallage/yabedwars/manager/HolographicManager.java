package com.yallage.yabedwars.manager;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.ResourceSpawner;
import io.github.bedwarsrel.game.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yallage.yabedwars.api.HolographicAPI;
import com.yallage.yabedwars.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HolographicManager {
    private final Map<Player, List<HolographicAPI>> holos = new HashMap<>();

    public void displayGameLocation(Player player, String g) {
        if (this.holos.containsKey(player))
            remove(player);
        this.holos.put(player, new ArrayList<>());
        List<HolographicAPI> list = this.holos.get(player);
        Game game = BedwarsRel.getInstance().getGameManager().getGame(g);
        if (game != null) {
            if (game.getLoc1() != null) {
                HolographicAPI holo = new HolographicAPI(game.getLoc1().clone().add(0.0D, -1.75D, 0.0D),
                        Config.getLanguage("holographic.edit_game.loc1"));
                list.add(holo);
                holo.display(player);
            }
            if (game.getLoc2() != null) {
                HolographicAPI holo = new HolographicAPI(game.getLoc2().clone().add(0.0D, -1.75D, 0.0D),
                        Config.getLanguage("holographic.edit_game.loc2"));
                list.add(holo);
                holo.display(player);
            }
            if (game.getLobby() != null) {
                HolographicAPI holo = new HolographicAPI(game.getLobby().clone().add(0.0D, -1.75D, 0.0D),
                        Config.getLanguage("holographic.edit_game.lobby"));
                list.add(holo);
                holo.display(player);
            }
            for (Team team : game.getTeams().values()) {
                if (team.getTargetHeadBlock() != null) {
                    HolographicAPI holo = new HolographicAPI(team.getTargetHeadBlock().clone().add(0.5D, -1.5D, 0.5D),
                            Config.getLanguage("holographic.edit_game.bed").replace("{team}",
                                    team.getChatColor() + team.getName()));
                    list.add(holo);
                    holo.display(player);
                }
                if (team.getSpawnLocation() != null) {
                    HolographicAPI holo = new HolographicAPI(team.getSpawnLocation().clone().add(0.0D, -1.75D, 0.0D),
                            Config.getLanguage("holographic.edit_game.spawn").replace("{team}",
                                    team.getChatColor() + team.getName()));
                    list.add(holo);
                    holo.display(player);
                }
            }
            for (ResourceSpawner spawner : game.getResourceSpawners()) {
                HolographicAPI holo = new HolographicAPI(spawner.getLocation().clone().add(0.0D, -1.75D, 0.0D),
                        Config.getLanguage("holographic.edit_game.spawner").replace("{resource}", spawner.getName()));
                list.add(holo);
                holo.display(player);
            }
        }
   /*if (Config.shop_item.containsKey(g))
      for (Iterator<String> iterator = ((List)Config.shop_item.get(g)).iterator(); iterator.hasNext(); ) {
        String loc = iterator.next();
        Location location = toLocation(loc);
        if (location != null)
          try {
            Config.shop_shops.forEach((id, pl) -> {
                  if (pl.equals("shop." + g + ".item - " + )) {
                    HolographicAPI holo = new HolographicAPI(location.clone().add(0.0D, -1.75D, 0.0D), Config.getLanguage("holographic.shop.item").replace("{id}", id));
                    list.add(holo);
                    holo.display(player);
                  } 
                });
          } catch (Exception exception) {} 
      }  
    if (Config.shop_team.containsKey(g))
      for (String loc : Config.shop_team.get(g)) {
        Location location = toLocation(loc);
        if (location != null)
          try {
            Config.shop_shops.forEach((id, pl) -> {
              if (pl.equals("shop." + g + ".team - " + )) {
                HolographicAPI holo = new HolographicAPI(location.clone().add(0.0D, -1.75D, 0.0D), Config.getLanguage("holographic.shop.team").replace("{id}", id));
                list.add(holo);
                holo.display(player);
              }
            });
          } catch (Exception exception) {
          }
      }  */
    }

    public void remove(Player player) {
        if (this.holos.containsKey(player)) {
            this.holos.get(player).forEach(HolographicAPI::remove);
            this.holos.remove(player);
        }
    }

    public void removeAll() {
        this.holos.keySet().forEach(player -> (this.holos.get(player)).forEach(HolographicAPI::remove));
        this.holos.clear();
    }

    private Location toLocation(String loc) {
        try {
            String[] ary = loc.split(", ");
            if (Bukkit.getWorld(ary[0]) != null) {
                Location location = new Location(Bukkit.getWorld(ary[0]), Double.parseDouble(ary[1]),
                        Double.parseDouble(ary[2]), Double.parseDouble(ary[3]));
                if (ary.length > 4) {
                    location.setYaw(Float.parseFloat(ary[4]));
                    location.setPitch(Float.parseFloat(ary[5]));
                }
                return location;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
