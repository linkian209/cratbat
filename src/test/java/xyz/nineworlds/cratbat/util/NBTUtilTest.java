package xyz.nineworlds.cratbat.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for NBTUtil.
 * Tests UUID conversion, texture value creation, and skull owner tag generation.
 */
class NBTUtilTest {

    private static final String VALID_UUID_STRING = "550e8400-e29b-41d4-a716-446655440000";
    private static final UUID VALID_UUID = UUID.fromString(VALID_UUID_STRING);
    private static final String TEST_TEXTURE_URL = "http://textures.minecraft.net/texture/abc123";
    private static final String TEST_PLAYER_NAME = "TestPlayer";

    @Nested
    @DisplayName("uuidToIntArray(UUID) tests")
    class UuidToIntArrayFromUuidTests {

        @Test
        @DisplayName("Converts UUID to 4-element int array")
        void convertsUuidTo4ElementArray() {
            int[] result = NBTUtil.uuidToIntArray(VALID_UUID);

            assertNotNull(result);
            assertEquals(4, result.length);
        }

        @Test
        @DisplayName("Conversion is deterministic")
        void conversionIsDeterministic() {
            int[] result1 = NBTUtil.uuidToIntArray(VALID_UUID);
            int[] result2 = NBTUtil.uuidToIntArray(VALID_UUID);

            assertArrayEquals(result1, result2);
        }

        @Test
        @DisplayName("Different UUIDs produce different arrays")
        void differentUuidsProduceDifferentArrays() {
            UUID uuid1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
            UUID uuid2 = UUID.fromString("00000000-0000-0000-0000-000000000002");

            int[] result1 = NBTUtil.uuidToIntArray(uuid1);
            int[] result2 = NBTUtil.uuidToIntArray(uuid2);

            assertFalse(java.util.Arrays.equals(result1, result2));
        }

        @Test
        @DisplayName("Zero UUID produces all zeros")
        void zeroUuidProducesAllZeros() {
            UUID zeroUuid = new UUID(0, 0);
            int[] result = NBTUtil.uuidToIntArray(zeroUuid);

            assertArrayEquals(new int[]{0, 0, 0, 0}, result);
        }
    }

    @Nested
    @DisplayName("uuidToIntArray(String) tests")
    class UuidToIntArrayFromStringTests {

        @Test
        @DisplayName("Converts valid UUID string to int array")
        void convertsValidUuidString() {
            int[] result = NBTUtil.uuidToIntArray(VALID_UUID_STRING);

            assertNotNull(result);
            assertEquals(4, result.length);
        }

        @Test
        @DisplayName("Returns null for null string")
        void returnsNullForNullString() {
            int[] result = NBTUtil.uuidToIntArray((String) null);

            assertNull(result);
        }

        @Test
        @DisplayName("Returns null for empty string")
        void returnsNullForEmptyString() {
            int[] result = NBTUtil.uuidToIntArray("");

            assertNull(result);
        }

        @Test
        @DisplayName("Returns null for invalid UUID format")
        void returnsNullForInvalidFormat() {
            int[] result = NBTUtil.uuidToIntArray("not-a-uuid");

            assertNull(result);
        }

        @Test
        @DisplayName("String and UUID conversions produce same result")
        void stringAndUuidConversionsSameResult() {
            int[] fromString = NBTUtil.uuidToIntArray(VALID_UUID_STRING);
            int[] fromUuid = NBTUtil.uuidToIntArray(VALID_UUID);

            assertArrayEquals(fromUuid, fromString);
        }
    }

    @Nested
    @DisplayName("createTextureValue tests")
    class CreateTextureValueTests {

        @Test
        @DisplayName("Creates non-empty Base64 string for valid URL")
        void createsNonEmptyBase64ForValidUrl() {
            String result = NBTUtil.createTextureValue(TEST_TEXTURE_URL);

            assertNotNull(result);
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("Returns empty string for null URL")
        void returnsEmptyForNullUrl() {
            String result = NBTUtil.createTextureValue(null);

            assertEquals("", result);
        }

        @Test
        @DisplayName("Returns empty string for empty URL")
        void returnsEmptyForEmptyUrl() {
            String result = NBTUtil.createTextureValue("");

            assertEquals("", result);
        }

        @Test
        @DisplayName("Result is valid Base64")
        void resultIsValidBase64() {
            String result = NBTUtil.createTextureValue(TEST_TEXTURE_URL);

            // Should not throw exception
            byte[] decoded = Base64.getDecoder().decode(result);
            assertNotNull(decoded);
        }

        @Test
        @DisplayName("Decoded result contains texture URL")
        void decodedResultContainsTextureUrl() {
            String result = NBTUtil.createTextureValue(TEST_TEXTURE_URL);
            String decoded = new String(Base64.getDecoder().decode(result));

            assertTrue(decoded.contains(TEST_TEXTURE_URL));
        }

        @Test
        @DisplayName("Decoded result is valid JSON structure")
        void decodedResultIsValidJsonStructure() {
            String result = NBTUtil.createTextureValue(TEST_TEXTURE_URL);
            String decoded = new String(Base64.getDecoder().decode(result));

            assertTrue(decoded.contains("\"textures\""));
            assertTrue(decoded.contains("\"SKIN\""));
            assertTrue(decoded.contains("\"url\""));
        }

        @Test
        @DisplayName("Different URLs produce different results")
        void differentUrlsProduceDifferentResults() {
            String result1 = NBTUtil.createTextureValue("http://example.com/texture1");
            String result2 = NBTUtil.createTextureValue("http://example.com/texture2");

            assertFalse(result1.equals(result2));
        }
    }

