package com.yallage.yabedwars.addon;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.yallage.yabedwars.YaBedwars;
import com.yallage.yabedwars.api.HolographicAPI;
import com.yallage.yabedwars.config.Config;
import com.yallage.yabedwars.events.BoardAddonPlayerOpenItemShopEvent;
import com.yallage.yabedwars.events.BoardAddonPlayerOpenTeamShopEvent;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameOverEvent;
import io.github.bedwarsrel.events.BedwarsGameStartedEvent;
import io.github.bedwarsrel.events.BedwarsOpenShopEvent;
import io.github.bedwarsrel.events.BedwarsPlayerJoinEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.shop.NewItemShop;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Shop implements Listener {
    private WrappedDataWatcher.Serializer booleanserializer;

    private final Map<String, List<NPC>> shops = new HashMap<>();

    private final Map<String, List<NPC>> teamshops = new HashMap<>();

    private final Map<String, List<HolographicAPI>> titles = new HashMap<>();

    private final List<Integer> npcid = new ArrayList<>();

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
                        this.shops.get(game.getName()).add(spawnShop(game, location.clone()));
                        setTitle(game, location.clone().add(0.0D, -0.1D, 0.0D), Config.shop_item_shop_name);
                    }
                }
            if (Config.shop_team.containsKey(game.getName()))
                for (String loc : Config.shop_team.get(game.getName())) {
                    Location location = toLocation(loc);
                    if (location != null) {
                        this.teamshops.get(game.getName()).add(spawnTeamShop(game, location.clone()));
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
        }).runTaskTimer(YaBedwars.getInstance(), 0L, 0L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNPCLeftClick(NPCLeftClickEvent e) {
        e.setCancelled(onNPCClick(e.getClicker(), e.getNPC(), e.isCancelled()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNPCRightClick(NPCRightClickEvent e) {
        e.setCancelled(onNPCClick(e.getClicker(), e.getNPC(), e.isCancelled()));
    }

    private Boolean onNPCClick(Player player, NPC npc, Boolean isCancelled) {
        if (!Config.shop_enabled)
            return isCancelled;
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game != null)
            if (this.shops.get(game.getName()).contains(npc)) {
                if (isGamePlayer(player)) {
                    isCancelled = Boolean.TRUE;
                    BoardAddonPlayerOpenItemShopEvent openItemhopEvent = new BoardAddonPlayerOpenItemShopEvent(game,
                            player);
                    Bukkit.getPluginManager().callEvent(openItemhopEvent);
                    if (!openItemhopEvent.isCancelled()) {
                        player.closeInventory();
                        NewItemShop itemShop = game.openNewItemShop(player);
                        itemShop.setCurrentCategory(null);
                        itemShop.openCategoryInventory(player);
                    }
                }
            } else if (this.teamshops.get(game.getName()).contains(npc) &&
                    isGamePlayer(player)) {
                isCancelled = Boolean.TRUE;
                BoardAddonPlayerOpenTeamShopEvent openTeamShopEvent = new BoardAddonPlayerOpenTeamShopEvent(game,
                        player);
                Bukkit.getPluginManager().callEvent(openTeamShopEvent);
                if (!openTeamShopEvent.isCancelled()) {
                    player.closeInventory();
                    YaBedwars.getInstance().getArenaManager().getArena(game.getName()).getTeamShop()
                            .openTeamShop(player);
                }
            }
        return isCancelled;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onOpenShop(BedwarsOpenShopEvent e) {
        if (!Config.shop_enabled)
            return;
        Player player = (Player) e.getPlayer();
        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
            e.setCancelled(true);
            return;
        }
        if (e.getEntity() instanceof org.bukkit.entity.Villager &&
                CitizensAPI.getNPCRegistry().isNPC(e.getEntity()) && this.teamshops.get(e.getGame().getName())
                .contains(CitizensAPI.getNPCRegistry().getNPC(e.getEntity()))) {
            e.setCancelled(true);
            player.closeInventory();
            YaBedwars.getInstance().getArenaManager().getArena(e.getGame().getName()).getTeamShop().openTeamShop(player);
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
            }).runTaskLater(YaBedwars.getInstance(), 10L);
    }

    private void packetListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(YaBedwars.getInstance(),
                ListenerPriority.HIGHEST, PacketType.Play.Server.ENTITY_METADATA) {
            public void onPacketSending(PacketEvent e) {
                PacketContainer packet = e.getPacket();
                int id = packet.getIntegers().read(0);
                if (Shop.this.npcid.contains(id)) {
                    WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
                    wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, Shop.this.booleanserializer),
                            Boolean.FALSE);
                    packet.getWatchableCollectionModifier().write(0, wrappedDataWatcher.getWatchableObjects());
                }
            }
        });
    }

    private Boolean isGamePlayer(Player player) {
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null)
            return Boolean.FALSE;
        if (game.isSpectator(player))
            return Boolean.FALSE;
        if (player.getGameMode() == GameMode.SPECTATOR)
            return Boolean.FALSE;
        return Boolean.TRUE;
    }

    private NPC spawnShop(Game game, Location location) {
        if (!location.getBlock().getChunk().isLoaded())
            location.getBlock().getChunk().load(true);
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "");
        npc.setProtected(true);
        npc.getTrait(Gravity.class).toggle();
        if (Config.shop_item_shop_look)
            npc.getTrait(LookClose.class).toggle();
        npc.spawn(location);
        try {
            EntityType entityType = EntityType.valueOf(Config.shop_item_shop_type);
            npc.setBukkitEntityType(entityType);
        } catch (Exception ignored) {
        }
        this.npcid.add(npc.getEntity().getEntityId());
        hideEntityTag(game, npc.getEntity());
        Config.addShopNPC(npc.getId());
        try {
            if (npc.isSpawned() && npc.getEntity() instanceof SkinnableEntity) {
                SkinnableEntity skinnable = (SkinnableEntity) npc.getEntity();
                skinnable.setSkinName(Config.shop_item_shop_skin, true);
            }
        } catch (Exception ignored) {
        }
        return npc;
    }

    private NPC spawnTeamShop(Game game, Location location) {
        if (!location.getBlock().getChunk().isLoaded())
            location.getBlock().getChunk().load(true);
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "");
        npc.setProtected(true);
        npc.getTrait(Gravity.class).toggle();
        if (Config.shop_team_shop_look)
            npc.getTrait(LookClose.class).toggle();
        npc.spawn(location);
        try {
            EntityType entityType = EntityType.valueOf(Config.shop_team_shop_type);
            npc.setBukkitEntityType(entityType);
        } catch (Exception ignored) {
        }
        this.npcid.add(npc.getEntity().getEntityId());
        hideEntityTag(game, npc.getEntity());
        Config.addShopNPC(npc.getId());
        try {
            if (npc.isSpawned() && npc.getEntity() instanceof SkinnableEntity) {
                SkinnableEntity skinnable = (SkinnableEntity) npc.getEntity();
                skinnable.setSkinName(Config.shop_team_shop_skin, true);
            }
        } catch (Exception ignored) {
        }
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
                    packet.getIntegers().write(0, entity.getEntityId());
                    WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
                    wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, Shop.this.booleanserializer),
                            Boolean.FALSE);
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
        }).runTaskLater(YaBedwars.getInstance(), 1L);
    }

    private void setTitle(final Game game, Location location, List<String> title) {
        final Location loc = location.clone();
        if (!loc.getBlock().getChunk().isLoaded())
            loc.getBlock().getChunk().load(true);
        List<String> list = new ArrayList<>(title);
        Collections.reverse(list);
        for (String line : list) {
            final HolographicAPI holo = new HolographicAPI(loc, line);
            this.titles.get(game.getName()).add(holo);
            (new BukkitRunnable() {
                public void run() {
                    for (Player player : game.getPlayers())
                        holo.display(player);
                }
            }).runTaskLater(YaBedwars.getInstance(), 20L);
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
        }).runTaskTimer(YaBedwars.getInstance(), 0L, 0L);
    }

    private Location toLocation(String loc) {
        try {
            String[] ary = loc.split(", ");
            if (Bukkit.getWorld(ary[0]) != null) {
                Location location = new Location(Bukkit.getWorld(ary[0]), Double.parseDouble(ary[1]),
                        Double.parseDouble(ary[2]), Double.parseDouble(ary[3]));
                if (ary.length > 4) {
                    location.setYaw(Float.parseFloat(ary[4]));
                    location.setPitch(Float.parseFloat(ary[5]));
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
        if (e.getPlugin().equals(YaBedwars.getInstance())) {
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
