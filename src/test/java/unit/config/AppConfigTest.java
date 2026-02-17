package unit.config;

import com.shaurya.hospitalManagement.config.AppConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("AppConfig Tests")
class AppConfigTest {

    private AppConfig appConfig;

    @BeforeEach
    void setUp() {
        appConfig = new AppConfig();
    }

    @Test
    @DisplayName("Should create ModelMapper bean")
    void modelMapper_ShouldReturnModelMapperInstance() {
        ModelMapper result = appConfig.modelMapper();
        assertThat(result).isNotNull().isInstanceOf(ModelMapper.class);
    }

    @Test
    @DisplayName("Should create BCryptPasswordEncoder bean")
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
        PasswordEncoder result = appConfig.passwordEncoder();
        assertThat(result).isNotNull().isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    @DisplayName("Should encode passwords with BCrypt")
    void passwordEncoder_ShouldEncodePassword() {
        PasswordEncoder encoder = appConfig.passwordEncoder();
        String encoded = encoder.encode("password");
        assertThat(encoder.matches("password", encoded)).isTrue();
    }

    @Test
    @DisplayName("Should return AuthenticationManager from configuration")
    void authenticationManager_ShouldReturnAuthenticationManager() throws Exception {
        AuthenticationConfiguration configuration = mock(AuthenticationConfiguration.class);
        AuthenticationManager manager = mock(AuthenticationManager.class);
        when(configuration.getAuthenticationManager()).thenReturn(manager);

        AuthenticationManager result = appConfig.authenticationManager(configuration);

        assertThat(result).isEqualTo(manager);
        verify(configuration).getAuthenticationManager();
    }
}