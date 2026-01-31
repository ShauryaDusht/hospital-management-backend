package unit.type;

import com.shaurya.hospitalManagement.entity.type.PermissionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for PermissionType
 * Tests cover:
 * - Enum values verification
 * - Permission string format validation
 * - valueOf() method
 * - Getter method functionality
 */
@DisplayName("PermissionType Unit Tests")
class PermissionTypeTest {

    /**
     * Test: Enum contains all expected permission types
     * Why: Verifies all permission types are defined
     */
    @Test
    @DisplayName("Should contain exactly 7 permission types")
    void values_ShouldContainAllPermissionTypes() {
        // Arrange & Act
        PermissionType[] values = PermissionType.values();

        // Assert
        assertThat(values).hasSize(7);
        assertThat(values).containsExactly(
                PermissionType.PATIENT_READ,
                PermissionType.PATIENT_WRITE,
                PermissionType.APPOINTMENT_READ,
                PermissionType.APPOINTMENT_WRITE,
                PermissionType.APPOINTMENT_DELETE,
                PermissionType.USER_MANAGE,
                PermissionType.REPORT_VIEW
        );
    }

    /**
     * Test: Permission getter returns correct values
     * Why: Verifies getPermission() method works for all types
     */
    @Test
    @DisplayName("Should return correct permission string for each type via getter")
    void getPermission_ForAllTypes_ShouldReturnCorrectStrings() {
        // Arrange & Act & Assert
        assertThat(PermissionType.PATIENT_READ.getPermission()).isEqualTo("patient:read");
        assertThat(PermissionType.PATIENT_WRITE.getPermission()).isEqualTo("patient:write");
        assertThat(PermissionType.APPOINTMENT_READ.getPermission()).isEqualTo("appointment:read");
        assertThat(PermissionType.APPOINTMENT_WRITE.getPermission()).isEqualTo("appointment:write");
        assertThat(PermissionType.APPOINTMENT_DELETE.getPermission()).isEqualTo("appointment:delete");
        assertThat(PermissionType.USER_MANAGE.getPermission()).isEqualTo("user:manage");
        assertThat(PermissionType.REPORT_VIEW.getPermission()).isEqualTo("report:view");
    }

    /**
     * Test: valueOf() with valid names
     * Why: Verifies string to enum conversion works correctly
     */
    @Test
    @DisplayName("Should convert valid string names to enum constants")
    void valueOf_WithValidNames_ShouldReturnCorrectEnumConstants() {
        // Arrange & Act & Assert
        assertThat(PermissionType.valueOf("PATIENT_READ")).isEqualTo(PermissionType.PATIENT_READ);
        assertThat(PermissionType.valueOf("USER_MANAGE")).isEqualTo(PermissionType.USER_MANAGE);
        assertThat(PermissionType.valueOf("REPORT_VIEW")).isEqualTo(PermissionType.REPORT_VIEW);
    }

    /**
     * Test: valueOf() with invalid name
     * Why: Verifies proper error handling for invalid permission types
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid permission type name")
    void valueOf_WithInvalidName_ShouldThrowException() {
        // Arrange & Act & Assert
        assertThatThrownBy(() -> PermissionType.valueOf("INVALID_PERMISSION"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test: All permissions are unique
     * Why: Ensures no duplicate permission strings exist
     */
    @Test
    @DisplayName("Should have unique permission strings for all types")
    void getPermission_AllPermissions_ShouldBeUnique() {
        // Arrange
        List<String> permissions = Arrays.stream(PermissionType.values())
                .map(PermissionType::getPermission)
                .toList();

        // Act
        long uniqueCount = permissions.stream().distinct().count();

        // Assert
        assertThat(uniqueCount).isEqualTo(permissions.size());
    }

    /**
     * Test: Permission string format
     * Why: Verifies all permissions follow resource:action pattern
     */
    @Test
    @DisplayName("Should follow resource:action format for all permission strings")
    void getPermission_AllPermissions_ShouldFollowCorrectFormat() {
        // Arrange & Act & Assert
        for (PermissionType type : PermissionType.values()) {
            String permission = type.getPermission();
            assertThat(permission).contains(":");
            assertThat(permission.split(":")).hasSize(2);
        }
    }
}