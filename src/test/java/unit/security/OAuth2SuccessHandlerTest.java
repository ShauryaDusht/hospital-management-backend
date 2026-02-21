package unit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaurya.hospitalManagement.dto.LoginResponseDto;
import com.shaurya.hospitalManagement.security.AuthService;
import com.shaurya.hospitalManagement.security.OAuth2SuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuth2SuccessHandler Tests")
class OAuth2SuccessHandlerTest {

    @Mock
    private AuthService authService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private OAuth2AuthenticationToken authentication;

    @Mock
    private OAuth2User oAuth2User;

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws Exception {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    @DisplayName("Should handle OAuth2 login success and write response")
    void onAuthenticationSuccess_ShouldWriteLoginResponse() throws Exception {
        // Arrange
        when(authentication.getAuthorizedClientRegistrationId()).thenReturn("google");
        when(authentication.getPrincipal()).thenReturn(oAuth2User);

        LoginResponseDto loginResponseDto = new LoginResponseDto("token123", 1L);
        ResponseEntity<LoginResponseDto> responseEntity = ResponseEntity.ok(loginResponseDto);

        when(authService.handleOAuth2LoginRequest(any(OAuth2User.class), anyString()))
                .thenReturn(responseEntity);
        when(objectMapper.writeValueAsString(loginResponseDto)).thenReturn("{\"token\":\"token123\"}");

        // Act
        oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(authService).handleOAuth2LoginRequest(oAuth2User, "google");
        verify(response).setStatus(HttpStatus.OK.value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValueAsString(loginResponseDto);
    }

    @Test
    @DisplayName("Should handle GitHub OAuth2 login")
    void onAuthenticationSuccess_WithGithub_ShouldProcessCorrectly() throws Exception {
        // Arrange
        when(authentication.getAuthorizedClientRegistrationId()).thenReturn("github");
        when(authentication.getPrincipal()).thenReturn(oAuth2User);

        LoginResponseDto loginResponseDto = new LoginResponseDto("githubToken", 2L);
        ResponseEntity<LoginResponseDto> responseEntity = ResponseEntity.ok(loginResponseDto);

        when(authService.handleOAuth2LoginRequest(oAuth2User, "github")).thenReturn(responseEntity);
        when(objectMapper.writeValueAsString(loginResponseDto)).thenReturn("{\"token\":\"githubToken\"}");

        // Act
        oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(authService).handleOAuth2LoginRequest(oAuth2User, "github");
        verify(response).setStatus(HttpStatus.OK.value());
    }
}