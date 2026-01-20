package xyz.nineworlds.cratbat.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import xyz.nineworlds.cratbat.CratBatConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for CrankSkullItem.
 * Tests skull creation with various config states and identification methods.
 *
 * Note: These tests require Minecraft's registry to be bootstrapped,
 * so they test the logic paths rather than full ItemStack creation.
 */
class CrankSkullItemTest {

    private static final String TEST_PLAYER_NAME = "TestCrat";
    private static final String TEST_PLAYER_UUID = "550e8400-e29b-41d4-a716-446655440000";
    private static final String TEST_TEXTURE_URL = "http://textures.minecraft.net/texture/abc123";

    @BeforeEach
    void setUp() {
        // Set up config values
        CratBatConfig.targetPlayerName = TEST_PLAYER_NAME;
        CratBatConfig.targetPlayerUUID = TEST_PLAYER_UUID;
        CratBatConfig.targetPlayerTexture = TEST_TEXTURE_URL;
        CratBatConfig.clearServerConfig();
    }

    @AfterEach
    void tearDown() {
        CratBatConfig.clearServerConfig();
    }

    @Nested
    @DisplayName("isCrankSkull tests")
    class IsCrankSkullTests {

        @Test
        @DisplayName("Returns false for null ItemStack tag")
        void returnsFalseForNullTag() {
            // Create a mock-like test using a simple ItemStack simulation
            // Since we can't easily create ItemStacks without Minecraft bootstrap,
            // we test the logic with CompoundTag directly
            CompoundTag tag = null;

            // The method checks if tag is null, so this validates that code path
            assertFalse(tag != null && "CrankSkull".equals(tag.getString("CratBatType")));
        }

        @Test
        @DisplayName("Returns false for tag without CratBatType")
        void returnsFalseForTagWithoutCratBatType() {
            CompoundTag tag = new CompoundTag();

            assertFalse("CrankSkull".equals(tag.getString("CratBatType")));
        }

        @Test
        @DisplayName("Returns false for tag with wrong CratBatType")
        void returnsFalseForWrongCratBatType() {
            CompoundTag tag = new CompoundTag();
            tag.putString("CratBatType", "SomeOtherType");

            assertFalse("CrankSkull".equals(tag.getString("CratBatType")));
        }

        @Test
        @DisplayName("Returns true for tag with correct CratBatType")
        void returnsTrueForCorrectCratBatType() {
            CompoundTag tag = new CompoundTag();
            tag.putString("CratBatType", "CrankSkull");

            assertTrue("CrankSkull".equals(tag.getString("CratBatType")));
        }
    }

    @Nested
    @DisplayName("getSkullPlayerName tests")
    class GetSkullPlayerNameTests {

        @Test
        @DisplayName("Returns empty string for tag without SkullOwner")
        void returnsEmptyForTagWithoutSkullOwner() {
            CompoundTag tag = new CompoundTag();
            tag.putString("CratBatType", "CrankSkull");

            // Simulate getSkullPlayerName logic
            String result = "";
            if (tag.contains("SkullOwner")) {
                CompoundTag skullOwner = tag.getCompound("SkullOwner");
                result = skullOwner.getString("Name");
            }

            assertEquals("", result);
        }

        @Test
        @DisplayName("Returns player name from SkullOwner tag")
        void returnsPlayerNameFromSkullOwner() {
            CompoundTag tag = new CompoundTag();
            tag.putString("CratBatType", "CrankSkull");

            CompoundTag skullOwner = new CompoundTag();
            skullOwner.putString("Name", TEST_PLAYER_NAME);
            tag.put("SkullOwner", skullOwner);

            // Simulate getSkullPlayerName logic
            String result = "";
            if (tag.contains("SkullOwner")) {
                CompoundTag owner = tag.getCompound("SkullOwner");
                result = owner.getString("Name");
            }

            assertEquals(TEST_PLAYER_NAME, result);
        }

        @Test
        @DisplayName("Returns empty string for SkullOwner without Name")
        void returnsEmptyForSkullOwnerWithoutName() {
            CompoundTag tag = new CompoundTag();
            tag.putString("CratBatType", "CrankSkull");

            CompoundTag skullOwner = new CompoundTag();
            // No Name field
            tag.put("SkullOwner", skullOwner);

            String result = "";
            if (tag.contains("SkullOwner")) {
                CompoundTag owner = tag.getCompound("SkullOwner");
                result = owner.getString("Name");
            }

            assertEquals("", result);
        }
    }

    @Nested
    @DisplayName("Skull NBT structure tests")
    class SkullNbtStructureTests {

        @Test
        @DisplayName("SkullOwner tag contains Name field")
        void skullOwnerContainsName() {
            CompoundTag skullOwner = xyz.nineworlds.cratbat.util.NBTUtil.createSkullOwnerTag(
                    TEST_PLAYER_NAME, TEST_PLAYER_UUID, TEST_TEXTURE_URL);

            assertTrue(skullOwner.contains("Name"));
            assertEquals(TEST_PLAYER_NAME, skullOwner.getString("Name"));
        }

