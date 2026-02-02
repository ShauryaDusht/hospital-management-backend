package integration.controller;

import com.shaurya.hospitalManagement.controller.AdminController;
import com.shaurya.hospitalManagement.dto.DoctorResponseDto;
import com.shaurya.hospitalManagement.dto.OnBoardDoctorRequestDto;
import com.shaurya.hospitalManagement.dto.PatientResponseDto;
import com.shaurya.hospitalManagement.service.DoctorService;
import com.shaurya.hospitalManagement.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private PatientService patientService;

    @Mock
    private DoctorService doctorService;

    @InjectMocks
    private AdminController adminController;

    private PatientResponseDto patientResponseDto;
    private DoctorResponseDto doctorResponseDto;
    private OnBoardDoctorRequestDto onBoardDoctorRequestDto;

    @BeforeEach
    void setUp() {
        patientResponseDto = new PatientResponseDto();
        patientResponseDto.setId(1L);
        patientResponseDto.setName("John Doe");

        doctorResponseDto = new DoctorResponseDto();
        doctorResponseDto.setId(1L);
        doctorResponseDto.setName("Dr. Smith");
        doctorResponseDto.setSpecialization("Cardiology");

        onBoardDoctorRequestDto = new OnBoardDoctorRequestDto();
        onBoardDoctorRequestDto.setUserId("1");
        onBoardDoctorRequestDto.setName("Dr. Smith");
        onBoardDoctorRequestDto.setSpecialization("Cardiology");
    }

    @Test
    void getAllPatients_ShouldReturnPatientList_WithDefaultPagination() {
        // Arrange
        List<PatientResponseDto> patients = Collections.singletonList(patientResponseDto);
        when(patientService.getAllPatients(0, 10)).thenReturn(patients);

        // Act
        ResponseEntity<List<PatientResponseDto>> response = adminController.getAllPatients(0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("John Doe", response.getBody().getFirst().getName());
        verify(patientService, times(1)).getAllPatients(0, 10);
    }

    @Test
    void getAllPatients_ShouldReturnEmptyList_WhenNoPatientsExist() {
        // Arrange
        when(patientService.getAllPatients(anyInt(), anyInt())).thenReturn(List.of());

        // Act
        ResponseEntity<List<PatientResponseDto>> response = adminController.getAllPatients(0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getAllPatients_ShouldApplyCustomPagination() {
        // Arrange
        List<PatientResponseDto> patients = Collections.singletonList(patientResponseDto);
        when(patientService.getAllPatients(2, 5)).thenReturn(patients);

        // Act
        ResponseEntity<List<PatientResponseDto>> response = adminController.getAllPatients(2, 5);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(patientService, times(1)).getAllPatients(2, 5);
    }

    @Test
    void onBoardNewDoctor_ShouldReturnCreatedDoctor() {
        // Arrange
        when(doctorService.onBoardNewDoctor(any(OnBoardDoctorRequestDto.class)))
                .thenReturn(doctorResponseDto);

        // Act
        ResponseEntity<DoctorResponseDto> response =
                adminController.onBoardNewDoctor(onBoardDoctorRequestDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Dr. Smith", response.getBody().getName());
        assertEquals("Cardiology", response.getBody().getSpecialization());
        verify(doctorService, times(1)).onBoardNewDoctor(onBoardDoctorRequestDto);
    }

    @Test
    void onBoardNewDoctor_ShouldPassCorrectDtoToService() {
        // Arrange
        when(doctorService.onBoardNewDoctor(any(OnBoardDoctorRequestDto.class)))
                .thenReturn(doctorResponseDto);

        // Act
        adminController.onBoardNewDoctor(onBoardDoctorRequestDto);

        // Assert
        verify(doctorService).onBoardNewDoctor(argThat(dto ->
                dto.getUserId().equals("1") &&
                        dto.getName().equals("Dr. Smith") &&
                        dto.getSpecialization().equals("Cardiology")
        ));
    }
}