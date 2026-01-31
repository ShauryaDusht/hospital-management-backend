package unit.error;

import com.shaurya.hospitalManagement.error.ApiError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ApiError
 * Tests cover:
 * - Constructor initialization
 * - Getter and setter methods
 * - Timestamp generation
 */
@DisplayName("ApiError Unit Tests")
class ApiErrorTest {

    /**
     * Test: Default constructor
     * Why: Verifies timestamp is set on instantiation
     */
    @Test
    @DisplayName("Should initialize with timestamp when using default constructor")
    void defaultConstructor_ShouldInitializeWithTimestamp() {
        // Arrange & Act
        ApiError apiError = new ApiError();

        // Assert
        assertThat(apiError.getTimeStamp()).isNotNull();
        assertThat(apiError.getError()).isNull();
        assertThat(apiError.getStatusCode()).isNull();
    }

    /**
     * Test: Parameterized constructor
     * Why: Verifies all fields are properly initialized
     */
    @Test
    @DisplayName("Should initialize all fields when using parameterized constructor")
    void parameterizedConstructor_WithValidInputs_ShouldInitializeAllFields() {
        // Arrange
        String errorMessage = "Test error message";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        // Act
        ApiError apiError = new ApiError(errorMessage, status);

        // Assert
        assertThat(apiError.getError()).isEqualTo(errorMessage);
        assertThat(apiError.getStatusCode()).isEqualTo(status);
        assertThat(apiError.getTimeStamp()).isNotNull();
    }

    /**
     * Test: Setters
     * Why: Verifies fields can be updated
     */
    @Test
    @DisplayName("Should update fields when setters are called")
    void setters_WithNewValues_ShouldUpdateFields() {
        // Arrange
        ApiError apiError = new ApiError();
        String errorMessage = "Updated error";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        LocalDateTime customTime = LocalDateTime.now().minusHours(1);

        // Act
        apiError.setError(errorMessage);
        apiError.setStatusCode(status);
        apiError.setTimeStamp(customTime);

        // Assert
        assertThat(apiError.getError()).isEqualTo(errorMessage);
        assertThat(apiError.getStatusCode()).isEqualTo(status);
        assertThat(apiError.getTimeStamp()).isEqualTo(customTime);
    }
}