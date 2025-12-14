package xyz.nineworlds.cratbat.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;
import xyz.nineworlds.cratbat.CratBatMod;

import java.util.Optional;

/**
 * Handles network communication between server and clients for the CratBat mod.
 * Responsible for syncing configuration from server to clients.
 */
public class CratBatNetwork {
    private static final String PROTOCOL_VERSION = "1";

    private static SimpleChannel CHANNEL;

    /**
     * Registers the network channel and all packets.
     * Must be called during mod initialization (FMLCommonSetupEvent).
     */
    public static void register() {
        CHANNEL = NetworkRegistry.newSimpleChannel(
                ResourceLocation.fromNamespaceAndPath(CratBatMod.MODID, "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );

        CHANNEL.registerMessage(0, ConfigSyncPacket.class,
                ConfigSyncPacket::encode,
                ConfigSyncPacket::decode,
                ConfigSyncPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    /**
     * Sends a configuration sync packet to a specific player.
     *
     * @param player The player to send the config to
     */
    public static void sendToPlayer(ServerPlayer player) {
        if (CHANNEL != null && player != null) {
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), ConfigSyncPacket.fromServerConfig());
        }
    }

    /**
     * Broadcasts the current configuration to all connected players.
     * Used when an admin changes the config via command.
     */
    public static void broadcastConfigToAll() {
        if (CHANNEL != null && ServerLifecycleHooks.getCurrentServer() != null) {
            ConfigSyncPacket packet = ConfigSyncPacket.fromServerConfig();
            CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
        }
    }
}