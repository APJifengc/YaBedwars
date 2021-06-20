package com.yallage.yabedwars.command;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import com.yallage.yabedwars.config.Config;
import com.yallage.yabedwars.utils.SendMessageUtils;
import com.yallage.yabedwars.api.XPManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditXPCommandListener implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
        int xp;
        if (args.length == 0) {
            SendMessageUtils.sendMessage(sender, "§6§l[BedwarsXP] §b经验起床插件 §lBy.Ldcr", "§6§l[BedwarsXP] §a/editxp <经验值>  设置自己的经验值", "§6§l[BedwarsXP] §a/editxp <玩家> <经验值> 设置其他玩家的经验值");
            return true;
        }
        if (!sender.hasPermission("bedwarsxp.admin")) {
            sender.sendMessage("§6§l[BedwarsXP] §c你没有权限执行此命令");
            return true;
        }
        String user = args[0];
        Player player1 = Bukkit.getPlayer(user);
        if (player1 != null) {
            if (args.length < 2) {
                SendMessageUtils.sendMessage(sender, "§6§l[BedwarsXP] §b经验起床插件 §lBy.Ldcr", "§6§l[BedwarsXP] §a/editxp <经验值>  设置自己的经验值", "§6§l[BedwarsXP] §a/editxp <玩家> <经验值> 设置其他玩家的经验值");
                return true;
            }
            if (player1.isOnline()) {
                Player player = player1.getPlayer();
                Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
                if (game == null) {
                    sender.sendMessage("§6§l[BedwarsXP] §c玩家 " + player.getName() + " 不在游戏中!");
                    return true;
                }
                if (!Config.isGameEnabledXP(game.getName())) {
                    sender.sendMessage("§6§l[BedwarsXP] §c玩家 " + player.getName() + " 所在的游戏没有开启经验起床模式!");
                    return true;
                }
                try {
                    xp = Integer.valueOf(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§6§l[BedwarsXP] §c输入经验值的不是一个有效数字!");
                    return true;
                }
                XPManager.getXPManager(game.getName()).setXP(player, xp);
                sender.sendMessage("§6§l[BedwarsXP] §a玩家 " + player.getName() + " 的经验值已被设置为 " + xp);
            } else {
                sender.sendMessage("§6§l[BedwarsXP] §c玩家 " + player1.getName() + " 不在线!");
                return true;
            }
            return true;
        }
        if (!(sender instanceof Player)) {
            SendMessageUtils.sendMessage(sender, "§6§l[BedwarsXP] §b经验起床插件 §lBy.Ldcr", "§6§l[BedwarsXP] §a/editxp <玩家> <经验值> 设置玩家的经验值");
            return true;
        }
        Player p = (Player) sender;
        Game bw = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(p);
        if (bw == null) {
            sender.sendMessage("§6§l[BedwarsXP] §c玩家 " + p.getName() + " 不在游戏中!");
            return true;
        }
        if (!Config.isGameEnabledXP(bw.getName())) {
            sender.sendMessage("§6§l[BedwarsXP] §c玩家 " + p.getName() + " 所在的游戏没有开启经验起床模式!");
            return true;
        }
        try {
            xp = Integer.valueOf(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§6§l[BedwarsXP] §c输入经验值的不是一个有效数字!");
            return true;
        }
        XPManager.getXPManager(bw.getName()).setXP(p, xp);
        sender.sendMessage("§6§l[BedwarsXP] §a玩家 " + p.getName() + " 的经验值已被设置为 " + xp);
        return true;
    }
}
