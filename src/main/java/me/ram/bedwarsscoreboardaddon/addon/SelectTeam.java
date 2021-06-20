package me.ram.bedwarsscoreboardaddon.addon;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import java.util.ArrayList;
import java.util.List;
import me.ram.bedwarsscoreboardaddon.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

public class SelectTeam {
  public static void openSelectTeam(Game game, Player player) {
    int size = 27 + 9 * game.getTeams().values().size() / 7;
    Inventory inventory = Bukkit.createInventory(null, size, BedwarsRel._l((CommandSender)player, "lobby.chooseteam"));
    int slot = 10;
    for (Team team : game.getTeams().values()) {
      switch (slot) {
        case 17:
          slot = 19;
          break;
        case 26:
          slot = 28;
          break;
      } 
      Wool wool = new Wool(team.getColor().getDyeColor());
      ItemStack itemStack = wool.toItemStack(1);
      ItemMeta itemMeta = itemStack.getItemMeta();
      String color = team.getChatColor().toString();
      String status = Config.select_team_status_select;
      if (team.getPlayers().contains(player)) {
        status = Config.select_team_status_inteam;
      } else if (team.getPlayers().size() >= team.getMaxPlayers()) {
        status = Config.select_team_status_team_full;
      } 
      itemMeta.setDisplayName(
          Config.select_team_item_name.replace("{status}", status).replace("{team}", team.getName())
          .replace("{color}", color).replace("{players}", (new StringBuilder(String.valueOf(team.getPlayers().size()))).toString())
          .replace("{maxplayers}", (new StringBuilder(String.valueOf(team.getMaxPlayers()))).toString()));
      List<String> lore = new ArrayList<>();
      for (String l : Config.select_team_item_lore) {
        if (l.contains("{players_list}")) {
          if (team.getPlayers().size() > 0) {
            for (Player p : team.getPlayers())
              lore.add(l.replace("{status}", status).replace("{team}", team.getName())
                  .replace("{color}", color).replace("{players}", (new StringBuilder(String.valueOf(team.getPlayers().size()))).toString())
                  .replace("{maxplayers}", (new StringBuilder(String.valueOf(team.getMaxPlayers()))).toString())
                  .replace("{players_list}", p.getDisplayName())); 
            continue;
          } 
          lore.add(l.replace("{status}", status).replace("{team}", team.getName())
              .replace("{color}", color).replace("{players}", (new StringBuilder(String.valueOf(team.getPlayers().size()))).toString())
              .replace("{maxplayers}", (new StringBuilder(String.valueOf(team.getMaxPlayers()))).toString())
              .replace("{players_list}", Config.select_team_no_players));
          continue;
        } 
        lore.add(l.replace("{status}", status).replace("{team}", team.getName()).replace("{color}", color)
            .replace("{players}", (new StringBuilder(String.valueOf(team.getPlayers().size()))).toString())
            .replace("{maxplayers}", (new StringBuilder(String.valueOf(team.getMaxPlayers()))).toString()));
      } 
      itemMeta.setLore(lore);
      itemStack.setItemMeta(itemMeta);
      inventory.setItem(slot, itemStack);
      slot++;
    } 
    player.closeInventory();
    player.openInventory(inventory);
  }
}
