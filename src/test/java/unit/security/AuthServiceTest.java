package unit.security;

import com.shaurya.hospitalManagement.dto.LoginRequestDto;
import com.shaurya.hospitalManagement.dto.LoginResponseDto;
import com.shaurya.hospitalManagement.dto.SignUpRequestDto;
import com.shaurya.hospitalManagement.dto.SignupResponseDto;
import com.shaurya.hospitalManagement.entity.Patient;
import com.shaurya.hospitalManagement.entity.User;
import com.shaurya.hospitalManagement.entity.type.AuthProviderType;
import com.shaurya.hospitalManagement.entity.type.RoleType;
import com.shaurya.hospitalManagement.repository.PatientRepository;
import com.shaurya.hospitalManagement.repository.UserRepository;
import com.shaurya.hospitalManagement.security.AuthService;
import com.shaurya.hospitalManagement.security.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService
 * Focuses on: signup, login, JWT generation, authentication
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
@Disabled
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private SignUpRequestDto signUpRequest;
    private LoginRequestDto loginRequest;
    private User testUser;
    private Patient testPatient;

    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequestDto();
        signUpRequest.setUsername("arjun.k@email.com");
        signUpRequest.setPassword("password123");
        signUpRequest.setName("Arjun Krishnan");
        signUpRequest.setRoles(Set.of(RoleType.PATIENT));

        loginRequest = new LoginRequestDto();
        loginRequest.setUsername("arjun.k@email.com");
        loginRequest.setPassword("password123");

        testUser = User.builder()
                .id(1L)
                .username("arjun.k@email.com")
                .password("encodedPassword")
                .providerType(AuthProviderType.EMAIL)
                .roles(Set.of(RoleType.PATIENT))
                .build();

        testPatient = Patient.builder()
                .id(1L)
                .name("Arjun Krishnan")
                .email("arjun.k@email.com")
                .user(testUser)
                .build();
    }

    @Test
    @DisplayName("Should create user successfully on signup")
    void signup_WithValidData_ShouldCreateUser() {
        // Arrange
        when(userRepository.findByUsername("arjun.k@email.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        SignupResponseDto result = authService.signup(signUpRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("arjun.k@email.com");

        verify(userRepository, times(1)).findByUsername("arjun.k@email.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(patientRepository, times(1)).save(any(Patient.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should encode password during signup")
    void signup_ShouldEncodePassword() {
        // Arrange
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        authService.signup(signUpRequest);

        // Assert
        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void login_WithValidCredentials_ShouldReturnJwtToken() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(authUtil.generateAccessToken(testUser)).thenReturn("jwt-token-12345");

        // Act
        LoginResponseDto result = authService.login(loginRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getJwt()).isEqualTo("jwt-token-12345");
        assertThat(result.getUserId()).isEqualTo(1L);

        verify(authenticationManager, times(1)).authenticate(any());
        verify(authUtil, times(1)).generateAccessToken(testUser);
    }

    @Test
    @DisplayName("Should throw exception on invalid credentials")
    void login_WithInvalidCredentials_ShouldThrowException() {
        // Arrange
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);

        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    @DisplayName("Should throw exception when user already exists")
    void signup_WithExistingUsername_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername("arjun.k@email.com")).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThatThrownBy(() -> authService.signup(signUpRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User already exists");

        verify(userRepository, times(1)).findByUsername("arjun.k@email.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create patient record during signup")
    void signup_ShouldCreatePatientRecord() {
        // Arrange
        Patient[] savedPatient = new Patient[1];

        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            savedPatient[0] = invocation.getArgument(0);
            return savedPatient[0];
        });
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        authService.signup(signUpRequest);

        // Assert
        assertThat(savedPatient[0]).isNotNull();
        assertThat(savedPatient[0].getName()).isEqualTo("Arjun Krishnan");
        assertThat(savedPatient[0].getEmail()).isEqualTo("arjun.k@email.com");

        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    @DisplayName("Should assign PATIENT role by default during signup")
    void signup_ShouldAssignPatientRole() {
        // Arrange
        User[] savedUser = new User[1];

        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            savedUser[0] = invocation.getArgument(0);
            savedUser[0].setId(1L);
            return savedUser[0];
        });

        // Act
        authService.signup(signUpRequest);

        // Assert
        assertThat(savedUser[0].getRoles()).contains(RoleType.PATIENT);
    }
}