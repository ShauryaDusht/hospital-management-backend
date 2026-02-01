package unit.entity;

import com.shaurya.hospitalManagement.entity.User;
import com.shaurya.hospitalManagement.entity.type.AuthProviderType;
import com.shaurya.hospitalManagement.entity.type.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for User entity
 * Tests UserDetails implementation and authority generation
 */
@DisplayName("User Entity Tests")
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("arjun.k")
                .password("encodedPassword")
                .providerId("google123")
                .providerType(AuthProviderType.GOOGLE)
                .roles(new HashSet<>(Set.of(RoleType.PATIENT)))
                .build();
    }

    @Test
    @DisplayName("Should generate authorities with ROLE_ prefix")
    void getAuthorities_ShouldIncludeRolePrefix() {
        // Act
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // Assert
        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_PATIENT");
    }

    @Test
    @DisplayName("Should include permissions from RolePermissionMapping")
    void getAuthorities_ShouldIncludePermissions() {
        // Arrange
        user.setRoles(new HashSet<>(Set.of(RoleType.ADMIN)));

        // Act
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // Assert - Admin should have both role and permissions
        assertThat(authorities).isNotEmpty();
        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_ADMIN");
    }

    @Test
    @DisplayName("Should handle multiple roles")
    void getAuthorities_WithMultipleRoles_ShouldReturnAllAuthorities() {
        // Arrange
        user.setRoles(new HashSet<>(Set.of(RoleType.PATIENT, RoleType.DOCTOR)));

        // Act
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // Assert
        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_PATIENT", "ROLE_DOCTOR");
    }

    @Test
    @DisplayName("Should return empty authorities when no roles")
    void getAuthorities_WithNoRoles_ShouldReturnEmpty() {
        // Arrange
        user.setRoles(new HashSet<>());

        // Act
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // Assert
        assertThat(authorities).isEmpty();
    }

    @Test
    @DisplayName("Should build user with builder pattern")
    void builder_ShouldCreateUserWithAllFields() {
        // Act
        User newUser = User.builder()
                .id(2L)
                .username("test.user")
                .password("pass")
                .providerId("github456")
                .providerType(AuthProviderType.GITHUB)
                .roles(new HashSet<>(Set.of(RoleType.DOCTOR)))
                .build();

        // Assert
        assertThat(newUser.getId()).isEqualTo(2L);
        assertThat(newUser.getUsername()).isEqualTo("test.user");
        assertThat(newUser.getPassword()).isEqualTo("pass");
        assertThat(newUser.getProviderId()).isEqualTo("github456");
        assertThat(newUser.getProviderType()).isEqualTo(AuthProviderType.GITHUB);
        assertThat(newUser.getRoles()).containsExactly(RoleType.DOCTOR);
    }

    @Test
    @DisplayName("Should support local authentication provider")
    void user_WithLocalProvider_ShouldHaveNullProviderId() {
        // Arrange
        User localUser = User.builder()
                .username("local.user")
                .password("password")
                .providerType(AuthProviderType.EMAIL)
                .roles(new HashSet<>(Set.of(RoleType.PATIENT)))
                .build();

        // Assert
        assertThat(localUser.getProviderType()).isEqualTo(AuthProviderType.EMAIL);
        assertThat(localUser.getProviderId()).isNull();
    }

    @Test
    @DisplayName("Should maintain role uniqueness in Set")
    void roles_ShouldNotAllowDuplicates() {
        // Arrange - Create mutable HashSet
        Set<RoleType> roles = new HashSet<>();
        roles.add(RoleType.PATIENT);
        user.setRoles(roles);

        // Act
        user.getRoles().add(RoleType.PATIENT); // Try to add duplicate

        // Assert
        assertThat(user.getRoles()).hasSize(1);
    }

    @Test
    @DisplayName("Should allow adding new roles")
    void roles_ShouldAllowAddingNewRoles() {
        // Arrange - Create mutable HashSet
        Set<RoleType> roles = new HashSet<>();
        roles.add(RoleType.PATIENT);
        user.setRoles(roles);

        // Act
        user.getRoles().add(RoleType.DOCTOR);

        // Assert
        assertThat(user.getRoles()).hasSize(2);
        assertThat(user.getRoles()).containsExactlyInAnyOrder(
                RoleType.PATIENT,
                RoleType.DOCTOR
        );
    }
}