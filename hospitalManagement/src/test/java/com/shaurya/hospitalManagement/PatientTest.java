package com.shaurya.hospitalManagement;

import com.shaurya.hospitalManagement.entity.Patient;
import com.shaurya.hospitalManagement.repository.PatientRepository;
import com.shaurya.hospitalManagement.service.PatientService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

@SpringBootTest
@Disabled
public class PatientTest {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientService patientService;

    @Test
    public void testPatientRepository() {
        List<Patient> patientList = patientRepository.findAll();
        System.out.println(patientList);
    }

    @Test
    public void testTransactionMethods() {
        Patient p1 = patientService.getPatientById(1L);
        System.out.println(p1);
    }
}
