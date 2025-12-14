package xyz.nineworlds.cratbat.network;

import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;
import xyz.nineworlds.cratbat.CratBatConfig;

import java.util.function.Supplier;

/**
 * Network packet for synchronizing CratBat configuration from server to client.
 * Contains target player information that clients need to display correct textures
 * and validate game mechanics.
 */
public class ConfigSyncPacket {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final String targetPlayerName;
    private final String targetPlayerUUID;
    private final String targetPlayerTexture;

    /**
     * Creates a new ConfigSyncPacket with the specified values.
     *
     * @param targetPlayerName    The name of the target player
     * @param targetPlayerUUID    The UUID of the target player
     * @param targetPlayerTexture The texture URL for the target player's skin
     */
    public ConfigSyncPacket(String targetPlayerName, String targetPlayerUUID, String targetPlayerTexture) {
        this.targetPlayerName = targetPlayerName != null ? targetPlayerName : "";
        this.targetPlayerUUID = targetPlayerUUID != null ? targetPlayerUUID : "";
        this.targetPlayerTexture = targetPlayerTexture != null ? targetPlayerTexture : "";
    }

    /**
     * Creates a ConfigSyncPacket from the current server configuration.
     *
     * @return A new packet containing the server's config values
     */
    public static ConfigSyncPacket fromServerConfig() {
        return new ConfigSyncPacket(
                CratBatConfig.targetPlayerName,
                CratBatConfig.targetPlayerUUID,
                CratBatConfig.targetPlayerTexture
        );
    }

    /**
     * Encodes this packet into a byte buffer for network transmission.
     *
     * @param buf The buffer to write to
     */
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(targetPlayerName);
        buf.writeUtf(targetPlayerUUID);
        buf.writeUtf(targetPlayerTexture);
    }

    /**
     * Decodes a ConfigSyncPacket from a byte buffer.
     *
     * @param buf The buffer to read from
     * @return The decoded packet
     */
    public static ConfigSyncPacket decode(FriendlyByteBuf buf) {
        String name = buf.readUtf();
        String uuid = buf.readUtf();
        String texture = buf.readUtf();
        return new ConfigSyncPacket(name, uuid, texture);
    }

    /**
     * Handles receiving this packet on the client side.
     * Applies the server configuration to override local settings.
     *
     * @param ctx The network context supplier
     */
    public static void handle(ConfigSyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LOGGER.debug("Received config sync from server: targetPlayer={}, uuid={}, hasTexture={}",
                    packet.targetPlayerName, packet.targetPlayerUUID, !packet.targetPlayerTexture.isEmpty());

            CratBatConfig.applyServerConfig(packet.targetPlayerName, packet.targetPlayerUUID, packet.targetPlayerTexture);
        });
        ctx.get().setPacketHandled(true);
    }

    public String getTargetPlayerName() {
        return targetPlayerName;
    }

    public String getTargetPlayerUUID() {
        return targetPlayerUUID;
    }

    public String getTargetPlayerTexture() {
        return targetPlayerTexture;
    }
}