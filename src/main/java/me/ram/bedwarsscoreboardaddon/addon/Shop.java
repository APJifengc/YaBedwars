package me.ram.bedwarsscoreboardaddon.addon;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameOverEvent;
import io.github.bedwarsrel.events.BedwarsGameStartedEvent;
import io.github.bedwarsrel.events.BedwarsOpenShopEvent;
import io.github.bedwarsrel.events.BedwarsPlayerJoinEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.shop.NewItemShop;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.api.HolographicAPI;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.events.BoardAddonPlayerOpenItemShopEvent;
import me.ram.bedwarsscoreboardaddon.events.BoardAddonPlayerOpenTeamShopEvent;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import net.citizensnpcs.trait.Gravity;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Shop implements Listener {
  private WrappedDataWatcher.Serializer booleanserializer;
  
  private Map<String, List<NPC>> shops = new HashMap<>();
  
  private Map<String, List<NPC>> teamshops = new HashMap<>();
  
  private Map<String, List<HolographicAPI>> titles = new HashMap<>();
  
  private List<Integer> npcid = new ArrayList<>();
  
  public Shop() {
    if (!BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
      this.booleanserializer = WrappedDataWatcher.Registry.get(Boolean.class);
      packetListener();
    } 
  }
  
  @EventHandler
  public void onStarted(final BedwarsGameStartedEvent e) {
    final Game game = e.getGame();
    this.shops.put(game.getName(), new ArrayList<>());
    this.teamshops.put(game.getName(), new ArrayList<>());
    this.titles.put(game.getName(), new ArrayList<>());
    if (Config.shop_enabled) {
      if (Config.shop_item.containsKey(game.getName()))
        for (String loc : Config.shop_item.get(game.getName())) {
          Location location = toLocation(loc);
          if (location != null) {
            ((List<NPC>)this.shops.get(game.getName())).add(spawnShop(game, location.clone()));
            setTitle(game, location.clone().add(0.0D, -0.1D, 0.0D), Config.shop_item_shop_name);
          } 
        }  
      if (Config.shop_team.containsKey(game.getName()))
        for (String loc : Config.shop_team.get(game.getName())) {
          Location location = toLocation(loc);
          if (location != null) {
            ((List<NPC>)this.teamshops.get(game.getName())).add(spawnTeamShop(game, location.clone()));
            setTitle(game, location.clone().add(0.0D, -0.1D, 0.0D), Config.shop_team_shop_name);
          } 
        }  
    } 
    (new BukkitRunnable() {
        public void run() {
          if (game.getState() != GameState.RUNNING || game.getPlayers().size() < 1) {
            cancel();
            for (NPC npc : Shop.this.shops.get(e.getGame().getName()))
              CitizensAPI.getNPCRegistry().deregister(npc); 
            for (NPC npc : Shop.this.teamshops.get(e.getGame().getName()))
              CitizensAPI.getNPCRegistry().deregister(npc); 
            for (HolographicAPI holo : Shop.this.titles.get(e.getGame().getName()))
              holo.remove(); 
          } 
        }
      }).runTaskTimer((Plugin)Main.getInstance(), 0L, 0L);
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onNPCLeftClick(NPCLeftClickEvent e) {
    e.setCancelled(onNPCClick(e.getClicker(), e.getNPC(), Boolean.valueOf(e.isCancelled())).booleanValue());
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onNPCRightClick(NPCRightClickEvent e) {
    e.setCancelled(onNPCClick(e.getClicker(), e.getNPC(), Boolean.valueOf(e.isCancelled())).booleanValue());
  }
  
  private Boolean onNPCClick(Player player, NPC npc, Boolean isCancelled) {
    if (!Config.shop_enabled)
      return isCancelled; 
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
    if (game != null)
      if (((List)this.shops.get(game.getName())).contains(npc)) {
        if (isGamePlayer(player).booleanValue()) {
          isCancelled = Boolean.valueOf(true);
          BoardAddonPlayerOpenItemShopEvent openItemhopEvent = new BoardAddonPlayerOpenItemShopEvent(game, 
              player);
          Bukkit.getPluginManager().callEvent((Event)openItemhopEvent);
          if (!openItemhopEvent.isCancelled()) {
            player.closeInventory();
            NewItemShop itemShop = game.openNewItemShop(player);
            itemShop.setCurrentCategory(null);
            itemShop.openCategoryInventory(player);
          } 
        } 
      } else if (((List)this.teamshops.get(game.getName())).contains(npc) && 
        isGamePlayer(player).booleanValue()) {
        isCancelled = Boolean.valueOf(true);
        BoardAddonPlayerOpenTeamShopEvent openTeamShopEvent = new BoardAddonPlayerOpenTeamShopEvent(game, 
            player);
        Bukkit.getPluginManager().callEvent((Event)openTeamShopEvent);
        if (!openTeamShopEvent.isCancelled()) {
          player.closeInventory();
          Main.getInstance().getArenaManager().getArena(game.getName()).getTeamShop()
            .openTeamShop(player);
        } 
      }  
    return isCancelled;
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onOpenShop(BedwarsOpenShopEvent e) {
    if (!Config.shop_enabled)
      return; 
    Player player = (Player)e.getPlayer();
    if (player.getGameMode().equals(GameMode.SPECTATOR)) {
      e.setCancelled(true);
      return;
    } 
    if (e.getEntity() instanceof org.bukkit.entity.Villager && 
      CitizensAPI.getNPCRegistry().isNPC(e.getEntity()) && ((List)this.teamshops.get(e.getGame().getName()))
      .contains(CitizensAPI.getNPCRegistry().getNPC(e.getEntity()))) {
      e.setCancelled(true);
      player.closeInventory();
      Main.getInstance().getArenaManager().getArena(e.getGame().getName()).getTeamShop().openTeamShop(player);
    } 
  }
  
  @EventHandler
  public void onPlayerJoin(BedwarsPlayerJoinEvent e) {
    final Game game = e.getGame();
    final Player player = e.getPlayer();
    if (game.getState() == GameState.RUNNING)
      (new BukkitRunnable() {
          public void run() {
            if (game.getState() == GameState.RUNNING && player.isOnline() && 
              game.getPlayers().contains(player))
              for (HolographicAPI holo : Shop.this.titles.get(game.getName()))
                holo.display(player);  
          }
        }).runTaskLater((Plugin)Main.getInstance(), 10L); 
  }
  
  private void packetListener() {
    ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter((Plugin)Main.getInstance(), 
          ListenerPriority.HIGHEST, new PacketType[] { PacketType.Play.Server.ENTITY_METADATA }) {
          public void onPacketSending(PacketEvent e) {
            PacketContainer packet = e.getPacket();
            int id = ((Integer)packet.getIntegers().read(0)).intValue();
            if (Shop.this.npcid.contains(Integer.valueOf(id))) {
              WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
              wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, Shop.this.booleanserializer), 
                  Boolean.valueOf(false));
              packet.getWatchableCollectionModifier().write(0, wrappedDataWatcher.getWatchableObjects());
            } 
          }
        });
  }
  
  private Boolean isGamePlayer(Player player) {
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
    if (game == null)
      return Boolean.valueOf(false); 
    if (game.isSpectator(player))
      return Boolean.valueOf(false); 
    if (player.getGameMode() == GameMode.SPECTATOR)
      return Boolean.valueOf(false); 
    return Boolean.valueOf(true);
  }
  
  private NPC spawnShop(Game game, Location location) {
    if (!location.getBlock().getChunk().isLoaded())
      location.getBlock().getChunk().load(true); 
    NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "");
    npc.setProtected(true);
    ((Gravity)npc.getTrait(Gravity.class)).toggle();
    if (Config.shop_item_shop_look)
      ((LookClose)npc.getTrait(LookClose.class)).toggle(); 
    npc.spawn(location);
    try {
      EntityType entityType = EntityType.valueOf(Config.shop_item_shop_type);
      npc.setBukkitEntityType(entityType);
    } catch (Exception exception) {}
    this.npcid.add(Integer.valueOf(npc.getEntity().getEntityId()));
    hideEntityTag(game, npc.getEntity());
    Config.addShopNPC(Integer.valueOf(npc.getId()));
    try {
      if (npc.isSpawned() && npc.getEntity() instanceof SkinnableEntity) {
        SkinnableEntity skinnable = (SkinnableEntity)npc.getEntity();
        skinnable.setSkinName(Config.shop_item_shop_skin, true);
      } 
    } catch (Exception exception) {}
    return npc;
  }
  
  private NPC spawnTeamShop(Game game, Location location) {
    if (!location.getBlock().getChunk().isLoaded())
      location.getBlock().getChunk().load(true); 
    NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "");
    npc.setProtected(true);
    ((Gravity)npc.getTrait(Gravity.class)).toggle();
    if (Config.shop_team_shop_look)
      ((LookClose)npc.getTrait(LookClose.class)).toggle(); 
    npc.spawn(location);
    try {
      EntityType entityType = EntityType.valueOf(Config.shop_team_shop_type);
      npc.setBukkitEntityType(entityType);
    } catch (Exception exception) {}
    this.npcid.add(Integer.valueOf(npc.getEntity().getEntityId()));
    hideEntityTag(game, npc.getEntity());
    Config.addShopNPC(Integer.valueOf(npc.getId()));
    try {
      if (npc.isSpawned() && npc.getEntity() instanceof SkinnableEntity) {
        SkinnableEntity skinnable = (SkinnableEntity)npc.getEntity();
        skinnable.setSkinName(Config.shop_team_shop_skin, true);
      } 
    } catch (Exception exception) {}
    return npc;
  }
  
  private void hideEntityTag(final Game game, final Entity entity) {
    if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8"))
      return; 
    (new BukkitRunnable() {
        public void run() {
          if (game.getState() == GameState.RUNNING) {
            ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
            packet.getIntegers().write(0, Integer.valueOf(entity.getEntityId()));
            WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
            wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, Shop.this.booleanserializer), 
                Boolean.valueOf(false));
            packet.getWatchableCollectionModifier().write(0, wrappedDataWatcher.getWatchableObjects());
            for (Player player : game.getPlayers()) {
              try {
                protocolManager.sendServerPacket(player, packet);
              } catch (InvocationTargetException e) {
                e.printStackTrace();
              } 
            } 
          } 
        }
      }).runTaskLater((Plugin)Main.getInstance(), 1L);
  }
  
  private void setTitle(final Game game, Location location, List<String> title) {
    final Location loc = location.clone();
    if (!loc.getBlock().getChunk().isLoaded())
      loc.getBlock().getChunk().load(true); 
    List<String> list = new ArrayList<>();
    list.addAll(title);
    Collections.reverse(list);
    for (String line : list) {
      final HolographicAPI holo = new HolographicAPI(loc, line);
      ((List<HolographicAPI>)this.titles.get(game.getName())).add(holo);
      (new BukkitRunnable() {
          public void run() {
            for (Player player : game.getPlayers())
              holo.display(player); 
          }
        }).runTaskLater((Plugin)Main.getInstance(), 20L);
      loc.add(0.0D, 0.3D, 0.0D);
    } 
    (new BukkitRunnable() {
        public void run() {
          if (game.getState() == GameState.RUNNING) {
            if (!loc.getBlock().getChunk().isLoaded())
              loc.getBlock().getChunk().load(true); 
          } else {
            cancel();
          } 
        }
      }).runTaskTimer((Plugin)Main.getInstance(), 0L, 0L);
  }
  
  private Location toLocation(String loc) {
    try {
      String[] ary = loc.split(", ");
      if (Bukkit.getWorld(ary[0]) != null) {
        Location location = new Location(Bukkit.getWorld(ary[0]), Double.valueOf(ary[1]).doubleValue(), 
            Double.valueOf(ary[2]).doubleValue(), Double.valueOf(ary[3]).doubleValue());
        if (ary.length > 4) {
          location.setYaw(Float.valueOf(ary[4]).floatValue());
          location.setPitch(Float.valueOf(ary[5]).floatValue());
        } 
        return location;
      } 
    } catch (Exception e) {
      return null;
    } 
    return null;
  }
  
  @EventHandler
  public void onOver(BedwarsGameOverEvent e) {
    for (NPC npc : this.shops.get(e.getGame().getName()))
      CitizensAPI.getNPCRegistry().deregister(npc); 
    for (NPC npc : this.teamshops.get(e.getGame().getName()))
      CitizensAPI.getNPCRegistry().deregister(npc); 
    for (HolographicAPI holo : this.titles.get(e.getGame().getName()))
      holo.remove(); 
  }
  
  @EventHandler
  public void onDisable(PluginDisableEvent e) {
    if (e.getPlugin().equals(Main.getInstance())) {
      for (String game : this.shops.keySet()) {
        for (NPC npc : this.shops.get(game))
          CitizensAPI.getNPCRegistry().deregister(npc); 
      } 
      for (String game : this.teamshops.keySet()) {
        for (NPC npc : this.teamshops.get(game))
          CitizensAPI.getNPCRegistry().deregister(npc); 
      } 
      for (String game : this.titles.keySet()) {
        for (HolographicAPI holo : this.titles.get(game))
          holo.remove(); 
      } 
    } 
  }
}
