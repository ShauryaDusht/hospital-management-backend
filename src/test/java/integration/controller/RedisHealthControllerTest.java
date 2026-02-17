package integration.controller;

import com.shaurya.hospitalManagement.controller.RedisHealthController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RedisHealthController Tests")
class RedisHealthControllerTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisHealthController redisHealthController;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("Should return CONNECTED status when Redis is working")
    void checkRedisConnection_WhenRedisWorks_ShouldReturnConnected() {
        // Arrange
        when(valueOperations.get("health:check")).thenReturn("OK");

        // Act
        ResponseEntity<Map<String, Object>> response = redisHealthController.checkRedisConnection();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("CONNECTED");
        assertThat(response.getBody().get("message")).isEqualTo("Redis is working correctly");
        assertThat(response.getBody().get("testValue")).isEqualTo("OK");

        verify(valueOperations).set("health:check", "OK");
        verify(valueOperations).get("health:check");
        verify(redisTemplate).delete("health:check");
    }

    @Test
    @DisplayName("Should return FAILED status when Redis throws exception")
    void checkRedisConnection_WhenRedisFails_ShouldReturnFailed() {
        // Arrange
        RuntimeException exception = new RuntimeException("Redis connection failed");
        doThrow(exception).when(valueOperations).set(anyString(), anyString());

        // Act
        ResponseEntity<Map<String, Object>> response = redisHealthController.checkRedisConnection();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("FAILED");
        assertThat(response.getBody().get("error")).isEqualTo("Redis connection failed");
    }

    @Test
    @DisplayName("Should handle exception with cause")
    void checkRedisConnection_WhenExceptionHasCause_ShouldIncludeCause() {
        // Arrange
        Exception cause = new Exception("Connection timeout");
        RuntimeException exception = new RuntimeException("Redis error", cause);
        doThrow(exception).when(valueOperations).set(anyString(), anyString());

        // Act
        ResponseEntity<Map<String, Object>> response = redisHealthController.checkRedisConnection();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("FAILED");
        assertThat(response.getBody().get("cause")).isEqualTo("Connection timeout");
    }

    @Test
    @DisplayName("Should handle exception without cause")
    void checkRedisConnection_WhenExceptionHasNoCause_ShouldShowUnknown() {
        // Arrange
        RuntimeException exception = new RuntimeException("Redis error");
        doThrow(exception).when(valueOperations).set(anyString(), anyString());

        // Act
        ResponseEntity<Map<String, Object>> response = redisHealthController.checkRedisConnection();

        // Assert
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("cause")).isEqualTo("Unknown");
    }
}