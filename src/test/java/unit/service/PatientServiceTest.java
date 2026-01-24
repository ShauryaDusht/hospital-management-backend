package unit.service;

import com.shaurya.hospitalManagement.dto.PatientResponseDto;
import com.shaurya.hospitalManagement.entity.Patient;
import com.shaurya.hospitalManagement.entity.type.BloodGroupType;
import com.shaurya.hospitalManagement.repository.PatientRepository;
import com.shaurya.hospitalManagement.service.PatientService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PatientService
 *
 * Tests cover:
 * - Getting patient by ID
 * - Getting all patients with pagination
 * - Error handling for non-existent patients
 * - Edge cases (empty results, pagination)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PatientService Unit Tests")
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PatientService patientService;

    private Patient testPatient1;
    private Patient testPatient2;
    private Patient testPatient3;
    private PatientResponseDto responseDto1;
    private PatientResponseDto responseDto2;
    private PatientResponseDto responseDto3;

    @BeforeEach
    void setUp() {
        // Setup test patients
        testPatient1 = new Patient();
        testPatient1.setId(1L);
        testPatient1.setName("Arjun Krishnan");
        testPatient1.setGender("Male");
        testPatient1.setBirthDate(LocalDate.of(1995, 3, 15));
        testPatient1.setBloodGroup(BloodGroupType.O_POSITIVE);

        testPatient2 = new Patient();
        testPatient2.setId(2L);
        testPatient2.setName("Meera Saxena");
        testPatient2.setGender("Female");
        testPatient2.setBirthDate(LocalDate.of(1988, 7, 22));
        testPatient2.setBloodGroup(BloodGroupType.A_POSITIVE);

        testPatient3 = new Patient();
        testPatient3.setId(3L);
        testPatient3.setName("Rohan Malhotra");
        testPatient3.setGender("Male");
        testPatient3.setBirthDate(LocalDate.of(1992, 11, 10));
        testPatient3.setBloodGroup(BloodGroupType.B_POSITIVE);

        // Setup response DTOs
        responseDto1 = new PatientResponseDto();
        responseDto1.setId(1L);
        responseDto1.setName("Arjun Krishnan");
        responseDto1.setGender("Male");
        responseDto1.setBirthDate(LocalDate.of(1995, 3, 15));
        responseDto1.setBloodGroup(BloodGroupType.O_POSITIVE);

        responseDto2 = new PatientResponseDto();
        responseDto2.setId(2L);
        responseDto2.setName("Meera Saxena");
        responseDto2.setGender("Female");
        responseDto2.setBirthDate(LocalDate.of(1988, 7, 22));
        responseDto2.setBloodGroup(BloodGroupType.A_POSITIVE);

        responseDto3 = new PatientResponseDto();
        responseDto3.setId(3L);
        responseDto3.setName("Rohan Malhotra");
        responseDto3.setGender("Male");
        responseDto3.setBirthDate(LocalDate.of(1992, 11, 10));
        responseDto3.setBloodGroup(BloodGroupType.B_POSITIVE);
    }

    /**
     * Test: Get patient by ID - happy path
     * Why: Verifies retrieval and mapping of specific patient
     */
    @Test
    @DisplayName("Should return patient when valid ID provided")
    void getPatientById_WithValidId_ShouldReturnPatientResponseDto() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient1));
        when(modelMapper.map(testPatient1, PatientResponseDto.class)).thenReturn(responseDto1);

        // Act
        PatientResponseDto result = patientService.getPatientById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Arjun Krishnan");
        assertThat(result.getGender()).isEqualTo("Male");
        assertThat(result.getBloodGroup()).isEqualTo(BloodGroupType.O_POSITIVE);
        assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(1995, 3, 15));

        verify(patientRepository, times(1)).findById(1L);
        verify(modelMapper, times(1)).map(testPatient1, PatientResponseDto.class);
    }

    /**
     * Test: Get patient with non-existent ID
     * Why: Ensures proper error handling when patient not found
     */
    @Test
    @DisplayName("Should throw EntityNotFoundException when patient not found")
    void getPatientById_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> patientService.getPatientById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Patient Not Found with id: 999");

        verify(patientRepository, times(1)).findById(999L);
        verify(modelMapper, never()).map(any(), any());
    }

    /**
     * Test: Get all patients with pagination - happy path
     * Why: Verifies pagination and mapping works correctly
     */
    @Test
    @DisplayName("Should return paginated list of patients")
    void getAllPatients_WithPagination_ShouldReturnPatientList() {
        // Arrange
        List<Patient> patients = Arrays.asList(testPatient1, testPatient2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> patientPage = new PageImpl<>(patients, pageable, patients.size());

        when(patientRepository.findAllPatients(pageable)).thenReturn(patientPage);
        when(modelMapper.map(testPatient1, PatientResponseDto.class)).thenReturn(responseDto1);
        when(modelMapper.map(testPatient2, PatientResponseDto.class)).thenReturn(responseDto2);

        // Act
        List<PatientResponseDto> result = patientService.getAllPatients(0, 10);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Arjun Krishnan");
        assertThat(result.get(1).getName()).isEqualTo("Meera Saxena");

        verify(patientRepository, times(1)).findAllPatients(pageable);
        verify(modelMapper, times(2)).map(any(Patient.class), eq(PatientResponseDto.class));
    }

    /**
     * Test: Empty result with pagination
     * Why: Edge case - ensures empty list is handled correctly
     */
    @Test
    @DisplayName("Should return empty list when no patients exist")
    void getAllPatients_WhenNoPatients_ShouldReturnEmptyList() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(patientRepository.findAllPatients(pageable)).thenReturn(emptyPage);

        // Act
        List<PatientResponseDto> result = patientService.getAllPatients(0, 10);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(patientRepository, times(1)).findAllPatients(pageable);
        verify(modelMapper, never()).map(any(), any());
    }

    /**
     * Test: Different page sizes
     * Why: Verifies pagination parameters are passed correctly
     */
    @Test
    @DisplayName("Should respect custom page size parameter")
    void getAllPatients_WithCustomPageSize_ShouldUseCorrectPageable() {
        // Arrange
        List<Patient> patients = Collections.singletonList(testPatient1);
        Pageable expectedPageable = PageRequest.of(0, 5);
        Page<Patient> patientPage = new PageImpl<>(patients, expectedPageable, patients.size());

        when(patientRepository.findAllPatients(expectedPageable)).thenReturn(patientPage);
        when(modelMapper.map(any(Patient.class), eq(PatientResponseDto.class)))
                .thenReturn(responseDto1);

        // Act
        List<PatientResponseDto> result = patientService.getAllPatients(0, 5);

        // Assert
        assertThat(result).hasSize(1);
        verify(patientRepository, times(1)).findAllPatients(expectedPageable);
    }

    /**
     * Test: Second page retrieval
     * Why: Verifies pagination works for pages beyond the first
     */
    @Test
    @DisplayName("Should return correct page when requesting second page")
    void getAllPatients_WithSecondPage_ShouldReturnCorrectPage() {
        // Arrange
        List<Patient> patients = Arrays.asList(testPatient3);
        Pageable pageable = PageRequest.of(1, 2); // Page 1 (second page), size 2
        Page<Patient> patientPage = new PageImpl<>(patients, pageable, 3); // total count is 3

        when(patientRepository.findAllPatients(pageable)).thenReturn(patientPage);
        when(modelMapper.map(testPatient3, PatientResponseDto.class)).thenReturn(responseDto3);

        // Act
        List<PatientResponseDto> result = patientService.getAllPatients(1, 2);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Rohan Malhotra");

        verify(patientRepository, times(1)).findAllPatients(pageable);
    }

    /**
     * Test: Large page size
     * Why: Ensures service can handle requests for many records at once
     */
    @Test
    @DisplayName("Should handle large page size requests")
    void getAllPatients_WithLargePageSize_ShouldReturnAllPatients() {
        // Arrange
        List<Patient> allPatients = Arrays.asList(testPatient1, testPatient2, testPatient3);
        Pageable pageable = PageRequest.of(0, 100);
        Page<Patient> patientPage = new PageImpl<>(allPatients, pageable, allPatients.size());

        when(patientRepository.findAllPatients(pageable)).thenReturn(patientPage);
        when(modelMapper.map(testPatient1, PatientResponseDto.class)).thenReturn(responseDto1);
        when(modelMapper.map(testPatient2, PatientResponseDto.class)).thenReturn(responseDto2);
        when(modelMapper.map(testPatient3, PatientResponseDto.class)).thenReturn(responseDto3);

        // Act
        List<PatientResponseDto> result = patientService.getAllPatients(0, 100);

        // Assert
        assertThat(result).hasSize(3);
        verify(patientRepository, times(1)).findAllPatients(pageable);
        verify(modelMapper, times(3)).map(any(Patient.class), eq(PatientResponseDto.class));
    }

    /**
     * Test: Patient data integrity
     * Why: Ensures all patient fields are correctly mapped
     */
    @Test
    @DisplayName("Should correctly map all patient fields")
    void getPatientById_ShouldMapAllFields() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient1));
        when(modelMapper.map(testPatient1, PatientResponseDto.class)).thenReturn(responseDto1);

        // Act
        PatientResponseDto result = patientService.getPatientById(1L);

        // Assert - Verify all fields are present
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isNotBlank();
        assertThat(result.getGender()).isNotBlank();
        assertThat(result.getBirthDate()).isNotNull();
        assertThat(result.getBloodGroup()).isNotNull();
    }

    /**
     * Test: Different blood group types
     * Why: Ensures enum values are handled correctly
     */
    @Test
    @DisplayName("Should handle different blood group types correctly")
    void getPatientById_WithDifferentBloodGroups_ShouldMapCorrectly() {
        // Arrange
        Patient patientWithAB = new Patient();
        patientWithAB.setId(4L);
        patientWithAB.setBloodGroup(BloodGroupType.AB_NEGATIVE);

        PatientResponseDto responseDtoAB = new PatientResponseDto();
        responseDtoAB.setId(4L);
        responseDtoAB.setBloodGroup(BloodGroupType.AB_NEGATIVE);

        when(patientRepository.findById(4L)).thenReturn(Optional.of(patientWithAB));
        when(modelMapper.map(patientWithAB, PatientResponseDto.class)).thenReturn(responseDtoAB);

        // Act
        PatientResponseDto result = patientService.getPatientById(4L);

        // Assert
        assertThat(result.getBloodGroup()).isEqualTo(BloodGroupType.AB_NEGATIVE);
    }

    /**
     * Test: Zero page number (first page)
     * Why: Validates default behavior with page 0
     */
    @Test
    @DisplayName("Should handle page 0 correctly")
    void getAllPatients_WithPageZero_ShouldReturnFirstPage() {
        // Arrange
        List<Patient> patients = Arrays.asList(testPatient1, testPatient2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> patientPage = new PageImpl<>(patients, pageable, patients.size());

        when(patientRepository.findAllPatients(pageable)).thenReturn(patientPage);
        when(modelMapper.map(any(Patient.class), eq(PatientResponseDto.class)))
                .thenReturn(responseDto1, responseDto2);

        // Act
        List<PatientResponseDto> result = patientService.getAllPatients(0, 10);

        // Assert
        assertThat(result).hasSize(2);
        verify(patientRepository).findAllPatients(PageRequest.of(0, 10));
    }
}