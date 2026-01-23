package unit.service;

import com.shaurya.hospitalManagement.dto.DoctorResponseDto;
import com.shaurya.hospitalManagement.dto.OnBoardDoctorRequestDto;
import com.shaurya.hospitalManagement.entity.Doctor;
import com.shaurya.hospitalManagement.entity.User;
import com.shaurya.hospitalManagement.entity.type.RoleType;
import com.shaurya.hospitalManagement.repository.DoctorRepository;
import com.shaurya.hospitalManagement.repository.UserRepository;
import com.shaurya.hospitalManagement.service.DoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DoctorService
 *
 * Tests cover:
 * - Retrieving all doctors
 * - Onboarding new doctors
 * - Role assignment
 * - Error handling
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DoctorService Unit Tests")
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor testDoctor1;
    private Doctor testDoctor2;
    private DoctorResponseDto responseDto1;
    private DoctorResponseDto responseDto2;
    private User testUser;
    private OnBoardDoctorRequestDto onBoardRequest;

    @BeforeEach
    void setUp() {
        // Setup test doctors
        testDoctor1 = Doctor.builder()
                .id(1L)
                .name("Dr. Amit Mehta")
                .specialization("Cardiology")
                .email("amit.mehta@hospital.com")
                .build();

        testDoctor2 = Doctor.builder()
                .id(2L)
                .name("Dr. Neha Verma")
                .specialization("Neurology")
                .email("neha.verma@hospital.com")
                .build();

        // Setup response DTOs
        responseDto1 = new DoctorResponseDto();
        responseDto1.setId(1L);
        responseDto1.setName("Dr. Amit Mehta");
        responseDto1.setSpecialization("Cardiology");
        responseDto1.setEmail("amit.mehta@hospital.com");

        responseDto2 = new DoctorResponseDto();
        responseDto2.setId(2L);
        responseDto2.setName("Dr. Neha Verma");
        responseDto2.setSpecialization("Neurology");
        responseDto2.setEmail("neha.verma@hospital.com");

        // Setup test user
        testUser = new User();
        testUser.setId(5L);
        testUser.setUsername("new.doctor");
        testUser.setRoles(new HashSet<>(Set.of(RoleType.PATIENT)));

        // Setup onboard request
        onBoardRequest = new OnBoardDoctorRequestDto();
        onBoardRequest.setUserId("5");
        onBoardRequest.setName("Dr. Karan Patel");
        onBoardRequest.setSpecialization("Pediatrics");
    }

    /**
     * Test: Get all doctors
     * Why: Verifies retrieval and mapping of all doctors
     */
    @Test
    @DisplayName("Should return all doctors when doctors exist")
    void getAllDoctors_WhenDoctorsExist_ShouldReturnDoctorList() {
        // Arrange
        List<Doctor> doctors = Arrays.asList(testDoctor1, testDoctor2);
        when(doctorRepository.findAll()).thenReturn(doctors);
        when(modelMapper.map(testDoctor1, DoctorResponseDto.class)).thenReturn(responseDto1);
        when(modelMapper.map(testDoctor2, DoctorResponseDto.class)).thenReturn(responseDto2);

        // Act
        List<DoctorResponseDto> result = doctorService.getAllDoctors();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Dr. Amit Mehta");
        assertThat(result.get(0).getSpecialization()).isEqualTo("Cardiology");
        assertThat(result.get(1).getName()).isEqualTo("Dr. Neha Verma");
        assertThat(result.get(1).getSpecialization()).isEqualTo("Neurology");

        verify(doctorRepository, times(1)).findAll();
        verify(modelMapper, times(2)).map(any(Doctor.class), eq(DoctorResponseDto.class));
    }

    /**
     * Test: Get all doctors when none exist
     * Why: Edge case - ensures empty list handling
     */
    @Test
    @DisplayName("Should return empty list when no doctors exist")
    void getAllDoctors_WhenNoDoctors_ShouldReturnEmptyList() {
        // Arrange
        when(doctorRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<DoctorResponseDto> result = doctorService.getAllDoctors();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(doctorRepository, times(1)).findAll();
        verify(modelMapper, never()).map(any(), any());
    }

    /**
     * Test: Onboard new doctor
     * Why: Verifies doctor creation and role assignment
     */
    @Test
    @DisplayName("Should onboard new doctor successfully when valid data provided")
    void onBoardNewDoctor_WithValidData_ShouldCreateDoctorAndAssignRole() {
        // Arrange
        Doctor newDoctor = Doctor.builder()
                .id(5L)
                .name("Dr. Karan Patel")
                .specialization("Pediatrics")
                .user(testUser)
                .build();

        DoctorResponseDto expectedResponse = new DoctorResponseDto();
        expectedResponse.setId(5L);
        expectedResponse.setName("Dr. Karan Patel");
        expectedResponse.setSpecialization("Pediatrics");

        when(userRepository.findById(5L)).thenReturn(Optional.of(testUser));
        when(doctorRepository.existsById(5L)).thenReturn(false);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(newDoctor);
        when(modelMapper.map(newDoctor, DoctorResponseDto.class)).thenReturn(expectedResponse);

        // Act
        DoctorResponseDto result = doctorService.onBoardNewDoctor(onBoardRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getName()).isEqualTo("Dr. Karan Patel");
        assertThat(result.getSpecialization()).isEqualTo("Pediatrics");

        // Verify DOCTOR role was added to user
        assertThat(testUser.getRoles()).contains(RoleType.DOCTOR);
        assertThat(testUser.getRoles()).contains(RoleType.PATIENT); // Original role preserved

        verify(userRepository, times(1)).findById(5L);
        verify(doctorRepository, times(1)).existsById(5L);
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    /**
     * Test: Onboard existing doctor
     * Why: Prevents duplicate doctor entries
     */
    @Test
    @DisplayName("Should throw exception when user is already a doctor")
    void onBoardNewDoctor_WhenAlreadyDoctor_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(5L)).thenReturn(Optional.of(testUser));
        when(doctorRepository.existsById(5L)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> doctorService.onBoardNewDoctor(onBoardRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Already a doctor");

        verify(userRepository, times(1)).findById(5L);
        verify(doctorRepository, times(1)).existsById(5L);
        verify(doctorRepository, never()).save(any());
    }

    /**
     * Test: Onboard with non-existent user
     * Why: Ensures proper error handling for invalid user ID
     */
    @Test
    @DisplayName("Should throw exception when user not found")
    void onBoardNewDoctor_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(5L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> doctorService.onBoardNewDoctor(onBoardRequest))
                .isInstanceOf(NoSuchElementException.class);

        verify(userRepository, times(1)).findById(5L);
        verify(doctorRepository, never()).existsById(any());
        verify(doctorRepository, never()).save(any());
    }

    /**
     * Test: Role assignment preserves existing roles
     * Why: Ensures user can have multiple roles (e.g., PATIENT and DOCTOR)
     */
    @Test
    @DisplayName("Should preserve existing user roles when onboarding doctor")
    void onBoardNewDoctor_ShouldPreserveExistingRoles() {
        // Arrange
        testUser.setRoles(new HashSet<>(Set.of(RoleType.PATIENT, RoleType.ADMIN)));

        Doctor newDoctor = Doctor.builder()
                .id(5L)
                .name("Dr. Karan Patel")
                .specialization("Pediatrics")
                .user(testUser)
                .build();

        when(userRepository.findById(5L)).thenReturn(Optional.of(testUser));
        when(doctorRepository.existsById(5L)).thenReturn(false);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(newDoctor);
        when(modelMapper.map(any(), eq(DoctorResponseDto.class))).thenReturn(new DoctorResponseDto());

        // Act
        doctorService.onBoardNewDoctor(onBoardRequest);

        // Assert
        assertThat(testUser.getRoles()).hasSize(3);
        assertThat(testUser.getRoles()).containsExactlyInAnyOrder(
                RoleType.PATIENT,
                RoleType.ADMIN,
                RoleType.DOCTOR
        );
    }

    /**
     * Test: Single doctor retrieval
     * Why: Verifies list with one doctor works correctly
     */
    @Test
    @DisplayName("Should return single doctor when only one exists")
    void getAllDoctors_WithSingleDoctor_ShouldReturnListWithOneDoctor() {
        // Arrange
        when(doctorRepository.findAll()).thenReturn(Collections.singletonList(testDoctor1));
        when(modelMapper.map(testDoctor1, DoctorResponseDto.class)).thenReturn(responseDto1);

        // Act
        List<DoctorResponseDto> result = doctorService.getAllDoctors();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Dr. Amit Mehta");

        verify(modelMapper, times(1)).map(any(Doctor.class), eq(DoctorResponseDto.class));
    }
}