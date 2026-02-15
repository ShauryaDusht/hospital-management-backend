package unit.service;

import com.shaurya.hospitalManagement.entity.Insurance;
import com.shaurya.hospitalManagement.entity.Patient;
import com.shaurya.hospitalManagement.repository.PatientRepository;
import com.shaurya.hospitalManagement.service.InsuranceService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InsuranceService Tests")
class InsuranceServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private InsuranceService insuranceService;

    private Patient patient;
    private Insurance insurance;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);

        insurance = new Insurance();
        insurance.setId(1L);
    }

    @Test
    @DisplayName("Should assign insurance to patient successfully")
    void assignInsuranceToPatient_WhenPatientExists_ShouldAssignInsurance() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        // Act
        Patient result = insuranceService.assignInsuranceToPatient(insurance, 1L);

        // Assert
        assertThat(result.getInsurance()).isEqualTo(insurance);
        assertThat(insurance.getPatient()).isEqualTo(patient);
        verify(patientRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when patient not found")
    void assignInsuranceToPatient_WhenPatientNotFound_ShouldThrowException() {
        // Arrange
        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> insuranceService.assignInsuranceToPatient(insurance, 999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Patient not found with id: 999");

        verify(patientRepository).findById(999L);
    }

    @Test
    @DisplayName("Should disassociate insurance from patient successfully")
    void disassociateInsuranceFromPatient_WhenPatientExists_ShouldRemoveInsurance() {
        // Arrange
        patient.setInsurance(insurance);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        // Act
        Patient result = insuranceService.disaccociateInsuranceFromPatient(1L);

        // Assert
        assertThat(result.getInsurance()).isNull();
        verify(patientRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when disassociating from non-existent patient")
    void disassociateInsuranceFromPatient_WhenPatientNotFound_ShouldThrowException() {
        // Arrange
        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> insuranceService.disaccociateInsuranceFromPatient(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Patient not found with id: 999");

        verify(patientRepository).findById(999L);
    }
}