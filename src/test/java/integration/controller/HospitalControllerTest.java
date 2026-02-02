package integration.controller;

import com.shaurya.hospitalManagement.controller.HospitalController;
import com.shaurya.hospitalManagement.dto.DoctorResponseDto;
import com.shaurya.hospitalManagement.service.DoctorService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HospitalControllerTest {

    @Mock
    private DoctorService doctorService;

    @InjectMocks
    private HospitalController hospitalController;

    private DoctorResponseDto doctor1;
    private DoctorResponseDto doctor2;

    @BeforeEach
    void setUp() {
        doctor1 = new DoctorResponseDto();
        doctor1.setId(1L);
        doctor1.setName("Dr. Smith");
        doctor1.setSpecialization("Cardiology");
        doctor1.setEmail("dr.smith@hospital.com");

        doctor2 = new DoctorResponseDto();
        doctor2.setId(2L);
        doctor2.setName("Dr. Johnson");
        doctor2.setSpecialization("Neurology");
        doctor2.setEmail("dr.johnson@hospital.com");
    }

    @Test
    void getAllDoctors_ShouldReturnListOfDoctors() {
        // Arrange
        List<DoctorResponseDto> doctors = Arrays.asList(doctor1, doctor2);
        when(doctorService.getAllDoctors()).thenReturn(doctors);

        // Act
        ResponseEntity<List<DoctorResponseDto>> response = hospitalController.getAllDoctors();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Dr. Smith", response.getBody().get(0).getName());
        assertEquals("Cardiology", response.getBody().get(0).getSpecialization());
        assertEquals("Dr. Johnson", response.getBody().get(1).getName());
        verify(doctorService, times(1)).getAllDoctors();
    }

    @Test
    void getAllDoctors_ShouldReturnEmptyList_WhenNoDoctorsExist() {
        // Arrange
        when(doctorService.getAllDoctors()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<DoctorResponseDto>> response = hospitalController.getAllDoctors();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(doctorService, times(1)).getAllDoctors();
    }

    @Test
    void getAllDoctors_ShouldReturnSingleDoctor_WhenOnlyOneDoctorExists() {
        // Arrange
        List<DoctorResponseDto> doctors = Collections.singletonList(doctor1);
        when(doctorService.getAllDoctors()).thenReturn(doctors);

        // Act
        ResponseEntity<List<DoctorResponseDto>> response = hospitalController.getAllDoctors();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Dr. Smith", response.getBody().getFirst().getName());
    }

    @Test
    void getAllDoctors_ShouldInvokeDoctorServiceOnce() {
        // Arrange
        when(doctorService.getAllDoctors()).thenReturn(Arrays.asList(doctor1, doctor2));

        // Act
        hospitalController.getAllDoctors();

        // Assert
        verify(doctorService, times(1)).getAllDoctors();
        verifyNoMoreInteractions(doctorService);
    }
}