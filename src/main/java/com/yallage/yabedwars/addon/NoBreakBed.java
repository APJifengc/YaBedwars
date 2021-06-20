package com.yallage.yabedwars.addon;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import com.yallage.yabedwars.YaBedwars;
import com.yallage.yabedwars.config.Config;
import com.yallage.yabedwars.utils.Utils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class NoBreakBed {
    private final Game game;

    private boolean bre;

    private String formattime = "00:00";

    private PacketListener packetlistener;

    public NoBreakBed(final Game game) {
        this.game = game;
        this.bre = false;
        if (!Config.nobreakbed_enabled)
            return;
        breakbed();
        (new BukkitRunnable() {
            public void run() {
                if (Config.nobreakbed_enabled && game.getState() != GameState.WAITING &&
                        game.getState() == GameState.RUNNING) {
                    int time = game.getTimeLeft() - Config.nobreakbed_gametime;
                    NoBreakBed.this.formattime = time / 60 + ":" + ((time % 60 < 10) ? ("0" + (time % 60)) : time % 60);
                    if (game.getTimeLeft() <= Config.nobreakbed_gametime) {
                        NoBreakBed.this.bre = true;
                        for (Player player : game.getPlayers()) {
                            if (!Config.nobreakbed_title.equals("") || !Config.nobreakbed_subtitle.equals(""))
                                Utils.sendTitle(player, 10, 50, 10, Config.nobreakbed_title,
                                        Config.nobreakbed_subtitle);
                            if (!Config.nobreakbed_message.equals(""))
                                player.sendMessage(Config.nobreakbed_message);
                        }
                        cancel();
                    }
                } else {
                    cancel();
                }
            }
        }).runTaskTimer(YaBedwars.getInstance(), 0L, 21L);
    }

    public String getTime() {
        return this.formattime;
    }

    public void onOver() {
        if (this.packetlistener != null)
            ProtocolLibrary.getProtocolManager().removePacketListener(this.packetlistener);
    }

    private void breakbed() {
        PacketAdapter packetAdapter = new PacketAdapter(YaBedwars.getInstance(), ListenerPriority.HIGHEST,
                PacketType.Play.Client.BLOCK_DIG) {
            public void onPacketReceiving(PacketEvent e) {
                if (!Config.nobreakbed_enabled)
                    return;
                Player player = e.getPlayer();
                if (NoBreakBed.this.game.isSpectator(player) || NoBreakBed.this.game.getState() != GameState.RUNNING)
                    return;
                if (!NoBreakBed.this.bre && e.getPacketType() == PacketType.Play.Client.BLOCK_DIG) {
                    PacketContainer packet = e.getPacket();
                    BlockPosition position = packet.getBlockPositionModifier().read(0);
                    Location location = new Location(player.getWorld(), position.getX(), position.getY(),
                            position.getZ());
                    Block block = location.getBlock();
                    if (!block.getType().equals(NoBreakBed.this.game.getTargetMaterial()))
                        return;
                    if (!packet.getPlayerDigTypes().read(0).equals(EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK))
                        return;
                    player.sendMessage(Config.nobreakbed_nobreakmessage);
                    e.setCancelled(true);
                    block.getState().update();
                }
            }
        };
        ProtocolLibrary.getProtocolManager().addPacketListener(packetAdapter);
        this.packetlistener = packetAdapter;
    }
}
