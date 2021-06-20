package com.yallage.yabedwars.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import static java.lang.Math.*;

public class NMS {
    public static void teleportEntity(Game game, Entity entity, Location location) {
        if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
            try {
                Constructor<?> constructor = Objects.requireNonNull(Utils.getNMSClass("PacketPlayOutEntityTeleport")).getConstructor(int.class, int.class, int.class, int.class, byte.class, byte.class, boolean.class);
                Method method = Objects.requireNonNull(Utils.getNMSClass("MathHelper")).getMethod("floor", double.class);
                Object packet = constructor.newInstance(entity.getEntityId(),
                        floor(location.getX() * 32.0D),
                        floor(location.getY() * 32.0D),
                        floor(location.getZ() * 32.0D), (byte) (int) (location.getYaw() * 256.0F / 360.0F), (byte) (int) (location.getPitch() * 256.0F / 360.0F), Boolean.TRUE);
                for (Player player : game.getPlayers())
                    Utils.sendPacket(player, packet);
            } catch (Exception ignored) {
            }
        } else {
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
            packet.getIntegers().write(0, entity.getEntityId());
            packet.getDoubles().write(0, location.getX());
            packet.getDoubles().write(1, location.getY());
            packet.getDoubles().write(2, location.getZ());
            packet.getBytes().write(0, (byte) (int) (location.getYaw() * 256.0F / 360.0F));
            packet.getBytes().write(1, (byte) (int) (location.getPitch() * 256.0F / 360.0F));
            packet.getBooleans().write(0, Boolean.TRUE);
            try {
                for (Player player : game.getPlayers())
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            } catch (Exception ignored) {
            }
        }
    }
}
