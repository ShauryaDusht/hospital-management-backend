package com.shaurya.hospitalManagement.service;

import com.shaurya.hospitalManagement.entity.Patient;
import com.shaurya.hospitalManagement.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    @Transactional
    public Patient getPatientById(Long id) {
        Patient p1 = patientRepository.findById(id).orElseThrow();
        Patient p2 = patientRepository.findById(id).orElseThrow();

        // This returns true as when p1 is fetched it is stored in transient state after taking from db
        // When p2 is fetched the data is already in transient state
        System.out.println(p1 == p2);
        return p1;

    }
}
