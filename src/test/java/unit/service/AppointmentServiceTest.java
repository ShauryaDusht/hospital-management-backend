package unit.service;

import com.shaurya.hospitalManagement.dto.AppointmentResponseDto;
import com.shaurya.hospitalManagement.dto.CreateAppointmentRequestDto;
import com.shaurya.hospitalManagement.entity.Appointment;
import com.shaurya.hospitalManagement.entity.Doctor;
import com.shaurya.hospitalManagement.entity.Patient;
import com.shaurya.hospitalManagement.repository.AppointmentRepository;
import com.shaurya.hospitalManagement.repository.DoctorRepository;
import com.shaurya.hospitalManagement.repository.PatientRepository;
import com.shaurya.hospitalManagement.service.AppointmentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AppointmentService

 * Purpose: Test business logic in isolation without database
 * Uses: Mockito to mock dependencies (repositories)

 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentService Unit Tests")
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AppointmentService appointmentService;

    private Patient testPatient;
    private Doctor testDoctor;
    private Appointment testAppointment;
    private CreateAppointmentRequestDto createRequestDto;
    private AppointmentResponseDto responseDto;

    @BeforeEach
    void setUp() {
        // Setup test data before each test
        // This ensures each test has fresh, isolated data

        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setName("Arjun Krishnan");
        testPatient.setAppointments(new ArrayList<>());

        testDoctor = new Doctor();
        testDoctor.setId(1L);
        testDoctor.setName("Dr. Amit Mehta");
        testDoctor.setSpecialization("Cardiology");
        testDoctor.setAppointments(new ArrayList<>());

        testAppointment = Appointment.builder()
                .id(1L)
                .appointmentTime(LocalDateTime.of(2026, 1, 25, 10, 30))
                .reason("Routine checkup")
                .patient(testPatient)
                .doctor(testDoctor)
                .build();

        createRequestDto = new CreateAppointmentRequestDto();
        createRequestDto.setDoctorId(1L);
        createRequestDto.setPatientId(1L);
        createRequestDto.setAppointmentTime(LocalDateTime.of(2026, 1, 25, 10, 30));
        createRequestDto.setReason("Routine checkup");

        responseDto = new AppointmentResponseDto();
        responseDto.setId(1L);
        responseDto.setAppointmentTime(LocalDateTime.of(2026, 1, 25, 10, 30));
        responseDto.setReason("Routine checkup");
    }

    /**
     * Test: Happy path for creating appointment
     * Why: Verifies the main functionality works correctly
     */
    @Test
    @DisplayName("Should create appointment successfully when valid data provided")
    void createNewAppointment_WithValidData_ShouldReturnAppointmentResponseDto() {
        // Arrange: Setup mocked behavior
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);
        when(modelMapper.map(testAppointment, AppointmentResponseDto.class)).thenReturn(responseDto);

        // Act: Call the method under test
        AppointmentResponseDto result = appointmentService.createNewAppointment(createRequestDto);

        // Assert: Verify the results
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getReason()).isEqualTo("Routine checkup");
        assertThat(result.getAppointmentTime()).isEqualTo(LocalDateTime.of(2026, 1, 25, 10, 30));

        // Verify interactions with mocks
        verify(patientRepository, times(1)).findById(1L);
        verify(doctorRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
        verify(modelMapper, times(1)).map(testAppointment, AppointmentResponseDto.class);
    }

    /**
     * Test: Patient not found scenario
     * Why: Error handling when patient doesn't exist
     */
    @Test
    @DisplayName("Should throw EntityNotFoundException when patient not found")
    void createNewAppointment_WithInvalidPatientId_ShouldThrowException() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert: Verify exception is thrown
        assertThatThrownBy(() -> appointmentService.createNewAppointment(createRequestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Patient not found with ID: 1");

        // Verify that doctor repository and save were never called
        verify(patientRepository, times(1)).findById(1L);
        verify(doctorRepository, never()).findById(any());
        verify(appointmentRepository, never()).save(any());
    }

    /**
     * Test: Doctor not found scenario
     * Why: Error handling when doctor doesn't exist
     */
    @Test
    @DisplayName("Should throw EntityNotFoundException when doctor not found")
    void createNewAppointment_WithInvalidDoctorId_ShouldThrowException() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> appointmentService.createNewAppointment(createRequestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Doctor not found with ID: 1");

        verify(patientRepository, times(1)).findById(1L);
        verify(doctorRepository, times(1)).findById(1L);
        verify(appointmentRepository, never()).save(any());
    }

    /**
     * Test: Verify bidirectional relationship is maintained
     * Why: Ensures JPA consistency
     */
    @Test
    @DisplayName("Should maintain bidirectional relationship between appointment and patient")
    void createNewAppointment_ShouldAddAppointmentToPatientList() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);
        when(modelMapper.map(any(), eq(AppointmentResponseDto.class))).thenReturn(responseDto);

        // Act
        appointmentService.createNewAppointment(createRequestDto);

        // Assert: Verify patient's appointment list was updated
        assertThat(testPatient.getAppointments()).hasSize(1);
        assertThat(testPatient.getAppointments().getFirst().getReason()).isEqualTo("Routine checkup");
    }

    /**
     * Test: Get all appointments for a doctor
     * Why: Verifies retrieval logic and mapping works correctly
     */
    @Test
    @DisplayName("Should return all appointments for a specific doctor")
    void getAllAppointmentsOfDoctor_WithValidDoctorId_ShouldReturnAppointmentList() {
        // Arrange
        List<Appointment> appointments = new ArrayList<>();
        appointments.add(testAppointment);
        testDoctor.setAppointments(appointments);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(modelMapper.map(any(Appointment.class), eq(AppointmentResponseDto.class)))
                .thenReturn(responseDto);

        // Act
        List<AppointmentResponseDto> result = appointmentService.getAllAppointmentsOfDoctor(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getReason()).isEqualTo("Routine checkup");

        verify(doctorRepository, times(1)).findById(1L);
        verify(modelMapper, times(1)).map(any(Appointment.class), eq(AppointmentResponseDto.class));
    }

    /**
     * Test: Reassign appointment to another doctor
     * Why: Tests update functionality and relationship management
     */
    @Test
    @DisplayName("Should reassign appointment to another doctor successfully")
    void reAssignAppointmentToAnotherDoctor_WithValidIds_ShouldUpdateDoctorAndReturn() {
        // Arrange
        Doctor newDoctor = new Doctor();
        newDoctor.setId(2L);
        newDoctor.setName("Dr. Neha Verma");
        newDoctor.setAppointments(new ArrayList<>());

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(newDoctor));

        // Act
        Appointment result = appointmentService.reAssignAppointmentToAnotherDoctor(1L, 2L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getDoctor().getId()).isEqualTo(2L);
        assertThat(result.getDoctor().getName()).isEqualTo("Dr. Neha Verma");
        assertThat(newDoctor.getAppointments()).contains(testAppointment);

        verify(appointmentRepository, times(1)).findById(1L);
        verify(doctorRepository, times(1)).findById(2L);
    }

    /**
     * Test: Multiple appointments mapping
     * Why: Ensures bulk operations work correctly
     */
    @Test
    @DisplayName("Should map multiple appointments correctly")
    void getAllAppointmentsOfDoctor_WithMultipleAppointments_ShouldReturnAllMapped() {
        // Arrange
        Appointment appointment2 = Appointment.builder()
                .id(2L)
                .appointmentTime(LocalDateTime.of(2026, 1, 26, 14, 0))
                .reason("Follow-up")
                .build();

        List<Appointment> appointments = List.of(testAppointment, appointment2);
        testDoctor.setAppointments(appointments);

        AppointmentResponseDto responseDto2 = new AppointmentResponseDto();
        responseDto2.setId(2L);
        responseDto2.setReason("Follow-up");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(modelMapper.map(testAppointment, AppointmentResponseDto.class)).thenReturn(responseDto);
        when(modelMapper.map(appointment2, AppointmentResponseDto.class)).thenReturn(responseDto2);

        // Act
        List<AppointmentResponseDto> result = appointmentService.getAllAppointmentsOfDoctor(1L);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getReason()).isEqualTo("Routine checkup");
        assertThat(result.get(1).getReason()).isEqualTo("Follow-up");

        verify(modelMapper, times(2)).map(any(Appointment.class), eq(AppointmentResponseDto.class));
    }
}