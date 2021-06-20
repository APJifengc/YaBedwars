package com.yallage.yabedwars.command;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.game.TeamColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class BedwarsRelCommandTabCompleter implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> suggest = getSuggest(sender, args);
        String last = args[args.length - 1];
        if (suggest != null && !last.equals("")) {
            List<String> list = new ArrayList<>();
            suggest.forEach(s -> {
                if (s.startsWith(last))
                    list.add(s);
            });
            return list;
        }
        return suggest;
    }

    private List<String> getSuggest(CommandSender sender, String[] args) {
        if (args.length == 1)
            return Arrays.asList("help", "setspawner", "addgame", "start", "stop", "addteam", "save", "setregion",
                    "join", "setspawn",
                    "setlobby", "settarget", "setbed", "leave", "reload", "setmainlobby", "list",
                    "regionname", "removeteam", "removegame",
                    "clearspawner", "gametime", "stats", "setminplayers",
                    "setgameblock", "setbuilder", "setautobalance", "kick", "addteamjoin", "addholo",
                    "removeholo",
                    "debugpaste", "itemspaste");
        List<String> games = new ArrayList<>();
        BedwarsRel.getInstance().getGameManager().getGames().forEach(game -> games.add(game.getName()));
        if (args.length == 2)
            if (args[0].equalsIgnoreCase("setspawner")) {
                if (sender.hasPermission("bw.setup"))
                    return games;
            } else if (args[0].equalsIgnoreCase("start")) {
                if (sender.hasPermission("bw.setup"))
                    return games;
            } else if (args[0].equalsIgnoreCase("addteam")) {
                if (sender.hasPermission("bw.setup"))
                    return games;
            } else if (args[0].equalsIgnoreCase("save")) {
                if (sender.hasPermission("bw.setup"))
                    return games;
            } else if (args[0].equalsIgnoreCase("setregion")) {
                if (sender.hasPermission("bw.setup"))
                    return games;
            } else {
                if (args[0].equalsIgnoreCase("join"))
                    return games;
                if (args[0].equalsIgnoreCase("setspawn")) {
                    if (sender.hasPermission("bw.setup"))
                        return games;
                } else if (args[0].equalsIgnoreCase("setlobby")) {
                    if (sender.hasPermission("bw.setup"))
                        return games;
                } else if (args[0].equalsIgnoreCase("settarget")) {
                    if (sender.hasPermission("bw.setup"))
                        return games;
                } else if (args[0].equalsIgnoreCase("setbed")) {
                    if (sender.hasPermission("bw.setup"))
                        return games;
                } else if (args[0].equalsIgnoreCase("setmainlobby")) {
                    if (sender.hasPermission("bw.setup"))
                        return games;
                } else if (args[0].equalsIgnoreCase("regionname")) {
                    if (sender.hasPermission("bw.setup"))
                        return games;
                } else if (args[0].equalsIgnoreCase("removeteam")) {
                    if (sender.hasPermission("bw.setup"))
                        return games;
                } else if (args[0].equalsIgnoreCase("removegame")) {
                    if (sender.hasPermission("bw.setup"))
                        return games;
                } else if (args[0].equalsIgnoreCase("clearspawner")) {
                    if (sender.hasPermission("bw.setup"))
                        return games;
                } else if (args[0].equalsIgnoreCase("gametime")) {
                    if (sender.hasPermission("bw.setup"))
                        return games;
                } else if (args[0].equalsIgnoreCase("stats")) {
                    if (sender.hasPermission("bw.base"))
                        return null;
                } else if (args[0].equalsIgnoreCase("stop")) {
                    if (sender.hasPermission("bw.base"))
                        return games;
                } else if (args[0].equalsIgnoreCase("setminplayers")) {
                    if (sender.hasPermission("bw.setup"))
                        return games;
                } else if (args[0].equalsIgnoreCase("setgameblock")) {
                    if (sender.hasPermission("bw.setup"))
                        return games;
                } else if (args[0].equalsIgnoreCase("setbuilder")) {
                    if (sender.hasPermission("bw.setup"))
                        return games;
                } else if (args[0].equalsIgnoreCase("setautobalance")) {
                    if (sender.hasPermission("bw.setup"))
                        return games;
                } else if (args[0].equalsIgnoreCase("kick")) {
                    if (sender.hasPermission("bw.kick"))
                        return null;
                } else if (args[0].equalsIgnoreCase("addteamjoin") &&
                        sender.hasPermission("bw.setup")) {
                    return games;
                }
            }
        if (args.length == 3) {
            Game game = BedwarsRel.getInstance().getGameManager().getGame(args[1]);
            List<String> teams = new ArrayList<>();
            if (game != null)
                for (Team team : game.getTeams().values())
                    teams.add(team.getName());
            if (args[0].equalsIgnoreCase("setspawner")) {
                if (sender.hasPermission("bw.setup")) {
                    return new ArrayList<>(
                            BedwarsRel.getInstance().getConfig().getConfigurationSection("resource").getKeys(false));
                }
            } else if (args[0].equalsIgnoreCase("setregion")) {
                if (sender.hasPermission("bw.setup"))
                    return Arrays.asList("loc1", "loc2");
            } else if (args[0].equalsIgnoreCase("setspawn")) {
                if (sender.hasPermission("bw.setup"))
                    return teams;
            } else if (args[0].equalsIgnoreCase("settarget")) {
                if (sender.hasPermission("bw.setup"))
                    return teams;
            } else if (args[0].equalsIgnoreCase("setbed")) {
                if (sender.hasPermission("bw.setup"))
                    return teams;
            } else if (args[0].equalsIgnoreCase("removeteam")) {
                if (sender.hasPermission("bw.setup"))
                    return teams;
            } else if (args[0].equalsIgnoreCase("setgameblock")) {
                if (sender.hasPermission("bw.setup")) {
                    List<String> list = new ArrayList<>();
                    byte b;
                    int i;
                    Material[] arrayOfMaterial;
                    for (i = (arrayOfMaterial = Material.values()).length, b = 0; b < i; ) {
                        Material type = arrayOfMaterial[b];
                        list.add(type.name());
                        b++;
                    }
                    return list;
                }
            } else if (args[0].equalsIgnoreCase("setbuilder")) {
                if (sender.hasPermission("bw.setup"))
                    return null;
            } else if (args[0].equalsIgnoreCase("addteamjoin") &&
                    sender.hasPermission("bw.setup")) {
                return teams;
            }
        }
        if (args.length == 4 &&
                args[0].equalsIgnoreCase("addteam") &&
                sender.hasPermission("bw.setup")) {
            List<String> list = new ArrayList<>();
            byte b;
            int i;
            TeamColor[] arrayOfTeamColor;
            for (i = (arrayOfTeamColor = TeamColor.values()).length, b = 0; b < i; ) {
                TeamColor teamColor = arrayOfTeamColor[b];
                list.add(teamColor.name());
                b++;
            }
            return list;
        }
        return new ArrayList<>();
    }
}
