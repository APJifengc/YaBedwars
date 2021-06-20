package com.yallage.yabedwars.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class LocationUtil {
    public static Location getLocation(Location location, int x, int y, int z) {
        Location loc = location.getBlock().getLocation();
        loc.add(x, y, z);
        return loc;
    }

    public static Vector getPositionVector(Location location1, Location location2) {
        double X = location1.getX() - location2.getX();
        double Y = location1.getY() - location2.getY();
        double Z = location1.getZ() - location2.getZ();
        return new Vector(X, Y, Z);
    }

    public static Location getLocationYaw(Location location, double X, double Y, double Z) {
        double radians = Math.toRadians(location.getYaw());
        double x = Math.cos(radians) * X;
        double z = Math.sin(radians) * X;
        location.add(x, Y, z);
        location.setPitch(0.0F);
        return location;
    }

    public static Location getPositionLoc(Location location1, Location location2) {
        double X = location1.getX() - location2.getX();
        double Y = location1.getY() - location2.getY();
        double Z = location1.getZ() - location2.getZ();
        return new Location(location1.getWorld(), location1.getX() + X, location1.getY() + Y, location1.getZ() + Z,
                location1.getYaw(), location1.getPitch());
    }

    public static Vector getPosition(Location location1, Location location2) {
        double X = location1.getX() - location2.getX();
        double Y = location1.getY() - location2.getY();
        double Z = location1.getZ() - location2.getZ();
        return new Vector(X, Y, Z);
    }

    public static Vector getPosition(Location location1, Location location2, double Y) {
        double X = location1.getX() - location2.getX();
        double Z = location1.getZ() - location2.getZ();
        return new Vector(X, Y, Z);
    }

    public static List<Player> getLocationPlayers(Location location) {
        List<Player> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld() == location.getWorld() && (int) location.getX() == (int) player.getLocation().getX() && (int) location.getY() == (int) player.getLocation().getY() && (int) location.getZ() == (int) player.getLocation().getZ())
                players.add(player);
        }
        return players;
    }
}
