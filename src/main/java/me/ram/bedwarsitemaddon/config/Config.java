package me.ram.bedwarsitemaddon.config;

import me.ram.bedwarsitemaddon.Main;
import me.ram.bedwarsitemaddon.utils.ColorUtil;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
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
  
  public static void loadConfig() {
    Main.getInstance().saveDefaultConfig();
    Main.getInstance().reloadConfig();
    FileConfiguration config = Main.getInstance().getConfig();
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
  }
}
