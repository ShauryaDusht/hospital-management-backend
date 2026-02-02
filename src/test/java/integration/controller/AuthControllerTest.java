package integration.controller;

import com.shaurya.hospitalManagement.controller.AuthController;
import com.shaurya.hospitalManagement.dto.LoginRequestDto;
import com.shaurya.hospitalManagement.dto.LoginResponseDto;
import com.shaurya.hospitalManagement.dto.SignUpRequestDto;
import com.shaurya.hospitalManagement.dto.SignupResponseDto;
import com.shaurya.hospitalManagement.entity.type.RoleType;
import com.shaurya.hospitalManagement.security.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private LoginRequestDto loginRequestDto;
    private LoginResponseDto loginResponseDto;
    private SignUpRequestDto signUpRequestDto;
    private SignupResponseDto signupResponseDto;

    @BeforeEach
    void setUp() {
        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("testuser@example.com");
        loginRequestDto.setPassword("password123");

        loginResponseDto = new LoginResponseDto();
        loginResponseDto.setJwt("mock-jwt-token");
        loginResponseDto.setUserId(1L);

        Set<RoleType> roles = new HashSet<>();
        roles.add(RoleType.PATIENT);

        signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setUsername("newuser@example.com");
        signUpRequestDto.setPassword("password123");
        signUpRequestDto.setName("New User");
        signUpRequestDto.setRoles(roles);

        signupResponseDto = new SignupResponseDto();
        signupResponseDto.setId(1L);
        signupResponseDto.setUsername("newuser@example.com");
    }

    @Test
    void login_ShouldReturnJwtToken_WhenCredentialsAreValid() {
        // Arrange
        when(authService.login(any(LoginRequestDto.class))).thenReturn(loginResponseDto);

        // Act
        ResponseEntity<LoginResponseDto> response = authController.login(loginRequestDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("mock-jwt-token", response.getBody().getJwt());
        assertEquals(1L, response.getBody().getUserId());
        verify(authService, times(1)).login(loginRequestDto);
    }

    @Test
    void login_ShouldPassCorrectCredentials() {
        // Arrange
        when(authService.login(any(LoginRequestDto.class))).thenReturn(loginResponseDto);

        // Act
        authController.login(loginRequestDto);

        // Assert
        verify(authService).login(argThat(dto ->
                dto.getUsername().equals("testuser@example.com") &&
                        dto.getPassword().equals("password123")
        ));
    }

    @Test
    void login_ShouldPropagateException_WhenCredentialsAreInvalid() {
        // Arrange
        when(authService.login(any(LoginRequestDto.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            authController.login(loginRequestDto);
        });
        verify(authService, times(1)).login(loginRequestDto);
    }

    @Test
    void signup_ShouldReturnCreatedUser_WhenRequestIsValid() {
        // Arrange
        when(authService.signup(any(SignUpRequestDto.class))).thenReturn(signupResponseDto);

        // Act
        ResponseEntity<SignupResponseDto> response = authController.signup(signUpRequestDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("newuser@example.com", response.getBody().getUsername());
        verify(authService, times(1)).signup(signUpRequestDto);
    }

    @Test
    void signup_ShouldPassCorrectUserDetails() {
        // Arrange
        when(authService.signup(any(SignUpRequestDto.class))).thenReturn(signupResponseDto);

        // Act
        authController.signup(signUpRequestDto);

        // Assert
        verify(authService).signup(argThat(dto ->
                dto.getUsername().equals("newuser@example.com") &&
                        dto.getPassword().equals("password123") &&
                        dto.getName().equals("New User") &&
                        dto.getRoles().contains(RoleType.PATIENT)
        ));
    }

    @Test
    void signup_ShouldPropagateException_WhenUserAlreadyExists() {
        // Arrange
        when(authService.signup(any(SignUpRequestDto.class)))
                .thenThrow(new IllegalArgumentException("User already exists"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            authController.signup(signUpRequestDto);
        });
        verify(authService, times(1)).signup(signUpRequestDto);
    }
}