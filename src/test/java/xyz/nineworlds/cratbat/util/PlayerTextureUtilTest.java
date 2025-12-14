package xyz.nineworlds.cratbat.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for PlayerTextureUtil.
 * Note: Tests for getTextureUrl(GameProfile) would require mocking
 * Mojang's authlib which is complex. These tests focus on the
 * fallback and UUID-based methods.
 */
class PlayerTextureUtilTest {

    private static final String VALID_UUID = "550e8400-e29b-41d4-a716-446655440000";
    private static final UUID VALID_UUID_OBJ = UUID.fromString(VALID_UUID);

    @Nested
    @DisplayName("getFallbackTextureUrl tests")
    class GetFallbackTextureUrlTests {

        @Test
        @DisplayName("Returns Crafatar URL for valid UUID")
        void returnsCrafatarUrlForValidUuid() {
            String result = PlayerTextureUtil.getFallbackTextureUrl(VALID_UUID_OBJ);

            assertTrue(result.startsWith("https://crafatar.com/skins/"));
            assertTrue(result.contains(VALID_UUID));
        }

        @Test
        @DisplayName("Returns empty string for null UUID")
        void returnsEmptyStringForNullUuid() {
            String result = PlayerTextureUtil.getFallbackTextureUrl(null);

            assertEquals("", result);
        }

        @Test
        @DisplayName("URL contains the exact UUID string")
        void urlContainsExactUuidString() {
            String result = PlayerTextureUtil.getFallbackTextureUrl(VALID_UUID_OBJ);

            assertEquals("https://crafatar.com/skins/" + VALID_UUID, result);
        }
    }

    @Nested
    @DisplayName("getTextureUrlFromUUID tests")
    class GetTextureUrlFromUUIDTests {

        @Test
        @DisplayName("Returns URL for valid UUID string")
        void returnsUrlForValidUuidString() {
            String result = PlayerTextureUtil.getTextureUrlFromUUID(VALID_UUID);

            assertFalse(result.isEmpty());
            assertTrue(result.contains(VALID_UUID));
        }

        @Test
        @DisplayName("Returns empty string for null UUID string")
        void returnsEmptyStringForNullUuidString() {
            String result = PlayerTextureUtil.getTextureUrlFromUUID(null);

            assertEquals("", result);
        }

        @Test
        @DisplayName("Returns empty string for empty UUID string")
        void returnsEmptyStringForEmptyUuidString() {
            String result = PlayerTextureUtil.getTextureUrlFromUUID("");

            assertEquals("", result);
        }

        @Test
        @DisplayName("Returns empty string for invalid UUID format")
        void returnsEmptyStringForInvalidUuidFormat() {
            String result = PlayerTextureUtil.getTextureUrlFromUUID("not-a-valid-uuid");

            assertEquals("", result);
        }

        @Test
        @DisplayName("Returns empty string for malformed UUID")
        void returnsEmptyStringForMalformedUuid() {
            String result = PlayerTextureUtil.getTextureUrlFromUUID("12345");

            assertEquals("", result);
        }

        @Test
        @DisplayName("Handles UUID without dashes gracefully")
        void handlesUuidWithoutDashesGracefully() {
            // UUID.fromString requires dashes, so this should return empty
            String result = PlayerTextureUtil.getTextureUrlFromUUID("550e8400e29b41d4a716446655440000");

            assertEquals("", result);
        }

        @Test
        @DisplayName("Works with different valid UUIDs")
        void worksWithDifferentValidUuids() {
            String uuid1 = "00000000-0000-0000-0000-000000000000";
            String uuid2 = "ffffffff-ffff-ffff-ffff-ffffffffffff";

            String result1 = PlayerTextureUtil.getTextureUrlFromUUID(uuid1);
            String result2 = PlayerTextureUtil.getTextureUrlFromUUID(uuid2);

            assertTrue(result1.contains(uuid1));
            assertTrue(result2.contains(uuid2));
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("Whitespace-only UUID returns empty string")
        void whitespaceOnlyUuidReturnsEmpty() {
            String result = PlayerTextureUtil.getTextureUrlFromUUID("   ");

            assertEquals("", result);
        }

        @Test
        @DisplayName("UUID with extra whitespace returns empty string")
        void uuidWithExtraWhitespaceReturnsEmpty() {
            String result = PlayerTextureUtil.getTextureUrlFromUUID(" " + VALID_UUID + " ");

            // UUID.fromString doesn't trim, so this should fail
            assertEquals("", result);
        }
    }
}
