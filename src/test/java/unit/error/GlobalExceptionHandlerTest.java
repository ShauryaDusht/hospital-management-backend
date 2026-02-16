package unit.error;

import com.shaurya.hospitalManagement.error.ApiError;
import com.shaurya.hospitalManagement.error.GlobalExceptionHandler;
import com.shaurya.hospitalManagement.error.RateLimitError;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should handle UsernameNotFoundException and return NOT_FOUND")
    void handleUsernameNotFoundException_ShouldReturnNotFound() {
        // Arrange
        UsernameNotFoundException exception = new UsernameNotFoundException("test@example.com");

        // Act
        ResponseEntity<ApiError> response = exceptionHandler.handleUsernameNotFoundException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).contains("Username not found with username:");
        assertThat(response.getBody().getError()).contains("test@example.com");
    }

    @Test
    @DisplayName("Should handle AuthenticationException and return UNAUTHORIZED")
    void handleAuthenticationException_ShouldReturnUnauthorized() {
        // Arrange
        BadCredentialsException exception = new BadCredentialsException("Invalid credentials");

        // Act
        ResponseEntity<ApiError> response = exceptionHandler.handleAuthenticationException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Authentication failed: Invalid credentials");
    }

    @Test
    @DisplayName("Should handle JwtException and return UNAUTHORIZED")
    void handleJwtException_ShouldReturnUnauthorized() {
        // Arrange
        JwtException exception = new JwtException("Token expired");

        // Act
        ResponseEntity<ApiError> response = exceptionHandler.handleJwtException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Invalid JWT token: Token expired");
    }

    @Test
    @DisplayName("Should handle AccessDeniedException and return FORBIDDEN")
    void handleAccessDeniedException_ShouldReturnForbidden() {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        // Act
        ResponseEntity<ApiError> response = exceptionHandler.handleAccessDeniedException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Access denied: Insufficient permissions");
    }

    @Test
    @DisplayName("Should handle generic Exception and return INTERNAL_SERVER_ERROR")
    void handleGenericException_ShouldReturnInternalServerError() {
        // Arrange
        Exception exception = new RuntimeException("Unexpected error");

        // Act
        ResponseEntity<ApiError> response = exceptionHandler.handleGenericException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("An unexpected error occurred: Unexpected error");
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
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getRetryAfter()).isEqualTo(7200L);
        assertThat(response.getBody().getRemaining()).isEqualTo(2);
    }
}