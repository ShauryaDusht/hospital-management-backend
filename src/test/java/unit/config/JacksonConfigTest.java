package unit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaurya.hospitalManagement.config.JacksonConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JacksonConfig Tests")
class JacksonConfigTest {

    private JacksonConfig jacksonConfig;

    @BeforeEach
    void setUp() {
        jacksonConfig = new JacksonConfig();
    }

    @Test
    @DisplayName("Should create ObjectMapper bean")
    void objectMapper_ShouldReturnObjectMapperInstance() {
        ObjectMapper result = jacksonConfig.objectMapper();
        assertThat(result).isNotNull().isInstanceOf(ObjectMapper.class);
    }
}