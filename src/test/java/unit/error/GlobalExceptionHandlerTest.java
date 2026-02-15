package unit.error;

import com.shaurya.hospitalManagement.error.GlobalExceptionHandler;
import com.shaurya.hospitalManagement.error.RateLimitError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler - RateLimitExceededException Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should handle RateLimitExceededException and return TOO_MANY_REQUESTS")
    void handleRateLimitExceededException_ShouldReturnTooManyRequests() {
        // Arrange
        long retryAfter = 3600L;
        int remaining = 0;
        String message = "Rate limit exceeded";
        RateLimitError.RateLimitExceededException exception =
                new RateLimitError.RateLimitExceededException(message, retryAfter, remaining);

        // Act
        ResponseEntity<RateLimitError> response = exceptionHandler.handleRateLimitExceededException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo(message);
        assertThat(response.getBody().getRetryAfter()).isEqualTo(retryAfter);
        assertThat(response.getBody().getRemaining()).isEqualTo(remaining);
    }

    @Test
    @DisplayName("Should preserve retryAfter and remaining values in response")
    void handleRateLimitExceededException_ShouldPreserveValues() {
        // Arrange
        long retryAfter = 7200L;
        int remaining = 2;
        RateLimitError.RateLimitExceededException exception =
                new RateLimitError.RateLimitExceededException("Too many requests", retryAfter, remaining);

        // Act
        ResponseEntity<RateLimitError> response = exceptionHandler.handleRateLimitExceededException(exception);

        // Assert
        assert response.getBody() != null;
        assertThat(response.getBody().getRetryAfter()).isEqualTo(7200L);
        assertThat(response.getBody().getRemaining()).isEqualTo(2);
    }
}