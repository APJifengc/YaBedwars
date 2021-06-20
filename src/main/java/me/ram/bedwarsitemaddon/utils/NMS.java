package me.ram.bedwarsitemaddon.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class NMS {
  public static void teleportEntity(Game game, Entity entity, Location location) {
    if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
      try {
        Constructor<?> constructor = Utils.getNMSClass("PacketPlayOutEntityTeleport").getConstructor(int.class, int.class, int.class, int.class, byte.class, byte.class, boolean.class);
        Method method = Utils.getNMSClass("MathHelper").getMethod("floor", double.class);
        Object packet = constructor.newInstance(Integer.valueOf(entity.getEntityId()), method.invoke(null, Double.valueOf(location.getX() * 32.0D)), method.invoke(null, Double.valueOf(location.getY() * 32.0D)), method.invoke(null, Double.valueOf(location.getZ() * 32.0D)), Byte.valueOf((byte)(int)(location.getYaw() * 256.0F / 360.0F)), Byte.valueOf((byte)(int)(location.getPitch() * 256.0F / 360.0F)), Boolean.valueOf(true));
        for (Player player : game.getPlayers())
          Utils.sendPacket(player, packet); 
      } catch (Exception exception) {}
    } else {
      PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
      packet.getIntegers().write(0, Integer.valueOf(entity.getEntityId()));
      packet.getDoubles().write(0, Double.valueOf(location.getX()));
      packet.getDoubles().write(1, Double.valueOf(location.getY()));
      packet.getDoubles().write(2, Double.valueOf(location.getZ()));
      packet.getBytes().write(0, Byte.valueOf((byte)(int)(location.getYaw() * 256.0F / 360.0F)));
      packet.getBytes().write(1, Byte.valueOf((byte)(int)(location.getPitch() * 256.0F / 360.0F)));
      packet.getBooleans().write(0, Boolean.valueOf(true));
      try {
        for (Player player : game.getPlayers())
          ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet); 
      } catch (Exception exception) {}
    } 
  }
}
