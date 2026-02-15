package unit.error;

import com.shaurya.hospitalManagement.error.RateLimitError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RateLimitError.RateLimitExceededException Tests")
class RateLimitExceededExceptionTest {

    @Test
    @DisplayName("Should create exception with message, retryAfter, and remaining")
    void constructor_ShouldSetAllFields() {
        // Arrange
        String message = "Rate limit exceeded";
        long retryAfter = 3600L;
        int remaining = 0;

        // Act
        RateLimitError.RateLimitExceededException exception =
                new RateLimitError.RateLimitExceededException(message, retryAfter, remaining);

        // Assert
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getRetryAfter()).isEqualTo(retryAfter);
        assertThat(exception.getRemaining()).isEqualTo(remaining);
    }

    @Test
    @DisplayName("Should preserve retryAfter value")
    void getRetryAfter_ShouldReturnCorrectValue() {
        // Arrange
        long expectedRetryAfter = 7200L;
        RateLimitError.RateLimitExceededException exception =
                new RateLimitError.RateLimitExceededException("Message", expectedRetryAfter, 5);

        // Act & Assert
        assertThat(exception.getRetryAfter()).isEqualTo(expectedRetryAfter);
    }

    @Test
    @DisplayName("Should preserve remaining value")
    void getRemaining_ShouldReturnCorrectValue() {
        // Arrange
        int expectedRemaining = 3;
        RateLimitError.RateLimitExceededException exception =
                new RateLimitError.RateLimitExceededException("Message", 1800L, expectedRemaining);

        // Act & Assert
        assertThat(exception.getRemaining()).isEqualTo(expectedRemaining);
    }
}