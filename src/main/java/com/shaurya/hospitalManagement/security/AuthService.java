package com.shaurya.hospitalManagement.security;

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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final RateLimiterService rateLimiterService;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        String identifier = loginRequestDto.getUsername();

        // Check rate limit before authentication
        if (!rateLimiterService.isLoginAllowed(identifier)) {
            long retryAfter = rateLimiterService.getLoginTimeUntilReset(identifier);
            int remaining = rateLimiterService.getRemainingLoginAttempts(identifier);
            throw new RateLimitExceededException(
                    "Too many login attempts. Please try again after " + retryAfter + " seconds",
                    retryAfter,
                    remaining
            );
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
            );

            User user = (User) authentication.getPrincipal();

            assert user != null;
            String token = authUtil.generateAccessToken(user);

            // Reset rate limit on successful login
            rateLimiterService.resetLoginAttempts(identifier);

            return new LoginResponseDto(token, user.getId());
        } catch (Exception e) {  // Catches all auth failures
            rateLimiterService.recordLoginAttempt(identifier);
            throw e;
        }
    }

    public User signUpInternal(SignUpRequestDto signupRequestDto, AuthProviderType authProviderType, String providerId) {
        User user = userRepository.findByUsername(signupRequestDto.getUsername()).orElse(null);

        if(user != null) throw new IllegalArgumentException("User already exists");

        user = User.builder()
                .username(signupRequestDto.getUsername())
                .providerId(providerId)
                .providerType(authProviderType)
                .roles(Set.of(RoleType.PATIENT)) // at start make everyone patient
                .build();

        if(authProviderType == AuthProviderType.EMAIL) {
            user.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
        }

        Patient patient = Patient.builder()
                .name(signupRequestDto.getName())
                .email(signupRequestDto.getUsername())
                .user(user)
                .build();
        patientRepository.save(patient);
        return userRepository.save(user);
    }

    public SignupResponseDto signup(SignUpRequestDto signupRequestDto) {
        String identifier = signupRequestDto.getUsername();

        // Check rate limit before signup
        if (!rateLimiterService.isSignupAllowed(identifier)) {
            long retryAfter = rateLimiterService.getSignupTimeUntilReset(identifier);
            int remaining = rateLimiterService.getRemainingSignupAttempts(identifier);
            throw new RateLimitExceededException(
                    "Too many signup attempts. Please try again after " + retryAfter + " seconds",
                    retryAfter,
                    remaining
            );
        }

        // Record signup attempt
        rateLimiterService.recordSignupAttempt(identifier);

        User user = signUpInternal(signupRequestDto, AuthProviderType.EMAIL, null);
        return new SignupResponseDto(user.getId(), user.getUsername());
    }

    @Transactional
    public ResponseEntity<LoginResponseDto> handleOAuth2LoginRequest(OAuth2User oAuth2User, String registrationId) {
        AuthProviderType providerType = authUtil.getProviderTypeFromRegistrationId(registrationId);
        String providerId = authUtil.determineProviderIdFromOAuth2User(oAuth2User, registrationId);

        User user = userRepository.findByProviderIdAndProviderType(providerId, providerType).orElse(null);
        String email = oAuth2User.getAttribute("email");
        String name =  oAuth2User.getAttribute("name");
        User emailUser = userRepository.findByUsername(email).orElse(null);

        if(user == null && emailUser == null) {
            // signup flow
            // user never had any account
            String username = authUtil.determineUsernameFromOAuth2User(oAuth2User, registrationId, providerId);
            user = signUpInternal(new SignUpRequestDto(username, null, name, Set.of(RoleType.PATIENT)), providerType, providerId);
        } else if(user != null) {
            // user already registered but now using oauth
            // so we store email of the user
            if(email != null && !email.isBlank() && !email.equals(user.getUsername())) {
                user.setUsername(email);
                userRepository.save(user);
            }
        } else {
            // emailUser - not null
            // user - null
            // means email is already registered
            throw new BadCredentialsException("This email is already registered with provider "+emailUser.getProviderType());
        }

        LoginResponseDto loginResponseDto = new LoginResponseDto(authUtil.generateAccessToken(user), user.getId());
        return ResponseEntity.ok(loginResponseDto);
    }
}