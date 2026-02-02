package integration.controller;

import com.shaurya.hospitalManagement.controller.PatientController;
import com.shaurya.hospitalManagement.dto.AppointmentResponseDto;
import com.shaurya.hospitalManagement.dto.CreateAppointmentRequestDto;
import com.shaurya.hospitalManagement.dto.DoctorResponseDto;
import com.shaurya.hospitalManagement.dto.PatientResponseDto;
import com.shaurya.hospitalManagement.entity.type.BloodGroupType;
import com.shaurya.hospitalManagement.service.AppointmentService;
import com.shaurya.hospitalManagement.service.PatientService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private PatientController patientController;

    private PatientResponseDto patientResponseDto;
    private CreateAppointmentRequestDto createAppointmentRequestDto;
    private AppointmentResponseDto appointmentResponseDto;

    @BeforeEach
    void setUp() {
        patientResponseDto = new PatientResponseDto();
        patientResponseDto.setId(4L);
        patientResponseDto.setName("John Doe");
        patientResponseDto.setGender("Male");
        patientResponseDto.setBirthDate(LocalDate.of(1990, 1, 1));
        patientResponseDto.setBloodGroup(BloodGroupType.O_POSITIVE);

        createAppointmentRequestDto = new CreateAppointmentRequestDto();
        createAppointmentRequestDto.setDoctorId(1L);
        createAppointmentRequestDto.setPatientId(4L);
        createAppointmentRequestDto.setAppointmentTime(LocalDateTime.now().plusDays(1));
        createAppointmentRequestDto.setReason("Annual checkup");

        DoctorResponseDto doctorDto = new DoctorResponseDto();
        doctorDto.setId(1L);
        doctorDto.setName("Dr. Smith");

        appointmentResponseDto = new AppointmentResponseDto();
        appointmentResponseDto.setId(1L);
        appointmentResponseDto.setAppointmentTime(createAppointmentRequestDto.getAppointmentTime());
        appointmentResponseDto.setReason("Annual checkup");
        appointmentResponseDto.setDoctor(doctorDto);
    }

    @Test
    void createNewAppointment_ShouldReturnCreatedAppointment() {
        // Arrange
        when(appointmentService.createNewAppointment(any(CreateAppointmentRequestDto.class)))
                .thenReturn(appointmentResponseDto);

        // Act
        ResponseEntity<AppointmentResponseDto> response =
                patientController.createNewAppointment(createAppointmentRequestDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Annual checkup", response.getBody().getReason());
        assertEquals("Dr. Smith", response.getBody().getDoctor().getName());
        verify(appointmentService, times(1)).createNewAppointment(createAppointmentRequestDto);
    }

    @Test
    void createNewAppointment_ShouldPassCorrectDetails() {
        // Arrange
        when(appointmentService.createNewAppointment(any(CreateAppointmentRequestDto.class)))
                .thenReturn(appointmentResponseDto);

        // Act
        patientController.createNewAppointment(createAppointmentRequestDto);

        // Assert
        verify(appointmentService).createNewAppointment(argThat(dto ->
                dto.getDoctorId().equals(1L) &&
                        dto.getPatientId().equals(4L) &&
                        dto.getReason().equals("Annual checkup")
        ));
    }

    @Test
    void createNewAppointment_ShouldPropagateException_WhenDoctorNotFound() {
        // Arrange
        when(appointmentService.createNewAppointment(any(CreateAppointmentRequestDto.class)))
                .thenThrow(new EntityNotFoundException("Doctor not found"));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> patientController.createNewAppointment(createAppointmentRequestDto));
    }

    @Test
    void createNewAppointment_ShouldPropagateException_WhenPatientNotFound() {
        // Arrange
        when(appointmentService.createNewAppointment(any(CreateAppointmentRequestDto.class)))
                .thenThrow(new EntityNotFoundException("Patient not found"));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> patientController.createNewAppointment(createAppointmentRequestDto));
    }

    @Test
    void getPatientProfile_ShouldReturnPatientDetails() {
        // Arrange
        when(patientService.getPatientById(4L)).thenReturn(patientResponseDto);

        // Act
        ResponseEntity<PatientResponseDto> response = patientController.getPatientProfile();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(4L, response.getBody().getId());
        assertEquals("John Doe", response.getBody().getName());
        assertEquals("Male", response.getBody().getGender());
        assertEquals(BloodGroupType.O_POSITIVE, response.getBody().getBloodGroup());
        verify(patientService, times(1)).getPatientById(4L);
    }

    @Test
    void getPatientProfile_ShouldUseHardcodedPatientId() {
        // Arrange
        when(patientService.getPatientById(anyLong())).thenReturn(patientResponseDto);

        // Act
        patientController.getPatientProfile();

        // Assert
        verify(patientService).getPatientById(4L);
    }

    @Test
    void getPatientProfile_ShouldPropagateException_WhenPatientNotFound() {
        // Arrange
        when(patientService.getPatientById(4L))
                .thenThrow(new EntityNotFoundException("Patient not found"));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> patientController.getPatientProfile());
    }
}