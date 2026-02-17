package unit.config;

import com.shaurya.hospitalManagement.config.RedisConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("RedisConfig Tests")
class RedisConfigTest {

    private RedisConfig redisConfig;

    @BeforeEach
    void setUp() {
        redisConfig = new RedisConfig();
        ReflectionTestUtils.setField(redisConfig, "redisHost", "localhost");
        ReflectionTestUtils.setField(redisConfig, "redisPort", 6379);
        ReflectionTestUtils.setField(redisConfig, "redisPassword", "testpassword");
    }

    @Test
    @DisplayName("Should create LettuceConnectionFactory with SSL enabled")
    void redisConnectionFactory_WithSslEnabled_ShouldReturnFactory() {
        ReflectionTestUtils.setField(redisConfig, "sslEnabled", true);

        LettuceConnectionFactory factory = redisConfig.redisConnectionFactory();

        assertThat(factory).isNotNull();
        assertThat(factory.getHostName()).isEqualTo("localhost");
        assertThat(factory.getPort()).isEqualTo(6379);
        factory.destroy();
    }

    @Test
    @DisplayName("Should create LettuceConnectionFactory with SSL disabled")
    void redisConnectionFactory_WithSslDisabled_ShouldReturnFactory() {
        ReflectionTestUtils.setField(redisConfig, "sslEnabled", false);

        LettuceConnectionFactory factory = redisConfig.redisConnectionFactory();

        assertThat(factory).isNotNull();
        assertThat(factory.getHostName()).isEqualTo("localhost");
        assertThat(factory.getPort()).isEqualTo(6379);
        factory.destroy();
    }

    @Test
    @DisplayName("Should create RedisTemplate with StringRedisSerializer")
    void redisTemplate_ShouldReturnConfiguredTemplate() {
        RedisConnectionFactory connectionFactory = mock(RedisConnectionFactory.class);

        RedisTemplate<String, String> template = redisConfig.redisTemplate(connectionFactory);

        assertThat(template).isNotNull();
        assertThat(template.getKeySerializer()).isInstanceOf(StringRedisSerializer.class);
        assertThat(template.getValueSerializer()).isInstanceOf(StringRedisSerializer.class);
        assertThat(template.getHashKeySerializer()).isInstanceOf(StringRedisSerializer.class);
        assertThat(template.getHashValueSerializer()).isInstanceOf(StringRedisSerializer.class);
    }
}