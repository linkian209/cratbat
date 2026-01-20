package xyz.nineworlds.cratbat;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.slf4j.Logger;

/**
 * Configuration for the CratBat mod.
 * Supports server-side config synchronization - when connected to a server,
 * server values override local config values.
 */
@Mod.EventBusSubscriber(modid = CratBatMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CratBatConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.ConfigValue<String> TARGET_PLAYER_NAME = BUILDER
            .comment("Name of the player who can be 'swatted' by the CratBat")
            .define("targetPlayerName", "TargetPlayerName");

    private static final ForgeConfigSpec.ConfigValue<String> TARGET_PLAYER_UUID = BUILDER
            .comment("UUID of the target player (more reliable than name)")
            .define("targetPlayerUUID", "00000000-0000-0000-0000-000000000000");

    private static final ForgeConfigSpec.ConfigValue<String> TARGET_PLAYER_TEXTURE = BUILDER
            .comment("Texture URL for the target player's skin (used for Crank Skull)")
            .define("targetPlayerTexture", "");

    private static final ForgeConfigSpec.BooleanValue ENABLE_TEST_CRAT = BUILDER
            .comment("Enable the TestCrat entity and spawner item for testing the CratBat in singleplayer.",
                     "Requires a game restart to take effect.")
            .define("enableTestCrat", false);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    // Local config values (from file)
    public static String targetPlayerName;
    public static String targetPlayerUUID;
    public static String targetPlayerTexture;
    public static boolean enableTestCrat;

    // Server override values
    private static String serverTargetPlayerName;
    private static String serverTargetPlayerUUID;
    private static String serverTargetPlayerTexture;
    private static boolean serverEnableTestCrat;
    private static boolean hasServerConfig = false;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        targetPlayerName = TARGET_PLAYER_NAME.get();
        targetPlayerUUID = TARGET_PLAYER_UUID.get();
        targetPlayerTexture = TARGET_PLAYER_TEXTURE.get();
        enableTestCrat = ENABLE_TEST_CRAT.get();
        LOGGER.debug("CratBat config loaded: targetPlayer={}, uuid={}, enableTestCrat={}", targetPlayerName, targetPlayerUUID, enableTestCrat);
    }

    /**
     * Checks if the TestCrat entity should be enabled.
     * Prefers server config if available (for runtime checks like JEI visibility).
     * Falls back to cached local config value.
     *
     * @return true if TestCrat entity and spawner should be enabled
     */
    public static boolean isTestCratEnabled() {
        return hasServerConfig ? serverEnableTestCrat : enableTestCrat;
    }

    /**
     * Checks if TestCrat is enabled by reading directly from the config spec.
     * Used during mod initialization when cached values may not be set yet.
     *
     * @return true if TestCrat entity and spawner should be registered
     */
    public static boolean isTestCratEnabledFromSpec() {
        return ENABLE_TEST_CRAT.get();
    }

    /**
     * Gets the target player name, preferring server config if available.
     *
     * @return The target player name
     */
    public static String getTargetPlayerName() {
        return hasServerConfig ? serverTargetPlayerName : targetPlayerName;
    }

    /**
     * Gets the target player UUID, preferring server config if available.
     *
     * @return The target player UUID
     */
    public static String getTargetPlayerUUID() {
        return hasServerConfig ? serverTargetPlayerUUID : targetPlayerUUID;
    }

    /**
     * Gets the target player texture URL, preferring server config if available.
     *
     * @return The target player texture URL
     */
    public static String getTargetPlayerTexture() {
        return hasServerConfig ? serverTargetPlayerTexture : targetPlayerTexture;
    }

    /**
     * Checks if server configuration is currently active.
     *
     * @return true if server config overrides are in effect
     */
    public static boolean hasServerConfig() {
        return hasServerConfig;
    }

    /**
     * Applies server configuration received from a multiplayer server.
     * These values will override local config until cleared.
     *
     * @param name           The target player name from the server
     * @param uuid           The target player UUID from the server
     * @param texture        The target player texture URL from the server
     * @param enableTestCrat Whether the TestCrat entity is enabled on the server
     */
    public static void applyServerConfig(String name, String uuid, String texture, boolean enableTestCrat) {
        serverTargetPlayerName = name;
        serverTargetPlayerUUID = uuid;
        serverTargetPlayerTexture = texture;
        serverEnableTestCrat = enableTestCrat;
        hasServerConfig = true;
        LOGGER.info("Applied server config: targetPlayer={}, uuid={}, hasTexture={}, enableTestCrat={}",
                name, uuid, texture != null && !texture.isEmpty(), enableTestCrat);
        updateJeiVisibility();
    }

    /**
     * Clears server configuration overrides, reverting to local config.
     * Should be called when disconnecting from a server.
     */
    public static void clearServerConfig() {
        serverTargetPlayerName = null;
        serverTargetPlayerUUID = null;
        serverTargetPlayerTexture = null;
        serverEnableTestCrat = false;
        hasServerConfig = false;
        LOGGER.info("Cleared server config, reverting to local config");
        updateJeiVisibility();
    }

    /**
     * Updates the local config values programmatically.
     * Used by the server command system to persist changes.
     *
     * @param name    The new target player name
     * @param uuid    The new target player UUID
     * @param texture The new target player texture URL
     */
    public static void updateLocalConfig(String name, String uuid, String texture) {
        TARGET_PLAYER_NAME.set(name);
        TARGET_PLAYER_UUID.set(uuid);
        TARGET_PLAYER_TEXTURE.set(texture);
        // Update cached values
        targetPlayerName = name;
        targetPlayerUUID = uuid;
        targetPlayerTexture = texture;
        LOGGER.info("Updated local config: targetPlayer={}, uuid={}", name, uuid);
    }

    /**
     * Safely updates JEI visibility for the TestCrat spawner.
     * Only calls JEI if the mod is loaded.
     */
    private static void updateJeiVisibility() {
        try {
            if (ModList.get() != null && ModList.get().isLoaded("jei")) {
                xyz.nineworlds.cratbat.integration.jei.CratBatJEIPlugin.updateTestCratVisibility();
            }
        } catch (Exception e) {
            LOGGER.debug("Failed to update JEI visibility: {}", e.getMessage());
        }
    }
}