package me.ram.bedwarsscoreboardaddon.api;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import io.github.bedwarsrel.BedwarsRel;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class HolographicAPI {
  private Map<UUID, Integer> ids;
  
  private Map<UUID, Object> packets;
  
  private List<UUID> players;
  
  private Location location;
  
  private String title;
  
  private final BukkitTask task;
  
  private List<ItemStack> equipment;
  
  private WrappedDataWatcher.Serializer stringserializer;
  
  private WrappedDataWatcher.Serializer booleanserializer;
  
  public HolographicAPI(Location loc, String title) {
    this.ids = new HashMap<>();
    this.packets = new HashMap<>();
    this.players = new ArrayList<>();
    this.equipment = new ArrayList<>();
    this.location = loc.clone();
    this.title = title;
    if (!BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
      this.stringserializer = WrappedDataWatcher.Registry.get(String.class);
      this.booleanserializer = WrappedDataWatcher.Registry.get(Boolean.class);
    } 
    this.task = (new BukkitRunnable() {
        public void run() {
          List<UUID> list = new ArrayList<>();
          for (UUID uuid : HolographicAPI.this.packets.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) {
              list.add(uuid);
              continue;
            } 
            Location loc2 = player.getLocation().clone();
            loc2.setY(HolographicAPI.this.location.getY());
            if (HolographicAPI.this.players.contains(uuid)) {
              if (!loc2.getWorld().getName().equals(HolographicAPI.this.location.getWorld().getName()) || 
                loc2.distance(HolographicAPI.this.location) >= 63.0D) {
                Utils.sendPacket(player, HolographicAPI.this.packets.get(player.getUniqueId()));
                HolographicAPI.this.players.remove(uuid);
              } 
              continue;
            } 
            if (loc2.getWorld().getName().equals(HolographicAPI.this.location.getWorld().getName()) && 
              loc2.distance(HolographicAPI.this.location) < 63.0D)
              HolographicAPI.this.display(player); 
          } 
          for (UUID uuid : list) {
            HolographicAPI.this.ids.remove(uuid);
            HolographicAPI.this.packets.remove(uuid);
            HolographicAPI.this.players.remove(uuid);
          } 
        }
      }).runTaskTimer(Main.getInstance(), 1L, 1L);
  }
  
  public void setEquipment(List<ItemStack> equipment) {
    this.equipment = new ArrayList<>();
    this.equipment.addAll(equipment);
  }
  
  public Location getLocation() {
    return this.location.clone();
  }
  
  public String getTitle() {
    return this.title;
  }
  
  public void setTitle(String title) {
    if (this.title == null) {
      this.title = title;
      for (UUID uuid : this.packets.keySet()) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline() && this.players.contains(uuid))
          display(player); 
      } 
    } else {
      this.title = title;
      for (UUID uuid : this.packets.keySet()) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
          Location loc = player.getLocation().clone();
          loc.setY(this.location.getY());
          if (loc.getWorld().getName().equals(this.location.getWorld().getName()) && loc.distance(this.location) < 64.0D && 
            player != null && player.isOnline() && this.players.contains(uuid)) {
            ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
            packet.getIntegers().write(0, this.ids.get(uuid));
            if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
              packet.getWatchableCollectionModifier().write(0, 
                  Arrays.asList(new WrappedWatchableObject(2, title)));
            } else {
              WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
              if (title != null) {
                wrappedDataWatcher.setObject(
                    new WrappedDataWatcher.WrappedDataWatcherObject(2, this.stringserializer), title);
                wrappedDataWatcher.setObject(
                    new WrappedDataWatcher.WrappedDataWatcherObject(3, this.booleanserializer), Boolean.valueOf(true));
              } else {
                wrappedDataWatcher.setObject(
                    new WrappedDataWatcher.WrappedDataWatcherObject(2, this.stringserializer), "");
                wrappedDataWatcher.setObject(
                    new WrappedDataWatcher.WrappedDataWatcherObject(3, this.booleanserializer), Boolean.valueOf(false));
              } 
              packet.getWatchableCollectionModifier().write(0, wrappedDataWatcher.getWatchableObjects());
            } 
            try {
              protocolManager.sendServerPacket(player, packet);
            } catch (InvocationTargetException e) {
              e.printStackTrace();
            } 
          } 
        } 
      } 
    } 
  }
  
  public void display(Player player) {
    if (player == null || this.task == null)
      return; 
    if (this.packets.containsKey(player.getUniqueId()))
      Utils.sendPacket(player, this.packets.get(player.getUniqueId())); 
    Object packet = getPacket(this.location);
    Object destroyPacket = null;
    Utils.sendPacket(player, packet);
    try {
      Field declaredField = Utils.getNMSClass("PacketPlayOutSpawnEntityLiving").getDeclaredField("a");
      declaredField.setAccessible(true);
      this.ids.put(player.getUniqueId(), Integer.valueOf(((Integer)declaredField.get(packet)).intValue()));
      destroyPacket = getDestroyPacket(((Integer)declaredField.get(packet)).intValue());
    } catch (Exception exception) {}
    if (this.equipment.size() > 0)
      try {
        Field declaredField = Utils.getNMSClass("PacketPlayOutSpawnEntityLiving").getDeclaredField("a");
        declaredField.setAccessible(true);
        int j = 5;
        for (ItemStack itemStack : this.equipment) {
          j--;
          if (j < 0)
            break; 
          PacketContainer equipmentPacket = getEquipmentPacket(((Integer)declaredField.get(packet)).intValue(), j, 
              itemStack);
          ProtocolLibrary.getProtocolManager().sendServerPacket(player, equipmentPacket);
        } 
      } catch (Exception e) {
        e.printStackTrace();
      }  
    this.packets.put(player.getUniqueId(), destroyPacket);
    this.players.add(player.getUniqueId());
  }
  
  public void destroy(Player player) {
    if (player != null && this.packets.containsKey(player.getUniqueId())) {
      Utils.sendPacket(player, this.packets.get(player.getUniqueId()));
      this.ids.remove(player.getUniqueId());
      this.packets.remove(player.getUniqueId());
      this.players.remove(player.getUniqueId());
    } 
  }
  
  public void remove() {
    this.task.cancel();
    for (UUID uuid : this.packets.keySet()) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null && player.isOnline())
        Utils.sendPacket(player, this.packets.get(uuid)); 
    } 
    this.ids = new HashMap<>();
    this.packets = new HashMap<>();
    this.players = new ArrayList<>();
  }
  
  public void teleport(Location loc) {
    if (!loc.getWorld().getName().equals(this.location.getWorld().getName()))
      return; 
    for (UUID uuid : this.packets.keySet()) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null && player.isOnline() && this.players.contains(uuid))
        sendTeleportPacket(this.ids.get(uuid).intValue(), loc, player);
    } 
    this.location = loc.clone();
  }
  
  private void sendTeleportPacket(int id, Location loc, Player player) {
    if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
      try {
        Constructor constructor = Utils.getNMSClass("PacketPlayOutEntityTeleport").getConstructor(int.class,
                int.class, int.class, int.class, byte.class, byte.class, boolean.class);
        Method method = Utils.getNMSClass("MathHelper").getMethod("floor", double.class);
        Object packet = constructor.newInstance(Integer.valueOf(id), method.invoke(null, Double.valueOf(this.location.getX() * 32.0D)), method.invoke(null, Double.valueOf(this.location.getY() * 32.0D)), method.invoke(null, Double.valueOf(this.location.getZ() * 32.0D)), Byte.valueOf((byte)(int)(this.location.getYaw() * 256.0F / 360.0F)), Byte.valueOf((byte)(int)(this.location.getPitch() * 256.0F / 360.0F)),
                Boolean.valueOf(true));
        Utils.sendPacket(player, packet);
      } catch (Exception exception) {}
    } else {
      PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
      packet.getIntegers().write(0, Integer.valueOf(id));
      packet.getDoubles().write(0, Double.valueOf(this.location.getX()));
      packet.getDoubles().write(1, Double.valueOf(this.location.getY()));
      packet.getDoubles().write(2, Double.valueOf(this.location.getZ()));
      packet.getBytes().write(0, Byte.valueOf((byte)(int)(this.location.getYaw() * 256.0F / 360.0F)));
      packet.getBytes().write(1, Byte.valueOf((byte)(int)(this.location.getPitch() * 256.0F / 360.0F)));
      packet.getBooleans().write(0, Boolean.valueOf(true));
      try {
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
      } catch (Exception exception) {}
    } 
  }
  
  private Object getPacket(Location location) {
    try {
      Object cast = Utils.getClass("CraftWorld").cast(location.getWorld());
      Object instance = Utils.getNMSClass("EntityArmorStand").getConstructor(new Class[] { Utils.getNMSClass("World") }).newInstance(cast.getClass().getMethod("getHandle", new Class[0]).invoke(cast
      ));
      if (this.title != null) {
        instance.getClass().getMethod("setCustomName", new Class[] { String.class }).invoke(instance, this.title);
        Utils.getNMSClass("Entity").getMethod("setCustomNameVisible", new Class[] { boolean.class }).invoke(instance, Boolean.valueOf(true));
      } 
      try {
        instance.getClass().getMethod("setGravity", new Class[] { boolean.class }).invoke(instance, Boolean.valueOf(false));
      } catch (Exception ex) {
        instance.getClass().getMethod("setNoGravity", new Class[] { boolean.class }).invoke(instance, Boolean.valueOf(true));
      } 
      instance.getClass().getMethod("setLocation", new Class[] { double.class, double.class, double.class, float.class, float.class }).invoke(instance, Double.valueOf(location.getX()), Double.valueOf(location.getY()), Double.valueOf(location.getZ()), Float.valueOf(0.0F), Float.valueOf(0.0F));
      instance.getClass().getMethod("setBasePlate", new Class[] { boolean.class }).invoke(instance, Boolean.valueOf(false));
      instance.getClass().getMethod("setInvisible", new Class[] { boolean.class }).invoke(instance, Boolean.valueOf(true));
      return Utils.getNMSClass("PacketPlayOutSpawnEntityLiving").getConstructor(new Class[] { Utils.getNMSClass("EntityLiving") }).newInstance(instance);
    } catch (Exception e) {
      return null;
    } 
  }
  
  private PacketContainer getEquipmentPacket(int id, int slot, ItemStack item) throws Exception {
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
    packet.getIntegers().write(0, Integer.valueOf(id));
    slot = (slot > 4) ? 4 : slot;
    slot = (slot < 0) ? 0 : slot;
    if (packet.getIntegers().size() > 1) {
      packet.getIntegers().write(1, Integer.valueOf(slot));
    } else {
      switch (slot) {
        case 1:
          packet.getItemSlots().write(0, EnumWrappers.ItemSlot.FEET);
          break;
        case 2:
          packet.getItemSlots().write(0, EnumWrappers.ItemSlot.LEGS);
          break;
        case 3:
          packet.getItemSlots().write(0, EnumWrappers.ItemSlot.CHEST);
          break;
        case 4:
          packet.getItemSlots().write(0, EnumWrappers.ItemSlot.HEAD);
          break;
        case 0:
          packet.getItemSlots().write(0, EnumWrappers.ItemSlot.MAINHAND);
          break;
      } 
    } 
    packet.getItemModifier().write(0, item);
    return packet;
  }
  
  private Object getDestroyPacket(int... array) throws Exception {
    try {
      return Utils.getNMSClass("PacketPlayOutEntityDestroy").getConstructor(new Class[] { int[].class }).newInstance(new Object[] { array });
    } catch (Exception e) {
      return null;
    } 
  }
}