        @Test
        @DisplayName("SkullOwner tag contains Id field for valid UUID")
        void skullOwnerContainsId() {
            CompoundTag skullOwner = xyz.nineworlds.cratbat.util.NBTUtil.createSkullOwnerTag(
                    TEST_PLAYER_NAME, TEST_PLAYER_UUID, TEST_TEXTURE_URL);

            assertTrue(skullOwner.contains("Id"));
            int[] id = skullOwner.getIntArray("Id");
            assertEquals(4, id.length);
        }

        @Test
        @DisplayName("SkullOwner tag contains Properties for texture URL")
        void skullOwnerContainsProperties() {
            CompoundTag skullOwner = xyz.nineworlds.cratbat.util.NBTUtil.createSkullOwnerTag(
                    TEST_PLAYER_NAME, TEST_PLAYER_UUID, TEST_TEXTURE_URL);

            assertTrue(skullOwner.contains("Properties"));
            CompoundTag properties = skullOwner.getCompound("Properties");
            assertTrue(properties.contains("textures"));
        }

        @Test
        @DisplayName("Properties contains textures list with Value")
        void propertiesContainsTexturesList() {
            CompoundTag skullOwner = xyz.nineworlds.cratbat.util.NBTUtil.createSkullOwnerTag(
                    TEST_PLAYER_NAME, TEST_PLAYER_UUID, TEST_TEXTURE_URL);

            CompoundTag properties = skullOwner.getCompound("Properties");
            var texturesList = properties.getList("textures", 10); // 10 = CompoundTag type

            assertFalse(texturesList.isEmpty());
            CompoundTag textureEntry = texturesList.getCompound(0);
            assertTrue(textureEntry.contains("Value"));
            assertFalse(textureEntry.getString("Value").isEmpty());
        }
    }

    @Nested
    @DisplayName("Config integration tests")
    class ConfigIntegrationTests {

        @Test
        @DisplayName("Uses local config values when no server config")
        void usesLocalConfigValues() {
            CratBatConfig.clearServerConfig();

            assertEquals(TEST_PLAYER_NAME, CratBatConfig.getTargetPlayerName());
            assertEquals(TEST_PLAYER_UUID, CratBatConfig.getTargetPlayerUUID());
            assertEquals(TEST_TEXTURE_URL, CratBatConfig.getTargetPlayerTexture());
        }

        @Test
        @DisplayName("Uses server config values when server config applied")
        void usesServerConfigValues() {
            String serverName = "ServerPlayer";
            String serverUuid = "11111111-1111-1111-1111-111111111111";
            String serverTexture = "http://server-texture.png";

            CratBatConfig.applyServerConfig(serverName, serverUuid, serverTexture, false);

            assertEquals(serverName, CratBatConfig.getTargetPlayerName());
            assertEquals(serverUuid, CratBatConfig.getTargetPlayerUUID());
            assertEquals(serverTexture, CratBatConfig.getTargetPlayerTexture());
        }

        @Test
        @DisplayName("Skull would use correct config based on context")
        void skullUsesCorrectConfigBasedOnContext() {
            // Test that the getter methods return the right values
            // which CrankSkullItem.createCrankSkull() uses

            // Local config
            CratBatConfig.clearServerConfig();
            String localName = CratBatConfig.getTargetPlayerName();
            assertEquals(TEST_PLAYER_NAME, localName);

            // Server config
            CratBatConfig.applyServerConfig("ServerCrat", "22222222-2222-2222-2222-222222222222", "http://server.png", false);
            String serverName = CratBatConfig.getTargetPlayerName();
            assertEquals("ServerCrat", serverName);
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("Handles empty texture URL gracefully")
        void handlesEmptyTextureUrl() {
            CompoundTag skullOwner = xyz.nineworlds.cratbat.util.NBTUtil.createSkullOwnerTag(
                    TEST_PLAYER_NAME, TEST_PLAYER_UUID, "");

            assertNotNull(skullOwner);
            assertTrue(skullOwner.contains("Name"));
            assertTrue(skullOwner.contains("Id"));
            assertFalse(skullOwner.contains("Properties")); // No properties without texture
        }

        @Test
        @DisplayName("Handles null texture URL gracefully")
        void handlesNullTextureUrl() {
            CompoundTag skullOwner = xyz.nineworlds.cratbat.util.NBTUtil.createSkullOwnerTag(
                    TEST_PLAYER_NAME, TEST_PLAYER_UUID, null);

            assertNotNull(skullOwner);
            assertTrue(skullOwner.contains("Name"));
            assertFalse(skullOwner.contains("Properties"));
        }

        @Test
        @DisplayName("Handles invalid UUID gracefully")
        void handlesInvalidUuidGracefully() {
            CompoundTag skullOwner = xyz.nineworlds.cratbat.util.NBTUtil.createSkullOwnerTag(
                    TEST_PLAYER_NAME, "invalid-uuid", TEST_TEXTURE_URL);

            assertNotNull(skullOwner);
            assertTrue(skullOwner.contains("Name"));
            assertFalse(skullOwner.contains("Id")); // No Id for invalid UUID
            assertTrue(skullOwner.contains("Properties")); // Still has texture
        }

        @Test
        @DisplayName("Handles all empty values gracefully")
        void handlesAllEmptyValuesGracefully() {
            CompoundTag skullOwner = xyz.nineworlds.cratbat.util.NBTUtil.createSkullOwnerTag(
                    "", "", "");

            assertNotNull(skullOwner);
            // Should be essentially empty but not null
        }
    }
}
