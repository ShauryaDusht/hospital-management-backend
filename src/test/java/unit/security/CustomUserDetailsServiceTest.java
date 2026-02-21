package unit.security;

import com.shaurya.hospitalManagement.entity.User;
import com.shaurya.hospitalManagement.repository.UserRepository;
import com.shaurya.hospitalManagement.security.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService Tests")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Should load user by username successfully")
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        // Arrange
        User user = User.builder().id(1L).username("test@example.com").build();
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(user));

        // Act
        UserDetails result = customUserDetailsService.loadUserByUsername("test@example.com");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("test@example.com");
        verify(userRepository).findByUsername("test@example.com");
    }

    @Test
    @DisplayName("Should throw NoSuchElementException when user not found")
    void loadUserByUsername_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("nonexistent@example.com"))
                .isInstanceOf(NoSuchElementException.class);

        verify(userRepository).findByUsername("nonexistent@example.com");
    }
}