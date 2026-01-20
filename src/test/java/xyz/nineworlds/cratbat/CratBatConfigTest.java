package xyz.nineworlds.cratbat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for CratBatConfig server override functionality.
 * Tests the getter methods and server config application/clearing.
 */
class CratBatConfigTest {

    private static final String LOCAL_NAME = "LocalPlayer";
    private static final String LOCAL_UUID = "local-uuid-1234";
    private static final String LOCAL_TEXTURE = "http://local-texture.png";

    private static final String SERVER_NAME = "ServerPlayer";
    private static final String SERVER_UUID = "server-uuid-5678";
    private static final String SERVER_TEXTURE = "http://server-texture.png";

    @BeforeEach
    void setUp() {
        // Set up local config values (simulating what would be loaded from file)
        CratBatConfig.targetPlayerName = LOCAL_NAME;
        CratBatConfig.targetPlayerUUID = LOCAL_UUID;
        CratBatConfig.targetPlayerTexture = LOCAL_TEXTURE;

        // Ensure no server config is active
        CratBatConfig.clearServerConfig();
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        CratBatConfig.clearServerConfig();
    }

    @Nested
    @DisplayName("Getter methods without server config")
    class GettersWithoutServerConfig {

        @Test
        @DisplayName("getTargetPlayerName returns local value when no server config")
        void getTargetPlayerName_returnsLocalValue() {
            assertEquals(LOCAL_NAME, CratBatConfig.getTargetPlayerName());
        }

        @Test
        @DisplayName("getTargetPlayerUUID returns local value when no server config")
        void getTargetPlayerUUID_returnsLocalValue() {
            assertEquals(LOCAL_UUID, CratBatConfig.getTargetPlayerUUID());
        }

        @Test
        @DisplayName("getTargetPlayerTexture returns local value when no server config")
        void getTargetPlayerTexture_returnsLocalValue() {
            assertEquals(LOCAL_TEXTURE, CratBatConfig.getTargetPlayerTexture());
        }

        @Test
        @DisplayName("hasServerConfig returns false when no server config applied")
        void hasServerConfig_returnsFalse() {
            assertFalse(CratBatConfig.hasServerConfig());
        }
    }

    @Nested
    @DisplayName("Getter methods with server config")
    class GettersWithServerConfig {

        @BeforeEach
        void applyServerConfig() {
            CratBatConfig.applyServerConfig(SERVER_NAME, SERVER_UUID, SERVER_TEXTURE, false);
        }

        @Test
        @DisplayName("getTargetPlayerName returns server value when server config applied")
        void getTargetPlayerName_returnsServerValue() {
            assertEquals(SERVER_NAME, CratBatConfig.getTargetPlayerName());
        }

        @Test
        @DisplayName("getTargetPlayerUUID returns server value when server config applied")
        void getTargetPlayerUUID_returnsServerValue() {
            assertEquals(SERVER_UUID, CratBatConfig.getTargetPlayerUUID());
        }

        @Test
        @DisplayName("getTargetPlayerTexture returns server value when server config applied")
        void getTargetPlayerTexture_returnsServerValue() {
            assertEquals(SERVER_TEXTURE, CratBatConfig.getTargetPlayerTexture());
        }

        @Test
        @DisplayName("hasServerConfig returns true when server config applied")
        void hasServerConfig_returnsTrue() {
            assertTrue(CratBatConfig.hasServerConfig());
        }
    }

    @Nested
    @DisplayName("applyServerConfig behavior")
    class ApplyServerConfigTests {

        @Test
        @DisplayName("applyServerConfig overrides all three values")
        void applyServerConfig_overridesAllValues() {
            CratBatConfig.applyServerConfig(SERVER_NAME, SERVER_UUID, SERVER_TEXTURE, false);

            assertEquals(SERVER_NAME, CratBatConfig.getTargetPlayerName());
            assertEquals(SERVER_UUID, CratBatConfig.getTargetPlayerUUID());
            assertEquals(SERVER_TEXTURE, CratBatConfig.getTargetPlayerTexture());
        }

        @Test
        @DisplayName("applyServerConfig sets hasServerConfig to true")
        void applyServerConfig_setsHasServerConfigTrue() {
            assertFalse(CratBatConfig.hasServerConfig());

            CratBatConfig.applyServerConfig(SERVER_NAME, SERVER_UUID, SERVER_TEXTURE, false);

            assertTrue(CratBatConfig.hasServerConfig());
        }

        @Test
        @DisplayName("applyServerConfig does not modify local config values")
        void applyServerConfig_doesNotModifyLocalValues() {
            CratBatConfig.applyServerConfig(SERVER_NAME, SERVER_UUID, SERVER_TEXTURE, false);

            // Local values should remain unchanged
            assertEquals(LOCAL_NAME, CratBatConfig.targetPlayerName);
            assertEquals(LOCAL_UUID, CratBatConfig.targetPlayerUUID);
            assertEquals(LOCAL_TEXTURE, CratBatConfig.targetPlayerTexture);
        }

