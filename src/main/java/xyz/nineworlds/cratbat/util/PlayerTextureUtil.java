package xyz.nineworlds.cratbat.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.Base64;
import java.util.Collection;
import java.util.UUID;

/**
 * Utility class for extracting player texture URLs from GameProfiles.
 * Used to get the skin texture URL for the Crank Skull item.
 */
public class PlayerTextureUtil {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String TEXTURES_PROPERTY = "textures";

    /**
     * Extracts the skin texture URL from a player's GameProfile.
     *
     * @param profile The player's GameProfile
     * @return The texture URL, or empty string if not found
     */
    public static String getTextureUrl(GameProfile profile) {
        if (profile == null) {
            return "";
        }

        try {
            Collection<Property> textures = profile.getProperties().get(TEXTURES_PROPERTY);
            if (textures == null || textures.isEmpty()) {
                LOGGER.debug("No texture properties found for player {}", profile.getName());
                return getFallbackTextureUrl(profile.getId());
            }

            for (Property property : textures) {
                String textureUrl = extractUrlFromProperty(property);
                if (!textureUrl.isEmpty()) {
                    return textureUrl;
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to extract texture URL for player {}: {}", profile.getName(), e.getMessage());
        }

        return getFallbackTextureUrl(profile.getId());
    }

    /**
     * Extracts the texture URL from a base64-encoded texture property.
     *
     * @param property The texture property
     * @return The texture URL, or empty string if extraction fails
     */
    private static String extractUrlFromProperty(Property property) {
        try {
            String value = property.getValue();
            if (value == null || value.isEmpty()) {
                return "";
            }

            // Decode base64
            byte[] decodedBytes = Base64.getDecoder().decode(value);
            String decodedJson = new String(decodedBytes);

            // Parse JSON
            JsonObject json = JsonParser.parseString(decodedJson).getAsJsonObject();
            if (!json.has("textures")) {
                return "";
            }

            JsonObject texturesObj = json.getAsJsonObject("textures");
            if (!texturesObj.has("SKIN")) {
                return "";
            }

            JsonObject skinObj = texturesObj.getAsJsonObject("SKIN");
            if (!skinObj.has("url")) {
                return "";
            }

            return skinObj.get("url").getAsString();
        } catch (Exception e) {
            LOGGER.debug("Failed to parse texture property: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Generates a fallback texture URL using Crafatar API.
     * This is used when the player's texture property is not available.
     *
     * @param uuid The player's UUID
     * @return The Crafatar skin URL, or empty string if UUID is null
     */
    public static String getFallbackTextureUrl(UUID uuid) {
        if (uuid == null) {
            return "";
        }
        // Crafatar provides player skins by UUID
        return "https://crafatar.com/skins/" + uuid.toString();
    }

    /**
     * Extracts texture URL from a player's UUID by using the fallback method.
     * This is useful when only the UUID is available.
     *
     * @param uuid The player's UUID as a string
     * @return The texture URL, or empty string if invalid
     */
    public static String getTextureUrlFromUUID(String uuid) {
        if (uuid == null || uuid.isEmpty()) {
            return "";
        }

        try {
            UUID playerUuid = UUID.fromString(uuid);
            return getFallbackTextureUrl(playerUuid);
        } catch (IllegalArgumentException e) {
            LOGGER.debug("Invalid UUID format: {}", uuid);
            return "";
        }
    }
}
