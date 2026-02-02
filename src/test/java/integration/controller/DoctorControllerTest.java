package integration.controller;

import com.shaurya.hospitalManagement.controller.DoctorController;
import com.shaurya.hospitalManagement.dto.AppointmentResponseDto;
import com.shaurya.hospitalManagement.dto.DoctorResponseDto;
import com.shaurya.hospitalManagement.entity.User;
import com.shaurya.hospitalManagement.entity.type.RoleType;
import com.shaurya.hospitalManagement.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private DoctorController doctorController;

    private User doctorUser;
    private AppointmentResponseDto appointmentResponseDto;

    @BeforeEach
    void setUp() {
        doctorUser = User.builder()
                .id(1L)
                .username("doctor@example.com")
                .roles(Set.of(RoleType.DOCTOR))
                .build();

        DoctorResponseDto doctorDto = new DoctorResponseDto();
        doctorDto.setId(1L);
        doctorDto.setName("Dr. Smith");

        appointmentResponseDto = new AppointmentResponseDto();
        appointmentResponseDto.setId(1L);
        appointmentResponseDto.setAppointmentTime(LocalDateTime.now());
        appointmentResponseDto.setReason("Checkup");
        appointmentResponseDto.setDoctor(doctorDto);
    }

    @Test
    void getAllAppointmentsOfDoctor_ShouldReturnAppointments_WhenUserIsAuthenticated() {
        // Arrange
        List<AppointmentResponseDto> appointments = Collections.singletonList(appointmentResponseDto);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(doctorUser);
        when(appointmentService.getAllAppointmentsOfDoctor(1L)).thenReturn(appointments);
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<List<AppointmentResponseDto>> response =
                doctorController.getAllAppointmentsOfDoctor();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Checkup", response.getBody().getFirst().getReason());
        verify(appointmentService, times(1)).getAllAppointmentsOfDoctor(1L);
    }

    @Test
    void getAllAppointmentsOfDoctor_ShouldReturnNotFound_WhenUserIsNull() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<List<AppointmentResponseDto>> response =
                doctorController.getAllAppointmentsOfDoctor();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(appointmentService, never()).getAllAppointmentsOfDoctor(anyLong());
    }

    @Test
    void getAllAppointmentsOfDoctor_ShouldReturnEmptyList_WhenNoAppointmentsExist() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(doctorUser);
        when(appointmentService.getAllAppointmentsOfDoctor(1L)).thenReturn(List.of());
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<List<AppointmentResponseDto>> response =
                doctorController.getAllAppointmentsOfDoctor();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getAllAppointmentsOfDoctor_ShouldUseDoctorIdFromAuthentication() {
        // Arrange
        List<AppointmentResponseDto> appointments = Collections.singletonList(appointmentResponseDto);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(doctorUser);
        when(appointmentService.getAllAppointmentsOfDoctor(anyLong())).thenReturn(appointments);
        SecurityContextHolder.setContext(securityContext);

        // Act
        doctorController.getAllAppointmentsOfDoctor();

        // Assert
        verify(appointmentService).getAllAppointmentsOfDoctor(1L);
    }
}