        @Test
        @DisplayName("applyServerConfig can be called multiple times")
        void applyServerConfig_canBeCalledMultipleTimes() {
            CratBatConfig.applyServerConfig("First", "first-uuid", "first-texture", false);
            assertEquals("First", CratBatConfig.getTargetPlayerName());

            CratBatConfig.applyServerConfig("Second", "second-uuid", "second-texture", true);
            assertEquals("Second", CratBatConfig.getTargetPlayerName());
        }
    }

    @Nested
    @DisplayName("clearServerConfig behavior")
    class ClearServerConfigTests {

        @Test
        @DisplayName("clearServerConfig reverts to local values")
        void clearServerConfig_revertsToLocalValues() {
            CratBatConfig.applyServerConfig(SERVER_NAME, SERVER_UUID, SERVER_TEXTURE, false);
            assertEquals(SERVER_NAME, CratBatConfig.getTargetPlayerName());

            CratBatConfig.clearServerConfig();

            assertEquals(LOCAL_NAME, CratBatConfig.getTargetPlayerName());
            assertEquals(LOCAL_UUID, CratBatConfig.getTargetPlayerUUID());
            assertEquals(LOCAL_TEXTURE, CratBatConfig.getTargetPlayerTexture());
        }

        @Test
        @DisplayName("clearServerConfig sets hasServerConfig to false")
        void clearServerConfig_setsHasServerConfigFalse() {
            CratBatConfig.applyServerConfig(SERVER_NAME, SERVER_UUID, SERVER_TEXTURE, false);
            assertTrue(CratBatConfig.hasServerConfig());

            CratBatConfig.clearServerConfig();

            assertFalse(CratBatConfig.hasServerConfig());
        }

        @Test
        @DisplayName("clearServerConfig is safe to call when no server config exists")
        void clearServerConfig_safeWhenNoServerConfig() {
            assertFalse(CratBatConfig.hasServerConfig());

            // Should not throw
            CratBatConfig.clearServerConfig();

            assertFalse(CratBatConfig.hasServerConfig());
            assertEquals(LOCAL_NAME, CratBatConfig.getTargetPlayerName());
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("Empty texture URL is handled correctly")
        void emptyTextureUrl_handledCorrectly() {
            CratBatConfig.applyServerConfig(SERVER_NAME, SERVER_UUID, "", false);

            assertEquals("", CratBatConfig.getTargetPlayerTexture());
        }

        @Test
        @DisplayName("Null local values are handled by getters")
        void nullLocalValues_handledByGetters() {
            CratBatConfig.targetPlayerName = null;
            CratBatConfig.targetPlayerUUID = null;
            CratBatConfig.targetPlayerTexture = null;

            // Without server config, getters return null (matching local behavior)
            assertEquals(null, CratBatConfig.getTargetPlayerName());
            assertEquals(null, CratBatConfig.getTargetPlayerUUID());
            assertEquals(null, CratBatConfig.getTargetPlayerTexture());
        }

        @Test
        @DisplayName("Server config with empty name still overrides")
        void serverConfigWithEmptyName_stillOverrides() {
            CratBatConfig.applyServerConfig("", SERVER_UUID, SERVER_TEXTURE, false);

            assertTrue(CratBatConfig.hasServerConfig());
            assertEquals("", CratBatConfig.getTargetPlayerName());
        }

        @Test
        @DisplayName("Server config enableTestCrat true overrides local false")
        void serverConfigEnableTestCrat_overridesLocal() {
            CratBatConfig.enableTestCrat = false;

            CratBatConfig.applyServerConfig(SERVER_NAME, SERVER_UUID, SERVER_TEXTURE, true);

            assertTrue(CratBatConfig.isTestCratEnabled());
        }

        @Test
        @DisplayName("Server config enableTestCrat false overrides local true")
        void serverConfigEnableTestCratFalse_overridesLocalTrue() {
            CratBatConfig.enableTestCrat = true;

            CratBatConfig.applyServerConfig(SERVER_NAME, SERVER_UUID, SERVER_TEXTURE, false);

            assertFalse(CratBatConfig.isTestCratEnabled());
        }

        @Test
        @DisplayName("clearServerConfig reverts enableTestCrat to local value")
        void clearServerConfig_revertsEnableTestCrat() {
            CratBatConfig.enableTestCrat = true;
            CratBatConfig.applyServerConfig(SERVER_NAME, SERVER_UUID, SERVER_TEXTURE, false);
            assertFalse(CratBatConfig.isTestCratEnabled());

            CratBatConfig.clearServerConfig();

            assertTrue(CratBatConfig.isTestCratEnabled());
        }
    }
}
