package unit.security;

import com.shaurya.hospitalManagement.entity.User;
import com.shaurya.hospitalManagement.entity.type.AuthProviderType;
import com.shaurya.hospitalManagement.security.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("AuthUtil Tests")
class AuthUtilTest {

    private AuthUtil authUtil;

    @BeforeEach
    void setUp() {
        authUtil = new AuthUtil();
        ReflectionTestUtils.setField(authUtil, "jwtSecretKey", "testsecretkeytestsecretkeytestsecretkey");
    }

    @Test
    @DisplayName("Should generate JWT token with username and userId")
    void generateAccessToken_ShouldReturnValidToken() {
        User user = User.builder().id(1L).username("test@example.com").build();

        String token = authUtil.generateAccessToken(user);

        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("Should extract username from token")
    void getUsernameFromToken_ShouldReturnUsername() {
        User user = User.builder().id(1L).username("test@example.com").build();
        String token = authUtil.generateAccessToken(user);

        String username = authUtil.getUsernameFromToken(token);

        assertThat(username).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should return GOOGLE provider type for google registration")
    void getProviderTypeFromRegistrationId_WithGoogle_ShouldReturnGoogle() {
        AuthProviderType result = authUtil.getProviderTypeFromRegistrationId("google");
        assertThat(result).isEqualTo(AuthProviderType.GOOGLE);
    }

    @Test
    @DisplayName("Should return GITHUB provider type for github registration")
    void getProviderTypeFromRegistrationId_WithGithub_ShouldReturnGithub() {
        AuthProviderType result = authUtil.getProviderTypeFromRegistrationId("github");
        assertThat(result).isEqualTo(AuthProviderType.GITHUB);
    }

    @Test
    @DisplayName("Should throw exception for unsupported provider")
    void getProviderTypeFromRegistrationId_WithUnsupported_ShouldThrowException() {
        assertThatThrownBy(() -> authUtil.getProviderTypeFromRegistrationId("facebook"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported OAuth2 provider");
    }

    @Test
    @DisplayName("Should extract Google provider ID from OAuth2User")
    void determineProviderIdFromOAuth2User_WithGoogle_ShouldReturnSub() {
        OAuth2User oAuth2User = mock(OAuth2User.class);
        when(oAuth2User.getAttribute("sub")).thenReturn("google123");

        String providerId = authUtil.determineProviderIdFromOAuth2User(oAuth2User, "google");

        assertThat(providerId).isEqualTo("google123");
    }

    @Test
    @DisplayName("Should extract GitHub provider ID from OAuth2User")
    void determineProviderIdFromOAuth2User_WithGithub_ShouldReturnId() {
        OAuth2User oAuth2User = mock(OAuth2User.class);
        when(oAuth2User.getAttribute("id")).thenReturn(12345);

        String providerId = authUtil.determineProviderIdFromOAuth2User(oAuth2User, "github");

        assertThat(providerId).isEqualTo("12345");
    }

    @Test
    @DisplayName("Should throw exception when provider ID is null")
    void determineProviderIdFromOAuth2User_WithNullProviderId_ShouldThrowException() {
        OAuth2User oAuth2User = mock(OAuth2User.class);
        when(oAuth2User.getAttribute("sub")).thenReturn(null);

        assertThatThrownBy(() -> authUtil.determineProviderIdFromOAuth2User(oAuth2User, "google"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unable to determine providerId");
    }

    @Test
    @DisplayName("Should throw exception when provider ID is blank")
    void determineProviderIdFromOAuth2User_WithBlankProviderId_ShouldThrowException() {
        OAuth2User oAuth2User = mock(OAuth2User.class);
        when(oAuth2User.getAttribute("sub")).thenReturn("");

        assertThatThrownBy(() -> authUtil.determineProviderIdFromOAuth2User(oAuth2User, "google"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unable to determine providerId");
    }

    @Test
    @DisplayName("Should use email as username when available")
    void determineUsernameFromOAuth2User_WithEmail_ShouldReturnEmail() {
        OAuth2User oAuth2User = mock(OAuth2User.class);
        when(oAuth2User.getAttribute("email")).thenReturn("user@example.com");

        String username = authUtil.determineUsernameFromOAuth2User(oAuth2User, "google", "google123");

        assertThat(username).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("Should use sub for Google when email not available")
    void determineUsernameFromOAuth2User_GoogleNoEmail_ShouldReturnSub() {
        OAuth2User oAuth2User = mock(OAuth2User.class);
        when(oAuth2User.getAttribute("email")).thenReturn(null);
        when(oAuth2User.getAttribute("sub")).thenReturn("google123");

        String username = authUtil.determineUsernameFromOAuth2User(oAuth2User, "google", "providerId");

        assertThat(username).isEqualTo("google123");
    }

    @Test
    @DisplayName("Should use login for GitHub when email not available")
    void determineUsernameFromOAuth2User_GithubNoEmail_ShouldReturnLogin() {
        OAuth2User oAuth2User = mock(OAuth2User.class);
        when(oAuth2User.getAttribute("email")).thenReturn(null);
        when(oAuth2User.getAttribute("login")).thenReturn("githubuser");

        String username = authUtil.determineUsernameFromOAuth2User(oAuth2User, "github", "providerId");

        assertThat(username).isEqualTo("githubuser");
    }

    @Test
    @DisplayName("Should use providerId for unsupported provider when email not available")
    void determineUsernameFromOAuth2User_UnsupportedProvider_ShouldReturnProviderId() {
        OAuth2User oAuth2User = mock(OAuth2User.class);
        when(oAuth2User.getAttribute("email")).thenReturn(null);

        String username = authUtil.determineUsernameFromOAuth2User(oAuth2User, "facebook", "fb123");

        assertThat(username).isEqualTo("fb123");
    }
}