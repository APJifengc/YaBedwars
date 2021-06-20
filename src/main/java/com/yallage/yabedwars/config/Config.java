package com.yallage.yabedwars.config;

import com.yallage.yabedwars.YaBedwars;
import com.yallage.yabedwars.utils.ListUtils;
import io.github.bedwarsrel.BedwarsRel;
import com.yallage.yabedwars.utils.ColorUtil;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Config {
    public static boolean update_check;

    public static boolean hide_player;

    public static boolean tab_health;

    public static boolean tag_health;

    public static boolean item_merge;

    public static boolean hunger_change;

    public static boolean clear_bottle;

    public static String date_format;

    public static boolean chatformat_enabled;

    public static String chatformat_lobby;

    public static String chatformat_lobby_team;

    public static List<String> chatformat_all_prefix;

    public static String chatformat_ingame;

    public static String chatformat_ingame_all;

    public static String chatformat_spectator;

    public static boolean final_killed_enabled;

    public static String final_killed_message;

    public static List<String> timecommand_startcommand;

    public static boolean select_team_enabled;

    public static String select_team_status_select;

    public static String select_team_status_inteam;

    public static String select_team_status_team_full;

    public static String select_team_no_players;

    public static String select_team_item_name;

    public static List<String> select_team_item_lore;

    public static boolean lobby_block_enabled;

    public static int lobby_block_position_1_x;

    public static int lobby_block_position_1_y;

    public static int lobby_block_position_1_z;

    public static int lobby_block_position_2_x;

    public static int lobby_block_position_2_y;

    public static int lobby_block_position_2_z;

    public static boolean rejoin_enabled;

    public static String rejoin_message_rejoin;

    public static String rejoin_message_error;

    public static boolean bowdamage_enabled;

    public static String bowdamage_title;

    public static String bowdamage_subtitle;

    public static String bowdamage_message;

    public static boolean damagetitle_enabled;

    public static String damagetitle_title;

    public static String damagetitle_subtitle;

    public static boolean jointitle_enabled;

    public static String jointitle_title;

    public static String jointitle_subtitle;

    public static boolean die_out_title_enabled;

    public static String die_out_title_title;

    public static String die_out_title_subtitle;

    public static boolean destroyed_title_enabled;

    public static String destroyed_title_title;

    public static String destroyed_title_subtitle;

    public static boolean start_title_enabled;

    public static List<String> start_title_title;

    public static String start_title_subtitle;

    public static boolean victory_title_enabled;

    public static List<String> victory_title_title;

    public static String victory_title_subtitle;

    public static boolean play_sound_enabled;

    public static List<String> play_sound_sound_start;

    public static List<String> play_sound_sound_death;

    public static List<String> play_sound_sound_kill;

    public static List<String> play_sound_sound_upgrade;

    public static List<String> play_sound_sound_no_resource;

    public static List<String> play_sound_sound_sethealth;

    public static List<String> play_sound_sound_enable_witherbow;

    public static List<String> play_sound_sound_witherbow;

    public static List<String> play_sound_sound_deathmode;

    public static List<String> play_sound_sound_over;

    public static boolean spectator_enabled;

    public static boolean spectator_centre_enabled;

    public static double spectator_centre_height;

    public static String spectator_spectator_target_title;

    public static String spectator_spectator_target_subtitle;

    public static String spectator_quit_spectator_title;

    public static String spectator_quit_spectator_subtitle;

    public static boolean spectator_speed_enabled;

    public static int spectator_speed_slot;

    public static int spectator_speed_item;

    public static String spectator_speed_item_name;

    public static List<String> spectator_speed_item_lore;

    public static String spectator_speed_gui_title;

    public static String spectator_speed_no_speed;

    public static String spectator_speed_speed_1;

    public static String spectator_speed_speed_2;

    public static String spectator_speed_speed_3;

    public static String spectator_speed_speed_4;

    public static boolean spectator_fast_join_enabled;

    public static int spectator_fast_join_slot;

    public static int spectator_fast_join_item;

    public static String spectator_fast_join_item_name;

    public static List<String> spectator_fast_join_item_lore;

    public static String spectator_fast_join_group;

    public static boolean compass_enabled;

    public static String compass_item_name;

    public static String compass_back;

    public static List<String> compass_item_lore;

    public static List<String> compass_lore_send_message;

    public static List<String> compass_lore_select_team;

    public static List<String> compass_lore_select_resources;

    public static List<String> compass_resources;

    public static Map<String, String> compass_resources_name;

    public static String compass_gui_title;

    public static String compass_item_III_II;

    public static String compass_item_IV_II;

    public static String compass_item_V_II;

    public static String compass_item_VI_II;

    public static String compass_item_VII_II;

    public static String compass_item_VIII_II;

    public static String compass_item_III_III;

    public static String compass_item_IV_III;

    public static String compass_item_V_III;

    public static String compass_item_VI_III;

    public static String compass_item_VII_III;

    public static String compass_message_III_II;

    public static String compass_message_IV_II;

    public static String compass_message_V_II;

    public static String compass_message_VI_II;

    public static String compass_message_VII_II;

    public static String compass_message_VIII_II;

    public static String compass_message_III_III;

    public static String compass_message_IV_III;

    public static String compass_message_V_III;

    public static String compass_message_VI_III;

    public static String compass_message_VII_III;

    public static boolean shop_enabled;

    public static String shop_item_shop_type;

    public static String shop_item_shop_skin;

    public static boolean shop_item_shop_look;

    public static String shop_team_shop_type;

    public static String shop_team_shop_skin;

    public static boolean shop_team_shop_look;

    public static List<String> shop_item_shop_name;

    public static List<String> shop_team_shop_name;

    public static boolean respawn_enabled;

    public static boolean respawn_centre_enabled;

    public static double respawn_centre_height;

    public static int respawn_respawn_delay;

    public static String respawn_respawning_title;

    public static String respawn_respawning_subtitle;

    public static String respawn_respawning_message;

    public static String respawn_respawned_title;

    public static String respawn_respawned_subtitle;

    public static String respawn_respawned_message;

    public static boolean giveitem_keeparmor;

    public static Map<String, Object> giveitem_armor_helmet_item;

    public static Map<String, Object> giveitem_armor_chestplate_item;

    public static Map<String, Object> giveitem_armor_leggings_item;

    public static Map<String, Object> giveitem_armor_boots_item;

    public static boolean giveitem_armor_helmet_give;

    public static boolean giveitem_armor_chestplate_give;

    public static boolean giveitem_armor_leggings_give;

    public static boolean giveitem_armor_boots_give;

    public static boolean giveitem_armor_helmet_move;

    public static boolean giveitem_armor_chestplate_move;

    public static boolean giveitem_armor_leggings_move;

    public static boolean giveitem_armor_boots_move;

    public static boolean sethealth_start_enabled;

    public static int sethealth_start_health;

    public static String sethealth_start_title;

    public static String sethealth_start_subtitle;

    public static String sethealth_start_message;

    public static boolean resourcelimit_enabled;

    public static List<String[]> resourcelimit_limit;

    public static boolean invisibility_player_enabled;

    public static boolean invisibility_player_footstep;

    public static boolean invisibility_player_hide_particles;

    public static boolean invisibility_player_damage_show_player;

    public static boolean witherbow_enabled;

    public static int witherbow_gametime;

    public static String witherbow_already_starte;

    public static String witherbow_title;

    public static String witherbow_subtitle;

    public static String witherbow_message;

    public static boolean teamshop_enabled;

    public static String teamshop_title;

    public static String teamshop_message;

    public static String teamshop_no_resource;

    public static List<String> teamshop_frame;

    public static String teamshop_upgrade_fast_dig_item;

    public static String teamshop_upgrade_fast_dig_name;

    public static Map<Integer, String> teamshop_upgrade_fast_dig_level_cost;

    public static List<String> teamshop_upgrade_fast_dig_level_1_lore;

    public static List<String> teamshop_upgrade_fast_dig_level_2_lore;

    public static List<String> teamshop_upgrade_fast_dig_level_full_lore;

    public static String teamshop_upgrade_sword_sharpness_item;

    public static String teamshop_upgrade_sword_sharpness_name;

    public static Map<Integer, String> teamshop_upgrade_sword_sharpness_level_cost;

    public static List<String> teamshop_upgrade_sword_sharpness_level_1_lore;

    public static List<String> teamshop_upgrade_sword_sharpness_level_2_lore;

    public static List<String> teamshop_upgrade_sword_sharpness_level_full_lore;

    public static String teamshop_upgrade_armor_protection_item;

    public static String teamshop_upgrade_armor_protection_name;

    public static Map<Integer, String> teamshop_upgrade_armor_protection_level_cost;

    public static List<String> teamshop_upgrade_armor_protection_level_1_lore;

    public static List<String> teamshop_upgrade_armor_protection_level_2_lore;

    public static List<String> teamshop_upgrade_armor_protection_level_3_lore;

    public static List<String> teamshop_upgrade_armor_protection_level_4_lore;

    public static List<String> teamshop_upgrade_armor_protection_level_full_lore;

    public static String teamshop_upgrade_trap_item;

    public static String teamshop_upgrade_trap_name;

    public static int teamshop_upgrade_trap_trigger_range;

    public static String teamshop_upgrade_trap_trigger_title;

    public static String teamshop_upgrade_trap_trigger_subtitle;

    public static String teamshop_upgrade_trap_trigger_message;

    public static String teamshop_upgrade_trap_level_1_cost;

    public static List<String> teamshop_upgrade_trap_level_1_lore;

    public static List<String> teamshop_upgrade_trap_level_full_lore;

    public static Boolean teamshop_upgrade_defense_permanent;

    public static String teamshop_upgrade_defense_item;

    public static String teamshop_upgrade_defense_name;

    public static int teamshop_upgrade_defense_trigger_range;

    public static String teamshop_upgrade_defense_level_1_cost;

    public static List<String> teamshop_upgrade_defense_level_1_lore;

    public static List<String> teamshop_upgrade_defense_level_full_lore;

    public static String teamshop_upgrade_heal_item;

    public static String teamshop_upgrade_heal_name;

    public static int teamshop_upgrade_heal_trigger_range;

    public static String teamshop_upgrade_heal_level_1_cost;

    public static List<String> teamshop_upgrade_heal_level_1_lore;

    public static List<String> teamshop_upgrade_heal_level_full_lore;

    public static boolean deathmode_enabled;

    public static int deathmode_gametime;

    public static String deathmode_title;

    public static String deathmode_subtitle;

    public static String deathmode_message;

    public static boolean deathitem_enabled;

    public static List<String> deathitem_items;

    public static boolean deathitem_item_name_chinesize;

    public static String deathitem_message;

    public static boolean nobreakbed_enabled;

    public static int nobreakbed_gametime;

    public static String nobreakbed_nobreakmessage;

    public static String nobreakbed_title;

    public static String nobreakbed_subtitle;

    public static String nobreakbed_message;

    public static boolean spawn_no_build_enabled;

    public static int spawn_no_build_spawn_range;

    public static int spawn_no_build_resource_range;

    public static String spawn_no_build_message;

    public static boolean holographic_resource_enabled;

    public static boolean holographic_bed_title_enabled;

    public static double holographic_resource_speed;

    public static List<String> holographic_resource;

    public static String holographic_bedtitle_bed_alive;

    public static String holographic_bedtitle_bed_destroyed;

    public static boolean overstats_enabled;

    public static List<String> overstats_message;

    public static String actionbar;

    public static Map<String, Integer> timer;

    public static List<String> planinfo;

    public static String playertag_prefix;

    public static String playertag_suffix;

    public static int scoreboard_interval;

    public static List<String> scoreboard_title;

    public static String scoreboard_you;

    public static String scoreboard_team_bed_status_bed_alive;

    public static String scoreboard_team_bed_status_bed_destroyed;

    public static String scoreboard_team_status_format_bed_alive;

    public static String scoreboard_team_status_format_bed_destroyed;

    public static String scoreboard_team_status_format_team_dead;

    public static Map<String, List<String>> scoreboard_lines;

    public static boolean lobby_scoreboard_enabled;

    public static int lobby_scoreboard_interval;

    public static String lobby_scoreboard_state_waiting;

    public static String lobby_scoreboard_state_countdown;

    public static List<String> lobby_scoreboard_title;

    public static List<String> lobby_scoreboard_lines;

    public static Map<String, List<String>> shop_item;

    public static Map<String, List<String>> shop_team;

    public static Map<String, String> shop_shops;

    private static FileConfiguration language_config;

    public static String message_cooling;

    public static boolean items_fireball_enabled;

    public static boolean items_fireball_ejection;

    public static boolean items_tnt_enabled;

    public static boolean items_tnt_ejection;

    public static boolean items_parachute_enabled;

    public static boolean items_trampoline_enabled;

    public static boolean items_bridge_egg_enabled;

    public static boolean items_ender_pearl_chair_enabled;

    public static boolean items_team_iron_golem_enabled;

    public static boolean items_team_silver_fish_enabled;

    public static boolean items_explosion_proof_enabled;

    public static boolean items_walk_platform_enabled;

    public static boolean items_tnt_launch_enabled;

    public static boolean items_tnt_launch_ejection;

    public static int items_fireball_cooldown;

    public static int items_tnt_cooldown;

    public static int items_parachute_cooldown;

    public static int items_trampoline_cooldown;

    public static int items_bridge_egg_cooldown;

    public static int items_ender_pearl_chair_cooldown;

    public static int items_team_iron_golem_cooldown;

    public static int items_walk_platform_cooldown;

    public static int items_walk_platform_break_time;

    public static int items_fireball_damage;

    public static int items_tnt_damage;

    public static int items_tnt_launch_cooldown;

    public static int items_tnt_launch_damage;

    public static int items_tnt_fuse_ticks;

    public static int items_tnt_launch_fuse_ticks;

    public static double items_fireball_velocity;

    public static double items_tnt_velocity;

    public static double items_parachute_velocity;

    public static double items_parachute_landing_velocity;

    public static double items_parachute_gliding_velocity;

    public static double items_trampoline_velocity;

    public static double items_tnt_launch_launch_velocity;

    public static double items_tnt_launch_velocity;

    public static int items_trampoline_size;

    public static int items_trampoline_staytime;

    public static int items_team_iron_golem_staytime;

    public static int items_team_iron_golem_health;

    public static int items_team_iron_golem_damage;

    public static int items_team_silver_fish_staytime;

    public static int items_team_silver_fish_health;

    public static int items_team_silver_fish_damage;

    public static int items_bridge_egg_maxblock;

    public static String items_parachute_item;

    public static String items_team_iron_golem_item;

    public static String items_team_iron_golem_name;

    public static String items_team_silver_fish_name;

    public static String items_trampoline_item;

    public static String items_trampoline_lack_space;

    public static String items_walk_platform_item;

    public static String items_tnt_launch_item;

    protected static YamlConfiguration yamlConfiguration1;

    protected static File file_config;

    protected static YamlConfiguration yaml_enabledGames;

    protected static File file_enabledGames;

    public static String xpMessage;

    public static boolean addResShop;

    public static double deathCost;

    public static double deathDrop;

    public static int maxXP;

    public static String maxXPMessage;

    public static boolean fullXPBedwars;

    public static final HashMap<Material, Integer> resources = new HashMap<>();

    public static final HashSet<String> resourceskey = new HashSet<>();

    private static final HashSet<String> enabledGameList = new HashSet<>();

    public static void loadConfig() {
        YaBedwars.getInstance().saveDefaultConfig();
        YaBedwars.getInstance().getHolographicManager().removeAll();
        String prefix = "[" + YaBedwars.getInstance().getDescription().getName() + "] ";
        Bukkit.getConsoleSender().sendMessage(prefix + "§f正在加载配置文件...");
        File folder = new File(YaBedwars.getInstance().getDataFolder(), "/");
        if (!folder.exists())
            folder.mkdirs();
        File cfile = new File(folder.getAbsolutePath() + "/config.yml");
        if (!cfile.exists())
            YaBedwars.getInstance().saveResource("config.yml", false);
        File tsfile = new File(folder.getAbsolutePath() + "/team_shop.yml");
        if (!tsfile.exists())
            YaBedwars.getInstance().saveResource("team_shop.yml", false);
        YamlConfiguration yamlConfiguration1 = YamlConfiguration.loadConfiguration(cfile);
        language_config = YamlConfiguration.loadConfiguration(getLanguageFile());
        YamlConfiguration yamlConfiguration2 = YamlConfiguration.loadConfiguration(tsfile);
        Bukkit.getConsoleSender().sendMessage(prefix + "§a默认配置文件已保存！");
        YaBedwars.getInstance().reloadConfig();
        FileConfiguration config = YaBedwars.getInstance().getConfig();
        message_cooling = ColorUtil.color(config.getString("message.cooling"));
        items_fireball_enabled = config.getBoolean("items.fireball.enabled");
        items_fireball_ejection = config.getBoolean("items.fireball.ejection");
        items_tnt_enabled = config.getBoolean("items.tnt.enabled");
        items_tnt_ejection = config.getBoolean("items.tnt.ejection");
        items_fireball_velocity = config.getDouble("items.fireball.velocity");
        items_tnt_velocity = config.getDouble("items.tnt.velocity");
        items_tnt_launch_velocity = config.getDouble("items.tnt_launch.velocity");
        items_tnt_launch_launch_velocity = config.getDouble("items.tnt_launch.launch_velocity");
        items_parachute_enabled = config.getBoolean("items.parachute.enabled");
        items_trampoline_enabled = config.getBoolean("items.trampoline.enabled");
        items_bridge_egg_enabled = config.getBoolean("items.bridge_egg.enabled");
        items_ender_pearl_chair_enabled = config.getBoolean("items.ender_pearl_chair.enabled");
        items_team_iron_golem_enabled = config.getBoolean("items.team_iron_golem.enabled");
        items_team_silver_fish_enabled = config.getBoolean("items.team_silver_fish.enabled");
        items_explosion_proof_enabled = config.getBoolean("items.explosion_proof.enabled");
        items_walk_platform_enabled = config.getBoolean("items.walk_platform.enabled");
        items_tnt_launch_enabled = config.getBoolean("items.tnt_launch.enabled");
        items_tnt_launch_ejection = config.getBoolean("items.tnt_launch.ejection");
        items_fireball_cooldown = config.getInt("items.fireball.cooldown");
        items_tnt_cooldown = config.getInt("items.tnt.cooldown");
        items_parachute_cooldown = config.getInt("items.parachute.cooldown");
        items_trampoline_cooldown = config.getInt("items.trampoline.cooldown");
        items_bridge_egg_cooldown = config.getInt("items.bridge_egg.cooldown");
        items_ender_pearl_chair_cooldown = config.getInt("items.ender_pearl_chair.cooldown");
        items_team_iron_golem_cooldown = config.getInt("items.team_iron_golem.cooldown");
        items_walk_platform_cooldown = config.getInt("items.walk_platform.cooldown");
        items_walk_platform_break_time = config.getInt("items.walk_platform.break_time");
        items_fireball_damage = config.getInt("items.fireball.damage");
        items_tnt_damage = config.getInt("items.tnt.damage");
        items_tnt_launch_cooldown = config.getInt("items.tnt_launch.cooldown");
        items_tnt_launch_damage = config.getInt("items.tnt_launch.damage");
        items_tnt_fuse_ticks = config.getInt("items.tnt.fuse_ticks");
        items_tnt_launch_fuse_ticks = config.getInt("items.tnt_launch.fuse_ticks");
        items_parachute_velocity = config.getDouble("items.parachute.velocity");
        items_parachute_landing_velocity = config.getDouble("items.parachute.landing_velocity");
        items_parachute_gliding_velocity = config.getDouble("items.parachute.gliding_velocity");
        items_trampoline_velocity = config.getDouble("items.trampoline.velocity");
        items_trampoline_size = config.getInt("items.trampoline.size");
        items_trampoline_staytime = config.getInt("items.trampoline.staytime");
        items_team_iron_golem_staytime = config.getInt("items.team_iron_golem.staytime");
        items_team_iron_golem_health = config.getInt("items.team_iron_golem.health");
        items_team_iron_golem_damage = config.getInt("items.team_iron_golem.damage");
        items_team_silver_fish_staytime = config.getInt("items.team_silver_fish.staytime");
        items_team_silver_fish_health = config.getInt("items.team_silver_fish.health");
        items_team_silver_fish_damage = config.getInt("items.team_silver_fish.damage");
        items_bridge_egg_maxblock = config.getInt("items.bridge_egg.maxblock");
        items_parachute_item = config.getString("items.parachute.item");
        items_team_iron_golem_item = config.getString("items.team_iron_golem.item");
        items_team_iron_golem_name = ColorUtil.color(config.getString("items.team_iron_golem.name"));
        items_team_silver_fish_name = ColorUtil.color(config.getString("items.team_silver_fish.name"));
        items_trampoline_item = config.getString("items.trampoline.item");
        items_trampoline_lack_space = ColorUtil.color(config.getString("items.trampoline.lack_space"));
        items_walk_platform_item = config.getString("items.walk_platform.item");
        items_tnt_launch_item = config.getString("items.tnt_launch.item");
        update_check = config.getBoolean("update_check");
        hide_player = config.getBoolean("hide_player");
        tab_health = config.getBoolean("tab_health");
        tag_health = config.getBoolean("tag_health");
        item_merge = config.getBoolean("item_merge");
        hunger_change = config.getBoolean("hunger_change");
        clear_bottle = config.getBoolean("clear_bottle");
        date_format = config.getString("date_format");
        chatformat_enabled = config.getBoolean("chatformat.enabled");
        chatformat_all_prefix = config.getStringList("chatformat.all_prefix");
        chatformat_lobby = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("chatformat.lobby"));
        chatformat_lobby_team = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("chatformat.lobby_team"));
        chatformat_ingame = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("chatformat.ingame"));
        chatformat_ingame_all = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("chatformat.ingame_all"));
        chatformat_spectator = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("chatformat.spectator"));
        final_killed_enabled = config.getBoolean("final_killed.enabled");
        final_killed_message = ColorUtil.color(config.getString("final_killed.message"));
        timecommand_startcommand =
                ColorUtil.listColor(YaBedwars.getInstance().getConfig().getStringList("timecommand.startcommand"));
        giveitem_keeparmor = config.getBoolean("giveitem.keeparmor");
        giveitem_armor_helmet_item = (Map<String, Object>) config.getList("giveitem.armor.helmet.item").get(0);
        giveitem_armor_chestplate_item = (Map<String, Object>) config.getList("giveitem.armor.chestplate.item").get(0);
        giveitem_armor_leggings_item = (Map<String, Object>) config.getList("giveitem.armor.leggings.item").get(0);
        giveitem_armor_boots_item = (Map<String, Object>) config.getList("giveitem.armor.boots.item").get(0);
        giveitem_armor_helmet_give = config.getBoolean("giveitem.armor.helmet.give");
        giveitem_armor_chestplate_give = config.getBoolean("giveitem.armor.chestplate.give");
        giveitem_armor_leggings_give = config.getBoolean("giveitem.armor.leggings.give");
        giveitem_armor_boots_give = config.getBoolean("giveitem.armor.boots.give");
        giveitem_armor_helmet_move = config.getBoolean("giveitem.armor.helmet.move");
        giveitem_armor_chestplate_move = config.getBoolean("giveitem.armor.chestplate.move");
        giveitem_armor_leggings_move = config.getBoolean("giveitem.armor.leggings.move");
        giveitem_armor_boots_move = config.getBoolean("giveitem.armor.boots.move");
        select_team_enabled = config.getBoolean("select_team.enabled");
        select_team_status_select = ColorUtil.color(config.getString("select_team.status.select"));
        select_team_status_inteam = ColorUtil.color(config.getString("select_team.status.inteam"));
        select_team_status_team_full = ColorUtil.color(config.getString("select_team.status.team_full"));
        select_team_no_players = ColorUtil.color(config.getString("select_team.no_players"));
        select_team_item_name = ColorUtil.color(config.getString("select_team.item.name"));
        select_team_item_lore = ColorUtil.listColor(config.getStringList("select_team.item.lore"));
        lobby_block_enabled = config.getBoolean("lobby_block.enabled");
        lobby_block_position_1_x = config.getInt("lobby_block.position_1.x");
        lobby_block_position_1_y = config.getInt("lobby_block.position_1.y");
        lobby_block_position_1_z = config.getInt("lobby_block.position_1.z");
        lobby_block_position_2_x = config.getInt("lobby_block.position_2.x");
        lobby_block_position_2_y = config.getInt("lobby_block.position_2.y");
        lobby_block_position_2_z = config.getInt("lobby_block.position_2.z");
        rejoin_enabled = config.getBoolean("rejoin.enabled");
        rejoin_message_rejoin = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("rejoin.message.rejoin"));
        rejoin_message_error = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("rejoin.message.error"));
        bowdamage_enabled = config.getBoolean("bowdamage.enabled");
        bowdamage_title = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("bowdamage.title"));
        bowdamage_subtitle = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("bowdamage.subtitle"));
        bowdamage_message = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("bowdamage.message"));
        damagetitle_enabled = config.getBoolean("damagetitle.enabled");
        damagetitle_title = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("damagetitle.title"));
        damagetitle_subtitle = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("damagetitle.subtitle"));
        jointitle_enabled = config.getBoolean("jointitle.enabled");
        jointitle_title = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("jointitle.title"));
        jointitle_subtitle = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("jointitle.subtitle"));
        die_out_title_enabled = config.getBoolean("die_out_title.enabled");
        die_out_title_title = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("die_out_title.title"));
        die_out_title_subtitle = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("die_out_title.subtitle"));
        destroyed_title_enabled = config.getBoolean("destroyed_title.enabled");
        destroyed_title_title = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("destroyed_title.title"));
        destroyed_title_subtitle =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("destroyed_title.subtitle"));
        start_title_enabled = config.getBoolean("start_title.enabled");
        start_title_title = ColorUtil.listColor(YaBedwars.getInstance().getConfig().getStringList("start_title.title"));
        start_title_subtitle = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("start_title.subtitle"));
        victory_title_enabled = config.getBoolean("victory_title.enabled");
        victory_title_title = ColorUtil.listColor(YaBedwars.getInstance().getConfig().getStringList("victory_title.title"));
        victory_title_subtitle = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("victory_title.subtitle"));
        play_sound_enabled = config.getBoolean("play_sound.enabled");
        play_sound_sound_start = config.getStringList("play_sound.sound.start");
        play_sound_sound_death = config.getStringList("play_sound.sound.death");
        play_sound_sound_kill = config.getStringList("play_sound.sound.kill");
        play_sound_sound_no_resource = config.getStringList("play_sound.sound.no_resource");
        play_sound_sound_upgrade = config.getStringList("play_sound.sound.upgrade");
        play_sound_sound_sethealth = config.getStringList("play_sound.sound.sethealth");
        play_sound_sound_enable_witherbow = config.getStringList("play_sound.sound.enable_witherbow");
        play_sound_sound_witherbow = config.getStringList("play_sound.sound.witherbow");
        play_sound_sound_deathmode = config.getStringList("play_sound.sound.deathmode");
        play_sound_sound_over = config.getStringList("play_sound.sound.over");
        spectator_enabled = config.getBoolean("spectator.enabled");
        spectator_centre_enabled = config.getBoolean("spectator.centre.enabled");
        spectator_centre_height = config.getDouble("spectator.centre.height");
        spectator_spectator_target_title =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("spectator.spectator_target.title"));
        spectator_spectator_target_subtitle =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("spectator.spectator_target.subtitle"));
        spectator_quit_spectator_title =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("spectator.quit_spectator.title"));
        spectator_quit_spectator_subtitle =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("spectator.quit_spectator.subtitle"));
        spectator_speed_enabled = config.getBoolean("spectator.speed.enabled");
        spectator_speed_slot = config.getInt("spectator.speed.slot");
        spectator_speed_item = config.getInt("spectator.speed.item");
        spectator_speed_item_name =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("spectator.speed.item_name")) + "§7";
        spectator_speed_item_lore =
                ColorUtil.listColor(YaBedwars.getInstance().getConfig().getStringList("spectator.speed.item_lore"));
        spectator_speed_gui_title =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("spectator.speed.gui_title")) + "§s§s";
        spectator_speed_no_speed =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("spectator.speed.no_speed"));
        spectator_speed_speed_1 = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("spectator.speed.speed_1"));
        spectator_speed_speed_2 = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("spectator.speed.speed_2"));
        spectator_speed_speed_3 = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("spectator.speed.speed_3"));
        spectator_speed_speed_4 = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("spectator.speed.speed_4"));
        spectator_fast_join_enabled = config.getBoolean("spectator.fast_join.enabled");
        spectator_fast_join_slot = config.getInt("spectator.fast_join.slot");
        spectator_fast_join_item = config.getInt("spectator.fast_join.item");
        spectator_fast_join_item_name =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("spectator.fast_join.item_name")) + "§7";
        spectator_fast_join_item_lore =
                ColorUtil.listColor(YaBedwars.getInstance().getConfig().getStringList("spectator.fast_join.item_lore"));
        spectator_fast_join_group = config.getString("spectator.fast_join.group");
        compass_enabled = config.getBoolean("compass.enabled");
        compass_item_name = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.item_name"));
        compass_item_lore = ColorUtil.listColor(YaBedwars.getInstance().getConfig().getStringList("compass.item_lore"));
        compass_lore_send_message =
                ColorUtil.listColor(YaBedwars.getInstance().getConfig().getStringList("compass.lore.send_message"));
        compass_lore_select_team =
                ColorUtil.listColor(YaBedwars.getInstance().getConfig().getStringList("compass.lore.select_team"));
        compass_lore_select_resources =
                ColorUtil.listColor(YaBedwars.getInstance().getConfig().getStringList("compass.lore.select_resources"));
        compass_resources_name = new HashMap<>();
        compass_resources = new ArrayList<>();
        for (String type : YaBedwars.getInstance().getConfig().getConfigurationSection("compass.resources").getKeys(false)) {
            compass_resources_name.put(type,
                    ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.resources." + type)));
            compass_resources.add(type);
        }
        compass_back = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.back"));
        compass_gui_title = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.gui_title")) + "§c§g";
        compass_item_III_II = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.item.III_II"));
        compass_item_IV_II = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.item.IV_II"));
        compass_item_V_II = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.item.V_II"));
        compass_item_VI_II = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.item.VI_II"));
        compass_item_VII_II = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.item.VII_II"));
        compass_item_VIII_II = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.item.VIII_II"));
        compass_item_III_III = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.item.III_III"));
        compass_item_IV_III = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.item.IV_III"));
        compass_item_V_III = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.item.V_III"));
        compass_item_VI_III = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.item.VI_III"));
        compass_item_VII_III = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.item.VII_III"));
        compass_message_III_II = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.message.III_II"));
        compass_message_IV_II = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.message.IV_II"));
        compass_message_V_II = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.message.V_II"));
        compass_message_VI_II = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.message.VI_II"));
        compass_message_VII_II = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.message.VII_II"));
        compass_message_VIII_II = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.message.VIII_II"));
        compass_message_III_III = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.message.III_III"));
        compass_message_IV_III = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.message.IV_III"));
        compass_message_V_III = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.message.V_III"));
        compass_message_VI_III = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.message.VI_III"));
        compass_message_VII_III = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("compass.message.VII_III"));
        shop_enabled = config.getBoolean("shop.enabled");
        shop_item_shop_type = config.getString("shop.item_shop.type");
        shop_item_shop_skin = config.getString("shop.item_shop.skin");
        shop_item_shop_look = config.getBoolean("shop.item_shop.look");
        shop_team_shop_type = config.getString("shop.team_shop.type");
        shop_team_shop_skin = config.getString("shop.team_shop.skin");
        shop_team_shop_look = config.getBoolean("shop.team_shop.look");
        shop_item_shop_name = ColorUtil.listColor(YaBedwars.getInstance().getConfig().getStringList("shop.item_shop.name"));
        shop_team_shop_name = ColorUtil.listColor(YaBedwars.getInstance().getConfig().getStringList("shop.team_shop.name"));
        respawn_enabled = config.getBoolean("respawn.enabled");
        respawn_centre_enabled = config.getBoolean("respawn.centre.enabled");
        respawn_centre_height = config.getDouble("respawn.centre.height");
        respawn_respawn_delay = config.getInt("respawn.respawn_delay");
        respawn_respawning_title =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("respawn.respawning.title"));
        respawn_respawning_subtitle =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("respawn.respawning.subtitle"));
        respawn_respawning_message =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("respawn.respawning.message"));
        respawn_respawned_title = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("respawn.respawned.title"));
        respawn_respawned_subtitle =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("respawn.respawned.subtitle"));
        respawn_respawned_message =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("respawn.respawned.message"));
        sethealth_start_enabled = config.getBoolean("sethealth.start.enabled");
        sethealth_start_health = config.getInt("sethealth.start.health");
        sethealth_start_title = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("sethealth.start.title"));
        sethealth_start_subtitle =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("sethealth.start.subtitle"));
        sethealth_start_message = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("sethealth.start.message"));
        resourcelimit_enabled = config.getBoolean("resourcelimit.enabled");
        resourcelimit_limit = new ArrayList<>();
        for (String w : YaBedwars.getInstance().getConfig().getStringList("resourcelimit.limit")) {
            String[] ary = w.split(",");
            resourcelimit_limit.add(ary);
        }
        invisibility_player_enabled = config.getBoolean("invisibility_player.enabled");
        invisibility_player_footstep = config.getBoolean("invisibility_player.footstep");
        invisibility_player_hide_particles = config.getBoolean("invisibility_player.hide_particles");
        invisibility_player_damage_show_player = config.getBoolean("invisibility_player.damage_show_player");
        witherbow_enabled = config.getBoolean("witherbow.enabled");
        witherbow_gametime = config.getInt("witherbow.gametime");
        witherbow_already_starte =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("witherbow.already_starte"));
        witherbow_title = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("witherbow.title"));
        witherbow_subtitle = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("witherbow.subtitle"));
        witherbow_message = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("witherbow.message"));
        teamshop_enabled = yamlConfiguration2.getBoolean("enabled");
        teamshop_title = ColorUtil.color(yamlConfiguration2.getString("title") + "§1§0§0§0§0§0");
        teamshop_message = ColorUtil.color(yamlConfiguration2.getString("message"));
        teamshop_no_resource = ColorUtil.color(yamlConfiguration2.getString("no_resource"));
        teamshop_frame = ColorUtil.listColor(yamlConfiguration2.getStringList("frame"));
        teamshop_upgrade_fast_dig_item = yamlConfiguration2.getString("upgrade.fast_dig.item");
        teamshop_upgrade_fast_dig_name = ColorUtil.color(yamlConfiguration2.getString("upgrade.fast_dig.name"));
        teamshop_upgrade_fast_dig_level_cost = new HashMap<>();
        teamshop_upgrade_fast_dig_level_cost.put(Integer.valueOf(1), yamlConfiguration2.getString("upgrade.fast_dig.level_1.cost"));
        teamshop_upgrade_fast_dig_level_cost.put(Integer.valueOf(2), yamlConfiguration2.getString("upgrade.fast_dig.level_2.cost"));
        teamshop_upgrade_fast_dig_level_1_lore =
                ColorUtil.listColor(yamlConfiguration2.getStringList("upgrade.fast_dig.level_1.lore"));
        teamshop_upgrade_fast_dig_level_2_lore =
                ColorUtil.listColor(yamlConfiguration2.getStringList("upgrade.fast_dig.level_2.lore"));
        teamshop_upgrade_fast_dig_level_full_lore =
                ColorUtil.listColor(yamlConfiguration2.getStringList("upgrade.fast_dig.level_full.lore"));
        teamshop_upgrade_sword_sharpness_item = yamlConfiguration2.getString("upgrade.sword_sharpness.item");
        teamshop_upgrade_sword_sharpness_name = ColorUtil.color(yamlConfiguration2.getString("upgrade.sword_sharpness.name"));
        teamshop_upgrade_sword_sharpness_level_cost = new HashMap<>();
        teamshop_upgrade_sword_sharpness_level_cost.put(Integer.valueOf(1), yamlConfiguration2.getString("upgrade.sword_sharpness.level_1.cost"));
        teamshop_upgrade_sword_sharpness_level_cost.put(Integer.valueOf(2), yamlConfiguration2.getString("upgrade.sword_sharpness.level_2.cost"));
        teamshop_upgrade_sword_sharpness_level_1_lore =
                ColorUtil.listColor(yamlConfiguration2.getStringList("upgrade.sword_sharpness.level_1.lore"));
        teamshop_upgrade_sword_sharpness_level_2_lore =
                ColorUtil.listColor(yamlConfiguration2.getStringList("upgrade.sword_sharpness.level_2.lore"));
        teamshop_upgrade_sword_sharpness_level_full_lore =
                ColorUtil.listColor(yamlConfiguration2.getStringList("upgrade.sword_sharpness.level_full.lore"));
        teamshop_upgrade_armor_protection_item = yamlConfiguration2.getString("upgrade.armor_protection.item");
        teamshop_upgrade_armor_protection_name = ColorUtil.color(yamlConfiguration2.getString("upgrade.armor_protection.name"));
        teamshop_upgrade_armor_protection_level_cost = new HashMap<>();
        teamshop_upgrade_armor_protection_level_cost.put(Integer.valueOf(1), yamlConfiguration2.getString("upgrade.armor_protection.level_1.cost"));
        teamshop_upgrade_armor_protection_level_cost.put(Integer.valueOf(2), yamlConfiguration2.getString("upgrade.armor_protection.level_2.cost"));
        teamshop_upgrade_armor_protection_level_cost.put(Integer.valueOf(3), yamlConfiguration2.getString("upgrade.armor_protection.level_3.cost"));
        teamshop_upgrade_armor_protection_level_cost.put(Integer.valueOf(4), yamlConfiguration2.getString("upgrade.armor_protection.level_4.cost"));
        teamshop_upgrade_armor_protection_level_1_lore =
                ColorUtil.listColor(yamlConfiguration2.getStringList("upgrade.armor_protection.level_1.lore"));
        teamshop_upgrade_armor_protection_level_2_lore =
                ColorUtil.listColor(yamlConfiguration2.getStringList("upgrade.armor_protection.level_2.lore"));
        teamshop_upgrade_armor_protection_level_3_lore =
                ColorUtil.listColor(yamlConfiguration2.getStringList("upgrade.armor_protection.level_3.lore"));
        teamshop_upgrade_armor_protection_level_4_lore =
                ColorUtil.listColor(yamlConfiguration2.getStringList("upgrade.armor_protection.level_4.lore"));
        teamshop_upgrade_armor_protection_level_full_lore =
                ColorUtil.listColor(yamlConfiguration2.getStringList("upgrade.armor_protection.level_full.lore"));
        teamshop_upgrade_trap_item = yamlConfiguration2.getString("upgrade.trap.item");
        teamshop_upgrade_trap_name = ColorUtil.color(yamlConfiguration2.getString("upgrade.trap.name"));
        teamshop_upgrade_trap_trigger_range = yamlConfiguration2.getInt("upgrade.trap.trigger_range");
        teamshop_upgrade_trap_level_1_cost = yamlConfiguration2.getString("upgrade.trap.level_1.cost");
        teamshop_upgrade_trap_trigger_title = ColorUtil.color(yamlConfiguration2.getString("upgrade.trap.trigger.title"));
        teamshop_upgrade_trap_trigger_subtitle = ColorUtil.color(yamlConfiguration2.getString("upgrade.trap.trigger.subtitle"));
        teamshop_upgrade_trap_trigger_message = ColorUtil.color(yamlConfiguration2.getString("upgrade.trap.trigger.message"));
        teamshop_upgrade_trap_level_1_lore = ColorUtil.listColor(yamlConfiguration2.getStringList("upgrade.trap.level_1.lore"));
        teamshop_upgrade_trap_level_full_lore =
                ColorUtil.listColor(yamlConfiguration2.getStringList("upgrade.trap.level_full.lore"));
        teamshop_upgrade_defense_permanent = Boolean.valueOf(yamlConfiguration2.getBoolean("upgrade.defense.permanent"));
        teamshop_upgrade_defense_item = yamlConfiguration2.getString("upgrade.defense.item");
        teamshop_upgrade_defense_name = ColorUtil.color(yamlConfiguration2.getString("upgrade.defense.name"));
        teamshop_upgrade_defense_trigger_range = yamlConfiguration2.getInt("upgrade.defense.trigger_range");
        teamshop_upgrade_defense_level_1_cost = yamlConfiguration2.getString("upgrade.defense.level_1.cost");
        teamshop_upgrade_defense_level_1_lore =
                ColorUtil.listColor(yamlConfiguration2.getStringList("upgrade.defense.level_1.lore"));
        teamshop_upgrade_defense_level_full_lore =
                ColorUtil.listColor(yamlConfiguration2.getStringList("upgrade.defense.level_full.lore"));
        teamshop_upgrade_heal_item = yamlConfiguration2.getString("upgrade.heal.item");
        teamshop_upgrade_heal_name = ColorUtil.color(yamlConfiguration2.getString("upgrade.heal.name"));
        teamshop_upgrade_heal_trigger_range = yamlConfiguration2.getInt("upgrade.heal.trigger_range");
        teamshop_upgrade_heal_level_1_cost = yamlConfiguration2.getString("upgrade.heal.level_1.cost");
        teamshop_upgrade_heal_level_1_lore = ColorUtil.listColor(yamlConfiguration2.getStringList("upgrade.heal.level_1.lore"));
        teamshop_upgrade_heal_level_full_lore =
                ColorUtil.listColor(yamlConfiguration2.getStringList("upgrade.heal.level_full.lore"));
        deathmode_enabled = config.getBoolean("deathmode.enabled");
        deathmode_gametime = config.getInt("deathmode.gametime");
        deathmode_title = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("deathmode.title"));
        deathmode_subtitle = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("deathmode.subtitle"));
        deathmode_message = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("deathmode.message"));
        deathitem_enabled = config.getBoolean("deathitem.enabled");
        deathitem_items = config.getStringList("deathitem.items");
        deathitem_item_name_chinesize = config.getBoolean("deathitem.item_name_chinesize");
        deathitem_message = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("deathitem.message"));
        nobreakbed_nobreakmessage =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("nobreakbed.nobreakmessage"));
        nobreakbed_enabled = config.getBoolean("nobreakbed.enabled");
        nobreakbed_gametime = config.getInt("nobreakbed.gametime");
        nobreakbed_title = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("nobreakbed.title"));
        nobreakbed_subtitle = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("nobreakbed.subtitle"));
        nobreakbed_message = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("nobreakbed.message"));
        spawn_no_build_enabled = config.getBoolean("spawn_no_build.enabled");
        spawn_no_build_spawn_range = config.getInt("spawn_no_build.spawn_range");
        spawn_no_build_resource_range = config.getInt("spawn_no_build.resource_range");
        spawn_no_build_message = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("spawn_no_build.message"));
        holographic_resource_enabled = config.getBoolean("holographic.resource.enabled");
        holographic_bed_title_enabled = config.getBoolean("holographic.bed_title.enabled");
        holographic_resource_speed = config.getDouble("holographic.resource.speed");
        holographic_resource = new ArrayList<>();
        holographic_resource.addAll(YaBedwars.getInstance().getConfig().getConfigurationSection("holographic.resource.resources").getKeys(false));
        holographic_bedtitle_bed_destroyed =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("holographic.bed_title.bed_destroyed"));
        holographic_bedtitle_bed_alive =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("holographic.bed_title.bed_alive"));
        overstats_enabled = config.getBoolean("overstats.enabled");
        overstats_message = ColorUtil.listColor(YaBedwars.getInstance().getConfig().getStringList("overstats.message"));
        actionbar = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("actionbar"));
        timer = new HashMap<>();
        for (String w : YaBedwars.getInstance().getConfig().getConfigurationSection("timer").getKeys(false))
            timer.put(w, Integer.valueOf(YaBedwars.getInstance().getConfig().getInt("timer." + w)));
        planinfo = new ArrayList<>();
        for (String w : YaBedwars.getInstance().getConfig().getConfigurationSection("planinfo").getKeys(false))
            planinfo.add(w);
        playertag_prefix = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("playertag.prefix"));
        playertag_suffix = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("playertag.suffix"));
        scoreboard_interval = config.getInt("scoreboard.interval");
        scoreboard_title = ColorUtil.listColor(YaBedwars.getInstance().getConfig().getStringList("scoreboard.title"));
        scoreboard_you = ColorUtil.color(YaBedwars.getInstance().getConfig().getString("scoreboard.you"));
        scoreboard_team_bed_status_bed_alive =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("scoreboard.team_bed_status.bed_alive"));
        scoreboard_team_bed_status_bed_destroyed =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("scoreboard.team_bed_status.bed_destroyed"));
        scoreboard_team_status_format_bed_alive =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("scoreboard.team_status_format.bed_alive"));
        scoreboard_team_status_format_bed_destroyed =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("scoreboard.team_status_format.bed_destroyed"));
        scoreboard_team_status_format_team_dead =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("scoreboard.team_status_format.team_dead"));
        scoreboard_lines = new HashMap<>();
        for (String key : YaBedwars.getInstance().getConfig().getConfigurationSection("scoreboard.lines").getKeys(false))
            scoreboard_lines.put(key,
                    ColorUtil.listColor(YaBedwars.getInstance().getConfig().getStringList("scoreboard.lines." + key)));
        lobby_scoreboard_enabled = config.getBoolean("lobby_scoreboard.enabled");
        lobby_scoreboard_interval = config.getInt("lobby_scoreboard.interval");
        lobby_scoreboard_state_waiting =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("lobby_scoreboard.state.waiting"));
        lobby_scoreboard_state_countdown =
                ColorUtil.color(YaBedwars.getInstance().getConfig().getString("lobby_scoreboard.state.countdown"));
        lobby_scoreboard_title =
                ColorUtil.listColor(YaBedwars.getInstance().getConfig().getStringList("lobby_scoreboard.title"));
        lobby_scoreboard_lines = new ArrayList<>();
        for (String w : YaBedwars.getInstance().getConfig().getStringList("lobby_scoreboard.lines")) {
            String line = ColorUtil.color(w);
            if (lobby_scoreboard_lines.size() < 15) {
                if (lobby_scoreboard_lines.contains(line)) {
                    lobby_scoreboard_lines.add(conflict(lobby_scoreboard_lines, line));
                    continue;
                }
                lobby_scoreboard_lines.add(line);
            }
        }
        updateShop();

        xpMessage = yamlConfiguration1.getString("Message").replaceAll("&", "§").replaceAll("§§", "&");
        addResShop = yamlConfiguration1.getBoolean("Add_Res_Shop");
        if (addResShop)
        deathCost = yamlConfiguration1.getInt("DeathCostXP", 0) / 100.0D;
        deathDrop = yamlConfiguration1.getInt("DeathDropXP", 0) / 100.0D;
        maxXP = yamlConfiguration1.getInt("MaxXP");
        maxXPMessage = yamlConfiguration1.getString("MaxXPMessage").replaceAll("&", "§").replaceAll("§§", "&");
        fullXPBedwars = yamlConfiguration1.getBoolean("Full_XP_Bedwars");
        ConfigurationSection resourceSection = BedwarsRel.getInstance().getConfig().getConfigurationSection("resource");
        for (String key : resourceSection.getKeys(false)) {
            List<Map<String, Object>> resourceList = (List<Map<String, Object>>) BedwarsRel.getInstance().getConfig().getList("resource." + key + ".item");
            for (Map<String, Object> resource : resourceList) {
                ItemStack itemStack = ItemStack.deserialize(resource);
                Material mat = itemStack.getType();
                int xp = yamlConfiguration1.getInt("XP." + key, 0);
                resources.put(mat, xp);
                resourceskey.add(key);
            }
        }
        file_enabledGames = new File("plugins/YaBedwars/enabledGames.yml");
        if (!file_enabledGames.exists()) {
            YaBedwars.getInstance().saveResource("enabledGames.yml", true);
        }
        yaml_enabledGames = YamlConfiguration.loadConfiguration(file_enabledGames);
        enabledGameList.addAll(yaml_enabledGames.getStringList("enabledGame"));
        Bukkit.getConsoleSender().sendMessage(prefix + "§a配置文件加载成功！");
    }

    private static String conflict(List<String> lines, String line) {
        String l = line;
        for (int i = 0; i == 0; ) {
            l = l + "§r";
            if (!lines.contains(l))
                return l;
        }
        return l;
    }

    public static void setShop(String game, Location location, String type) {
        File file = getGameFile();
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        List<String> loc = new ArrayList<>();
        if (yamlConfiguration.getStringList("shop." + game + "." + type) != null)
            loc.addAll(yamlConfiguration.getStringList("shop." + game + "." + type));
        loc.add(location.getWorld().getName() + ", " + location.getX() + ", " + location.getY() + ", " + location.getZ() +
                ", " + location.getYaw() + ", " + location.getPitch());
        yamlConfiguration.set("shop." + game + "." + type, loc);
        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateShop();
    }

    public static void removeShop(String data) {
        File file = getGameFile();
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        String path = data.split(" - ")[0];
        List<String> loc = new ArrayList<>();
        if (yamlConfiguration.getStringList(path) != null)
            loc.addAll(yamlConfiguration.getStringList(path));
        loc.remove(data.split(" - ")[1]);
        yamlConfiguration.set(path, loc);
        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateShop();
    }

    private static void updateShop() {
        File folder = new File(YaBedwars.getInstance().getDataFolder(), "/");
        if (!folder.exists())
            folder.mkdirs();
        File file = new File(folder.getAbsolutePath() + "/game.yml");
        shop_item = new HashMap<>();
        shop_team = new HashMap<>();
        shop_shops = new HashMap<>();
        int i = 0;
        if (!file.exists())
            return;
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if (!yamlConfiguration.getKeys(false).contains("shop"))
            return;
        for (String game : yamlConfiguration.getConfigurationSection("shop").getKeys(false)) {
            if (yamlConfiguration.getStringList("shop." + game + ".item") != null) {
                shop_item.put(game, yamlConfiguration.getStringList("shop." + game + ".item"));
                for (String shop : yamlConfiguration.getStringList("shop." + game + ".item")) {
                    shop_shops.put(String.valueOf(i), "shop." + game + ".item - " + shop);
                    i++;
                }
            }
            if (yamlConfiguration.getStringList("shop." + game + ".team") != null) {
                shop_team.put(game, yamlConfiguration.getStringList("shop." + game + ".team"));
                for (String shop : yamlConfiguration.getStringList("shop." + game + ".team")) {
                    shop_shops.put(String.valueOf(i), "shop." + game + ".team - " + shop);
                    i++;
                }
            }
        }
    }

    public static void addShopNPC(Integer id) {
        File folder = getNPCFile();
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(folder);
        List<String> npcs = new ArrayList<>();
        if (yamlConfiguration.getKeys(false).contains("npcs"))
            npcs.addAll(yamlConfiguration.getStringList("npcs"));
        npcs.add(String.valueOf(id));
        yamlConfiguration.set("npcs", npcs);
        try {
            yamlConfiguration.save(folder);
        } catch (IOException iOException) {
        }
    }

    public static File getNPCFile() {
        File folder = new File(CitizensAPI.getDataFolder(), "/");
        if (!folder.exists())
            folder.mkdirs();
        File file = new File(folder.getAbsolutePath() + "/npcs.yml");
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException iOException) {
            }
        return file;
    }

    private static File getGameFile() {
        File folder = new File(YaBedwars.getInstance().getDataFolder(), "/");
        if (!folder.exists())
            folder.mkdirs();
        File file = new File(folder.getAbsolutePath() + "/game.yml");
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException iOException) {
            }
        return file;
    }

    public static String setGameEnableXP(String bw, boolean isEnabled) {
        if (isEnabled) {
            enabledGameList.add(bw);
        } else {
            enabledGameList.remove(bw);
        }
        yaml_enabledGames.set("enabledGame", ListUtils.hashSetToList(enabledGameList));
        try {
            yaml_enabledGames.save(file_enabledGames);
        } catch (IOException e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
        return "";
    }

    public static boolean isGameEnabledXP(String bw) {
        return enabledGameList.contains(bw);
    }

    public static String getLanguage(String path) {
        if (language_config.contains(path) && language_config.isString(path))
            return ColorUtil.color(language_config.getString(path));
        return "null";
    }

    public static List<String> getLanguageList(String path) {
        if (language_config.contains(path) && language_config.isList(path))
            return ColorUtil.listColor(language_config.getStringList(path));
        return Collections.singletonList("null");
    }

    private static File getLanguageFile() {
        File folder = new File(YaBedwars.getInstance().getDataFolder(), "/");
        if (!folder.exists())
            folder.mkdirs();
        File file = new File(folder.getAbsolutePath() + "/language.yml");
        if (!file.exists())
            YaBedwars.getInstance().saveResource("language.yml", false);
        return file;
    }
}
