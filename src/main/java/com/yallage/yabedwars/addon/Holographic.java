package com.yallage.yabedwars.addon;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsTargetBlockDestroyedEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.ResourceSpawner;
import io.github.bedwarsrel.game.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yallage.yabedwars.YaBedwars;
import com.yallage.yabedwars.api.HolographicAPI;
import com.yallage.yabedwars.arena.Arena;
import com.yallage.yabedwars.config.Config;
import com.yallage.yabedwars.utils.ColorUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Holographic {
    private final Game game;

    private final List<HolographicAPI> ablocks;

    private final List<HolographicAPI> atitles;

    private final List<HolographicAPI> btitles;

    private final Map<String, HolographicAPI> pbtitles;

    private final ResourceUpgrade resourceupgrade;

    private final HashMap<HolographicAPI, Location> armorloc = new HashMap<>();

    private final HashMap<HolographicAPI, Boolean> armorupward = new HashMap<>();

    private final HashMap<HolographicAPI, Integer> armoralgebra = new HashMap<>();

    public Holographic(final Game game, ResourceUpgrade resourceupgrade) {
        this.game = game;
        this.ablocks = new ArrayList<>();
        this.atitles = new ArrayList<>();
        this.btitles = new ArrayList<>();
        this.pbtitles = new HashMap<>();
        this.resourceupgrade = resourceupgrade;
        if (Config.holographic_resource_enabled)
            for (String r : Config.holographic_resource)
                setArmorStand(game, r);
        (new BukkitRunnable() {
            public void run() {
                if (game.getState() != GameState.RUNNING || game.getPlayers().size() < 1) {
                    cancel();
                    for (HolographicAPI holo : Holographic.this.ablocks)
                        holo.remove();
                    for (HolographicAPI holo : Holographic.this.atitles)
                        holo.remove();
                    for (HolographicAPI holo : Holographic.this.btitles)
                        holo.remove();
                    for (HolographicAPI holo : Holographic.this.pbtitles.values())
                        holo.remove();
                }
            }
        }).runTaskTimer(YaBedwars.getInstance(), 1L, 1L);
    }

    public Game getGame() {
        return this.game;
    }

    public void onTargetBlockDestroyed(BedwarsTargetBlockDestroyedEvent e) {

    }

    private void setArmorStand(final Game game, String res) {
        for (ResourceSpawner spawner : game.getResourceSpawners()) {
            for (ItemStack itemStack : spawner.getResources()) {
                if (itemStack.getType() == Material.getMaterial(
                        YaBedwars.getInstance().getConfig().getInt("holographic.resource.resources." + res + ".item"))) {
                    if (!spawner.getLocation().getBlock().getChunk().isLoaded())
                        spawner.getLocation().getBlock().getChunk().load(true);
                    final HolographicAPI holo = new HolographicAPI(spawner.getLocation().clone().add(0.0D, YaBedwars.getInstance()
                            .getConfig().getDouble("holographic.resource.resources." + res + ".height"), 0.0D), null);
                    holo.setEquipment(Collections.singletonList(new ItemStack(Material.getMaterial(YaBedwars.getInstance().getConfig()
                            .getInt("holographic.resource.resources." + res + ".block")))));
                    (new BukkitRunnable() {
                        public void run() {
                            for (Player player : game.getPlayers())
                                holo.display(player);
                        }
                    }).runTaskLater(YaBedwars.getInstance(), 20L);
                    ArrayList<String> titles = new ArrayList<>();
                    for (String title : YaBedwars.getInstance().getConfig().getStringList("holographic.resource.resources." + res + ".title")) {
                        titles.add(ColorUtil.color(title));
                    }
                    setArmorStandRun(game, holo, titles, itemStack);
                }
            }
        }
    }

    private void setArmorStandRun(final Game game, final HolographicAPI holo, ArrayList<String> titles, ItemStack itemStack) {
        Collections.reverse(titles);
        Location aslocation = holo.getLocation().clone().add(0.0D, 0.35D, 0.0D);
        for (String title : titles) {
            setTitle(game, aslocation, title, itemStack);
            aslocation.add(0.0D, 0.4D, 0.0D);
        }
        this.ablocks.add(holo);
        (new BukkitRunnable() {
            double y;

            public void run() {
                if (game.getState() == GameState.RUNNING) {
                    if (!holo.getLocation().getBlock().getChunk().isLoaded())
                        holo.getLocation().getBlock().getChunk().load(true);
                    Holographic.this.moveArmorStand(holo, this.y, game);
                } else {
                    Holographic.this.armorloc.remove(holo);
                    Holographic.this.armorupward.remove(holo);
                    Holographic.this.armoralgebra.remove(holo);
                    holo.remove();
                    Holographic.this.ablocks.remove(holo);
                    cancel();
                }
            }
        }).runTaskTimer(YaBedwars.getInstance(), 1L, 1L);
    }

    private void setTitle(final Game game, Location location, final String title, final ItemStack itemStack) {
        if (!location.getBlock().getChunk().isLoaded())
            location.getBlock().getChunk().load(true);
        final HolographicAPI holo = new HolographicAPI(location, " ");
        this.atitles.add(holo);
        (new BukkitRunnable() {
            public void run() {
                for (Player player : game.getPlayers())
                    holo.display(player);
            }
        }).runTaskLater(YaBedwars.getInstance(), 20L);
        (new BukkitRunnable() {
            public void run() {
                if (game.getState() == GameState.RUNNING) {
                    if (!holo.getLocation().getBlock().getChunk().isLoaded())
                        holo.getLocation().getBlock().getChunk().load(true);
                    String customName = title;
                    customName = customName.replace("{level}", Holographic.this.resourceupgrade.getLevel().get(itemStack.getType()));
                    for (Material sitem : Holographic.this.resourceupgrade.getSpawnTime().keySet()) {
                        if (itemStack.getType() == sitem)
                            customName = customName.replace("{generate_time}",
                                    Holographic.this.resourceupgrade.getSpawnTime().get(sitem).toString());
                    }
                    holo.setTitle(customName);
                } else {
                    holo.remove();
                    Holographic.this.atitles.remove(holo);
                    cancel();
                }
            }
        }).runTaskTimer(YaBedwars.getInstance(), 5L, 5L);
    }

    public void onPlayerLeave(Player player) {
        if (player.isOnline())
            for (HolographicAPI holo : this.pbtitles.values())
                holo.destroy(player);
    }

    public void onPlayerJoin(final Player player) {
        if (this.game.getState() != GameState.RUNNING)
            return;
        (new BukkitRunnable() {
            public void run() {
                if (Holographic.this.game.getState() == GameState.RUNNING && player.isOnline() && Holographic.this.game.getPlayers().contains(player)) {
                    for (HolographicAPI holo : Holographic.this.ablocks)
                        holo.display(player);
                    for (HolographicAPI holo : Holographic.this.atitles)
                        holo.display(player);
                    for (HolographicAPI holo : Holographic.this.btitles)
                        holo.display(player);
                    Arena arena = YaBedwars.getInstance().getArenaManager().getArena(Holographic.this.game.getName());
                    if (arena.getRejoin().getPlayers().containsKey(player.getName())) {
                        Team team = Holographic.this.game.getPlayerTeam(player);
                        if (team != null && !team.isDead(Holographic.this.game))
                            Holographic.this.pbtitles.get(team.getName()).display(player);
                    }
                }
            }
        }).runTaskLater(YaBedwars.getInstance(), 10L);
    }

    public void remove() {
        for (HolographicAPI holo : this.ablocks)
            holo.remove();
        for (HolographicAPI holo : this.atitles)
            holo.remove();
        for (HolographicAPI holo : this.btitles)
            holo.remove();
        for (HolographicAPI holo : this.pbtitles.values())
            holo.remove();
    }

    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
        Player player = e.getPlayer();
        Game getGame = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (getGame == null)
            return;
        if (!getGame.getPlayers().contains(player))
            return;
        if (getGame.getState() != GameState.WAITING && getGame.getState() == GameState.RUNNING)
            e.setCancelled(true);
    }

    private void moveArmorStand(HolographicAPI holo, double height, Game game) {
        if (!this.armorloc.containsKey(holo))
            this.armorloc.put(holo, holo.getLocation().clone());
        if (!this.armorupward.containsKey(holo))
            this.armorupward.put(holo, Boolean.TRUE);
        if (!this.armoralgebra.containsKey(holo))
            this.armoralgebra.put(holo, 0);
        this.armoralgebra.put(holo, this.armoralgebra.get(holo) + 1);
        Location location = this.armorloc.get(holo);
        if (location.getY() >= height + 0.3D) {
            this.armoralgebra.put(holo, 0);
            this.armorupward.put(holo, Boolean.FALSE);
        } else if (location.getY() <= height - 0.3D) {
            this.armoralgebra.put(holo, 0);
            this.armorupward.put(holo, Boolean.TRUE);
        }
        Integer algebra = this.armoralgebra.get(holo);
        if (39 > algebra) {
            if (this.armorupward.get(holo)) {
                location.setY(location.getY() + 0.015D);
            } else {
                location.setY(location.getY() - 0.015D);
            }
        } else if (algebra >= 50) {
            this.armoralgebra.put(holo, 0);
            this.armorupward.put(holo, !this.armorupward.get(holo));
        }
        Float turn = 1.0F;
        if (!this.armorupward.get(holo))
            turn = -turn;
        Float changeyaw = 0.0F;
        if (algebra == 1 || algebra == 40) {
            changeyaw = changeyaw + 2.0F * turn;
        } else if (algebra == 2 || algebra == 39) {
            changeyaw = changeyaw + 3.0F * turn;
        } else if (algebra == 3 || algebra == 38) {
            changeyaw = changeyaw + 4.0F * turn;
        } else if (algebra == 4 || algebra == 37) {
            changeyaw = changeyaw + 5.0F * turn;
        } else if (algebra == 5 || algebra == 36) {
            changeyaw = changeyaw + 6.0F * turn;
        } else if (algebra == 6 || algebra == 35) {
            changeyaw = changeyaw + 7.0F * turn;
        } else if (algebra == 7 || algebra == 34) {
            changeyaw = changeyaw + 8.0F * turn;
        } else if (algebra == 8 || algebra == 33) {
            changeyaw = changeyaw + 9.0F * turn;
        } else if (algebra == 9 || algebra == 32) {
            changeyaw = changeyaw + 10.0F * turn;
        } else if (algebra == 10 || algebra == 31) {
            changeyaw = changeyaw + 11.0F * turn;
        } else if (algebra == 11 || algebra == 30) {
            changeyaw = changeyaw + 11.0F * turn;
        } else if (algebra == 12 || algebra == 29) {
            changeyaw = changeyaw + 12.0F * turn;
        } else if (algebra == 13 || algebra == 28) {
            changeyaw = changeyaw + 12.0F * turn;
        } else if (algebra == 14 || algebra == 27) {
            changeyaw = changeyaw + 13.0F * turn;
        } else if (algebra == 15 || algebra == 26) {
            changeyaw = changeyaw + 13.0F * turn;
        } else if (algebra == 16 || algebra == 25) {
            changeyaw = changeyaw + 14.0F * turn;
        } else if (algebra == 17 || algebra == 24) {
            changeyaw = changeyaw + 14.0F * turn;
        } else if (algebra == 18 || algebra == 23) {
            changeyaw = changeyaw + 15.0F * turn;
        } else if (algebra == 19 || algebra == 22) {
            changeyaw = changeyaw + 15.0F * turn;
        } else if (algebra == 20 || algebra == 21) {
            changeyaw = changeyaw + 16.0F * turn;
        } else if (algebra == 41) {
            changeyaw = changeyaw + 2.0F * turn;
        } else if (algebra == 42) {
            changeyaw = changeyaw + 2.0F * turn;
        } else if (algebra == 43) {
            changeyaw = changeyaw + 2.0F * turn;
        } else if (algebra == 44) {
            changeyaw = changeyaw + 1.0F * turn;
        } else if (algebra == 45) {
            changeyaw = changeyaw + -1.0F * turn;
        } else if (algebra == 46) {
            changeyaw = changeyaw + -1.0F * turn;
        } else if (algebra == 47) {
            changeyaw = changeyaw + -2.0F * turn;
        } else if (algebra == 48) {
            changeyaw = changeyaw + -2.0F * turn;
        } else if (algebra == 49) {
            changeyaw = changeyaw + -2.0F * turn;
        } else if (algebra == 50) {
            changeyaw = changeyaw + -2.0F * turn;
        }
        double yaw = location.getYaw();
        yaw += changeyaw * Config.holographic_resource_speed;
        yaw = (yaw > 360.0D) ? (yaw - 360.0D) : yaw;
        yaw = (yaw < -360.0D) ? (yaw + 360.0D) : yaw;
        location.setYaw((float) yaw);
        holo.teleport(location);
    }
}
