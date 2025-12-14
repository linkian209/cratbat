package xyz.nineworlds.cratbat.item;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import xyz.nineworlds.cratbat.CratBatConfig;
import xyz.nineworlds.cratbat.util.NBTUtil;

/**
 * Utility class for creating and identifying Crank Skull items.
 * The Crank Skull is a player head that displays the target player's skin,
 * using the texture URL stored in config for reliable rendering.
 */
public class CrankSkullItem {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String CRAT_BAT_TYPE_TAG = "CratBatType";
    private static final String CRANK_SKULL_TYPE = "CrankSkull";

    /**
     * Creates a Crank Skull item with the target player's appearance.
     * Uses the texture URL from config if available, otherwise falls back
     * to name-based lookup.
     *
     * @return An ItemStack containing a player head with the target player's skin
     */
    public static ItemStack createCrankSkull() {
        ItemStack skull = new ItemStack(Items.PLAYER_HEAD);
        CompoundTag tag = skull.getOrCreateTag();

        String playerName = CratBatConfig.getTargetPlayerName();
        String playerUuid = CratBatConfig.getTargetPlayerUUID();
        String textureUrl = CratBatConfig.getTargetPlayerTexture();

        LOGGER.debug("Creating Crank Skull for {} (UUID: {}, hasTexture: {})",
                playerName, playerUuid, !textureUrl.isEmpty());

        // Create SkullOwner with full texture data
        CompoundTag skullOwner = NBTUtil.createSkullOwnerTag(playerName, playerUuid, textureUrl);
        tag.put("SkullOwner", skullOwner);

        // Add custom identification tag
        tag.putString(CRAT_BAT_TYPE_TAG, CRANK_SKULL_TYPE);

        return skull;
    }

    /**
     * Checks if an ItemStack is a Crank Skull.
     *
     * @param stack The ItemStack to check
     * @return true if the item is a Crank Skull
     */
    public static boolean isCrankSkull(ItemStack stack) {
        if (stack.getItem() != Items.PLAYER_HEAD) {
            return false;
        }

        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return false;
        }

        return CRANK_SKULL_TYPE.equals(tag.getString(CRAT_BAT_TYPE_TAG));
    }

    /**
     * Gets the player name from a Crank Skull's NBT data.
     *
     * @param stack The Crank Skull ItemStack
     * @return The player name, or empty string if not found
     */
    public static String getSkullPlayerName(ItemStack stack) {
        if (!isCrankSkull(stack)) {
            return "";
        }

        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("SkullOwner")) {
            return "";
        }

        CompoundTag skullOwner = tag.getCompound("SkullOwner");
        return skullOwner.getString("Name");
    }
}
