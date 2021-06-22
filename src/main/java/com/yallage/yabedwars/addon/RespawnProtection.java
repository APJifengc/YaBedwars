package com.yallage.yabedwars.addon;

import com.yallage.yabedwars.YaBedwars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class RespawnProtection implements Listener {
    private final Map<Player, BukkitTask> map = new HashMap<>();
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        event.getPlayer().setNoDamageTicks(200);
        map.put(event.getPlayer(), new BukkitRunnable() {
            int i = 5;
            @Override
            public void run() {
                if (i == 0) {
                    event.getPlayer().sendMessage(ChatColor.YELLOW + "无敌时间结束，小心！");
                    map.remove(event.getPlayer());
                    cancel();
                    return;
                }
                event.getPlayer().sendMessage(ChatColor.YELLOW + "无敌时间剩余"+ ChatColor.RED + i-- + ChatColor.YELLOW + "秒！");
            }
        }.runTaskTimer(YaBedwars.getInstance(), 100L, 20L));
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            if (map.containsKey(event.getDamager())) {
                ((Player) event.getDamager()).setNoDamageTicks(0);
                map.get(event.getDamager()).cancel();
                map.remove(event.getDamager());
                event.getDamager().sendMessage(ChatColor.YELLOW + "你攻击了一名玩家，无敌时间已停止！");
            }
        }
    }
}
