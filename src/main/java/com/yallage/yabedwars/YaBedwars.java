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
        getLogger().info("已成功加载！");
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
        registerEvents(new FireBall());
        registerEvents(new LightTNT());
        registerEvents(new BridgeEgg());
        registerEvents(new Parachute());
        registerEvents(new TNTLaunch());
        registerEvents(new Trampoline());
        registerEvents(new WalkPlatform());
        registerEvents(new EventListener());
        registerEvents(new TeamIronGolem());
        registerEvents(new TeamSilverFish());
        registerEvents(new ExplosionProof());
        registerEvents(new EnderPearlChair());
        registerEvents(new Shop());
        registerEvents(new LobbyScoreBoard());
        registerEvents(new SpawnNoBuild());
        registerEvents(new ChatFormat());
        registerEvents(new HidePlayer());
        registerEvents(new WitherBow());
        registerEvents(new DeathItem());
        registerEvents(new Spectator());
        registerEvents(new GiveItem());
        registerEvents(new TimeTask());
        registerEvents(new EditGame());
        registerEvents(new Compass());
        registerEvents(new Title());
        registerEvents(new MultiPickup());
        registerEvents(new Tool());
        registerEvents(new VoidProtection());
        registerEvents(new RespawnProtection());
    }
    
    public void registerEvents(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
