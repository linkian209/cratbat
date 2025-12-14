package xyz.nineworlds.cratbat.util;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import org.slf4j.Logger;

import java.util.Base64;
import java.util.UUID;

/**
 * Utility class for creating NBT structures for player heads/skulls.
 * Handles UUID conversion and texture property encoding.
 */
public class NBTUtil {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Converts a UUID to an int array (4 integers) for NBT storage.
     * Minecraft stores UUIDs as int arrays in NBT data.
     *
     * @param uuid The UUID to convert
     * @return An int array of 4 elements representing the UUID
     */
    public static int[] uuidToIntArray(UUID uuid) {
        long mostSig = uuid.getMostSignificantBits();
        long leastSig = uuid.getLeastSignificantBits();

        return new int[]{
                (int) (mostSig >> 32),
                (int) mostSig,
                (int) (leastSig >> 32),
                (int) leastSig
        };
    }

    /**
     * Converts a UUID string to an int array for NBT storage.
     *
     * @param uuidString The UUID string to convert
     * @return An int array of 4 elements, or null if parsing fails
     */
    public static int[] uuidToIntArray(String uuidString) {
        if (uuidString == null || uuidString.isEmpty()) {
            return null;
        }

        try {
            UUID uuid = UUID.fromString(uuidString);
            return uuidToIntArray(uuid);
        } catch (IllegalArgumentException e) {
            LOGGER.debug("Failed to parse UUID: {}", uuidString);
            return null;
        }
    }

    /**
     * Creates a Base64-encoded texture value JSON string for skull NBT.
     * This is the format Minecraft uses for custom skull textures.
     *
     * @param textureUrl The URL of the skin texture
     * @return Base64-encoded JSON string for the texture property
     */
    public static String createTextureValue(String textureUrl) {
        if (textureUrl == null || textureUrl.isEmpty()) {
            return "";
        }

        // Create the JSON structure that Minecraft expects
        String json = "{\"textures\":{\"SKIN\":{\"url\":\"" + textureUrl + "\"}}}";

        // Base64 encode it
        return Base64.getEncoder().encodeToString(json.getBytes());
    }

    /**
     * Creates a complete SkullOwner NBT tag with name, UUID, and texture.
     * This structure allows the skull to display the correct skin without
     * needing to look up the player by name.
     *
     * @param playerName   The player's name
     * @param playerUuid   The player's UUID as a string
     * @param textureUrl   The URL of the player's skin texture
     * @return A CompoundTag containing the complete SkullOwner data
     */
    public static CompoundTag createSkullOwnerTag(String playerName, String playerUuid, String textureUrl) {
        CompoundTag skullOwner = new CompoundTag();

        // Always add the name
        if (playerName != null && !playerName.isEmpty()) {
            skullOwner.putString("Name", playerName);
        }

        // Add UUID as int array if valid
        int[] uuidArray = uuidToIntArray(playerUuid);
        if (uuidArray != null) {
            skullOwner.putIntArray("Id", uuidArray);
        }

        // Add texture properties if we have a texture URL
        if (textureUrl != null && !textureUrl.isEmpty()) {
            String textureValue = createTextureValue(textureUrl);
            if (!textureValue.isEmpty()) {
                CompoundTag properties = new CompoundTag();
                ListTag texturesList = new ListTag();

                CompoundTag textureEntry = new CompoundTag();
                textureEntry.putString("Value", textureValue);
                texturesList.add(textureEntry);

                properties.put("textures", texturesList);
                skullOwner.put("Properties", properties);
            }
        }

        return skullOwner;
    }

    /**
     * Creates a SkullOwner tag using a random UUID.
     * Useful when we have a texture URL but no actual player UUID.
     *
     * @param playerName The player's name
     * @param textureUrl The URL of the skin texture
     * @return A CompoundTag containing the SkullOwner data with a random UUID
     */
    public static CompoundTag createSkullOwnerTagWithRandomUuid(String playerName, String textureUrl) {
        UUID randomUuid = UUID.randomUUID();
        return createSkullOwnerTag(playerName, randomUuid.toString(), textureUrl);
    }
}
