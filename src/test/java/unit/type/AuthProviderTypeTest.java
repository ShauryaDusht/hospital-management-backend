package unit.type;

import com.shaurya.hospitalManagement.entity.type.AuthProviderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for AuthProviderType
 * Tests cover:
 * - Enum values verification
 * - valueOf() method
 * - Enum ordering
 */
@DisplayName("AuthProviderType Unit Tests")
class AuthProviderTypeTest {

    /**
     * Test: Enum contains all expected values
     * Why: Verifies all authentication provider types are defined
     */
    @Test
    @DisplayName("Should contain exactly 3 authentication provider types")
    void values_ShouldContainAllAuthProviderTypes() {
        // Arrange & Act
        AuthProviderType[] values = AuthProviderType.values();

        // Assert
        assertThat(values).hasSize(3);
        assertThat(values).containsExactly(
                AuthProviderType.GOOGLE,
                AuthProviderType.GITHUB,
                AuthProviderType.EMAIL
        );
    }

    /**
     * Test: valueOf() with valid names
     * Why: Verifies string to enum conversion works correctly
     */
    @Test
    @DisplayName("Should convert valid string names to enum constants")
    void valueOf_WithValidNames_ShouldReturnCorrectEnumConstants() {
        // Arrange & Act & Assert
        assertThat(AuthProviderType.valueOf("GOOGLE")).isEqualTo(AuthProviderType.GOOGLE);
        assertThat(AuthProviderType.valueOf("GITHUB")).isEqualTo(AuthProviderType.GITHUB);
        assertThat(AuthProviderType.valueOf("EMAIL")).isEqualTo(AuthProviderType.EMAIL);
    }

    /**
     * Test: valueOf() with invalid name
     * Why: Verifies proper error handling for invalid provider types
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid provider type name")
    void valueOf_WithInvalidName_ShouldThrowException() {
        // Arrange & Act & Assert
        assertThatThrownBy(() -> AuthProviderType.valueOf("INVALID"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test: Enum ordinal values
     * Why: Verifies correct ordering of authentication providers
     */
    @Test
    @DisplayName("Should have correct ordinal values for each provider type")
    void ordinal_ShouldReturnCorrectValues() {
        // Arrange & Act & Assert
        assertThat(AuthProviderType.GOOGLE.ordinal()).isEqualTo(0);
        assertThat(AuthProviderType.GITHUB.ordinal()).isEqualTo(1);
        assertThat(AuthProviderType.EMAIL.ordinal()).isEqualTo(2);
    }
}