    @Nested
    @DisplayName("createSkullOwnerTag tests")
    class CreateSkullOwnerTagTests {

        @Test
        @DisplayName("Creates non-null tag with all parameters")
        void createsNonNullTagWithAllParameters() {
            var result = NBTUtil.createSkullOwnerTag(TEST_PLAYER_NAME, VALID_UUID_STRING, TEST_TEXTURE_URL);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Tag contains Name when provided")
        void tagContainsNameWhenProvided() {
            var result = NBTUtil.createSkullOwnerTag(TEST_PLAYER_NAME, VALID_UUID_STRING, TEST_TEXTURE_URL);

            assertTrue(result.contains("Name"));
            assertEquals(TEST_PLAYER_NAME, result.getString("Name"));
        }

        @Test
        @DisplayName("Tag contains Id when valid UUID provided")
        void tagContainsIdWhenValidUuidProvided() {
            var result = NBTUtil.createSkullOwnerTag(TEST_PLAYER_NAME, VALID_UUID_STRING, TEST_TEXTURE_URL);

            assertTrue(result.contains("Id"));
        }

        @Test
        @DisplayName("Tag contains Properties when texture URL provided")
        void tagContainsPropertiesWhenTextureUrlProvided() {
            var result = NBTUtil.createSkullOwnerTag(TEST_PLAYER_NAME, VALID_UUID_STRING, TEST_TEXTURE_URL);

            assertTrue(result.contains("Properties"));
        }

        @Test
        @DisplayName("Tag does not contain Id when UUID is null")
        void tagDoesNotContainIdWhenUuidNull() {
            var result = NBTUtil.createSkullOwnerTag(TEST_PLAYER_NAME, null, TEST_TEXTURE_URL);

            assertFalse(result.contains("Id"));
        }

        @Test
        @DisplayName("Tag does not contain Id when UUID is empty")
        void tagDoesNotContainIdWhenUuidEmpty() {
            var result = NBTUtil.createSkullOwnerTag(TEST_PLAYER_NAME, "", TEST_TEXTURE_URL);

            assertFalse(result.contains("Id"));
        }

        @Test
        @DisplayName("Tag does not contain Properties when texture URL is null")
        void tagDoesNotContainPropertiesWhenTextureUrlNull() {
            var result = NBTUtil.createSkullOwnerTag(TEST_PLAYER_NAME, VALID_UUID_STRING, null);

            assertFalse(result.contains("Properties"));
        }

        @Test
        @DisplayName("Tag does not contain Properties when texture URL is empty")
        void tagDoesNotContainPropertiesWhenTextureUrlEmpty() {
            var result = NBTUtil.createSkullOwnerTag(TEST_PLAYER_NAME, VALID_UUID_STRING, "");

            assertFalse(result.contains("Properties"));
        }

        @Test
        @DisplayName("Tag does not contain Name when name is null")
        void tagDoesNotContainNameWhenNameNull() {
            var result = NBTUtil.createSkullOwnerTag(null, VALID_UUID_STRING, TEST_TEXTURE_URL);

            assertFalse(result.contains("Name"));
        }

        @Test
        @DisplayName("Tag does not contain Name when name is empty")
        void tagDoesNotContainNameWhenNameEmpty() {
            var result = NBTUtil.createSkullOwnerTag("", VALID_UUID_STRING, TEST_TEXTURE_URL);

            assertFalse(result.contains("Name"));
        }

        @Test
        @DisplayName("Works with only name provided")
        void worksWithOnlyNameProvided() {
            var result = NBTUtil.createSkullOwnerTag(TEST_PLAYER_NAME, null, null);

            assertNotNull(result);
            assertTrue(result.contains("Name"));
            assertFalse(result.contains("Id"));
            assertFalse(result.contains("Properties"));
        }

        @Test
        @DisplayName("Works with all null/empty values")
        void worksWithAllNullValues() {
            var result = NBTUtil.createSkullOwnerTag(null, null, null);

            assertNotNull(result);
            // Should be an empty compound tag
        }
    }

    @Nested
    @DisplayName("createSkullOwnerTagWithRandomUuid tests")
    class CreateSkullOwnerTagWithRandomUuidTests {

        @Test
        @DisplayName("Creates tag with Id field")
        void createsTagWithIdField() {
            var result = NBTUtil.createSkullOwnerTagWithRandomUuid(TEST_PLAYER_NAME, TEST_TEXTURE_URL);

            assertTrue(result.contains("Id"));
        }

        @Test
        @DisplayName("Creates tag with Name field")
        void createsTagWithNameField() {
            var result = NBTUtil.createSkullOwnerTagWithRandomUuid(TEST_PLAYER_NAME, TEST_TEXTURE_URL);

            assertTrue(result.contains("Name"));
            assertEquals(TEST_PLAYER_NAME, result.getString("Name"));
        }

        @Test
        @DisplayName("Creates different UUIDs on each call")
        void createsDifferentUuidsOnEachCall() {
            var result1 = NBTUtil.createSkullOwnerTagWithRandomUuid(TEST_PLAYER_NAME, TEST_TEXTURE_URL);
            var result2 = NBTUtil.createSkullOwnerTagWithRandomUuid(TEST_PLAYER_NAME, TEST_TEXTURE_URL);

            // The Id arrays should be different (random UUIDs)
            int[] id1 = result1.getIntArray("Id");
            int[] id2 = result2.getIntArray("Id");

            assertFalse(java.util.Arrays.equals(id1, id2));
        }
    }
}
