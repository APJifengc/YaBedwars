package com.yallage.yabedwars;

import com.yallage.yabedwars.addon.*;
import com.yallage.yabedwars.command.Commands;
import com.yallage.yabedwars.command.EditXPCommandListener;
import com.yallage.yabedwars.config.Config;
import com.yallage.yabedwars.edit.EditGame;
import com.yallage.yabedwars.items.*;
import com.yallage.yabedwars.listener.EventListener;
import com.yallage.yabedwars.manager.ArenaManager;
import com.yallage.yabedwars.manager.HolographicManager;
import com.yallage.yabedwars.shop.GHDShop;
import com.yallage.yabedwars.shop.NewHypixelShop;
import com.yallage.yabedwars.shop.OldHypixelShop;
import com.yallage.yabedwars.utils.ActionBarUtils;
import com.yallage.yabedwars.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class YaBedwars extends JavaPlugin {
    private static YaBedwars instance;

    public static YaBedwars getInstance() {
        return instance;
    }

    public YaBedwars() {
        instance = this;
    }

    public static int mode;

    public static String message_buy;

    public static List<String> item_frame;

    public static List<String> item_back;

    private static ArenaManager arenamanager = new ArenaManager();

    private static HolographicManager holographicmanager = new HolographicManager();

    public ArenaManager getArenaManager() {
        return arenamanager;
    }

    public HolographicManager getHolographicManager() {
        return holographicmanager;
    }


    @Override
    public void onEnable() {
        try {
            Config.loadConfig();
            loadConfig();
            ActionBarUtils.load();
            getCommand("bedwarsxpedit").setExecutor(new EditXPCommandListener());
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getPluginManager().registerEvents(new OldHypixelShop(), this);
        Bukkit.getPluginManager().registerEvents(new NewHypixelShop(), this);
        Bukkit.getPluginManager().registerEvents(new GHDShop(), this);
        Bukkit.getPluginCommand("yabedwars").setExecutor(new Commands());
        registerEvents();
    }

    public void loadConfig() {
        saveDefaultConfig();
        reloadConfig();
        mode = getConfig().getInt("mode");
        message_buy = ColorUtil.color(getConfig().getString("message.buy"));
        item_frame = ColorUtil.listColor(getConfig().getStringList("item.frame"));
        item_back = ColorUtil.listColor(getConfig().getStringList("item.back"));
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new FireBall(), this);
        Bukkit.getPluginManager().registerEvents(new LightTNT(), this);
        Bukkit.getPluginManager().registerEvents(new BridgeEgg(), this);
        Bukkit.getPluginManager().registerEvents(new Parachute(), this);
        Bukkit.getPluginManager().registerEvents(new TNTLaunch(), this);
        Bukkit.getPluginManager().registerEvents(new Trampoline(), this);
        Bukkit.getPluginManager().registerEvents(new WalkPlatform(), this);
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new TeamIronGolem(), this);
        Bukkit.getPluginManager().registerEvents(new TeamSilverFish(), this);
        Bukkit.getPluginManager().registerEvents(new ExplosionProof(), this);
        Bukkit.getPluginManager().registerEvents(new EnderPearlChair(), this);
        Bukkit.getPluginManager().registerEvents(new Shop(), this);
        Bukkit.getPluginManager().registerEvents(new LobbyScoreBoard(), this);
        Bukkit.getPluginManager().registerEvents(new SpawnNoBuild(), this);
        Bukkit.getPluginManager().registerEvents(new ChatFormat(), this);
        Bukkit.getPluginManager().registerEvents(new HidePlayer(), this);
        Bukkit.getPluginManager().registerEvents(new WitherBow(), this);
        Bukkit.getPluginManager().registerEvents(new DeathItem(), this);
        Bukkit.getPluginManager().registerEvents(new Spectator(), this);
        Bukkit.getPluginManager().registerEvents(new GiveItem(), this);
        Bukkit.getPluginManager().registerEvents(new TimeTask(), this);
        Bukkit.getPluginManager().registerEvents(new EditGame(), this);
        Bukkit.getPluginManager().registerEvents(new Compass(), this);
        Bukkit.getPluginManager().registerEvents(new Title(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
