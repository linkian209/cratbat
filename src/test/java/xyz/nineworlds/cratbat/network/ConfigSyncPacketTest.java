package xyz.nineworlds.cratbat.network;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for ConfigSyncPacket.
 * Tests packet construction, getters, and null handling.
 * Note: encode/decode tests would require mocking FriendlyByteBuf which is complex
 * due to Minecraft's networking infrastructure.
 */
class ConfigSyncPacketTest {

    private static final String TEST_NAME = "TestPlayer";
    private static final String TEST_UUID = "12345678-1234-1234-1234-123456789012";
    private static final String TEST_TEXTURE = "http://textures.minecraft.net/texture/abc123";

    @Nested
    @DisplayName("Constructor tests")
    class ConstructorTests {

        @Test
        @DisplayName("Constructor creates packet with provided values")
        void constructor_createsPacketWithValues() {
            ConfigSyncPacket packet = new ConfigSyncPacket(TEST_NAME, TEST_UUID, TEST_TEXTURE);

            assertEquals(TEST_NAME, packet.getTargetPlayerName());
            assertEquals(TEST_UUID, packet.getTargetPlayerUUID());
            assertEquals(TEST_TEXTURE, packet.getTargetPlayerTexture());
        }

        @Test
        @DisplayName("Constructor handles null name by converting to empty string")
        void constructor_handlesNullName() {
            ConfigSyncPacket packet = new ConfigSyncPacket(null, TEST_UUID, TEST_TEXTURE);

            assertEquals("", packet.getTargetPlayerName());
        }

        @Test
        @DisplayName("Constructor handles null UUID by converting to empty string")
        void constructor_handlesNullUUID() {
            ConfigSyncPacket packet = new ConfigSyncPacket(TEST_NAME, null, TEST_TEXTURE);

            assertEquals("", packet.getTargetPlayerUUID());
        }

        @Test
        @DisplayName("Constructor handles null texture by converting to empty string")
        void constructor_handlesNullTexture() {
            ConfigSyncPacket packet = new ConfigSyncPacket(TEST_NAME, TEST_UUID, null);

            assertEquals("", packet.getTargetPlayerTexture());
        }

        @Test
        @DisplayName("Constructor handles all null values")
        void constructor_handlesAllNullValues() {
            ConfigSyncPacket packet = new ConfigSyncPacket(null, null, null);

            assertEquals("", packet.getTargetPlayerName());
            assertEquals("", packet.getTargetPlayerUUID());
            assertEquals("", packet.getTargetPlayerTexture());
        }
    }

    @Nested
    @DisplayName("Getter tests")
    class GetterTests {

        @Test
        @DisplayName("getTargetPlayerName returns correct value")
        void getTargetPlayerName_returnsCorrectValue() {
            ConfigSyncPacket packet = new ConfigSyncPacket(TEST_NAME, TEST_UUID, TEST_TEXTURE);

            assertEquals(TEST_NAME, packet.getTargetPlayerName());
        }

        @Test
        @DisplayName("getTargetPlayerUUID returns correct value")
        void getTargetPlayerUUID_returnsCorrectValue() {
            ConfigSyncPacket packet = new ConfigSyncPacket(TEST_NAME, TEST_UUID, TEST_TEXTURE);

            assertEquals(TEST_UUID, packet.getTargetPlayerUUID());
        }

        @Test
        @DisplayName("getTargetPlayerTexture returns correct value")
        void getTargetPlayerTexture_returnsCorrectValue() {
            ConfigSyncPacket packet = new ConfigSyncPacket(TEST_NAME, TEST_UUID, TEST_TEXTURE);

            assertEquals(TEST_TEXTURE, packet.getTargetPlayerTexture());
        }
    }

    @Nested
    @DisplayName("Empty value handling")
    class EmptyValueTests {

        @Test
        @DisplayName("Empty name is preserved")
        void emptyName_isPreserved() {
            ConfigSyncPacket packet = new ConfigSyncPacket("", TEST_UUID, TEST_TEXTURE);

            assertEquals("", packet.getTargetPlayerName());
        }

        @Test
        @DisplayName("Empty UUID is preserved")
        void emptyUUID_isPreserved() {
            ConfigSyncPacket packet = new ConfigSyncPacket(TEST_NAME, "", TEST_TEXTURE);

            assertEquals("", packet.getTargetPlayerUUID());
        }

        @Test
        @DisplayName("Empty texture URL is preserved")
        void emptyTexture_isPreserved() {
            ConfigSyncPacket packet = new ConfigSyncPacket(TEST_NAME, TEST_UUID, "");

            assertEquals("", packet.getTargetPlayerTexture());
        }
    }

    @Nested
    @DisplayName("Special character handling")
    class SpecialCharacterTests {

        @Test
        @DisplayName("Player name with special characters is preserved")
        void playerNameWithSpecialChars_isPreserved() {
            String specialName = "Player_123-Test";
            ConfigSyncPacket packet = new ConfigSyncPacket(specialName, TEST_UUID, TEST_TEXTURE);

            assertEquals(specialName, packet.getTargetPlayerName());
        }

        @Test
        @DisplayName("Long texture URL is preserved")
        void longTextureUrl_isPreserved() {
            String longUrl = "http://textures.minecraft.net/texture/" + "a".repeat(200);
            ConfigSyncPacket packet = new ConfigSyncPacket(TEST_NAME, TEST_UUID, longUrl);

            assertEquals(longUrl, packet.getTargetPlayerTexture());
        }

        @Test
        @DisplayName("UUID with standard format is preserved")
        void uuidWithStandardFormat_isPreserved() {
            String uuid = "550e8400-e29b-41d4-a716-446655440000";
            ConfigSyncPacket packet = new ConfigSyncPacket(TEST_NAME, uuid, TEST_TEXTURE);

            assertEquals(uuid, packet.getTargetPlayerUUID());
        }
    }

    @Nested
    @DisplayName("Packet immutability")
    class ImmutabilityTests {

        @Test
        @DisplayName("Packet values are not null after construction")
        void packetValues_notNullAfterConstruction() {
            ConfigSyncPacket packet = new ConfigSyncPacket(TEST_NAME, TEST_UUID, TEST_TEXTURE);

            assertNotNull(packet.getTargetPlayerName());
            assertNotNull(packet.getTargetPlayerUUID());
            assertNotNull(packet.getTargetPlayerTexture());
        }

        @Test
        @DisplayName("Multiple calls to getters return same values")
        void multipleGetterCalls_returnSameValues() {
            ConfigSyncPacket packet = new ConfigSyncPacket(TEST_NAME, TEST_UUID, TEST_TEXTURE);

            String name1 = packet.getTargetPlayerName();
            String name2 = packet.getTargetPlayerName();

            assertEquals(name1, name2);
        }
    }
}
