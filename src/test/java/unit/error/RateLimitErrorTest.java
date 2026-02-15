package unit.error;

import com.shaurya.hospitalManagement.error.RateLimitError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RateLimitError Tests")
class RateLimitErrorTest {

    @Test
    @DisplayName("Should create RateLimitError with all fields")
    void constructor_ShouldSetAllFields() {
        // Arrange
        String error = "Too many requests";
        HttpStatus status = HttpStatus.TOO_MANY_REQUESTS;
        long retryAfter = 3600L;
        int remaining = 0;

        // Act
        RateLimitError rateLimitError = new RateLimitError(error, status, retryAfter, remaining);

        // Assert
        assertThat(rateLimitError.getError()).isEqualTo(error);
        assertThat(rateLimitError.getStatusCode()).isEqualTo(status);
        assertThat(rateLimitError.getRetryAfter()).isEqualTo(retryAfter);
        assertThat(rateLimitError.getRemaining()).isEqualTo(remaining);
    }

    @Test
    @DisplayName("Should set and get retryAfter")
    void setRetryAfter_ShouldUpdateValue() {
        // Arrange
        RateLimitError rateLimitError = new RateLimitError("Error", HttpStatus.TOO_MANY_REQUESTS, 1800L, 1);

        // Act
        rateLimitError.setRetryAfter(7200L);

        // Assert
        assertThat(rateLimitError.getRetryAfter()).isEqualTo(7200L);
    }

    @Test
    @DisplayName("Should set and get remaining")
    void setRemaining_ShouldUpdateValue() {
        // Arrange
        RateLimitError rateLimitError = new RateLimitError("Error", HttpStatus.TOO_MANY_REQUESTS, 3600L, 0);

        // Act
        rateLimitError.setRemaining(5);

        // Assert
        assertThat(rateLimitError.getRemaining()).isEqualTo(5);
    }
}