package unit.error;

import com.shaurya.hospitalManagement.error.ApiError;
import com.shaurya.hospitalManagement.error.GlobalExceptionHandler;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for GlobalExceptionHandler
 * Tests cover:
 * - Exception handling for all handler methods
 * - Response entity structure validation
 * - Status code consistency
 */
@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    /**
     * Test: Handle UsernameNotFoundException
     * Why: Verifies proper handling of user not found scenarios
     */
    @Test
    @DisplayName("Should return NOT_FOUND when username not found")
    void handleUsernameNotFoundException_WithValidUsername_ShouldReturnNotFound() {
        // Arrange
        String username = "testuser@example.com";
        UsernameNotFoundException exception = new UsernameNotFoundException(username);

        // Act
        ResponseEntity<ApiError> response = exceptionHandler.handleUsernameNotFoundException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Username not found with username: " + username);
        assertThat(response.getBody().getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getTimeStamp()).isNotNull();
    }

    /**
     * Test: Handle AuthenticationException
     * Why: Verifies proper handling of authentication failures
     */
    @Test
    @DisplayName("Should return UNAUTHORIZED when authentication fails")
    void handleAuthenticationException_WithBadCredentials_ShouldReturnUnauthorized() {
        // Arrange
        String message = "Bad credentials";
        AuthenticationException exception = new BadCredentialsException(message);

        // Act
        ResponseEntity<ApiError> response = exceptionHandler.handleAuthenticationException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assert response.getBody() != null;
        assertThat(response.getBody().getError()).isEqualTo("Authentication failed: " + message);
        assertThat(response.getBody().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Test: Handle JwtException
     * Why: Verifies proper handling of JWT token errors
     */
    @Test
    @DisplayName("Should return UNAUTHORIZED when JWT token is invalid")
    void handleJwtException_WithTokenError_ShouldReturnUnauthorized() {
        // Arrange
        String message = "Token has expired";
        JwtException exception = new JwtException(message);

        // Act
        ResponseEntity<ApiError> response = exceptionHandler.handleJwtException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assert response.getBody() != null;
        assertThat(response.getBody().getError()).isEqualTo("Invalid JWT token: " + message);
        assertThat(response.getBody().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Test: Handle AccessDeniedException
     * Why: Verifies proper handling of authorization failures
     */
    @Test
    @DisplayName("Should return FORBIDDEN when access is denied")
    void handleAccessDeniedException_WithInsufficientPermissions_ShouldReturnForbidden() {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        // Act
        ResponseEntity<ApiError> response = exceptionHandler.handleAccessDeniedException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assert response.getBody() != null;
        assertThat(response.getBody().getError()).isEqualTo("Access denied: Insufficient permissions");
        assertThat(response.getBody().getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /**
     * Test: Handle generic Exception
     * Why: Verifies proper handling of unexpected errors
     */
    @Test
    @DisplayName("Should return INTERNAL_SERVER_ERROR when generic exception occurs")
    void handleGenericException_WithRuntimeException_ShouldReturnInternalServerError() {
        // Arrange
        String message = "Unexpected error occurred";
        Exception exception = new RuntimeException(message);

        // Act
        ResponseEntity<ApiError> response = exceptionHandler.handleGenericException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assert response.getBody() != null;
        assertThat(response.getBody().getError()).isEqualTo("An unexpected error occurred: " + message);
        assertThat(response.getBody().getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}